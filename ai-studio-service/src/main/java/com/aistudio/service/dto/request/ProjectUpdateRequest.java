package com.aistudio.service.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectUpdateRequest {

    @JsonAlias({"projectName", "project_name"})
    private String projectName;

    @JsonAlias("description")
    private String description;

    @JsonAlias({"ownerId", "owner_id"})
    private Long ownerId;

    @JsonAlias({"deptId", "dept_id"})
    private Long deptId;

    @JsonAlias({"teamId", "team_id"})
    private Long teamId;

    @JsonAlias("status")
    private String status;

    @JsonAlias({"startedAt", "started_at"})
    private LocalDateTime startedAt;

    @JsonAlias({"endedAt", "ended_at"})
    private LocalDateTime endedAt;
}
