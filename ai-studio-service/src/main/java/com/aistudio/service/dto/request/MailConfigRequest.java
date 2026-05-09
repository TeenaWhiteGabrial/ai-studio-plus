package com.aistudio.service.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MailConfigRequest {

    @NotBlank(message = "发件邮箱不能为空")
    @Email(message = "发件邮箱格式不正确")
    @Size(max = 100, message = "发件邮箱不能超过100个字符")
    @JsonAlias("senderEmail")
    private String senderEmail;

    @Size(max = 200, message = "安全码不能超过200个字符")
    @JsonAlias("authCode")
    private String authCode;
}
