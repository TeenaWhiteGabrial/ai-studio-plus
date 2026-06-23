package com.aistudio.service.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ActivityWorkItemResponse {
    private Long id;
    private LocalDate analysis_date;
    private Long user_id;
    private Long gitlab_user_id;
    private Long project_id;
    private Long gitlab_project_id;
    private Long stat_group_id;
    private String title;
    private String summary;
    private String work_type;
    private String progress_status;
    private String risk_level;
    private String risk_summary;
    private String blocking_status;
    private String blocking_summary;
    private String module_names;
    private Integer related_commit_count;
    private Integer related_mr_count;
    private BigDecimal confidence;
    private String evidence_summary;
    private List<String> evidence_refs;
}
