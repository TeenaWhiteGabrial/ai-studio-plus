package com.aistudio.service.controller.portal;

import com.aistudio.service.common.Result;
import com.aistudio.service.dto.response.SiteConfigVO;
import com.aistudio.service.service.SiteConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Portal - 网站设置")
@RestController
@RequestMapping("/portal/site")
@RequiredArgsConstructor
public class SiteConfigController {

    private final SiteConfigService siteConfigService;

    @Operation(summary = "获取网站设置")
    @GetMapping("/config")
    public Result<SiteConfigVO> config() {
        return Result.success(siteConfigService.getConfig());
    }
}
