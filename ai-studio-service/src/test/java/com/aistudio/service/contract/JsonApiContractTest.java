package com.aistudio.service.contract;

import com.aistudio.service.common.Result;
import com.aistudio.service.dto.request.ArticleCreateRequest;
import com.aistudio.service.dto.request.GitlabProjectConfigRequest;
import com.aistudio.service.dto.request.GitlabWebhookRequest;
import com.aistudio.service.dto.response.GitlabEventLogResponse;
import com.aistudio.service.dto.request.LoginRequest;
import com.aistudio.service.dto.request.MemberOutputRequest;
import com.aistudio.service.dto.request.ProjectCreateRequest;
import com.aistudio.service.dto.request.SkillCreateRequest;
import com.aistudio.service.dto.request.UserCreateRequest;
import com.aistudio.service.dto.response.ActivityWorkItemResponse;
import com.aistudio.service.dto.response.LoginResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class JsonApiContractTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .findAndRegisterModules();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void loginRequestRequiresUsernameAndPassword() throws Exception {
        LoginRequest request = objectMapper.readValue("""
                {
                  "username": "",
                  "password": ""
                }
                """, LoginRequest.class);

        Set<?> violations = validator.validate(request);

        assertThat(violations).hasSize(2);
    }

    @Test
    void loginResponseSerializesWithSnakeCaseEnvelope() throws Exception {
        LoginResponse response = LoginResponse.builder()
                .token("token-1")
                .userId(7L)
                .username("tester")
                .gitName("tester.git")
                .realName("测试用户")
                .roles(List.of("SUPER_ADMIN"))
                .build();

        JsonNode json = objectMapper.valueToTree(Result.success(response));

        assertThat(json.path("code").asInt()).isEqualTo(200);
        assertThat(json.path("message").asText()).isEqualTo("success");
        assertThat(json.path("data").has("user_id")).isTrue();
        assertThat(json.path("data").has("git_name")).isTrue();
        assertThat(json.path("data").has("real_name")).isTrue();
        assertThat(json.path("data").has("userId")).isFalse();
        assertThat(json.path("data").path("roles").get(0).asText()).isEqualTo("SUPER_ADMIN");
    }

    @Test
    void articleCreateRequestAcceptsSnakeCaseApiBoundaryFields() throws Exception {
        ArticleCreateRequest request = objectMapper.readValue("""
                {
                  "title": "本地测试文章",
                  "content": "用于验证 snake_case API 合约",
                  "cover_image": "https://example.com/cover.png",
                  "folder_id": 12,
                  "tag_ids": [1, 2],
                  "publish_type": 1,
                  "scheduled_publish_time": "2026-05-12T10:00:00"
                }
                """, ArticleCreateRequest.class);

        assertThat(request.getCoverImage()).isEqualTo("https://example.com/cover.png");
        assertThat(request.getFolderId()).isEqualTo(12L);
        assertThat(request.getTagIds()).containsExactly(1L, 2L);
        assertThat(request.getPublishType()).isEqualTo(1);
        assertThat(request.getScheduledPublishTime()).isNotNull();
    }

    @Test
    void articleCreateRequestValidatesRequiredTitleAndContent() {
        ArticleCreateRequest request = new ArticleCreateRequest();
        request.setTitle("");
        request.setContent("");

        Set<?> violations = validator.validate(request);

        assertThat(violations).hasSize(2);
    }

    @Test
    void userCreateRequestAcceptsSnakeCaseAndCamelCaseTransitionFields() throws Exception {
        UserCreateRequest request = objectMapper.readValue("""
                {
                  "username": "tester",
                  "password": "secret",
                  "git_name": "tester.git",
                  "realName": "测试用户",
                  "dept_id": 2,
                  "teamId": 3,
                  "role_ids": [1, 4]
                }
                """, UserCreateRequest.class);

        assertThat(request.getGitName()).isEqualTo("tester.git");
        assertThat(request.getRealName()).isEqualTo("测试用户");
        assertThat(request.getDeptId()).isEqualTo(2L);
        assertThat(request.getTeamId()).isEqualTo(3L);
        assertThat(request.getRoleIds()).containsExactly(1L, 4L);
    }

    @Test
    void projectCreateRequestAcceptsAliasesAndValidatesRequiredFields() throws Exception {
        ProjectCreateRequest request = objectMapper.readValue("""
                {
                  "projectName": "AI Studio",
                  "owner_id": 7,
                  "deptId": 2,
                  "team_id": 3,
                  "started_at": "2026-05-12T10:00:00"
                }
                """, ProjectCreateRequest.class);

        assertThat(request.getProjectName()).isEqualTo("AI Studio");
        assertThat(request.getOwnerId()).isEqualTo(7L);
        assertThat(request.getDeptId()).isEqualTo(2L);
        assertThat(request.getTeamId()).isEqualTo(3L);
        assertThat(request.getStartedAt()).isNotNull();

        ProjectCreateRequest invalid = new ProjectCreateRequest();
        assertThat(validator.validate(invalid)).hasSize(2);
    }

    @Test
    void memberOutputRequestAcceptsOutputMetricAliasesAndRequiresStatDate() throws Exception {
        MemberOutputRequest request = objectMapper.readValue("""
                {
                  "stat_date": "2026-05-12",
                  "projectId": 8,
                  "frontend_code_lines": 120,
                  "testFileCount": 4,
                  "total_code_lines": 320
                }
                """, MemberOutputRequest.class);

        assertThat(request.getStatDate()).isNotNull();
        assertThat(request.getProjectId()).isEqualTo(8L);
        assertThat(request.getFrontendCodeLines()).isEqualTo(120);
        assertThat(request.getTestFileCount()).isEqualTo(4);
        assertThat(request.getTotalCodeLines()).isEqualTo(320);

        MemberOutputRequest invalid = new MemberOutputRequest();
        assertThat(validator.validate(invalid)).hasSize(1);
    }

    @Test
    void skillCreateRequestRequiresUploadBoundaryFields() {
        SkillCreateRequest request = new SkillCreateRequest();
        request.setName("");
        request.setCategory("");
        request.setOssKey("");

        Set<?> violations = validator.validate(request);

        assertThat(violations).hasSize(4);
    }

    @Test
    void gitlabProjectConfigRequestAcceptsSnakeCaseAndCamelCaseTransitionFields() throws Exception {
        GitlabProjectConfigRequest request = objectMapper.readValue("""
                {
                  "projectId": 11,
                  "gitlab_project_id": 101,
                  "gitlabProjectName": "AI Studio Service",
                  "gitlab_project_path": "group/ai-studio-service",
                  "gitlab_group_name": "group",
                  "enabled": 1
                }
                """, GitlabProjectConfigRequest.class);

        assertThat(request.getProject_id()).isEqualTo(11L);
        assertThat(request.getGitlab_project_id()).isEqualTo(101L);
        assertThat(request.getGitlab_project_name()).isEqualTo("AI Studio Service");
        assertThat(request.getGitlab_project_path()).isEqualTo("group/ai-studio-service");
        assertThat(request.getGitlab_group_name()).isEqualTo("group");
    }

    @Test
    void gitlabWebhookRequestAcceptsGitlabSnakeCasePayload() throws Exception {
        GitlabWebhookRequest request = objectMapper.readValue("""
                {
                  "object_kind": "push",
                  "event_name": "push",
                  "user_name": "tester",
                  "user_email": "tester@example.com",
                  "ref": "refs/heads/main",
                  "checkout_sha": "abcdef123456",
                  "total_commits_count": 1,
                  "project": {"id": 99},
                  "user": {"id": 7},
                  "commits": [
                    {
                      "id": "abcdef123456",
                      "author_name": "tester",
                      "author_email": "tester@example.com",
                      "message": "feat: add webhook pipeline",
                      "timestamp": "2026-06-22T10:15:30Z"
                    }
                  ]
                }
                """, GitlabWebhookRequest.class);

        assertThat(request.getObject_kind()).isEqualTo("push");
        assertThat(request.getEvent_name()).isEqualTo("push");
        assertThat(request.getUser_name()).isEqualTo("tester");
        assertThat(request.getUser_email()).isEqualTo("tester@example.com");
        assertThat(request.getCheckout_sha()).isEqualTo("abcdef123456");
        assertThat(request.getTotal_commits_count()).isEqualTo(1);
        assertThat(request.getProject()).containsEntry("id", 99);
        assertThat(request.getUser()).containsEntry("id", 7);
        assertThat(request.getCommits()).hasSize(1);
    }

    @Test
    void activityWorkItemResponseSerializesWithSnakeCaseFields() throws Exception {
        ActivityWorkItemResponse response = new ActivityWorkItemResponse();
        response.setId(1L);
        response.setAnalysis_date(java.time.LocalDate.of(2026, 6, 22));
        response.setGitlab_project_id(101L);
        response.setProgress_status("in_progress");
        response.setRisk_summary("主题集中");

        JsonNode json = objectMapper.valueToTree(Result.success(response));

        assertThat(json.path("data").has("analysis_date")).isTrue();
        assertThat(json.path("data").has("gitlab_project_id")).isTrue();
        assertThat(json.path("data").has("progress_status")).isTrue();
        assertThat(json.path("data").has("risk_summary")).isTrue();
        assertThat(json.path("data").has("gitlabProjectId")).isFalse();
    }

    @Test
    void taskAndEventLogResponsesSerializeWithSnakeCaseFields() {
        com.aistudio.service.dto.response.ActivityTaskExecutionResponse taskResponse = new com.aistudio.service.dto.response.ActivityTaskExecutionResponse();
        taskResponse.setId(10L);
        taskResponse.setEvent_log_id(77L);
        taskResponse.setTask_type("ANALYZE");

        GitlabEventLogResponse eventLogResponse = new GitlabEventLogResponse();
        eventLogResponse.setId(77L);
        eventLogResponse.setEvent_uid("evt-1");
        eventLogResponse.setGitlab_project_id(101L);
        eventLogResponse.setProcess_status("RECORDED");
        eventLogResponse.setRelated_commit_refs(List.of("abc12345"));
        eventLogResponse.setRelated_mr_refs(List.of(88L));
        eventLogResponse.setRelated_work_item_summaries(List.of("补齐 webhook | in_progress | low"));
        eventLogResponse.setRelated_report_titles(List.of("个人研发活动日报"));

        JsonNode taskJson = objectMapper.valueToTree(Result.success(taskResponse));
        JsonNode eventJson = objectMapper.valueToTree(Result.success(eventLogResponse));

        assertThat(taskJson.path("data").has("event_log_id")).isTrue();
        assertThat(taskJson.path("data").has("eventLogId")).isFalse();
        assertThat(eventJson.path("data").has("event_uid")).isTrue();
        assertThat(eventJson.path("data").has("gitlab_project_id")).isTrue();
        assertThat(eventJson.path("data").has("process_status")).isTrue();
        assertThat(eventJson.path("data").has("related_commit_refs")).isTrue();
        assertThat(eventJson.path("data").has("related_mr_refs")).isTrue();
        assertThat(eventJson.path("data").has("related_work_item_summaries")).isTrue();
        assertThat(eventJson.path("data").has("related_report_titles")).isTrue();
        assertThat(eventJson.path("data").has("processStatus")).isFalse();
    }
}
