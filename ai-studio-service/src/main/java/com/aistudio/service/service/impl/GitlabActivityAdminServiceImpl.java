package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.config.GitlabActivityProperties;
import com.aistudio.service.dto.request.ActivityManualTriggerRequest;
import com.aistudio.service.dto.request.ActivityStatGroupRequest;
import com.aistudio.service.dto.request.GitlabProjectConfigRequest;
import com.aistudio.service.dto.request.GitlabWebhookRequest;
import com.aistudio.service.dto.response.ActivityDailyReportResponse;
import com.aistudio.service.dto.response.ActivityTaskExecutionResponse;
import com.aistudio.service.dto.response.ActivityWorkItemResponse;
import com.aistudio.service.dto.response.GitlabEventLogResponse;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.ActivityDailyReport;
import com.aistudio.service.entity.ActivityStatGroup;
import com.aistudio.service.entity.ActivityStatGroupMember;
import com.aistudio.service.entity.ActivityTaskExecution;
import com.aistudio.service.entity.ActivityWorkItem;
import com.aistudio.service.entity.ActivityWorkItemEvidence;
import com.aistudio.service.entity.GitlabCommitFact;
import com.aistudio.service.entity.GitlabActivityLink;
import com.aistudio.service.entity.GitlabEventLog;
import com.aistudio.service.entity.GitlabMrFact;
import com.aistudio.service.entity.GitlabProjectConfig;
import com.aistudio.service.mapper.ActivityDailyReportMapper;
import com.aistudio.service.mapper.ActivityStatGroupMapper;
import com.aistudio.service.mapper.ActivityStatGroupMemberMapper;
import com.aistudio.service.mapper.ActivityTaskExecutionMapper;
import com.aistudio.service.mapper.ActivityWorkItemEvidenceMapper;
import com.aistudio.service.mapper.ActivityWorkItemMapper;
import com.aistudio.service.mapper.GitlabCommitFactMapper;
import com.aistudio.service.mapper.GitlabEventLogMapper;
import com.aistudio.service.mapper.GitlabMrFactMapper;
import com.aistudio.service.mapper.GitlabProjectConfigMapper;
import com.aistudio.service.mapper.SysUserMapper;
import com.aistudio.service.service.GitlabActivityAdminService;
import com.aistudio.service.service.GitlabActivityAnalysisService;
import com.aistudio.service.service.GitlabActivityLinkService;
import com.aistudio.service.service.GitlabRuntimeConfigService;
import com.aistudio.service.service.GitlabActivityTaskService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GitlabActivityAdminServiceImpl implements GitlabActivityAdminService {

    private final GitlabProjectConfigMapper projectConfigMapper;
    private final ActivityStatGroupMapper statGroupMapper;
    private final ActivityStatGroupMemberMapper statGroupMemberMapper;
    private final ActivityTaskExecutionMapper taskExecutionMapper;
    private final ActivityDailyReportMapper dailyReportMapper;
    private final ActivityWorkItemMapper workItemMapper;
    private final ActivityWorkItemEvidenceMapper evidenceMapper;
    private final GitlabEventLogMapper eventLogMapper;
    private final GitlabCommitFactMapper commitFactMapper;
    private final GitlabMrFactMapper mrFactMapper;
    private final SysUserMapper userMapper;
    private final GitlabActivityTaskService taskService;
    private final GitlabActivityAnalysisService analysisService;
    private final GitlabActivityLinkService linkService;
    private final GitlabRuntimeConfigService gitlabRuntimeConfigService;
    private final GitlabActivityProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    public PageResult<GitlabProjectConfig> listProjectConfigs(int page, int size) {
        Page<GitlabProjectConfig> result = projectConfigMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<GitlabProjectConfig>().orderByDesc(GitlabProjectConfig::getCreatedAt));
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional
    public Long saveProjectConfig(Long id, GitlabProjectConfigRequest request, Long operatorId) {
        GitlabProjectConfig entity = id == null ? new GitlabProjectConfig() : projectConfigMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(404, "GitLab 项目配置不存在");
        }
        entity.setProjectId(request.getProject_id());
        entity.setGitlabProjectId(request.getGitlab_project_id());
        entity.setGitlabProjectName(request.getGitlab_project_name());
        entity.setGitlabProjectPath(request.getGitlab_project_path());
        entity.setGitlabGroupName(request.getGitlab_group_name());
        entity.setEnabled(request.getEnabled() == null ? 1 : request.getEnabled());
        entity.setUpdatedBy(operatorId);
        if (entity.getId() == null) {
            entity.setCreatedBy(operatorId);
            projectConfigMapper.insert(entity);
        } else {
            projectConfigMapper.updateById(entity);
        }
        return entity.getId();
    }

    @Override
    public List<ActivityStatGroup> listStatGroups() {
        return statGroupMapper.selectList(new LambdaQueryWrapper<ActivityStatGroup>().orderByAsc(ActivityStatGroup::getGroupName));
    }

    @Override
    @Transactional
    public Long saveStatGroup(Long id, ActivityStatGroupRequest request, Long operatorId) {
        ActivityStatGroup entity = id == null ? new ActivityStatGroup() : statGroupMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(404, "统计组不存在");
        }
        entity.setGroupName(request.getGroup_name());
        entity.setGroupKey(request.getGroup_key());
        entity.setDescription(request.getDescription());
        entity.setScopeType(request.getScope_type() == null || request.getScope_type().isBlank() ? "TEAM" : request.getScope_type());
        entity.setEnabled(request.getEnabled() == null ? 1 : request.getEnabled());
        entity.setUpdatedBy(operatorId);
        if (entity.getId() == null) {
            entity.setCreatedBy(operatorId);
            statGroupMapper.insert(entity);
        } else {
            statGroupMapper.updateById(entity);
            statGroupMemberMapper.delete(new LambdaQueryWrapper<ActivityStatGroupMember>().eq(ActivityStatGroupMember::getStatGroupId, entity.getId()));
        }
        if (request.getUser_ids() != null) {
            for (Long userId : request.getUser_ids()) {
                ActivityStatGroupMember member = new ActivityStatGroupMember();
                member.setStatGroupId(entity.getId());
                member.setUserId(userId);
                statGroupMemberMapper.insert(member);
            }
        }
        return entity.getId();
    }

    @Override
    @Transactional
    public Long receiveWebhook(String eventType, String eventId, String token, String payload) {
        String webhookToken = resolveWebhookToken();
        if (webhookToken != null && !webhookToken.isBlank() && !webhookToken.equals(token)) {
            throw new BusinessException(401, "Webhook token 无效");
        }
        GitlabWebhookRequest request = parseWebhookPayload(payload);
        Long eventLogId = taskService.recordWebhookEvent(eventType, eventId == null || eventId.isBlank() ? "evt-" + System.nanoTime() : eventId, payload, request);
        persistFacts(eventType, request, eventLogId);
        return eventLogId;
    }

    @Override
    public void triggerBackfill(ActivityManualTriggerRequest request, Long operatorId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("operator_id", operatorId);
        payload.put("gitlab_project_id", request.getGitlab_project_id());
        payload.put("project_id", request.getProject_id());
        taskService.enqueueTask(GitlabActivityTaskServiceImpl.TASK_BACKFILL, request.getTarget_date(), request.getUser_id(), request.getProject_id(), "manual-backfill", toJson(payload));
    }

    @Override
    public void triggerDailyAnalysis(ActivityManualTriggerRequest request, Long operatorId) {
        analysisService.analyzeUserProject(request.getUser_id(), request.getGitlab_project_id(), request.getTarget_date() == null ? LocalDate.now() : request.getTarget_date());
        Map<String, Object> payload = new HashMap<>();
        payload.put("operator_id", operatorId);
        payload.put("gitlab_project_id", request.getGitlab_project_id());
        payload.put("project_id", request.getProject_id());
        taskService.enqueueTask(GitlabActivityTaskServiceImpl.TASK_ANALYZE, request.getTarget_date(), request.getUser_id(), request.getProject_id(), "manual-analyze", toJson(payload));
    }

    @Override
    public void triggerDailyReport(ActivityManualTriggerRequest request, Long operatorId) {
        analysisService.generateDailyReport(request.getUser_id(), request.getProject_id(), null, request.getTarget_date() == null ? LocalDate.now() : request.getTarget_date());
        Map<String, Object> payload = new HashMap<>();
        payload.put("operator_id", operatorId);
        payload.put("gitlab_project_id", request.getGitlab_project_id());
        payload.put("project_id", request.getProject_id());
        taskService.enqueueTask(GitlabActivityTaskServiceImpl.TASK_REPORT, request.getTarget_date(), request.getUser_id(), request.getProject_id(), "manual-report", toJson(payload));
    }

    @Override
    public PageResult<ActivityTaskExecutionResponse> listTasks(int page, int size, String status) {
        LambdaQueryWrapper<ActivityTaskExecution> wrapper = new LambdaQueryWrapper<ActivityTaskExecution>()
                .orderByDesc(ActivityTaskExecution::getCreatedAt);
        if (status != null && !status.isBlank()) {
            wrapper.eq(ActivityTaskExecution::getStatus, status);
        }
        Page<ActivityTaskExecution> result = taskExecutionMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getTotal(), result.getRecords().stream().map(this::toTaskResponse).toList());
    }

    @Override
    public ActivityTaskExecutionResponse getTask(Long taskId) {
        return toTaskResponse(taskService.getTask(taskId));
    }

    @Override
    public void retryTask(Long taskId, Long operatorId) {
        taskService.retryTask(taskId);
    }

    @Override
    public void runTaskNow(Long taskId, Long operatorId) {
        taskService.runTaskNow(taskId);
    }

    @Override
    public PageResult<GitlabEventLogResponse> listEventLogs(int page, int size, String processStatus, Long gitlabProjectId) {
        LambdaQueryWrapper<GitlabEventLog> wrapper = new LambdaQueryWrapper<GitlabEventLog>()
                .eq(processStatus != null && !processStatus.isBlank(), GitlabEventLog::getProcessStatus, processStatus)
                .eq(gitlabProjectId != null, GitlabEventLog::getGitlabProjectId, gitlabProjectId)
                .orderByDesc(GitlabEventLog::getCreatedAt);
        Page<GitlabEventLog> result = eventLogMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getTotal(), result.getRecords().stream().map(this::toEventLogResponse).toList());
    }

    @Override
    public GitlabEventLogResponse getEventLog(Long eventLogId) {
        GitlabEventLog eventLog = eventLogMapper.selectById(eventLogId);
        if (eventLog == null) {
            throw new BusinessException(404, "事件日志不存在");
        }
        return toEventLogResponse(eventLog);
    }

    @Override
    public PageResult<ActivityDailyReportResponse> listReports(int page, int size, LocalDate reportDate, Long statGroupId) {
        LambdaQueryWrapper<ActivityDailyReport> wrapper = new LambdaQueryWrapper<ActivityDailyReport>()
                .eq(reportDate != null, ActivityDailyReport::getReportDate, reportDate)
                .eq(statGroupId != null, ActivityDailyReport::getStatGroupId, statGroupId)
                .orderByDesc(ActivityDailyReport::getReportDate)
                .orderByDesc(ActivityDailyReport::getCreatedAt);
        Page<ActivityDailyReport> result = dailyReportMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getTotal(), result.getRecords().stream().map(this::toReportResponse).toList());
    }

    @Override
    public PageResult<ActivityWorkItemResponse> listWorkItems(int page, int size, LocalDate analysisDate, Long statGroupId, Long userId) {
        LambdaQueryWrapper<ActivityWorkItem> wrapper = new LambdaQueryWrapper<ActivityWorkItem>()
                .eq(analysisDate != null, ActivityWorkItem::getAnalysisDate, analysisDate)
                .eq(statGroupId != null, ActivityWorkItem::getStatGroupId, statGroupId)
                .eq(userId != null, ActivityWorkItem::getUserId, userId)
                .orderByDesc(ActivityWorkItem::getAnalysisDate)
                .orderByDesc(ActivityWorkItem::getCreatedAt);
        Page<ActivityWorkItem> result = workItemMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(result.getTotal(), result.getRecords().stream().map(this::toWorkItemResponse).toList());
    }

    private void persistFacts(String eventType, GitlabWebhookRequest request, Long eventLogId) {
        if ("Push Hook".equalsIgnoreCase(eventType) || "push".equalsIgnoreCase(request.getObject_kind())) {
            persistCommitFacts(request, eventLogId);
        }
        if ("Merge Request Hook".equalsIgnoreCase(eventType) || "merge_request".equalsIgnoreCase(request.getObject_kind())) {
            persistMrFact(request, eventLogId);
        }
        GitlabEventLog log = eventLogMapper.selectById(eventLogId);
        if (log != null) {
            log.setProcessStatus("RECORDED");
            log.setProcessMessage("事实入库完成");
            eventLogMapper.updateById(log);
        }
    }

    private void persistCommitFacts(GitlabWebhookRequest request, Long eventLogId) {
        if (request.getCommits() == null) {
            return;
        }
        Long gitlabProjectId = parseLong(request.getProject(), "id");
        for (Map<String, Object> commit : request.getCommits()) {
            String sha = stringValue(commit.get("id"));
            GitlabCommitFact entity = commitFactMapper.selectOne(new LambdaQueryWrapper<GitlabCommitFact>()
                    .eq(GitlabCommitFact::getGitlabProjectId, gitlabProjectId)
                    .eq(GitlabCommitFact::getCommitSha, sha));
            if (entity == null) {
                entity = new GitlabCommitFact();
                entity.setGitlabProjectId(gitlabProjectId);
                entity.setCommitSha(sha);
            }
            entity.setShortSha(sha == null ? null : sha.substring(0, Math.min(8, sha.length())));
            entity.setAuthorName(stringValue(commit.get("author_name")));
            entity.setAuthorEmail(stringValue(commit.get("author_email")));
            entity.setCommitMessage(stringValue(commit.get("message")));
            entity.setBranchName(extractBranchName(request.getRef()));
            entity.setCommittedAt(parseTime(commit.get("timestamp")));
            entity.setPushedAt(LocalDateTime.now());
            entity.setFilePathsJson("[]");
            entity.setDiffSummary("push webhook commit");
            if (entity.getId() == null) {
                commitFactMapper.insert(entity);
            } else {
                commitFactMapper.updateById(entity);
            }
            linkService.link(eventLogId, "COMMIT_FACT", entity.getId(), "RECORDED_FACT");
        }
    }

    private void persistMrFact(GitlabWebhookRequest request, Long eventLogId) {
        Map<String, Object> attr = request.getObject_attributes();
        if (attr == null) {
            return;
        }
        Long mrId = parseLong(attr, "id");
        GitlabMrFact entity = mrFactMapper.selectOne(new LambdaQueryWrapper<GitlabMrFact>().eq(GitlabMrFact::getGitlabMrId, mrId));
        if (entity == null) {
            entity = new GitlabMrFact();
            entity.setGitlabMrId(mrId);
        }
        entity.setGitlabProjectId(parseLong(request.getProject(), "id"));
        entity.setIid(parseLong(attr, "iid"));
        entity.setTitle(stringValue(attr.get("title")));
        entity.setDescription(stringValue(attr.get("description")));
        entity.setState(stringValue(attr.get("state")));
        entity.setSourceBranch(stringValue(attr.get("source_branch")));
        entity.setTargetBranch(stringValue(attr.get("target_branch")));
        entity.setAuthorGitlabUserId(parseLong(request.getUser(), "id"));
        entity.setCreatedAtGitlab(parseTime(attr.get("created_at")));
        entity.setUpdatedAtGitlab(parseTime(attr.get("updated_at")));
        entity.setLastSyncedAt(LocalDateTime.now());
        if (entity.getId() == null) {
            mrFactMapper.insert(entity);
        } else {
            mrFactMapper.updateById(entity);
        }
        linkService.link(eventLogId, "MR_FACT", entity.getId(), "RECORDED_FACT");
    }

    private GitlabWebhookRequest parseWebhookPayload(String payload) {
        try {
            return objectMapper.readValue(payload, GitlabWebhookRequest.class);
        } catch (Exception ex) {
            throw new BusinessException(400, "Webhook payload 解析失败");
        }
    }

    private ActivityTaskExecutionResponse toTaskResponse(ActivityTaskExecution item) {
        ActivityTaskExecutionResponse response = new ActivityTaskExecutionResponse();
        response.setId(item.getId());
        response.setTask_type(item.getTaskType());
        response.setTarget_date(item.getTargetDate());
        response.setUser_id(item.getUserId());
        response.setProject_id(item.getProjectId());
        response.setEvent_log_id(extractEventLogId(item.getPayloadJson()));
        response.setTarget_ref(item.getTargetRef());
        response.setStatus(item.getStatus());
        response.setAttempt_count(item.getAttemptCount());
        response.setNext_run_at(item.getNextRunAt());
        response.setError_message(item.getErrorMessage());
        response.setPayload_json(item.getPayloadJson());
        response.setCreated_at(item.getCreatedAt());
        response.setUpdated_at(item.getUpdatedAt());
        return response;
    }

    private GitlabEventLogResponse toEventLogResponse(GitlabEventLog item) {
        GitlabEventLogResponse response = new GitlabEventLogResponse();
        response.setId(item.getId());
        response.setEvent_uid(item.getEventUid());
        response.setEvent_type(item.getEventType());
        response.setProject_id(item.getProjectId());
        response.setGitlab_project_id(item.getGitlabProjectId());
        response.setGitlab_user_id(item.getGitlabUserId());
        response.setRef_name(item.getRefName());
        response.setEvent_time(item.getEventTime());
        response.setPayload_json(item.getPayloadJson());
        response.setProcess_status(item.getProcessStatus());
        response.setProcess_message(item.getProcessMessage());
        response.setRelated_commit_refs(resolveRelatedCommitRefs(item));
        response.setRelated_mr_refs(resolveRelatedMrRefs(item));
        response.setRelated_work_item_summaries(resolveRelatedWorkItemSummaries(item));
        response.setRelated_report_titles(resolveRelatedReportTitles(item));
        response.setCreated_at(item.getCreatedAt());
        response.setUpdated_at(item.getUpdatedAt());
        return response;
    }

    private ActivityDailyReportResponse toReportResponse(ActivityDailyReport item) {
        ActivityDailyReportResponse response = new ActivityDailyReportResponse();
        response.setId(item.getId());
        response.setReport_date(item.getReportDate());
        response.setReport_scope(item.getReportScope());
        response.setUser_id(item.getUserId());
        response.setProject_id(item.getProjectId());
        response.setStat_group_id(item.getStatGroupId());
        response.setTitle(item.getTitle());
        response.setContent(item.getContent());
        response.setReport_data_json(item.getReportDataJson());
        response.setGenerated_by(item.getGeneratedBy());
        response.setCreated_at(item.getCreatedAt());
        return response;
    }

    private ActivityWorkItemResponse toWorkItemResponse(ActivityWorkItem item) {
        ActivityWorkItemResponse response = new ActivityWorkItemResponse();
        response.setId(item.getId());
        response.setAnalysis_date(item.getAnalysisDate());
        response.setUser_id(item.getUserId());
        response.setGitlab_user_id(item.getGitlabUserId());
        response.setProject_id(item.getProjectId());
        response.setGitlab_project_id(item.getGitlabProjectId());
        response.setStat_group_id(item.getStatGroupId());
        response.setTitle(item.getTitle());
        response.setSummary(item.getSummary());
        response.setWork_type(item.getWorkType());
        response.setProgress_status(item.getProgressStatus());
        response.setRisk_level(item.getRiskLevel());
        response.setRisk_summary(item.getRiskSummary());
        response.setBlocking_status(item.getBlockingStatus());
        response.setBlocking_summary(item.getBlockingSummary());
        response.setModule_names(item.getModuleNames());
        response.setRelated_commit_count(item.getRelatedCommitCount());
        response.setRelated_mr_count(item.getRelatedMrCount());
        response.setConfidence(item.getConfidence());
        response.setEvidence_summary(item.getEvidenceSummary());
        response.setEvidence_refs(evidenceMapper.selectList(new LambdaQueryWrapper<ActivityWorkItemEvidence>()
                        .eq(ActivityWorkItemEvidence::getWorkItemId, item.getId()))
                .stream()
                .map(ActivityWorkItemEvidence::getEvidenceRef)
                .collect(Collectors.toList()));
        return response;
    }

    private Long parseLong(Map<String, Object> map, String key) {
        if (map == null || map.get(key) == null) {
            return null;
        }
        Object value = map.get(key);
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private LocalDateTime parseTime(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return java.time.OffsetDateTime.parse(String.valueOf(value)).toLocalDateTime();
        } catch (Exception ignored) {
        }
        try {
            return LocalDateTime.parse(String.valueOf(value).replace("Z", ""));
        } catch (Exception ignored) {
        }
        return LocalDateTime.now();
    }

    private String extractBranchName(String ref) {
        if (ref == null || ref.isBlank()) {
            return null;
        }
        int index = ref.lastIndexOf('/');
        return index >= 0 ? ref.substring(index + 1) : ref;
    }

    private String toJson(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception ex) {
            return "{}";
        }
    }

    private String resolveWebhookToken() {
        String runtimeToken = gitlabRuntimeConfigService.getRuntimeConfig().getWebhookToken();
        if (runtimeToken != null && !runtimeToken.isBlank()) {
            return runtimeToken;
        }
        return properties.getWebhookToken();
    }

    private Long extractEventLogId(String payloadJson) {
        if (payloadJson == null || payloadJson.isBlank()) {
            return null;
        }
        try {
            Map<String, Object> payload = objectMapper.readValue(payloadJson, Map.class);
            Object value = payload.get("event_log_id");
            if (value == null) {
                return null;
            }
            if (value instanceof Number number) {
                return number.longValue();
            }
            return Long.parseLong(String.valueOf(value));
        } catch (Exception ignored) {
            return null;
        }
    }

    private List<String> resolveRelatedCommitRefs(GitlabEventLog eventLog) {
        List<GitlabActivityLink> links = linkService.listByEventLogId(eventLog.getId());
        List<Long> commitFactIds = links.stream()
                .filter(link -> "COMMIT_FACT".equals(link.getTargetType()))
                .map(GitlabActivityLink::getTargetId)
                .toList();
        if (!commitFactIds.isEmpty()) {
            return commitFactMapper.selectList(new LambdaQueryWrapper<GitlabCommitFact>()
                            .in(GitlabCommitFact::getId, commitFactIds))
                    .stream()
                    .map(item -> item.getShortSha() != null && !item.getShortSha().isBlank() ? item.getShortSha() : item.getCommitSha())
                    .filter(text -> text != null && !text.isBlank())
                    .toList();
        }
        if (eventLog.getGitlabProjectId() == null) {
            return List.of();
        }
        List<GitlabCommitFact> commits = commitFactMapper.selectList(new LambdaQueryWrapper<GitlabCommitFact>()
                .eq(GitlabCommitFact::getGitlabProjectId, eventLog.getGitlabProjectId())
                .eq(eventLog.getRefName() != null && !eventLog.getRefName().isBlank(), GitlabCommitFact::getBranchName, extractBranchName(eventLog.getRefName()))
                .between(eventLog.getEventTime() != null, GitlabCommitFact::getCommittedAt, resolveEventWindowStart(eventLog), resolveEventWindowEnd(eventLog))
                .orderByDesc(GitlabCommitFact::getCommittedAt)
                .last("LIMIT 10"));
        return commits.stream()
                .map(item -> item.getShortSha() != null && !item.getShortSha().isBlank() ? item.getShortSha() : item.getCommitSha())
                .filter(text -> text != null && !text.isBlank())
                .toList();
    }

    private List<Long> resolveRelatedMrRefs(GitlabEventLog eventLog) {
        List<GitlabActivityLink> links = linkService.listByEventLogId(eventLog.getId());
        List<Long> mrFactIds = links.stream()
                .filter(link -> "MR_FACT".equals(link.getTargetType()))
                .map(GitlabActivityLink::getTargetId)
                .toList();
        if (!mrFactIds.isEmpty()) {
            return mrFactMapper.selectList(new LambdaQueryWrapper<GitlabMrFact>()
                            .in(GitlabMrFact::getId, mrFactIds))
                    .stream()
                    .map(GitlabMrFact::getGitlabMrId)
                    .filter(java.util.Objects::nonNull)
                    .toList();
        }
        if (eventLog.getGitlabProjectId() == null) {
            return List.of();
        }
        List<GitlabMrFact> mrs = mrFactMapper.selectList(new LambdaQueryWrapper<GitlabMrFact>()
                .eq(GitlabMrFact::getGitlabProjectId, eventLog.getGitlabProjectId())
                .between(eventLog.getEventTime() != null, GitlabMrFact::getUpdatedAtGitlab, resolveEventWindowStart(eventLog), resolveEventWindowEnd(eventLog))
                .orderByDesc(GitlabMrFact::getUpdatedAtGitlab)
                .last("LIMIT 10"));
        return mrs.stream()
                .map(GitlabMrFact::getGitlabMrId)
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    private List<String> resolveRelatedWorkItemSummaries(GitlabEventLog eventLog) {
        List<GitlabActivityLink> links = linkService.listByEventLogId(eventLog.getId());
        List<Long> workItemIds = links.stream()
                .filter(link -> "WORK_ITEM".equals(link.getTargetType()))
                .map(GitlabActivityLink::getTargetId)
                .toList();
        if (!workItemIds.isEmpty()) {
            return workItemMapper.selectList(new LambdaQueryWrapper<ActivityWorkItem>()
                            .in(ActivityWorkItem::getId, workItemIds))
                    .stream()
                    .map(workItem -> workItem.getTitle() + " | " + defaultString(workItem.getProgressStatus()) + " | " + defaultString(workItem.getRiskLevel()))
                    .toList();
        }
        if (eventLog.getGitlabProjectId() == null) {
            return List.of();
        }
        LocalDate analysisDate = eventLog.getEventTime() == null ? LocalDate.now() : eventLog.getEventTime().toLocalDate();
        List<ActivityWorkItem> workItems = workItemMapper.selectList(new LambdaQueryWrapper<ActivityWorkItem>()
                .eq(ActivityWorkItem::getGitlabProjectId, eventLog.getGitlabProjectId())
                .eq(ActivityWorkItem::getAnalysisDate, analysisDate)
                .orderByDesc(ActivityWorkItem::getCreatedAt)
                .last("LIMIT 10"));
        List<String> results = new ArrayList<>();
        for (ActivityWorkItem workItem : workItems) {
            results.add(workItem.getTitle() + " | " + defaultString(workItem.getProgressStatus()) + " | " + defaultString(workItem.getRiskLevel()));
        }
        return results;
    }

    private List<String> resolveRelatedReportTitles(GitlabEventLog eventLog) {
        List<GitlabActivityLink> links = linkService.listByEventLogId(eventLog.getId());
        List<Long> reportIds = links.stream()
                .filter(link -> "DAILY_REPORT".equals(link.getTargetType()))
                .map(GitlabActivityLink::getTargetId)
                .toList();
        if (!reportIds.isEmpty()) {
            return dailyReportMapper.selectList(new LambdaQueryWrapper<ActivityDailyReport>()
                            .in(ActivityDailyReport::getId, reportIds))
                    .stream()
                    .map(ActivityDailyReport::getTitle)
                    .filter(text -> text != null && !text.isBlank())
                    .toList();
        }
        LocalDate reportDate = eventLog.getEventTime() == null ? LocalDate.now() : eventLog.getEventTime().toLocalDate();
        List<ActivityDailyReport> reports = dailyReportMapper.selectList(new LambdaQueryWrapper<ActivityDailyReport>()
                .eq(ActivityDailyReport::getReportDate, reportDate)
                .eq(eventLog.getProjectId() != null, ActivityDailyReport::getProjectId, eventLog.getProjectId())
                .orderByDesc(ActivityDailyReport::getCreatedAt)
                .last("LIMIT 10"));
        return reports.stream()
                .map(ActivityDailyReport::getTitle)
                .filter(text -> text != null && !text.isBlank())
                .toList();
    }

    private LocalDateTime resolveEventWindowStart(GitlabEventLog eventLog) {
        LocalDateTime base = eventLog.getEventTime() == null ? LocalDateTime.now() : eventLog.getEventTime();
        return base.minusHours(12);
    }

    private LocalDateTime resolveEventWindowEnd(GitlabEventLog eventLog) {
        LocalDateTime base = eventLog.getEventTime() == null ? LocalDateTime.now() : eventLog.getEventTime();
        return base.plusHours(12);
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }
}
