package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.request.McpServerRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.McpServer;
import com.aistudio.service.mapper.McpServerMapper;
import com.aistudio.service.service.McpServerService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class McpServerServiceImpl implements McpServerService {

    private final McpServerMapper mcpServerMapper;

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
