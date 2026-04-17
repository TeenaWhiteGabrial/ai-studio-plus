package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.request.AuditRequest;
import com.aistudio.service.dto.request.PluginRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Plugin;
import com.aistudio.service.service.PluginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin - Plugin 审核管理
 */
@Tag(name = "Admin - Plugin管理")
@RestController
@RequestMapping("/admin/plugin")
@RequiredArgsConstructor
public class AdminPluginController {

    private final PluginService pluginService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "Plugin 列表（管理端）")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<PageResult<Plugin>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        return Result.success(pluginService.listPlugins(page, size, keyword, category, status));
    }

    @Operation(summary = "Plugin 详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Plugin> detail(@PathVariable Long id) {
        Plugin plugin = pluginService.getPluginById(id);
        if (plugin == null) return Result.error(404, "Plugin不存在");
        return Result.success(plugin);
    }

    @Operation(summary = "审核 Plugin")
    @PostMapping("/{id}/audit")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Void> audit(@PathVariable Long id, @Valid @RequestBody AuditRequest request) {
        pluginService.auditPlugin(id, request, securityUtils.getCurrentUserId());
        return Result.success();
    }

    @Operation(summary = "下载 Plugin")
    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<String> download(@PathVariable Long id) {
        return Result.success(pluginService.downloadPlugin(id));
    }
}
