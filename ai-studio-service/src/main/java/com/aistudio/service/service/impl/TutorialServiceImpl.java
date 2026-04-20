package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.request.AuditRequest;
import com.aistudio.service.dto.request.TutorialRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Tutorial;
import com.aistudio.service.mapper.TutorialMapper;
import com.aistudio.service.service.OssService;
import com.aistudio.service.service.TutorialService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TutorialServiceImpl implements TutorialService {

    private final TutorialMapper tutorialMapper;
    private final OssService ossService;

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
    }

    @Override
    public Tutorial getTutorialById(Long id) {
        Tutorial tutorial = tutorialMapper.selectById(id);
        if (tutorial == null) throw new BusinessException(404, "教程不存在");
        tutorialMapper.incrementViewCount(id);
        return tutorial;
    }

    private void copyFromRequest(Tutorial tutorial, TutorialRequest request) {
        tutorial.setTitle(request.getTitle());
        tutorial.setDescription(request.getDescription());
        tutorial.setCategory(request.getCategory());
        tutorial.setCoverImage(request.getCoverImage());
        tutorial.setContentType(request.getContentType());
        tutorial.setContent(request.getContent());
        tutorial.setVideoUrl(request.getVideoUrl());
        tutorial.setZipFileUrl(request.getZipFileUrl());
        tutorial.setZipFileName(request.getZipFileName());
    }
}
