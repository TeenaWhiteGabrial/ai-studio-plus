package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.request.GitlabWebhookRequest;
import com.aistudio.service.entity.ActivityTaskExecution;
import com.aistudio.service.entity.GitlabEventLog;
import com.aistudio.service.mapper.GitlabProjectConfigMapper;
import com.aistudio.service.mapper.ActivityTaskExecutionMapper;
import com.aistudio.service.mapper.GitlabEventLogMapper;
import com.aistudio.service.service.GitlabActivityAnalysisService;
import com.aistudio.service.service.GitlabActivityBackfillService;
import com.aistudio.service.service.GitlabActivityTaskService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitlabActivityTaskServiceImpl implements GitlabActivityTaskService {

    public static final String TASK_BACKFILL = "BACKFILL";
    public static final String TASK_ANALYZE = "ANALYZE";
    public static final String TASK_REPORT = "REPORT";

    private final GitlabEventLogMapper eventLogMapper;
    private final ActivityTaskExecutionMapper taskExecutionMapper;
    private final GitlabActivityAnalysisService analysisService;
    private final GitlabActivityBackfillService backfillService;
    private final GitlabProjectConfigMapper projectConfigMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Long recordWebhookEvent(String eventType, String eventId, String payload, GitlabWebhookRequest request) {
        Long gitlabProjectId = parseLong(request.getProject(), "id");
        GitlabEventLog entity = new GitlabEventLog();
        entity.setEventUid(eventId);
        entity.setEventType(eventType);
        entity.setGitlabProjectId(gitlabProjectId);
        entity.setGitlabUserId(parseLong(request.getUser(), "id"));
        entity.setRefName(request.getRef());
        entity.setPayloadJson(payload);
        entity.setProcessStatus("PENDING");
        entity.setEventTime(LocalDateTime.now());
        eventLogMapper.insert(entity);
        Long projectId = resolveProjectId(gitlabProjectId);
        LocalDate targetDate = resolveTargetDate(request);
        String taskPayload = toJson(Map.of(
                "event_log_id", entity.getId(),
                "gitlab_project_id", gitlabProjectId,
                "project_id", projectId,
                "event_type", defaultString(eventType),
                "ref", defaultString(request.getRef())
        ));
        enqueueTask(TASK_ANALYZE, targetDate, null, projectId, String.valueOf(entity.getId()), taskPayload);
        return entity.getId();
    }

    @Override
    public void enqueueTask(String taskType, LocalDate targetDate, Long userId, Long projectId, String targetRef, String payloadJson) {
        ActivityTaskExecution task = new ActivityTaskExecution();
        task.setTaskType(taskType);
        task.setTargetDate(targetDate);
        task.setUserId(userId);
        task.setProjectId(projectId);
        task.setTargetRef(targetRef);
        task.setPayloadJson(payloadJson);
        task.setStatus("PENDING");
        task.setAttemptCount(0);
        task.setNextRunAt(LocalDateTime.now());
        taskExecutionMapper.insert(task);
    }

    @Override
    @Transactional
    public void processPendingTasks() {
        List<ActivityTaskExecution> tasks = taskExecutionMapper.selectList(new LambdaQueryWrapper<ActivityTaskExecution>()
                .in(ActivityTaskExecution::getStatus, List.of("PENDING", "RETRY"))
                .le(ActivityTaskExecution::getNextRunAt, LocalDateTime.now())
                .last("LIMIT 20"));
        for (ActivityTaskExecution task : tasks) {
            executeTask(task);
        }
    }

    @Override
    public ActivityTaskExecution getTask(Long taskId) {
        ActivityTaskExecution task = taskExecutionMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }
        return task;
    }

    @Override
    @Transactional
    public void retryTask(Long taskId) {
        ActivityTaskExecution task = getTask(taskId);
        if (!List.of("FAILED", "RETRY").contains(task.getStatus())) {
            throw new BusinessException(400, "仅失败或待重试任务可重新执行");
        }
        task.setStatus("PENDING");
        task.setErrorMessage(null);
        task.setNextRunAt(LocalDateTime.now());
        taskExecutionMapper.updateById(task);
    }

    @Override
    @Transactional
    public void runTaskNow(Long taskId) {
        ActivityTaskExecution task = getTask(taskId);
        if ("RUNNING".equals(task.getStatus())) {
            throw new BusinessException(400, "任务正在执行中");
        }
        if ("SUCCESS".equals(task.getStatus())) {
            throw new BusinessException(400, "成功任务无需重复执行");
        }
        executeTask(task);
    }

    private void executeTask(ActivityTaskExecution task) {
        task.setStatus("RUNNING");
        task.setAttemptCount(task.getAttemptCount() + 1);
        taskExecutionMapper.updateById(task);
        try {
            Map<String, Object> payload = parsePayload(task.getPayloadJson());
            Long gitlabProjectId = parseLong(payload, "gitlab_project_id");
            Long projectId = parseLong(payload, "project_id");
            Long eventLogId = parseLong(payload, "event_log_id");
            if (TASK_ANALYZE.equals(task.getTaskType())) {
                analysisService.analyzeUserProject(task.getUserId(), gitlabProjectId, resolveTargetDate(task), eventLogId);
            } else if (TASK_REPORT.equals(task.getTaskType())) {
                analysisService.generateDailyReport(task.getUserId(), projectId != null ? projectId : task.getProjectId(), parseLong(payload, "stat_group_id"), resolveTargetDate(task), eventLogId);
            } else if (TASK_BACKFILL.equals(task.getTaskType())) {
                backfillService.backfillProject(gitlabProjectId, resolveTargetDate(task));
            } else {
                throw new BusinessException(400, "未知任务类型: " + task.getTaskType());
            }
            task.setStatus("SUCCESS");
            task.setErrorMessage(null);
        } catch (Exception ex) {
            log.error("处理 GitLab 活动任务失败 id={}", task.getId(), ex);
            task.setStatus(task.getAttemptCount() >= 3 ? "FAILED" : "RETRY");
            task.setErrorMessage(ex.getMessage());
            task.setNextRunAt(LocalDateTime.now().plusMinutes(10));
        }
        taskExecutionMapper.updateById(task);
    }

    private LocalDate resolveTargetDate(ActivityTaskExecution task) {
        return task.getTargetDate() == null ? LocalDate.now() : task.getTargetDate();
    }

    private LocalDate resolveTargetDate(GitlabWebhookRequest request) {
        if (request.getCommits() != null) {
            for (Map<String, Object> commit : request.getCommits()) {
                LocalDateTime committedAt = parseTime(commit.get("timestamp"));
                if (committedAt != null) {
                    return committedAt.toLocalDate();
                }
            }
        }
        return LocalDate.now();
    }

    private Long resolveProjectId(Long gitlabProjectId) {
        if (gitlabProjectId == null) {
            return null;
        }
        com.aistudio.service.entity.GitlabProjectConfig config = projectConfigMapper.selectOne(
                new LambdaQueryWrapper<com.aistudio.service.entity.GitlabProjectConfig>()
                        .eq(com.aistudio.service.entity.GitlabProjectConfig::getGitlabProjectId, gitlabProjectId)
                        .eq(com.aistudio.service.entity.GitlabProjectConfig::getEnabled, 1)
                        .last("LIMIT 1")
        );
        return config == null ? null : config.getProjectId();
    }

    private Map<String, Object> parsePayload(String payloadJson) {
        if (payloadJson == null || payloadJson.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(payloadJson, new TypeReference<>() {});
        } catch (Exception ex) {
            log.warn("解析 GitLab 活动任务 payload 失败, payload={}", payloadJson, ex);
            return Map.of();
        }
    }

    private String toJson(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception ex) {
            log.warn("序列化 GitLab 活动任务 payload 失败", ex);
            return "{}";
        }
    }

    private Long parseLong(java.util.Map<String, Object> map, String key) {
        if (map == null || map.get(key) == null) {
            return null;
        }
        Object value = map.get(key);
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    private LocalDateTime parseTime(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value);
        try {
            return java.time.OffsetDateTime.parse(text).toLocalDateTime();
        } catch (Exception ignored) {
        }
        try {
            return LocalDateTime.parse(text.replace("Z", ""));
        } catch (Exception ignored) {
        }
        return null;
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }
}
