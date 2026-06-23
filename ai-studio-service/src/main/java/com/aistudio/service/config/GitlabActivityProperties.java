package com.aistudio.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "gitlab.activity")
public class GitlabActivityProperties {

    private String webhookToken = "";
    private String apiBaseUrl = "";
    private String privateToken = "";
    private boolean schedulesEnabled = false;
    private String dailyAnalyzeCron = "0 15 20 * * ?";
    private String dailyReportCron = "0 30 20 * * ?";
    private String backfillCron = "0 0/30 * * * ?";
}
