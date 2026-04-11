package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.request.PluginRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Plugin;
import com.aistudio.service.mapper.PluginMapper;
import com.aistudio.service.service.OssService;
import com.aistudio.service.service.PluginService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class PluginServiceImpl implements PluginService {

    private static final long MAX_FILE_SIZE = 100L * 1024 * 1024; // 100MB

    private final PluginMapper pluginMapper;
    private final OssService ossService;

    @Override
    public PageResult<Plugin> listPlugins(int page, int size, String keyword, String type, Integer status) {
        LambdaQueryWrapper<Plugin> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) wrapper.like(Plugin::getName, keyword);
        if (StringUtils.hasText(type)) wrapper.eq(Plugin::getType, type);
        if (status != null) wrapper.eq(Plugin::getStatus, status);
        wrapper.orderByDesc(Plugin::getCreatedAt);
        Page<Plugin> p = pluginMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(p.getTotal(), p.getRecords());
    }

    @Override
    public Long createPlugin(PluginRequest request, Long userId) {
        if (request.getFileSize() != null && request.getFileSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小超过 100MB 限制");
        }
        Plugin plugin = new Plugin();
        copyFromRequest(plugin, request);
        plugin.setCreatedBy(userId);
        plugin.setDownloadCount(0);
        pluginMapper.insert(plugin);
        return plugin.getId();
    }

    @Override
    public void updatePlugin(Long id, PluginRequest request, Long userId) {
        Plugin plugin = getPluginById(id);
        copyFromRequest(plugin, request);
        pluginMapper.updateById(plugin);
    }

    @Override
    public void deletePlugin(Long id, Long userId) {
        Plugin plugin = getPluginById(id);
        ossService.deleteFile(plugin.getFileOssKey());
        pluginMapper.deleteById(id);
    }

    @Override
    public Plugin getPluginById(Long id) {
        Plugin plugin = pluginMapper.selectById(id);
        if (plugin == null) throw new BusinessException(404, "Plugin 不存在");
        return plugin;
    }

    @Override
    public String downloadPlugin(Long id) {
        Plugin plugin = getPluginById(id);
        pluginMapper.incrementDownloadCount(id);
        return plugin.getFileUrl();
    }

    private void copyFromRequest(Plugin plugin, PluginRequest request) {
        plugin.setName(request.getName());
        plugin.setDescription(request.getDescription());
        plugin.setType(request.getType());
        plugin.setVersion(request.getVersion());
        plugin.setFileOssKey(request.getFileOssKey());
        plugin.setFileUrl(request.getFileUrl());
        plugin.setFileSize(request.getFileSize());
        if (request.getStatus() != null) plugin.setStatus(request.getStatus());
    }
}
