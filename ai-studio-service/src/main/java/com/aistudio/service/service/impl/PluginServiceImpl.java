package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.request.AuditRequest;
import com.aistudio.service.dto.request.PluginRequest;
import com.aistudio.service.dto.request.PluginVersionRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Plugin;
import com.aistudio.service.entity.PluginVersion;
import com.aistudio.service.mapper.PluginMapper;
import com.aistudio.service.mapper.PluginVersionMapper;
import com.aistudio.service.service.OssService;
import com.aistudio.service.service.NotificationService;
import com.aistudio.service.service.PluginService;
import com.aistudio.service.util.EmojiFilter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aistudio.service.dto.response.PluginVersionVO;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PluginServiceImpl implements PluginService {

    private static final long MAX_FILE_SIZE = 100L * 1024 * 1024; // 100MB

    private final PluginMapper pluginMapper;
    private final PluginVersionMapper versionMapper;
    private final OssService ossService;
    private final NotificationService notificationService;

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

        // 创建 plugin 主记录
        Plugin plugin = new Plugin();
        plugin.setName(EmojiFilter.filter(request.getName()));
        plugin.setDescription(EmojiFilter.filter(request.getDescription()));
        plugin.setCategory(EmojiFilter.filter(request.getCategory()));
        plugin.setIcon(EmojiFilter.filter(request.getIcon()));
        plugin.setFileOssKey(request.getFileOssKey());
        plugin.setFileUrl(ossService.getPublicUrl(request.getFileOssKey()));
        plugin.setFileSize(request.getFileSize());
        plugin.setCreatorId(userId);
        plugin.setStatus(0);  // 待审核
        plugin.setDownloadCount(0);
        plugin.setTotalVersions(0);
        pluginMapper.insert(plugin);

        // 发布初始版本 1.0.0
        PluginVersionRequest versionRequest = new PluginVersionRequest();
        versionRequest.setOssKey(request.getFileOssKey());
        versionRequest.setFileSize(request.getFileSize());
        versionRequest.setChangelog("初始版本");
        versionRequest.setBumpType(PluginVersionRequest.VersionBumpType.PATCH);
        publishVersion(plugin.getId(), versionRequest, userId);

        return plugin.getId();
    }

    @Override
    public void updatePlugin(Long id, PluginRequest request, Long userId) {
        Plugin plugin = getPluginById(id);
        // 只更新基础信息，不更新文件信息（文件信息通过版本管理更新）
        plugin.setName(EmojiFilter.filter(request.getName()));
        plugin.setDescription(EmojiFilter.filter(request.getDescription()));
        plugin.setCategory(EmojiFilter.filter(request.getCategory()));
        plugin.setIcon(EmojiFilter.filter(request.getIcon()));
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
        notifyResourceAuditResult(plugin.getCreatorId(), "Plugin", plugin.getName(), id, request.getStatus());
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

    @Override
    @Transactional
    public String publishVersion(Long pluginId, PluginVersionRequest request, Long userId) {
        Plugin plugin = getPluginById(pluginId);
        if (plugin.getCreatorId() != null && !plugin.getCreatorId().equals(userId)) {
            throw new BusinessException("只能为自己创建的插件发布版本");
        }

        // 生成新版本号
        String currentVersion = plugin.getLatestVersion() != null ? plugin.getLatestVersion() : "0.0.0";
        String newVersion = generateNextVersion(currentVersion, request.getBumpType());

        // 检查版本是否已存在
        if (versionMapper.existsByPluginIdAndVersion(pluginId, newVersion)) {
            throw new BusinessException("版本 " + newVersion + " 已存在");
        }

        // 创建版本记录
        PluginVersion version = new PluginVersion();
        version.setPluginId(pluginId);
        version.setVersion(newVersion);
        version.setOssKey(request.getOssKey());
        version.setOssUrl(ossService.getPublicUrl(request.getOssKey()));
        version.setFileSize(request.getFileSize());
        version.setChangelog(request.getChangelog());
        version.setStatus(1); // 启用
        version.setPublishedBy(userId);
        version.setPublishedAt(LocalDateTime.now());
        version.setCreatedAt(LocalDateTime.now());
        versionMapper.insert(version);

        // 更新插件主表
        plugin.setLatestVersionId(version.getId());
        plugin.setLatestVersion(newVersion);
        plugin.setFileOssKey(request.getOssKey());
        plugin.setFileUrl(ossService.getPublicUrl(request.getOssKey()));
        plugin.setFileSize(request.getFileSize());
        plugin.setTotalVersions((plugin.getTotalVersions() != null ? plugin.getTotalVersions() : 0) + 1);
        pluginMapper.updateById(plugin);

        return newVersion;
    }

    private String generateNextVersion(String currentVersion, PluginVersionRequest.VersionBumpType bumpType) {
        // 如果当前版本为空或 0.0.0，使用 MAJOR 获取 1.0.0
        if (currentVersion == null || "0.0.0".equals(currentVersion)) {
            return "1.0.0";
        }

        String[] parts = currentVersion.split("\\.");
        int major = Integer.parseInt(parts[0]);
        int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;

        return switch (bumpType) {
            case MAJOR -> (major + 1) + ".0.0";
            case MINOR -> major + "." + (minor + 1) + ".0";
            case PATCH -> major + "." + minor + "." + (patch + 1);
        };
    }

    @Override
    public List<PluginVersionVO> getVersions(Long pluginId) {
        List<PluginVersion> versions = versionMapper.selectByPluginId(pluginId);
        return versions.stream().map(v -> {
            PluginVersionVO vo = new PluginVersionVO();
            vo.setId(v.getId());
            vo.setVersion(v.getVersion());
            vo.setOssUrl(v.getOssUrl());
            vo.setFileSize(v.getFileSize());
            vo.setChangelog(v.getChangelog());
            vo.setCreatedAt(v.getCreatedAt());
            return vo;
        }).collect(java.util.stream.Collectors.toList());
    }
}
