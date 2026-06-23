package com.aistudio.service.controller.admin;

import com.aistudio.service.dto.request.GitlabRuntimeConfigRequest;
import com.aistudio.service.dto.response.GitlabRuntimeConfigVO;
import com.aistudio.service.entity.GitlabRuntimeConfig;
import com.aistudio.service.service.GitlabRuntimeConfigService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminGitlabRuntimeConfigControllerTest {

    private final RecordingGitlabRuntimeConfigService configService = new RecordingGitlabRuntimeConfigService();
    private final ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new AdminGitlabRuntimeConfigController(configService))
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();

    @Test
    void updateAcceptsSnakeCasePayload() throws Exception {
        mockMvc.perform(post("/admin/system/gitlab-runtime-config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "base_url": "https://gitlab.example.com",
                                  "private_token": "token-1",
                                  "webhook_token": "webhook-1",
                                  "schedules_enabled": true,
                                  "daily_analyze_cron": "0 15 20 * * ?",
                                  "daily_report_cron": "0 30 20 * * ?",
                                  "backfill_cron": "0 0/30 * * * ?",
                                  "enabled": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.base_url").value("https://gitlab.example.com"))
                .andExpect(jsonPath("$.data.has_private_token").value(true))
                .andExpect(jsonPath("$.data.has_webhook_token").value(true))
                .andExpect(jsonPath("$.data.schedules_enabled").value(true));

        assertThat(configService.lastRequest.getBase_url()).isEqualTo("https://gitlab.example.com");
        assertThat(configService.lastRequest.getPrivate_token()).isEqualTo("token-1");
        assertThat(configService.lastRequest.getWebhook_token()).isEqualTo("webhook-1");
        assertThat(configService.lastRequest.getSchedules_enabled()).isTrue();
    }

    private static class RecordingGitlabRuntimeConfigService implements GitlabRuntimeConfigService {
        private GitlabRuntimeConfigRequest lastRequest;

        @Override
        public GitlabRuntimeConfigVO getConfig() {
            throw new UnsupportedOperationException();
        }

        @Override
        public GitlabRuntimeConfigVO updateConfig(GitlabRuntimeConfigRequest request) {
            this.lastRequest = request;
            GitlabRuntimeConfigVO response = new GitlabRuntimeConfigVO();
            response.setBase_url(request.getBase_url());
            response.setHas_private_token(true);
            response.setHas_webhook_token(true);
            response.setSchedules_enabled(Boolean.TRUE.equals(request.getSchedules_enabled()));
            return response;
        }

        @Override
        public GitlabRuntimeConfig getRuntimeConfig() {
            throw new UnsupportedOperationException();
        }
    }
}
