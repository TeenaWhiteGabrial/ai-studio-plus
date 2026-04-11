package com.aistudio.service.controller;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
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

@Tag(name = "MCP 服务器管理")
@RestController
@RequestMapping("/api/mcp")
@RequiredArgsConstructor
public class McpServerController {

    private final McpServerService mcpServerService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "MCP 列表")
    @GetMapping("/list")
    public Result<PageResult<McpServer>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(mcpServerService.listMcpServers(page, size, keyword, status));
    }

    @Operation(summary = "创建 MCP 服务器")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody McpServerRequest request) {
        return Result.success(mcpServerService.createMcpServer(request, securityUtils.getCurrentUserId()));
    }

    @Operation(summary = "更新 MCP 服务器")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody McpServerRequest request) {
        McpServer server = mcpServerService.getMcpServerById(id);
        if (!securityUtils.isAdmin() && !server.getCreatedBy().equals(securityUtils.getCurrentUserId())) {
            return Result.error(403, "无权限操作他人资源");
        }
        mcpServerService.updateMcpServer(id, request, securityUtils.getCurrentUserId());
        return Result.success();
    }

    @Operation(summary = "删除 MCP 服务器")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        McpServer server = mcpServerService.getMcpServerById(id);
        if (!securityUtils.isAdmin() && !server.getCreatedBy().equals(securityUtils.getCurrentUserId())) {
            return Result.error(403, "无权限操作他人资源");
        }
        mcpServerService.deleteMcpServer(id, securityUtils.getCurrentUserId());
        return Result.success();
    }

    @Operation(summary = "测试连接")
    @PostMapping("/{id}/test")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public Result<Map<String, Object>> testConnection(@PathVariable Long id) {
        return Result.success(mcpServerService.testConnection(id));
    }
}
