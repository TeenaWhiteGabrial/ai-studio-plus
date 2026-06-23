package com.aistudio.service.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GitlabRuntimeConfigRequest {

    @NotBlank(message = "GitLab base URL不能为空")
    @JsonAlias("baseUrl")
    private String base_url;

    @JsonAlias("privateToken")
    private String private_token;

    @JsonAlias("webhookToken")
    private String webhook_token;

    @JsonAlias("schedulesEnabled")
    private Boolean schedules_enabled;

    @JsonAlias("dailyAnalyzeCron")
    private String daily_analyze_cron;

    @JsonAlias("dailyReportCron")
    private String daily_report_cron;

    @JsonAlias("backfillCron")
    private String backfill_cron;

    private Integer enabled;
}
