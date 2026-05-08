package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.request.AuditRequest;
import com.aistudio.service.dto.request.McpServerRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.McpServer;
import com.aistudio.service.mapper.McpServerMapper;
import com.aistudio.service.service.McpServerService;
import com.aistudio.service.service.NotificationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class McpServerServiceImpl implements McpServerService {

    private final McpServerMapper mcpServerMapper;
    private final NotificationService notificationService;

    @Override
    public PageResult<McpServer> listMcpServers(int page, int size, String keyword, Integer status) {
        LambdaQueryWrapper<McpServer> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) wrapper.like(McpServer::getName, keyword);
        if (status != null) wrapper.eq(McpServer::getStatus, status);
        wrapper.orderByDesc(McpServer::getCreatedAt);
        Page<McpServer> p = mcpServerMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(p.getTotal(), p.getRecords());
    }

    @Override
    public Long createMcpServer(McpServerRequest request, Long userId) {
        McpServer server = new McpServer();
        copyFromRequest(server, request);
        server.setCreatedBy(userId);
        mcpServerMapper.insert(server);
        return server.getId();
    }

    @Override
    public void updateMcpServer(Long id, McpServerRequest request, Long userId) {
        McpServer server = getMcpServerById(id);
        copyFromRequest(server, request);
        mcpServerMapper.updateById(server);
    }

    @Override
    public void deleteMcpServer(Long id, Long userId) {
        McpServer server = getMcpServerById(id);
        mcpServerMapper.deleteById(id);
    }

    @Override
    public McpServer getMcpServerById(Long id) {
        McpServer server = mcpServerMapper.selectById(id);
        if (server == null) throw new BusinessException(404, "MCP 服务器不存在");
        return server;
    }

    @Override
    public void auditMcpServer(Long id, AuditRequest request, Long userId) {
        McpServer server = getMcpServerById(id);
        server.setStatus(request.getStatus());
        server.setReviewTime(LocalDateTime.now());
        if (request.getStatus() == 2) {
            server.setReviewComment(request.getReviewComment());
        } else {
            server.setReviewComment(null);
        }
        mcpServerMapper.updateById(server);
        notifyResourceAuditResult(server.getCreatedBy() != null ? server.getCreatedBy() : server.getCreatorId(), "MCP", server.getName(), id, request.getStatus());
    }

    private void notifyResourceAuditResult(Long creatorId, String resourceType, String resourceName, Long resourceId, Integer status) {
        if (creatorId == null || status == null) return;
        if (status == 1) {
            notificationService.createRuleNotification(
                    creatorId, "RESOURCE_APPROVED", "资源审核通过",
                    "你的" + resourceType + "《" + resourceName + "》已审核通过。",
                    resourceId, resourceType.toLowerCase(), "RESOURCE_APPROVED:" + resourceType + ":" + resourceId + ":" + status);
        } else if (status == 2) {
            notificationService.createRuleNotification(
                    creatorId, "RESOURCE_TAKEN_DOWN", "资源未通过或已下架",
                    "你的" + resourceType + "《" + resourceName + "》未通过审核或已被下架。",
                    resourceId, resourceType.toLowerCase(), "RESOURCE_TAKEN_DOWN:" + resourceType + ":" + resourceId + ":" + status);
        }
    }

    @Override
    public Map<String, Object> testConnection(Long id) {
        McpServer server = getMcpServerById(id);
        Map<String, Object> result = new HashMap<>();
        long start = System.currentTimeMillis();
        try {
            URL url = new URL(server.getApiEndpoint());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            long cost = System.currentTimeMillis() - start;
            result.put("status", "success");
            result.put("responseTime", cost);
            result.put("httpCode", code);
        } catch (Exception e) {
            long cost = System.currentTimeMillis() - start;
            result.put("status", "failed");
            result.put("responseTime", cost);
            result.put("error", e.getMessage());
        }
        return result;
    }

    private void copyFromRequest(McpServer server, McpServerRequest request) {
        server.setName(request.getName());
        server.setDescription(request.getDescription());
        server.setApiEndpoint(request.getApiEndpoint());
        server.setAuthType(request.getAuthType());
        server.setConfigJson(request.getConfigJson());
        if (request.getStatus() != null) server.setStatus(request.getStatus());
    }
}
