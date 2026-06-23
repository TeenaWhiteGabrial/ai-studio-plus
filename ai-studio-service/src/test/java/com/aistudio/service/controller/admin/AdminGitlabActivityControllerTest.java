package com.aistudio.service.controller.admin;

import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.common.exception.GlobalExceptionHandler;
import com.aistudio.service.dto.request.ActivityManualTriggerRequest;
import com.aistudio.service.dto.response.ActivityTaskExecutionResponse;
import com.aistudio.service.dto.response.GitlabEventLogResponse;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.service.GitlabActivityAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminGitlabActivityControllerTest {

    private final RecordingGitlabActivityAdminService adminService = new RecordingGitlabActivityAdminService();
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new AdminGitlabActivityController(adminService, new FixedSecurityUtils()))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

    @Test
    void triggerBackfillAcceptsSnakeCasePayload() throws Exception {
        mockMvc.perform(post("/admin/gitlab-activity/tasks/backfill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "target_date": "2026-06-22",
                                  "user_id": 9,
                                  "project_id": 12,
                                  "gitlab_project_id": 101
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertThat(adminService.lastTriggerRequest.getTarget_date()).isEqualTo(LocalDate.of(2026, 6, 22));
        assertThat(adminService.lastTriggerRequest.getUser_id()).isEqualTo(9L);
        assertThat(adminService.lastTriggerRequest.getProject_id()).isEqualTo(12L);
        assertThat(adminService.lastTriggerRequest.getGitlab_project_id()).isEqualTo(101L);
        assertThat(adminService.lastOperatorId).isEqualTo(99L);
    }

    @Test
    void getTaskReturnsSnakeCaseEventLogId() throws Exception {
        mockMvc.perform(get("/admin/gitlab-activity/tasks/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(7))
                .andExpect(jsonPath("$.data.event_log_id").value(70))
                .andExpect(jsonPath("$.data.task_type").value("ANALYZE"));
    }

    @Test
    void getEventLogReturnsAggregatedSummaries() throws Exception {
        mockMvc.perform(get("/admin/gitlab-activity/event-logs/70"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(70))
                .andExpect(jsonPath("$.data.event_uid").value("evt-70"))
                .andExpect(jsonPath("$.data.related_commit_refs[0]").value("abc12345"))
                .andExpect(jsonPath("$.data.related_mr_refs[0]").value(88))
                .andExpect(jsonPath("$.data.related_report_titles[0]").value("个人研发活动日报"));
    }

    @Test
    void getMissingEventLogUsesBusinessErrorEnvelope() throws Exception {
        mockMvc.perform(get("/admin/gitlab-activity/event-logs/404"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("事件日志不存在"));
    }

    private static class FixedSecurityUtils extends SecurityUtils {
        FixedSecurityUtils() {
            super(null);
        }

        @Override
        public Long getCurrentUserId() {
            return 99L;
        }
    }

    private static class RecordingGitlabActivityAdminService implements GitlabActivityAdminService {
        private ActivityManualTriggerRequest lastTriggerRequest;
        private Long lastOperatorId;

        @Override
        public com.aistudio.service.dto.response.PageResult<com.aistudio.service.entity.GitlabProjectConfig> listProjectConfigs(int page, int size) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Long saveProjectConfig(Long id, com.aistudio.service.dto.request.GitlabProjectConfigRequest request, Long operatorId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<com.aistudio.service.entity.ActivityStatGroup> listStatGroups() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Long saveStatGroup(Long id, com.aistudio.service.dto.request.ActivityStatGroupRequest request, Long operatorId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Long receiveWebhook(String eventType, String eventId, String token, String payload) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void triggerBackfill(ActivityManualTriggerRequest request, Long operatorId) {
            this.lastTriggerRequest = request;
            this.lastOperatorId = operatorId;
        }

        @Override
        public void triggerDailyAnalysis(ActivityManualTriggerRequest request, Long operatorId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void triggerDailyReport(ActivityManualTriggerRequest request, Long operatorId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public PageResult<ActivityTaskExecutionResponse> listTasks(int page, int size, String status) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ActivityTaskExecutionResponse getTask(Long taskId) {
            ActivityTaskExecutionResponse response = new ActivityTaskExecutionResponse();
            response.setId(taskId);
            response.setEvent_log_id(70L);
            response.setTask_type("ANALYZE");
            return response;
        }

        @Override
        public void retryTask(Long taskId, Long operatorId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void runTaskNow(Long taskId, Long operatorId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public PageResult<GitlabEventLogResponse> listEventLogs(int page, int size, String processStatus, Long gitlabProjectId) {
            GitlabEventLogResponse response = buildEventLogResponse(70L);
            return PageResult.of(1L, List.of(response));
        }

        @Override
        public GitlabEventLogResponse getEventLog(Long eventLogId) {
            if (eventLogId == 404L) {
                throw new BusinessException(404, "事件日志不存在");
            }
            return buildEventLogResponse(eventLogId);
        }

        @Override
        public PageResult<com.aistudio.service.dto.response.ActivityDailyReportResponse> listReports(int page, int size, LocalDate reportDate, Long statGroupId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public PageResult<com.aistudio.service.dto.response.ActivityWorkItemResponse> listWorkItems(int page, int size, LocalDate analysisDate, Long statGroupId, Long userId) {
            throw new UnsupportedOperationException();
        }

        private GitlabEventLogResponse buildEventLogResponse(Long eventLogId) {
            GitlabEventLogResponse response = new GitlabEventLogResponse();
            response.setId(eventLogId);
            response.setEvent_uid("evt-" + eventLogId);
            response.setRelated_commit_refs(List.of("abc12345"));
            response.setRelated_mr_refs(List.of(88L));
            response.setRelated_work_item_summaries(List.of("补齐 webhook | in_progress | low"));
            response.setRelated_report_titles(List.of("个人研发活动日报"));
            return response;
        }
    }
}
