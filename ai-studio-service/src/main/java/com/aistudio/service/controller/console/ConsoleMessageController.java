package com.aistudio.service.controller.console;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Notification;
import com.aistudio.service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/console/message")
@RequiredArgsConstructor
public class ConsoleMessageController {

    private final NotificationService notificationService;
    private final SecurityUtils securityUtils;

    @GetMapping("/summary")
    public Result<Map<String, Object>> summary() {
        return Result.success(notificationService.getConsoleUnreadSummary(securityUtils.getCurrentUserId()));
    }

    @GetMapping("/notifications")
    public Result<PageResult<Notification>> notifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer isRead) {
        return Result.success(notificationService.pageNotifications(securityUtils.getCurrentUserId(), null, isRead, page, size));
    }

    @PostMapping("/notifications/{id}/read")
    public Result<Void> markNotificationRead(@PathVariable Long id) {
        notificationService.markAsRead(id, securityUtils.getCurrentUserId());
        return Result.success();
    }

    @PostMapping("/notifications/read-all")
    public Result<Void> markAllNotificationsRead() {
        notificationService.markAllNotificationsAsRead(securityUtils.getCurrentUserId());
        return Result.success();
    }

    @GetMapping("/announcements")
    public Result<List<Map<String, Object>>> announcements() {
        return Result.success(notificationService.listConsoleAnnouncements(securityUtils.getCurrentUserId()));
    }

    @GetMapping("/announcements/{id}")
    public Result<Map<String, Object>> announcementDetail(@PathVariable Long id) {
        return Result.success(notificationService.getConsoleAnnouncementDetail(id, securityUtils.getCurrentUserId()));
    }

    @PostMapping("/announcements/{id}/read")
    public Result<Void> markAnnouncementRead(@PathVariable Long id) {
        notificationService.markAnnouncementAsRead(id, securityUtils.getCurrentUserId());
        return Result.success();
    }
}
