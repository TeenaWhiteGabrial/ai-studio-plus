package com.aistudio.service.service;

import com.aistudio.service.entity.Notification;
import com.aistudio.service.dto.response.PageResult;

import java.util.List;
import java.util.Map;

public interface NotificationService {

    void createNotification(Long userId, String type, String content, Long sourceId, String sourceType);

    List<Notification> listNotifications(Long userId);

    void markAsRead(Long notificationId, Long userId);

    int getUnreadCount(Long userId);

    void createRuleNotification(Long userId, String ruleType, String title, String content,
                                Long sourceId, String sourceType, String dedupeKey);

    PageResult<Notification> pageNotifications(Long userId, String type, Integer isRead, int page, int size);

    Map<String, Object> getConsoleUnreadSummary(Long userId);

    List<Map<String, Object>> listConsoleAnnouncements(Long userId);

    Map<String, Object> getConsoleAnnouncementDetail(Long announcementId, Long userId);

    void markAllNotificationsAsRead(Long userId);

    void markAnnouncementAsRead(Long announcementId, Long userId);
}
