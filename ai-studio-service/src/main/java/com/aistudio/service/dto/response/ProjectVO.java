package com.aistudio.service.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectVO {

    private Long id;
    private String projectName;
    private String description;
    private Long ownerId;
    private String ownerName;
    private Long deptId;
    private String deptName;
    private Long teamId;
    private String teamName;
    private Long ownerDeptId;
    private String ownerDeptName;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
