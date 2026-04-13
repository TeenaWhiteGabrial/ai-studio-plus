package com.aistudio.service.controller.console;

import com.aistudio.service.common.Result;
import com.aistudio.service.dto.request.PluginRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Plugin;
import com.aistudio.service.service.PluginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Console - Plugin 浏览（只读）
 */
@Tag(name = "Console - Plugin浏览")
@RestController
@RequestMapping("/console/plugin")
@RequiredArgsConstructor
public class ConsolePluginController {

    private final PluginService pluginService;

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

    @Operation(summary = "Plugin 详情")
    @GetMapping("/{id}")
    public Result<Plugin> detail(@PathVariable Long id) {
        return Result.success(pluginService.getPluginById(id));
    }

    @Operation(summary = "下载 Plugin")
    @GetMapping("/{id}/download")
    public Result<String> download(@PathVariable Long id) {
        return Result.success(pluginService.downloadPlugin(id));
    }
}
