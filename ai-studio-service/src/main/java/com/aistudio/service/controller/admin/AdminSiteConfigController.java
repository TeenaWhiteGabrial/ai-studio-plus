package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.dto.request.SiteConfigRequest;
import com.aistudio.service.dto.response.SiteConfigVO;
import com.aistudio.service.service.SiteConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin - 网站设置")
@RestController
@RequestMapping("/admin/site/config")
@RequiredArgsConstructor
public class AdminSiteConfigController {

    private final SiteConfigService siteConfigService;

    @Operation(summary = "获取网站设置")
    @GetMapping
    public Result<SiteConfigVO> getConfig() {
        return Result.success(siteConfigService.getConfig());
    }

    @Operation(summary = "更新网站设置")
    @PostMapping
    public Result<SiteConfigVO> updateConfig(@Valid @RequestBody SiteConfigRequest request) {
        return Result.success(siteConfigService.updateConfig(request));
    }
}
