package com.aistudio.service.dto.request;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String realName;
    private Long deptId;
    private String email;
    private Integer status;
}
