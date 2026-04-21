package com.aistudio.service.dto.request;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String realName;
    private Long deptId;
    private Long teamId;  // 团队ID
    private String email;
    private String avatar;  // 头像URL
    private Integer status;
}
