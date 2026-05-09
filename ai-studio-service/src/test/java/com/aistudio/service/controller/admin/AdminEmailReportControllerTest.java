package com.aistudio.service.controller.admin;

import com.aistudio.service.dto.request.EmailReportRuleRequest;
import com.aistudio.service.service.EmailReportService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminEmailReportControllerTest {

    private final EmailReportService emailReportService = mock(EmailReportService.class);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new AdminEmailReportController(emailReportService))
            .build();

    @Test
    void createAcceptsSnakeCaseRecipientUserPayload() throws Exception {
        when(emailReportService.createRule(any(EmailReportRuleRequest.class))).thenReturn(18L);

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

        ArgumentCaptor<EmailReportRuleRequest> captor = ArgumentCaptor.forClass(EmailReportRuleRequest.class);
        verify(emailReportService).createRule(captor.capture());
        assertThat(captor.getValue().getToRecipientUserIds()).containsExactly(1L, 2L);
        assertThat(captor.getValue().getCcRecipientUserIds()).containsExactly(3L);
        assertThat(captor.getValue().getUserIds()).containsExactly(4L, 5L);
        assertThat(captor.getValue().getSendTime()).isEqualTo("18:00");
    }
}
