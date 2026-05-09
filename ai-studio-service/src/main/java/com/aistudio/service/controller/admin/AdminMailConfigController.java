package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.dto.request.MailConfigRequest;
import com.aistudio.service.dto.response.MailConfigVO;
import com.aistudio.service.service.MailConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin - 邮件配置")
@RestController
@RequestMapping("/admin/system/mail-config")
@RequiredArgsConstructor
public class AdminMailConfigController {

    private final MailConfigService mailConfigService;

    @Operation(summary = "获取邮件配置")
    @GetMapping
    public Result<MailConfigVO> getConfig() {
        return Result.success(mailConfigService.getConfig());
    }

    @Operation(summary = "更新邮件配置")
    @PostMapping
    public Result<MailConfigVO> updateConfig(@Valid @RequestBody MailConfigRequest request) {
        return Result.success(mailConfigService.updateConfig(request));
    }
}
