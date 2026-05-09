package com.aistudio.service.controller.admin;

import com.aistudio.service.dto.request.MailConfigRequest;
import com.aistudio.service.dto.response.MailConfigVO;
import com.aistudio.service.entity.MailConfig;
import com.aistudio.service.service.MailConfigService;
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

class AdminMailConfigControllerTest {

    private final RecordingMailConfigService mailConfigService = new RecordingMailConfigService();
    private final ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new AdminMailConfigController(mailConfigService))
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();

    @Test
    void updateAcceptsSnakeCasePayload() throws Exception {
        mockMvc.perform(post("/admin/system/mail-config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sender_email": "robot@qq.com",
                                  "auth_code": "abc123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.sender_email").value("robot@qq.com"))
                .andExpect(jsonPath("$.data.has_auth_code").value(true));

        assertThat(mailConfigService.lastRequest.getSenderEmail()).isEqualTo("robot@qq.com");
        assertThat(mailConfigService.lastRequest.getAuthCode()).isEqualTo("abc123");
    }

    private static class RecordingMailConfigService implements MailConfigService {
        private MailConfigRequest lastRequest;

        @Override
        public MailConfigVO getConfig() {
            throw new UnsupportedOperationException();
        }

        @Override
        public MailConfigVO updateConfig(MailConfigRequest request) {
            this.lastRequest = request;
            MailConfigVO response = new MailConfigVO();
            response.setSenderEmail(request.getSenderEmail());
            response.setHasAuthCode(true);
            return response;
        }

        @Override
        public MailConfig getMailConfig() {
            throw new UnsupportedOperationException();
        }
    }
}
