package com.aistudio.service.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ActivityManualTriggerRequest {

    @JsonAlias("targetDate")
    private LocalDate target_date;

    @JsonAlias("userId")
    private Long user_id;

    @JsonAlias("projectId")
    private Long project_id;

    @NotNull(message = "GitLab 项目ID不能为空")
    @JsonAlias("gitlabProjectId")
    private Long gitlab_project_id;
}
