package com.aistudio.service.service.impl;

import com.aistudio.service.entity.AiModelConfig;
import com.aistudio.service.entity.GitlabCommitFact;
import com.aistudio.service.service.AiActivityAnalysisService;
import com.aistudio.service.service.AiModelConfigService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiActivityAnalysisServiceImpl implements AiActivityAnalysisService {

    private final AiModelConfigService aiModelConfigService;
    @Qualifier("aiModelRestTemplate")
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<AiActivityAnalysisResult> analyze(Long userId, Long gitlabProjectId, LocalDate analysisDate, List<GitlabCommitFact> commits) {
        AiModelConfig config = aiModelConfigService.getAiModelConfig();
        if (config.getEnabled() == null || config.getEnabled() != 1) {
            return Optional.empty();
        }
        if (blank(config.getBaseUrl()) || blank(config.getModelName()) || blank(config.getApiKey())) {
            log.warn("AI 模型配置不完整，回退 heuristic 分析");
            return Optional.empty();
        }
        try {
            String prompt = buildPrompt(userId, gitlabProjectId, analysisDate, commits);
            JsonNode root = callModel(config, prompt);
            String content = root.path("choices").path(0).path("message").path("content").asText("");
            if (blank(content)) {
                return Optional.empty();
            }
            return Optional.of(parseResult(content, config));
        } catch (Exception ex) {
            log.warn("AI 研发活动分析失败，回退 heuristic 分析", ex);
            return Optional.empty();
        }
    }

    private JsonNode callModel(AiModelConfig config, String prompt) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(config.getApiKey());
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(Map.of(
                "model", config.getModelName(),
                "temperature", 0.2,
                "messages", List.of(
                        Map.of("role", "system", "content", "你是研发活动分析助手，请仅输出合法JSON。"),
                        Map.of("role", "user", "content", prompt)
                ),
                "response_format", Map.of("type", "json_object")
        ), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(trimTrailingSlash(config.getBaseUrl()) + "/chat/completions", request, String.class);
        return objectMapper.readTree(response.getBody());
    }

    private String buildPrompt(Long userId, Long gitlabProjectId, LocalDate analysisDate, List<GitlabCommitFact> commits) {
        StringBuilder builder = new StringBuilder();
        builder.append("请基于以下研发活动事实，输出 JSON，字段包括 work_items, work_summary, progress_summary, risk_summary, blocking_summary, report_title, report_content。\n");
        builder.append("日期: ").append(analysisDate).append("\n");
        builder.append("用户ID: ").append(userId).append("\n");
        builder.append("GitLab项目ID: ").append(gitlabProjectId).append("\n");
        builder.append("commit事实:\n");
        for (GitlabCommitFact commit : commits) {
            builder.append("- sha=").append(commit.getShortSha() != null ? commit.getShortSha() : commit.getCommitSha())
                    .append(", author=").append(commit.getAuthorName())
                    .append(", branch=").append(commit.getBranchName())
                    .append(", message=").append(commit.getCommitMessage())
                    .append(", time=").append(commit.getCommittedAt())
                    .append("\n");
        }
        builder.append("""
                work_items 每项字段:
                title, summary, work_type, progress_status, risk_level, risk_summary,
                blocking_status, blocking_summary, module_names, related_commit_count,
                related_mr_count, evidence_summary, confidence
                枚举约束:
                progress_status in [started,in_progress,converging,near_completion,likely_completed,uncertain]
                risk_level in [low,medium,high]
                blocking_status in [normal,uncertain,blocked]
                work_type in [feature,bugfix,refactor,docs,ops,unknown]
                """);
        return builder.toString();
    }

    private AiActivityAnalysisResult parseResult(String content, AiModelConfig config) throws Exception {
        JsonNode root = objectMapper.readTree(content);
        List<AiWorkItemResult> workItems = new ArrayList<>();
        for (JsonNode item : root.path("work_items")) {
            workItems.add(new AiWorkItemResult(
                    text(item, "title"),
                    text(item, "summary"),
                    enumOrDefault(text(item, "work_type"), List.of("feature", "bugfix", "refactor", "docs", "ops", "unknown"), "unknown"),
                    enumOrDefault(text(item, "progress_status"), List.of("started", "in_progress", "converging", "near_completion", "likely_completed", "uncertain"), "uncertain"),
                    enumOrDefault(text(item, "risk_level"), List.of("low", "medium", "high"), "low"),
                    text(item, "risk_summary"),
                    enumOrDefault(text(item, "blocking_status"), List.of("normal", "uncertain", "blocked"), "uncertain"),
                    text(item, "blocking_summary"),
                    text(item, "module_names"),
                    intOrDefault(item, "related_commit_count", 0),
                    intOrDefault(item, "related_mr_count", 0),
                    text(item, "evidence_summary"),
                    decimalOrDefault(item, "confidence", new BigDecimal("0.70"))
            ));
        }
        if (workItems.isEmpty()) {
            return Optional.<AiActivityAnalysisResult>empty().orElseThrow();
        }
        return new AiActivityAnalysisResult(
                workItems,
                text(root, "work_summary"),
                text(root, "progress_summary"),
                text(root, "risk_summary"),
                text(root, "blocking_summary"),
                text(root, "report_title"),
                text(root, "report_content"),
                defaultString(config.getProviderName()) + ":" + defaultString(config.getModelName())
        );
    }

    private String text(JsonNode node, String field) {
        return node.path(field).asText("");
    }

    private Integer intOrDefault(JsonNode node, String field, int fallback) {
        return node.path(field).isNumber() ? node.path(field).asInt() : fallback;
    }

    private BigDecimal decimalOrDefault(JsonNode node, String field, BigDecimal fallback) {
        try {
            return new BigDecimal(node.path(field).asText());
        } catch (Exception ex) {
            return fallback;
        }
    }

    private String enumOrDefault(String value, List<String> allowed, String fallback) {
        return allowed.contains(value) ? value : fallback;
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }

    private String trimTrailingSlash(String baseUrl) {
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }
}
