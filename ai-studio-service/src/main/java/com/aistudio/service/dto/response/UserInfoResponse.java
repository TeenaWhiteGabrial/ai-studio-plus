package com.aistudio.service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserInfoResponse {
    private Long userId;
    private String username;
    private String gitName;
    private String realName;
    private List<String> roles;
    private String avatar;       // 头像URL
    private Long deptId;         // 部门ID
    private String deptName;     // 部门名称
    private Long teamId;         // 团队ID
    private String teamName;     // 团队名称
    private String email;         // 邮箱
}
