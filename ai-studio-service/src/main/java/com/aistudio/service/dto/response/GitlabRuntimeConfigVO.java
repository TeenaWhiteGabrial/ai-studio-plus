package com.aistudio.service.dto.response;

import lombok.Data;

@Data
public class GitlabRuntimeConfigVO {
    private String base_url;
    private Boolean has_private_token;
    private Boolean has_webhook_token;
    private Boolean schedules_enabled;
    private String daily_analyze_cron;
    private String daily_report_cron;
    private String backfill_cron;
    private Integer enabled;
}
