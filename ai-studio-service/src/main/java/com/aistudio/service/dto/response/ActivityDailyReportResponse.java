package com.aistudio.service.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ActivityDailyReportResponse {
    private Long id;
    private LocalDate report_date;
    private String report_scope;
    private Long user_id;
    private Long project_id;
    private Long stat_group_id;
    private String title;
    private String content;
    private String report_data_json;
    private String generated_by;
    private LocalDateTime created_at;
}
