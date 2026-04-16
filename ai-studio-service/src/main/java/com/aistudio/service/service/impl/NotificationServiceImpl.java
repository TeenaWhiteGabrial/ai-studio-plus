package com.aistudio.service.service.impl;

import com.aistudio.service.entity.Notification;
import com.aistudio.service.mapper.NotificationMapper;
import com.aistudio.service.service.NotificationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public void createNotification(Long userId, String type, String content, Long sourceId, String sourceType) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setContent(content);
        notification.setSourceId(sourceId);
        notification.setSourceType(sourceType);
        notification.setIsRead(0);
        notification.setCreatedAt(LocalDateTime.now());
        notificationMapper.insert(notification);
        log.info("创建通知: userId={}, type={}", userId, type);
    }

    @Override
    public List<Notification> listNotifications(Long userId) {
        LambdaQueryWrapper<Notification> q = new LambdaQueryWrapper<>();
        q.eq(Notification::getUserId, userId)
                .orderByDesc(Notification::getCreatedAt)
                .last("LIMIT 50");
        return notificationMapper.selectList(q);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        notificationMapper.update(null, new LambdaUpdateWrapper<Notification>()
                .eq(Notification::getId, notificationId)
                .eq(Notification::getUserId, userId)
                .set(Notification::getIsRead, 1));
        log.info("标记已读: notificationId={}, userId={}", notificationId, userId);
    }

    @Override
    public int getUnreadCount(Long userId) {
        LambdaQueryWrapper<Notification> q = new LambdaQueryWrapper<>();
        q.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0);
        return notificationMapper.selectCount(q).intValue();
    }
}
