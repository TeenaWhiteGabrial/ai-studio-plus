package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.request.AuditRequest;
import com.aistudio.service.dto.request.TutorialRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Tutorial;
import com.aistudio.service.entity.TutorialVersion;
import com.aistudio.service.mapper.TutorialMapper;
import com.aistudio.service.mapper.TutorialVersionMapper;
import com.aistudio.service.service.OssService;
import com.aistudio.service.service.NotificationService;
import com.aistudio.service.service.TutorialService;
import com.aistudio.service.util.EmojiFilter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.aistudio.service.dto.response.TutorialVersionVO;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TutorialServiceImpl implements TutorialService {

    private final TutorialMapper tutorialMapper;
    private final TutorialVersionMapper versionMapper;
    private final OssService ossService;
    private final NotificationService notificationService;

    @Override
    public PageResult<Tutorial> listTutorials(int page, int size, String keyword, String category, String tag, Integer status) {
        LambdaQueryWrapper<Tutorial> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) wrapper.like(Tutorial::getTitle, keyword);
        if (StringUtils.hasText(category)) wrapper.eq(Tutorial::getCategory, category);
        if (status != null) wrapper.eq(Tutorial::getStatus, status);
        wrapper.orderByDesc(Tutorial::getCreatedAt);
        Page<Tutorial> p = tutorialMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(p.getTotal(), p.getRecords());
    }

    @Override
    public Long createTutorial(TutorialRequest request, Long userId) {
        Tutorial tutorial = new Tutorial();
        copyFromRequest(tutorial, request);
        tutorial.setCreatorId(userId);
        tutorial.setStatus(0);  // 待审核
        tutorial.setViewCount(0);
        tutorialMapper.insert(tutorial);
        return tutorial.getId();
    }

    @Override
    public void updateTutorial(Long id, TutorialRequest request, Long userId) {
        Tutorial tutorial = getTutorialById(id);
        copyFromRequest(tutorial, request);
        tutorialMapper.updateById(tutorial);
    }

    @Override
    public void deleteTutorial(Long id, Long userId) {
        Tutorial tutorial = getTutorialById(id);
        // 删除关联的 OSS 文件
        if (tutorial.getVideoUrl() != null) {
            try {
                ossService.deleteFile(tutorial.getVideoUrl());
            } catch (Exception e) {
                // 忽略
            }
        }
        if (tutorial.getZipFileUrl() != null) {
            try {
                ossService.deleteFile(tutorial.getZipFileUrl());
            } catch (Exception e) {
                // 忽略
            }
        }
        tutorialMapper.deleteById(id);
    }

    @Override
    public void auditTutorial(Long id, AuditRequest request, Long userId) {
        Tutorial tutorial = getTutorialById(id);
        tutorial.setStatus(request.getStatus());
        tutorial.setReviewTime(LocalDateTime.now());
        tutorial.setReviewComment(request.getReviewComment());
        tutorialMapper.updateById(tutorial);
        notifyResourceAuditResult(tutorial.getCreatorId(), "教程", tutorial.getTitle(), id, request.getStatus());
    }

    private void notifyResourceAuditResult(Long creatorId, String resourceType, String resourceName, Long resourceId, Integer status) {
        if (creatorId == null || status == null) return;
        if (status == 1) {
            notificationService.createRuleNotification(
                    creatorId, "RESOURCE_APPROVED", "资源审核通过",
                    "你的" + resourceType + "《" + resourceName + "》已审核通过。",
                    resourceId, "tutorial", "RESOURCE_APPROVED:" + resourceType + ":" + resourceId + ":" + status);
        } else if (status == 2) {
            notificationService.createRuleNotification(
                    creatorId, "RESOURCE_TAKEN_DOWN", "资源未通过或已下架",
                    "你的" + resourceType + "《" + resourceName + "》未通过审核或已被下架。",
                    resourceId, "tutorial", "RESOURCE_TAKEN_DOWN:" + resourceType + ":" + resourceId + ":" + status);
        }
    }

    @Override
    public Tutorial getTutorialById(Long id) {
        Tutorial tutorial = tutorialMapper.selectById(id);
        if (tutorial == null) throw new BusinessException(404, "教程不存在");
        tutorialMapper.incrementViewCount(id);
        return tutorial;
    }

    @Override
    @Transactional
    public String publishVersion(Long tutorialId, String changelog, Long userId) {
        Tutorial tutorial = getTutorialById(tutorialId);
        if (tutorial.getCreatorId() != null && !tutorial.getCreatorId().equals(userId)) {
            throw new BusinessException("只能为自己创建的教程发布版本");
        }

        // 生成新版本号
        String currentVersion = tutorial.getLatestVersion() != null ? tutorial.getLatestVersion() : "0.0.0";
        String newVersion = generateNextVersion(currentVersion);

        // 检查版本是否已存在
        if (versionMapper.existsByTutorialIdAndVersion(tutorialId, newVersion)) {
            throw new BusinessException("版本 " + newVersion + " 已存在");
        }

        // 解析版本号
        TutorialVersion.VersionParts parts = TutorialVersion.parseVersion(newVersion);

        // 创建版本记录
        TutorialVersion version = new TutorialVersion();
        version.setTutorialId(tutorialId);
        version.setVersion(newVersion);
        version.setMajor(parts.major());
        version.setMinor(parts.minor());
        version.setPatch(parts.patch());
        version.setVersionNumber(parts.toNumber());
        version.setChangelog(changelog);
        version.setCreatedBy(userId);
        version.setCreatedAt(LocalDateTime.now());
        versionMapper.insert(version);

        // 更新教程主表
        tutorial.setLatestVersionId(version.getId());
        tutorial.setLatestVersion(newVersion);
        tutorial.setTotalVersions((tutorial.getTotalVersions() != null ? tutorial.getTotalVersions() : 0) + 1);
        tutorialMapper.updateById(tutorial);

        return newVersion;
    }

    private String generateNextVersion(String currentVersion) {
        String[] parts = currentVersion.split("\\.");
        int major = Integer.parseInt(parts[0]);
        int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;

        // 自动递增修订版本号
        patch++;
        if (patch >= 10) {
            patch = 0;
            minor++;
            if (minor >= 10) {
                minor = 0;
                major++;
            }
        }

        return major + "." + minor + "." + patch;
    }

    @Override
    public List<TutorialVersionVO> getVersions(Long tutorialId) {
        List<TutorialVersion> versions = versionMapper.selectByTutorialId(tutorialId);
        return versions.stream().map(v -> {
            TutorialVersionVO vo = new TutorialVersionVO();
            vo.setId(v.getId());
            vo.setVersion(v.getVersion());
            vo.setChangelog(v.getChangelog());
            vo.setCreatedAt(v.getCreatedAt());
            return vo;
        }).collect(java.util.stream.Collectors.toList());
    }

    private void copyFromRequest(Tutorial tutorial, TutorialRequest request) {
        tutorial.setTitle(EmojiFilter.filter(request.getTitle()));
        tutorial.setDescription(EmojiFilter.filter(request.getDescription()));
        tutorial.setCategory(EmojiFilter.filter(request.getCategory()));
        tutorial.setCoverImage(EmojiFilter.filter(request.getCoverImage()));
        tutorial.setContentType(EmojiFilter.filter(request.getContentType()));
        tutorial.setContent(EmojiFilter.filter(request.getContent()));
        tutorial.setVideoUrl(EmojiFilter.filter(request.getVideoUrl()));
        tutorial.setZipFileUrl(EmojiFilter.filter(request.getZipFileUrl()));
        tutorial.setZipFileName(EmojiFilter.filter(request.getZipFileName()));
    }
}
