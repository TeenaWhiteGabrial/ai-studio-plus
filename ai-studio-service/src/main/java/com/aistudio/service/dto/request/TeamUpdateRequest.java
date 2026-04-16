package com.aistudio.service.dto.request;

import lombok.Data;

@Data
public class TeamUpdateRequest {
    private String teamName;
    private String description;
    private Integer status;
}
