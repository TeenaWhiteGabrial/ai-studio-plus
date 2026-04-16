package com.aistudio.service.dto.request;

import lombok.Data;

@Data
public class TeamCreateRequest {
    private String teamName;
    private Long deptId;
    private String description;
}
