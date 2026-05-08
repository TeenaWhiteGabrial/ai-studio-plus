package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Announcement;
import com.aistudio.service.mapper.AnnouncementMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/announcement")
@RequiredArgsConstructor
public class AdminAnnouncementController {

    private final AnnouncementMapper announcementMapper;
    private final SecurityUtils securityUtils;

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<PageResult<Announcement>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        LambdaQueryWrapper<Announcement> q = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            q.like(Announcement::getTitle, keyword);
        }
        if (status != null && !status.isBlank()) {
            q.eq(Announcement::getStatus, status);
        }
        q.orderByDesc(Announcement::getPinned).orderByDesc(Announcement::getCreatedAt);
        Page<Announcement> result = announcementMapper.selectPage(new Page<>(page, size), q);
        return Result.success(PageResult.of(result.getTotal(), result.getRecords()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Long> create(@RequestBody Announcement request) {
        request.setId(null);
        request.setPublisherId(securityUtils.getCurrentUserId());
        request.setStatus(request.getStatus() == null || request.getStatus().isBlank() ? "DRAFT" : request.getStatus());
        if ("PUBLISHED".equals(request.getStatus()) && request.getPublishedAt() == null) {
            request.setPublishedAt(LocalDateTime.now());
        }
        announcementMapper.insert(request);
        return Result.success(request.getId());
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody Announcement request) {
        request.setId(id);
        if ("PUBLISHED".equals(request.getStatus()) && request.getPublishedAt() == null) {
            request.setPublishedAt(LocalDateTime.now());
        }
        announcementMapper.updateById(request);
        return Result.success();
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Void> publish(@PathVariable Long id) {
        Announcement item = announcementMapper.selectById(id);
        item.setStatus("PUBLISHED");
        item.setPublishedAt(LocalDateTime.now());
        announcementMapper.updateById(item);
        return Result.success();
    }

    @PostMapping("/{id}/offline")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Void> offline(@PathVariable Long id) {
        Announcement item = announcementMapper.selectById(id);
        item.setStatus("OFFLINE");
        announcementMapper.updateById(item);
        return Result.success();
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        announcementMapper.deleteById(id);
        return Result.success();
    }
}
