package com.aistudio.service.controller.admin;

import com.aistudio.service.dto.request.EmailReportRuleRequest;
import com.aistudio.service.entity.EmailReportRule;
import com.aistudio.service.service.EmailReportService;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminEmailReportControllerTest {

    private final RecordingEmailReportService emailReportService = new RecordingEmailReportService();
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new AdminEmailReportController(emailReportService))
            .setMessageConverters(new MappingJackson2HttpMessageConverter())
            .build();

    @Test
    void createAcceptsSnakeCaseRecipientUserPayload() throws Exception {
        mockMvc.perform(post("/admin/email-report/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "产品研发日报",
                                  "to_recipient_user_ids": [1, 2],
                                  "cc_recipient_user_ids": [3],
                                  "user_ids": [4, 5],
                                  "send_time": "18:00",
                                  "status": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(18));

        assertThat(emailReportService.lastCreateRequest.getToRecipientUserIds()).containsExactly(1L, 2L);
        assertThat(emailReportService.lastCreateRequest.getCcRecipientUserIds()).containsExactly(3L);
        assertThat(emailReportService.lastCreateRequest.getUserIds()).containsExactly(4L, 5L);
        assertThat(emailReportService.lastCreateRequest.getSendTime()).isEqualTo("18:00");
    }

    private static class RecordingEmailReportService implements EmailReportService {
        private EmailReportRuleRequest lastCreateRequest;

        @Override
        public List<Map<String, Object>> listRules() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Long createRule(EmailReportRuleRequest request) {
            this.lastCreateRequest = request;
            return 18L;
        }

        @Override
        public void updateRule(Long id, EmailReportRuleRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteRule(Long id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Map<String, Object>> listSendLogs(Long ruleId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String previewDailyReport(Long id, LocalDate date) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendDailyReport(Long id, LocalDate date) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendDueRules() {
            throw new UnsupportedOperationException();
        }
    }
}
