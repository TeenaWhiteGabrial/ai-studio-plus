package com.aistudio.service.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ActivityTaskExecutionResponse {
    private Long id;
    private String task_type;
    private LocalDate target_date;
    private Long user_id;
    private Long project_id;
    private Long event_log_id;
    private String target_ref;
    private String status;
    private Integer attempt_count;
    private LocalDateTime next_run_at;
    private String error_message;
    private String payload_json;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
