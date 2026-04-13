package com.aistudio.service.controller.console;

import com.aistudio.service.common.Result;
import com.aistudio.service.dto.request.McpServerRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.McpServer;
import com.aistudio.service.service.McpServerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Console - MCP 服务器浏览（只读）
 */
@Tag(name = "Console - MCP浏览")
@RestController
@RequestMapping("/console/mcp")
@RequiredArgsConstructor
public class ConsoleMcpController {

    private final McpServerService mcpServerService;

    @Operation(summary = "MCP 列表")
    @GetMapping("/list")
    public Result<PageResult<McpServer>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(mcpServerService.listMcpServers(page, size, keyword, status));
    }

    @Operation(summary = "MCP 服务器详情")
    @GetMapping("/{id}")
    public Result<McpServer> detail(@PathVariable Long id) {
        return Result.success(mcpServerService.getMcpServerById(id));
    }

    @Operation(summary = "测试连接")
    @PostMapping("/{id}/test")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','USER')")
    public Result<Map<String, Object>> testConnection(@PathVariable Long id) {
        return Result.success(mcpServerService.testConnection(id));
    }
}
