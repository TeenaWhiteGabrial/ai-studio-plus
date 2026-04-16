package com.aistudio.service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 批量导入用户请求项
 */
@Data
public class BatchUserImportRequest {

    @NotBlank(message = "账号不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String realName;

    private String department;  // 部门名称（Excel导入用）

    private String team;  // 团队名称（Excel导入用）

    @Email(message = "邮箱格式不正确")
    private String email;
}
