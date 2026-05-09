package com.aistudio.service.schedule;

import com.aistudio.service.service.EmailReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailReportScheduler {

    private final EmailReportService emailReportService;

    @Scheduled(cron = "0 * * * * ?")
    public void sendDueRules() {
        emailReportService.sendDueRules();
    }
}
