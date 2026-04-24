package com.aistudio.service.service;

import com.aistudio.service.dto.request.AuditRequest;
import com.aistudio.service.dto.request.McpServerRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.McpServer;

import java.util.Map;

public interface McpServerService {
    PageResult<McpServer> listMcpServers(int page, int size, String keyword, Integer status);
    Long createMcpServer(McpServerRequest request, Long userId);
    void updateMcpServer(Long id, McpServerRequest request, Long userId);
    void deleteMcpServer(Long id, Long userId);
    McpServer getMcpServerById(Long id);
    void auditMcpServer(Long id, AuditRequest request, Long userId);
    Map<String, Object> testConnection(Long id);
}
