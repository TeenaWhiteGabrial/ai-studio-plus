package com.aistudio.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class UserCreateRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String realName;
    private Long deptId;
    private Long teamId;  // 团队ID
    private String email;
    private String avatar;  // 头像URL

    /** 角色ID列表，若为空或null则默认分配"普通用户"角色 */
    private List<Long> roleIds;
}
