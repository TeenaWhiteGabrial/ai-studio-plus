package com.aistudio.service.controller;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.request.PluginRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Plugin;
import com.aistudio.service.service.PluginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Plugin 管理")
@RestController
@RequestMapping("/api/plugin")
@RequiredArgsConstructor
public class PluginController {

    private final PluginService pluginService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "Plugin 列表")
    @GetMapping("/list")
    public Result<PageResult<Plugin>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status) {
        return Result.success(pluginService.listPlugins(page, size, keyword, type, status));
    }

    @Operation(summary = "上传 Plugin")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody PluginRequest request) {
        return Result.success(pluginService.createPlugin(request, securityUtils.getCurrentUserId()));
    }

    @Operation(summary = "更新 Plugin")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody PluginRequest request) {
        Plugin plugin = pluginService.getPluginById(id);
        if (!securityUtils.isAdmin() && !plugin.getCreatedBy().equals(securityUtils.getCurrentUserId())) {
            return Result.error(403, "无权限操作他人资源");
        }
        pluginService.updatePlugin(id, request, securityUtils.getCurrentUserId());
        return Result.success();
    }

    @Operation(summary = "删除 Plugin")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Plugin plugin = pluginService.getPluginById(id);
        if (!securityUtils.isAdmin() && !plugin.getCreatedBy().equals(securityUtils.getCurrentUserId())) {
            return Result.error(403, "无权限操作他人资源");
        }
        pluginService.deletePlugin(id, securityUtils.getCurrentUserId());
        return Result.success();
    }

    @Operation(summary = "下载 Plugin")
    @GetMapping("/{id}/download")
    public Result<String> download(@PathVariable Long id) {
        return Result.success(pluginService.downloadPlugin(id));
    }
}
