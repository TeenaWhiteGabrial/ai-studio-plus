package com.aistudio.service.controller.portal;

import com.aistudio.service.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Portal - 公开信息（Banner、公告等）
 */
@Tag(name = "Portal - 公开信息")
@RestController
@RequestMapping("/portal/public")
@RequiredArgsConstructor
public class PortalPublicInfoController {

    @Operation(summary = "获取 Banner 列表")
    @GetMapping("/banner")
    public Result<Object> banners() {
        // TODO: 实现 Banner 查询
        return Result.success(null);
    }

    @Operation(summary = "获取公告列表")
    @GetMapping("/announcement")
    public Result<Object> announcements() {
        // TODO: 实现公告查询
        return Result.success(null);
    }

    @Operation(summary = "获取站点信息")
    @GetMapping("/site-info")
    public Result<Object> siteInfo() {
        // TODO: 实现站点信息查询（名称、描述、联系方式等）
        return Result.success(null);
    }
}
