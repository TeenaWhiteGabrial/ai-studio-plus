package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.dto.request.AiModelConfigRequest;
import com.aistudio.service.dto.response.AiModelConfigVO;
import com.aistudio.service.service.AiModelConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/system/ai-model-config")
@RequiredArgsConstructor
public class AdminAiModelConfigController {

    private final AiModelConfigService configService;

    @GetMapping
    public Result<AiModelConfigVO> getConfig() {
        return Result.success(configService.getConfig());
    }

    @PostMapping
    public Result<AiModelConfigVO> updateConfig(@Valid @RequestBody AiModelConfigRequest request) {
        return Result.success(configService.updateConfig(request));
    }
}
