package com.aistudio.service.service.impl;

import com.aistudio.service.entity.AiModelConfig;
import com.aistudio.service.entity.GitlabCommitFact;
import com.aistudio.service.service.AiActivityAnalysisService;
import com.aistudio.service.service.AiModelConfigService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class AiActivityAnalysisServiceImplTest {

    @Test
    void analyzeParsesOpenAiCompatibleJsonResponse() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        server.expect(once(), requestTo("https://llm.example.com/chat/completions"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {
                          "choices": [
                            {
                              "message": {
                                "content": "{\\"work_items\\":[{\\"title\\":\\"修复登录异常\\",\\"summary\\":\\"定位并修复登录链路问题\\",\\"work_type\\":\\"bugfix\\",\\"progress_status\\":\\"likely_completed\\",\\"risk_level\\":\\"low\\",\\"risk_summary\\":\\"已收敛\\",\\"blocking_status\\":\\"normal\\",\\"blocking_summary\\":\\"未见阻塞\\",\\"module_names\\":\\"auth/login\\",\\"related_commit_count\\":2,\\"related_mr_count\\":1,\\"evidence_summary\\":\\"关联 commits: abc12345, def67890\\",\\"confidence\\":0.88}],\\"work_summary\\":\\"完成登录异常修复\\",\\"progress_summary\\":\\"问题已收敛\\",\\"risk_summary\\":\\"风险较低\\",\\"blocking_summary\\":\\"无明显阻塞\\",\\"report_title\\":\\"个人研发活动日报\\",\\"report_content\\":\\"今天完成登录异常修复并验证通过\\"}"
                              }
                            }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        AiModelConfig config = new AiModelConfig();
        config.setEnabled(1);
        config.setProviderName("openai-compatible");
        config.setBaseUrl("https://llm.example.com");
        config.setModelName("gpt-4.1");
        config.setApiKey("sk-test");
        AiModelConfigService configService = new FixedAiModelConfigService(config);
        AiActivityAnalysisServiceImpl service = new AiActivityAnalysisServiceImpl(configService, restTemplate, new ObjectMapper().findAndRegisterModules());

        GitlabCommitFact commit = new GitlabCommitFact();
        commit.setCommitSha("abc123456789");
        commit.setShortSha("abc12345");
        commit.setAuthorName("tester");
        commit.setBranchName("main");
        commit.setCommitMessage("fix: repair login flow");
        commit.setCommittedAt(LocalDateTime.of(2026, 6, 23, 10, 0));

        Optional<AiActivityAnalysisService.AiActivityAnalysisResult> result =
                service.analyze(7L, 101L, LocalDate.of(2026, 6, 23), List.of(commit));

        assertThat(result).isPresent();
        assertThat(result.get().getWorkItems()).hasSize(1);
        assertThat(result.get().getWorkItems().get(0).getTitle()).isEqualTo("修复登录异常");
        assertThat(result.get().getWorkItems().get(0).getWorkType()).isEqualTo("bugfix");
        assertThat(result.get().getReportTitle()).isEqualTo("个人研发活动日报");
        assertThat(result.get().getAnalysisEngine()).isEqualTo("openai-compatible:gpt-4.1");
        server.verify();
    }

    private static class FixedAiModelConfigService implements AiModelConfigService {
        private final AiModelConfig config;

        private FixedAiModelConfigService(AiModelConfig config) {
            this.config = config;
        }

        @Override
        public com.aistudio.service.dto.response.AiModelConfigVO getConfig() {
            throw new UnsupportedOperationException();
        }

        @Override
        public com.aistudio.service.dto.response.AiModelConfigVO updateConfig(com.aistudio.service.dto.request.AiModelConfigRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public AiModelConfig getAiModelConfig() {
            return config;
        }
    }
}
