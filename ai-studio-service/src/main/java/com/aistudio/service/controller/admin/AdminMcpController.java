package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.request.AuditRequest;
import com.aistudio.service.dto.request.McpServerRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.McpServer;
import com.aistudio.service.service.McpServerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Admin - MCP 服务器管理
 */
@Tag(name = "Admin - MCP服务器管理")
@RestController
@RequestMapping("/admin/mcp")
@RequiredArgsConstructor
public class AdminMcpController {

    private final McpServerService mcpServerService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "MCP 列表（管理端）")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<PageResult<McpServer>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(mcpServerService.listMcpServers(page, size, keyword, status));
    }

    @Operation(summary = "MCP 服务器详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<McpServer> detail(@PathVariable Long id) {
        return Result.success(mcpServerService.getMcpServerById(id));
    }

    @Operation(summary = "审核 MCP 服务器")
    @PostMapping("/{id}/audit")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Void> audit(@PathVariable Long id, @Valid @RequestBody AuditRequest request) {
        mcpServerService.auditMcpServer(id, request, securityUtils.getCurrentUserId());
        return Result.success();
    }

    @Operation(summary = "创建 MCP 服务器")
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Long> create(@Valid @RequestBody McpServerRequest request) {
        return Result.success(mcpServerService.createMcpServer(request, securityUtils.getCurrentUserId()));
    }

    @Operation(summary = "更新 MCP 服务器")
    @PostMapping("/update/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody McpServerRequest request) {
        mcpServerService.updateMcpServer(id, request, securityUtils.getCurrentUserId());
        return Result.success();
    }

    @Operation(summary = "删除 MCP 服务器")
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        mcpServerService.deleteMcpServer(id, securityUtils.getCurrentUserId());
        return Result.success();
    }

    @Operation(summary = "测试连接")
    @PostMapping("/{id}/test")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Map<String, Object>> testConnection(@PathVariable Long id) {
        return Result.success(mcpServerService.testConnection(id));
    }
}
