package com.aistudio.service.service;

import com.aistudio.service.dto.request.AuditRequest;
import com.aistudio.service.dto.request.PluginRequest;
import com.aistudio.service.dto.request.PluginVersionRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.dto.response.PluginVersionVO;
import com.aistudio.service.entity.Plugin;

import java.util.List;

public interface PluginService {
    PageResult<Plugin> listPlugins(int page, int size, String keyword, String category, Integer status);
    Long createPlugin(PluginRequest request, Long userId);
    void updatePlugin(Long id, PluginRequest request, Long userId);
    void deletePlugin(Long id, Long userId);
    void auditPlugin(Long id, AuditRequest request, Long userId);
    Plugin getPluginById(Long id);
    String downloadPlugin(Long id);
    String publishVersion(Long pluginId, PluginVersionRequest request, Long userId);
    List<PluginVersionVO> getVersions(Long pluginId);
}
