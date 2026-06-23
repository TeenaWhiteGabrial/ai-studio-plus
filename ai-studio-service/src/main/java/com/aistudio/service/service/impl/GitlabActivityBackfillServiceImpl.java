package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.config.GitlabActivityProperties;
import com.aistudio.service.entity.GitlabBranchFact;
import com.aistudio.service.entity.GitlabCommitFact;
import com.aistudio.service.entity.GitlabMemberFact;
import com.aistudio.service.entity.GitlabMrFact;
import com.aistudio.service.entity.GitlabProjectSnapshot;
import com.aistudio.service.entity.GitlabRuntimeConfig;
import com.aistudio.service.entity.GitlabUserSnapshot;
import com.aistudio.service.mapper.GitlabBranchFactMapper;
import com.aistudio.service.mapper.GitlabCommitFactMapper;
import com.aistudio.service.mapper.GitlabMemberFactMapper;
import com.aistudio.service.mapper.GitlabMrFactMapper;
import com.aistudio.service.mapper.GitlabProjectSnapshotMapper;
import com.aistudio.service.mapper.GitlabUserSnapshotMapper;
import com.aistudio.service.service.GitlabActivityBackfillService;
import com.aistudio.service.service.GitlabRuntimeConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GitlabActivityBackfillServiceImpl implements GitlabActivityBackfillService {

    private final GitlabActivityProperties properties;
    private final GitlabRuntimeConfigService runtimeConfigService;
    @Qualifier("gitlabActivityRestTemplate")
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final GitlabProjectSnapshotMapper projectSnapshotMapper;
    private final GitlabUserSnapshotMapper userSnapshotMapper;
    private final GitlabBranchFactMapper branchFactMapper;
    private final GitlabMemberFactMapper memberFactMapper;
    private final GitlabMrFactMapper mrFactMapper;
    private final GitlabCommitFactMapper commitFactMapper;

    @Override
    @Transactional
    public void backfillProject(Long gitlabProjectId, LocalDate targetDate) {
        if (gitlabProjectId == null) {
            throw new BusinessException(400, "GitLab 项目ID不能为空");
        }
        GitlabRuntimeConfig runtimeConfig = runtimeConfigService.getRuntimeConfig();
        String apiBaseUrl = resolveApiBaseUrl(runtimeConfig);
        String privateToken = resolvePrivateToken(runtimeConfig);
        if (apiBaseUrl == null || apiBaseUrl.isBlank()) {
            throw new BusinessException(500, "gitlab.activity.api-base-url 未配置");
        }
        if (privateToken == null || privateToken.isBlank()) {
            throw new BusinessException(500, "gitlab.activity.private-token 未配置");
        }

        LocalDate date = targetDate == null ? LocalDate.now() : targetDate;
        syncProject(gitlabProjectId, apiBaseUrl, privateToken);
        syncBranches(gitlabProjectId, apiBaseUrl, privateToken);
        syncMembers(gitlabProjectId, apiBaseUrl, privateToken);
        syncMergeRequests(gitlabProjectId, date, apiBaseUrl, privateToken);
        syncCommits(gitlabProjectId, date, apiBaseUrl, privateToken);
    }

    private void syncProject(Long gitlabProjectId, String apiBaseUrl, String privateToken) {
        Map<String, Object> project = exchangeForObject("/projects/{id}", Map.of("id", gitlabProjectId), apiBaseUrl, privateToken);
        GitlabProjectSnapshot entity = projectSnapshotMapper.selectOne(new LambdaQueryWrapper<GitlabProjectSnapshot>()
                .eq(GitlabProjectSnapshot::getGitlabProjectId, gitlabProjectId)
                .last("LIMIT 1"));
        if (entity == null) {
            entity = new GitlabProjectSnapshot();
            entity.setGitlabProjectId(gitlabProjectId);
        }
        entity.setName(stringValue(project.get("name")));
        entity.setPathWithNamespace(stringValue(project.get("path_with_namespace")));
        entity.setWebUrl(stringValue(project.get("web_url")));
        entity.setDefaultBranch(stringValue(project.get("default_branch")));
        entity.setSyncedAt(LocalDateTime.now());
        saveProjectSnapshot(entity);
    }

    private void syncBranches(Long gitlabProjectId, String apiBaseUrl, String privateToken) {
        List<Map<String, Object>> branches = exchangeForList("/projects/{id}/repository/branches", Map.of("id", gitlabProjectId), Map.of("per_page", 100), apiBaseUrl, privateToken);
        for (Map<String, Object> branch : branches) {
            String branchName = stringValue(branch.get("name"));
            GitlabBranchFact entity = branchFactMapper.selectOne(new LambdaQueryWrapper<GitlabBranchFact>()
                    .eq(GitlabBranchFact::getGitlabProjectId, gitlabProjectId)
                    .eq(GitlabBranchFact::getBranchName, branchName)
                    .last("LIMIT 1"));
            if (entity == null) {
                entity = new GitlabBranchFact();
                entity.setGitlabProjectId(gitlabProjectId);
                entity.setBranchName(branchName);
            }
            entity.setIsDefaultBranch(Boolean.TRUE.equals(branch.get("default")) ? 1 : 0);
            entity.setLastCommitSha(parseNestedString(branch, "commit", "id"));
            entity.setSyncedAt(LocalDateTime.now());
            saveBranchFact(entity);
        }
    }

    private void syncMembers(Long gitlabProjectId, String apiBaseUrl, String privateToken) {
        List<Map<String, Object>> members = exchangeForList("/projects/{id}/members/all", Map.of("id", gitlabProjectId), Map.of("per_page", 100), apiBaseUrl, privateToken);
        for (Map<String, Object> member : members) {
            Long gitlabUserId = parseLong(member.get("id"));
            if (gitlabUserId == null) {
                continue;
            }
            GitlabMemberFact fact = memberFactMapper.selectOne(new LambdaQueryWrapper<GitlabMemberFact>()
                    .eq(GitlabMemberFact::getGitlabProjectId, gitlabProjectId)
                    .eq(GitlabMemberFact::getGitlabUserId, gitlabUserId)
                    .last("LIMIT 1"));
            if (fact == null) {
                fact = new GitlabMemberFact();
                fact.setGitlabProjectId(gitlabProjectId);
                fact.setGitlabUserId(gitlabUserId);
            }
            fact.setAccessLevel(parseInteger(member.get("access_level")));
            fact.setMemberState(stringValue(member.get("state")));
            fact.setSyncedAt(LocalDateTime.now());
            saveMemberFact(fact);

            GitlabUserSnapshot user = userSnapshotMapper.selectOne(new LambdaQueryWrapper<GitlabUserSnapshot>()
                    .eq(GitlabUserSnapshot::getGitlabUserId, gitlabUserId)
                    .last("LIMIT 1"));
            if (user == null) {
                user = new GitlabUserSnapshot();
                user.setGitlabUserId(gitlabUserId);
            }
            user.setUsername(stringValue(member.get("username")));
            user.setName(stringValue(member.get("name")));
            user.setEmail(stringValue(member.get("email")));
            user.setAvatarUrl(stringValue(member.get("avatar_url")));
            user.setState(stringValue(member.get("state")));
            user.setSyncedAt(LocalDateTime.now());
            saveUserSnapshot(user);
        }
    }

    private void syncMergeRequests(Long gitlabProjectId, LocalDate date, String apiBaseUrl, String privateToken) {
        List<Map<String, Object>> mergeRequests = exchangeForList(
                "/projects/{id}/merge_requests",
                Map.of("id", gitlabProjectId),
                Map.of(
                        "updated_after", date.atStartOfDay().toString(),
                        "updated_before", date.plusDays(1).atStartOfDay().toString(),
                        "scope", "all",
                        "per_page", 100
                ),
                apiBaseUrl,
                privateToken
        );
        for (Map<String, Object> mr : mergeRequests) {
            Long gitlabMrId = parseLong(mr.get("id"));
            if (gitlabMrId == null) {
                continue;
            }
            GitlabMrFact entity = mrFactMapper.selectOne(new LambdaQueryWrapper<GitlabMrFact>()
                    .eq(GitlabMrFact::getGitlabMrId, gitlabMrId)
                    .last("LIMIT 1"));
            if (entity == null) {
                entity = new GitlabMrFact();
                entity.setGitlabMrId(gitlabMrId);
            }
            entity.setGitlabProjectId(gitlabProjectId);
            entity.setIid(parseLong(mr.get("iid")));
            entity.setTitle(stringValue(mr.get("title")));
            entity.setDescription(stringValue(mr.get("description")));
            entity.setState(stringValue(mr.get("state")));
            entity.setSourceBranch(stringValue(mr.get("source_branch")));
            entity.setTargetBranch(stringValue(mr.get("target_branch")));
            entity.setAuthorGitlabUserId(parseNestedLong(mr, "author", "id"));
            entity.setCreatedAtGitlab(parseTime(mr.get("created_at")));
            entity.setUpdatedAtGitlab(parseTime(mr.get("updated_at")));
            entity.setLastSyncedAt(LocalDateTime.now());
            saveMrFact(entity);
        }
    }

    private void syncCommits(Long gitlabProjectId, LocalDate date, String apiBaseUrl, String privateToken) {
        List<Map<String, Object>> branches = exchangeForList("/projects/{id}/repository/branches", Map.of("id", gitlabProjectId), Map.of("per_page", 100), apiBaseUrl, privateToken);
        for (Map<String, Object> branch : branches) {
            String branchName = stringValue(branch.get("name"));
            if (branchName == null || branchName.isBlank()) {
                continue;
            }
            List<Map<String, Object>> commits = exchangeForList(
                    "/projects/{id}/repository/commits",
                    Map.of("id", gitlabProjectId),
                    Map.of(
                            "ref_name", branchName,
                            "since", date.atStartOfDay().toString(),
                            "until", date.plusDays(1).atStartOfDay().toString(),
                            "per_page", 100,
                            "with_stats", true
                    ),
                    apiBaseUrl,
                    privateToken
            );
            for (Map<String, Object> commit : commits) {
                String sha = stringValue(commit.get("id"));
                if (sha == null || sha.isBlank()) {
                    continue;
                }
                GitlabCommitFact entity = commitFactMapper.selectOne(new LambdaQueryWrapper<GitlabCommitFact>()
                        .eq(GitlabCommitFact::getGitlabProjectId, gitlabProjectId)
                        .eq(GitlabCommitFact::getCommitSha, sha)
                        .last("LIMIT 1"));
                if (entity == null) {
                    entity = new GitlabCommitFact();
                    entity.setGitlabProjectId(gitlabProjectId);
                    entity.setCommitSha(sha);
                }
                entity.setShortSha(sha.substring(0, Math.min(8, sha.length())));
                entity.setAuthorName(stringValue(commit.get("author_name")));
                entity.setAuthorEmail(stringValue(commit.get("author_email")));
                entity.setCommitMessage(stringValue(commit.get("message")));
                entity.setBranchName(branchName);
                entity.setCommittedAt(parseTime(commit.get("committed_date")));
                entity.setPushedAt(parseTime(commit.get("created_at")));
                entity.setFilePathsJson("[]");
                entity.setDiffSummary(buildCommitDiffSummary(commit));
                entity.setMrIdsJson("[]");
                entity.setLastSyncedAt(LocalDateTime.now());
                saveCommitFact(entity);
            }
        }
    }

    private Map<String, Object> exchangeForObject(String pathTemplate, Map<String, ?> uriVariables, String apiBaseUrl, String privateToken) {
        ResponseEntity<String> response = restTemplate.exchange(buildUri(pathTemplate, uriVariables, Map.of(), apiBaseUrl), HttpMethod.GET, buildRequestEntity(privateToken), String.class);
        return readObject(response.getBody());
    }

    private List<Map<String, Object>> exchangeForList(String pathTemplate, Map<String, ?> uriVariables, Map<String, ?> queryParams, String apiBaseUrl, String privateToken) {
        ResponseEntity<String> response = restTemplate.exchange(buildUri(pathTemplate, uriVariables, queryParams, apiBaseUrl), HttpMethod.GET, buildRequestEntity(privateToken), String.class);
        return readList(response.getBody());
    }

    private URI buildUri(String pathTemplate, Map<String, ?> uriVariables, Map<String, ?> queryParams, String apiBaseUrl) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(trimTrailingSlash(apiBaseUrl) + "/api/v4" + pathTemplate);
        queryParams.forEach(builder::queryParam);
        return builder.buildAndExpand(uriVariables).encode().toUri();
    }

    private HttpEntity<Void> buildRequestEntity(String privateToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("PRIVATE-TOKEN", privateToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(headers);
    }

    private String resolveApiBaseUrl(GitlabRuntimeConfig runtimeConfig) {
        if (runtimeConfig != null && runtimeConfig.getBaseUrl() != null && !runtimeConfig.getBaseUrl().isBlank()) {
            return runtimeConfig.getBaseUrl();
        }
        return properties.getApiBaseUrl();
    }

    private String resolvePrivateToken(GitlabRuntimeConfig runtimeConfig) {
        if (runtimeConfig != null && runtimeConfig.getPrivateToken() != null && !runtimeConfig.getPrivateToken().isBlank()) {
            return runtimeConfig.getPrivateToken();
        }
        return properties.getPrivateToken();
    }

    private Map<String, Object> readObject(String body) {
        try {
            return objectMapper.readValue(body, new TypeReference<>() {});
        } catch (Exception ex) {
            throw new BusinessException(500, "解析 GitLab project 响应失败");
        }
    }

    private List<Map<String, Object>> readList(String body) {
        try {
            return objectMapper.readValue(body, new TypeReference<>() {});
        } catch (Exception ex) {
            throw new BusinessException(500, "解析 GitLab 列表响应失败");
        }
    }

    private void saveProjectSnapshot(GitlabProjectSnapshot entity) {
        if (entity.getId() == null) {
            projectSnapshotMapper.insert(entity);
        } else {
            projectSnapshotMapper.updateById(entity);
        }
    }

    private void saveUserSnapshot(GitlabUserSnapshot entity) {
        if (entity.getId() == null) {
            userSnapshotMapper.insert(entity);
        } else {
            userSnapshotMapper.updateById(entity);
        }
    }

    private void saveBranchFact(GitlabBranchFact entity) {
        if (entity.getId() == null) {
            branchFactMapper.insert(entity);
        } else {
            branchFactMapper.updateById(entity);
        }
    }

    private void saveMemberFact(GitlabMemberFact entity) {
        if (entity.getId() == null) {
            memberFactMapper.insert(entity);
        } else {
            memberFactMapper.updateById(entity);
        }
    }

    private void saveMrFact(GitlabMrFact entity) {
        if (entity.getId() == null) {
            mrFactMapper.insert(entity);
        } else {
            mrFactMapper.updateById(entity);
        }
    }

    private void saveCommitFact(GitlabCommitFact entity) {
        if (entity.getId() == null) {
            commitFactMapper.insert(entity);
        } else {
            commitFactMapper.updateById(entity);
        }
    }

    private String buildCommitDiffSummary(Map<String, Object> commit) {
        Integer additions = parseInteger(parseNested(commit, "stats", "additions"));
        Integer deletions = parseInteger(parseNested(commit, "stats", "deletions"));
        if (additions == null && deletions == null) {
            return "gitlab api commit";
        }
        return "changes +" + defaultInt(additions) + "/-" + defaultInt(deletions);
    }

    private Integer defaultInt(Integer value) {
        return value == null ? 0 : value;
    }

    private Object parseNested(Map<String, Object> source, String parentKey, String childKey) {
        if (source == null) {
            return null;
        }
        Object nested = source.get(parentKey);
        if (nested instanceof Map<?, ?> nestedMap) {
            return nestedMap.get(childKey);
        }
        return null;
    }

    private String parseNestedString(Map<String, Object> source, String parentKey, String childKey) {
        Object value = parseNested(source, parentKey, childKey);
        return value == null ? null : String.valueOf(value);
    }

    private Long parseNestedLong(Map<String, Object> source, String parentKey, String childKey) {
        return parseLong(parseNested(source, parentKey, childKey));
    }

    private Long parseLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    private Integer parseInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }

    private LocalDateTime parseTime(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value);
        try {
            return OffsetDateTime.parse(text).toLocalDateTime();
        } catch (Exception ignored) {
        }
        try {
            return LocalDateTime.parse(text.replace("Z", ""));
        } catch (Exception ignored) {
        }
        return null;
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private String trimTrailingSlash(String baseUrl) {
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }
}
