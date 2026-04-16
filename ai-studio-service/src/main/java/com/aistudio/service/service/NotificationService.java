package com.aistudio.service.service;

import com.aistudio.service.entity.Notification;

import java.util.List;

public interface NotificationService {

    void createNotification(Long userId, String type, String content, Long sourceId, String sourceType);

    List<Notification> listNotifications(Long userId);

    void markAsRead(Long notificationId, Long userId);

    int getUnreadCount(Long userId);
}
