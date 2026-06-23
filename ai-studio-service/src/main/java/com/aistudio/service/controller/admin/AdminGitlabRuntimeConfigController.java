package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.dto.request.GitlabRuntimeConfigRequest;
import com.aistudio.service.dto.response.GitlabRuntimeConfigVO;
import com.aistudio.service.service.GitlabRuntimeConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/system/gitlab-runtime-config")
@RequiredArgsConstructor
public class AdminGitlabRuntimeConfigController {

    private final GitlabRuntimeConfigService configService;

    @GetMapping
    public Result<GitlabRuntimeConfigVO> getConfig() {
        return Result.success(configService.getConfig());
    }

    @PostMapping
    public Result<GitlabRuntimeConfigVO> updateConfig(@Valid @RequestBody GitlabRuntimeConfigRequest request) {
        return Result.success(configService.updateConfig(request));
    }
}
