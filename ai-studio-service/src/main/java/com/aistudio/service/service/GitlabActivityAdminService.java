package com.aistudio.service.service;

import com.aistudio.service.dto.request.ActivityManualTriggerRequest;
import com.aistudio.service.dto.request.ActivityStatGroupRequest;
import com.aistudio.service.dto.request.GitlabProjectConfigRequest;
import com.aistudio.service.dto.response.ActivityDailyReportResponse;
import com.aistudio.service.dto.response.ActivityTaskExecutionResponse;
import com.aistudio.service.dto.response.ActivityWorkItemResponse;
import com.aistudio.service.dto.response.GitlabEventLogResponse;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.ActivityStatGroup;
import com.aistudio.service.entity.GitlabProjectConfig;

import java.time.LocalDate;
import java.util.List;

public interface GitlabActivityAdminService {
    PageResult<GitlabProjectConfig> listProjectConfigs(int page, int size);
    Long saveProjectConfig(Long id, GitlabProjectConfigRequest request, Long operatorId);
    List<ActivityStatGroup> listStatGroups();
    Long saveStatGroup(Long id, ActivityStatGroupRequest request, Long operatorId);
    Long receiveWebhook(String eventType, String eventId, String token, String payload);
    void triggerBackfill(ActivityManualTriggerRequest request, Long operatorId);
    void triggerDailyAnalysis(ActivityManualTriggerRequest request, Long operatorId);
    void triggerDailyReport(ActivityManualTriggerRequest request, Long operatorId);
    PageResult<ActivityTaskExecutionResponse> listTasks(int page, int size, String status);
    ActivityTaskExecutionResponse getTask(Long taskId);
    void retryTask(Long taskId, Long operatorId);
    void runTaskNow(Long taskId, Long operatorId);
    PageResult<GitlabEventLogResponse> listEventLogs(int page, int size, String processStatus, Long gitlabProjectId);
    GitlabEventLogResponse getEventLog(Long eventLogId);
    PageResult<ActivityDailyReportResponse> listReports(int page, int size, LocalDate reportDate, Long statGroupId);
    PageResult<ActivityWorkItemResponse> listWorkItems(int page, int size, LocalDate analysisDate, Long statGroupId, Long userId);
}
