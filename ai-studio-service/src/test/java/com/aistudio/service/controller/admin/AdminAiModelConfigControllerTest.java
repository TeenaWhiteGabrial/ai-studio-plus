package com.aistudio.service.controller.admin;

import com.aistudio.service.dto.request.AiModelConfigRequest;
import com.aistudio.service.dto.response.AiModelConfigVO;
import com.aistudio.service.entity.AiModelConfig;
import com.aistudio.service.service.AiModelConfigService;
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

class AdminAiModelConfigControllerTest {

    private final RecordingAiModelConfigService configService = new RecordingAiModelConfigService();
    private final ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new AdminAiModelConfigController(configService))
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();

    @Test
    void updateAcceptsSnakeCasePayload() throws Exception {
        mockMvc.perform(post("/admin/system/ai-model-config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "provider_name": "openai-compatible",
                                  "base_url": "https://llm.example.com",
                                  "model_name": "gpt-4.1",
                                  "api_key": "sk-test",
                                  "prompt_version": "v1",
                                  "request_timeout_ms": 30000,
                                  "max_retries": 2,
                                  "enabled": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.provider_name").value("openai-compatible"))
                .andExpect(jsonPath("$.data.base_url").value("https://llm.example.com"))
                .andExpect(jsonPath("$.data.model_name").value("gpt-4.1"))
                .andExpect(jsonPath("$.data.has_api_key").value(true));

        assertThat(configService.lastRequest.getProvider_name()).isEqualTo("openai-compatible");
        assertThat(configService.lastRequest.getBase_url()).isEqualTo("https://llm.example.com");
        assertThat(configService.lastRequest.getModel_name()).isEqualTo("gpt-4.1");
        assertThat(configService.lastRequest.getApi_key()).isEqualTo("sk-test");
    }

    private static class RecordingAiModelConfigService implements AiModelConfigService {
        private AiModelConfigRequest lastRequest;

        @Override
        public AiModelConfigVO getConfig() {
            throw new UnsupportedOperationException();
        }

        @Override
        public AiModelConfigVO updateConfig(AiModelConfigRequest request) {
            this.lastRequest = request;
            AiModelConfigVO response = new AiModelConfigVO();
            response.setProvider_name(request.getProvider_name());
            response.setBase_url(request.getBase_url());
            response.setModel_name(request.getModel_name());
            response.setHas_api_key(true);
            return response;
        }

        @Override
        public AiModelConfig getAiModelConfig() {
            throw new UnsupportedOperationException();
        }
    }
}
