package com.aistudio.service.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GitlabEventLogResponse {
    private Long id;
    private String event_uid;
    private String event_type;
    private Long project_id;
    private Long gitlab_project_id;
    private Long gitlab_user_id;
    private String ref_name;
    private LocalDateTime event_time;
    private String payload_json;
    private String process_status;
    private String process_message;
    private List<String> related_commit_refs;
    private List<Long> related_mr_refs;
    private List<String> related_work_item_summaries;
    private List<String> related_report_titles;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
