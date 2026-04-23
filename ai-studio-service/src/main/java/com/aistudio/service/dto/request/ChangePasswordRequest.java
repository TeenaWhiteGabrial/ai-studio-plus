package com.aistudio.service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "旧密码不能为空")
    @JsonProperty("oldPassword")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @JsonProperty("newPassword")
    private String newPassword;
}
