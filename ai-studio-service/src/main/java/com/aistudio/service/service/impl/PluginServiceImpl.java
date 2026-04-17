package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.request.AuditRequest;
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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PluginServiceImpl implements PluginService {

    private static final long MAX_FILE_SIZE = 100L * 1024 * 1024; // 100MB

    private final PluginMapper pluginMapper;
    private final OssService ossService;

    @Override
    public PageResult<Plugin> listPlugins(int page, int size, String keyword, String category, Integer status) {
        LambdaQueryWrapper<Plugin> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) wrapper.like(Plugin::getName, keyword);
        if (StringUtils.hasText(category)) wrapper.eq(Plugin::getCategory, category);
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
        plugin.setCreatorId(userId);
        plugin.setStatus(0);  // 待审核
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
        if (plugin.getFileOssKey() != null) {
            try {
                ossService.deleteFile(plugin.getFileOssKey());
            } catch (Exception e) {
                // 忽略 OSS 删除失败
            }
        }
        pluginMapper.deleteById(id);
    }

    @Override
    public void auditPlugin(Long id, AuditRequest request, Long userId) {
        Plugin plugin = getPluginById(id);
        plugin.setStatus(request.getStatus());
        plugin.setReviewTime(LocalDateTime.now());
        plugin.setReviewComment(request.getReviewComment());
        pluginMapper.updateById(plugin);
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
        plugin.setCategory(request.getCategory());
        plugin.setIcon(request.getIcon());
        plugin.setVersion(request.getVersion());
        plugin.setFileOssKey(request.getFileOssKey());
        plugin.setFileUrl(request.getFileUrl());
        plugin.setFileSize(request.getFileSize());
    }
}
