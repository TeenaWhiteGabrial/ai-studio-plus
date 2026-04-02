package com.aistudio.service.service;

import com.aistudio.service.dto.request.PluginRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Plugin;

public interface PluginService {
    PageResult<Plugin> listPlugins(int page, int size, String keyword, String type, Integer status);
    Long createPlugin(PluginRequest request, Long userId);
    void updatePlugin(Long id, PluginRequest request, Long userId);
    void deletePlugin(Long id, Long userId);
    Plugin getPluginById(Long id);
    String downloadPlugin(Long id);
}
