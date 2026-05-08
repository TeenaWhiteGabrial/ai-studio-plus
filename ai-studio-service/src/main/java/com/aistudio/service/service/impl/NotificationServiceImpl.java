package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Announcement;
import com.aistudio.service.entity.AnnouncementRead;
import com.aistudio.service.entity.NotificationRule;
import com.aistudio.service.entity.NotificationRuleGroup;
import com.aistudio.service.entity.UserNotificationRuleGroup;
import com.aistudio.service.mapper.AnnouncementMapper;
import com.aistudio.service.mapper.AnnouncementReadMapper;
import com.aistudio.service.entity.Notification;
import com.aistudio.service.mapper.NotificationMapper;
import com.aistudio.service.mapper.NotificationRuleGroupMapper;
import com.aistudio.service.mapper.NotificationRuleMapper;
import com.aistudio.service.mapper.UserNotificationRuleGroupMapper;
import com.aistudio.service.service.NotificationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final AnnouncementMapper announcementMapper;
    private final AnnouncementReadMapper announcementReadMapper;
    private final NotificationRuleGroupMapper ruleGroupMapper;
    private final NotificationRuleMapper ruleMapper;
    private final UserNotificationRuleGroupMapper userRuleGroupMapper;

    @Override
    @Transactional
    public void createNotification(Long userId, String type, String content, Long sourceId, String sourceType) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(type);
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
                .set(Notification::getIsRead, 1)
                .set(Notification::getReadAt, LocalDateTime.now()));
        log.info("标记已读: notificationId={}, userId={}", notificationId, userId);
    }

    @Override
    public int getUnreadCount(Long userId) {
        LambdaQueryWrapper<Notification> q = new LambdaQueryWrapper<>();
        q.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0);
        return notificationMapper.selectCount(q).intValue();
    }

    @Override
    @Transactional
    public void createRuleNotification(Long userId, String ruleType, String title, String content,
                                       Long sourceId, String sourceType, String dedupeKey) {
        if (userId == null || !isRuleEnabled(userId, ruleType)) {
            return;
        }
        if (dedupeKey != null && notificationMapper.exists(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getDedupeKey, dedupeKey))) {
            return;
        }
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(ruleType);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setSourceId(sourceId);
        notification.setSourceType(sourceType);
        notification.setDedupeKey(dedupeKey);
        notification.setIsRead(0);
        notification.setCreatedAt(LocalDateTime.now());
        notificationMapper.insert(notification);
        log.info("创建规则通知: userId={}, ruleType={}, sourceType={}, sourceId={}", userId, ruleType, sourceType, sourceId);
    }

    @Override
    public PageResult<Notification> pageNotifications(Long userId, String type, Integer isRead, int page, int size) {
        LambdaQueryWrapper<Notification> q = new LambdaQueryWrapper<>();
        if (userId != null) q.eq(Notification::getUserId, userId);
        if (type != null && !type.isBlank()) q.eq(Notification::getType, type);
        if (isRead != null) q.eq(Notification::getIsRead, isRead);
        q.orderByDesc(Notification::getCreatedAt);
        Page<Notification> result = notificationMapper.selectPage(new Page<>(page, size), q);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    public Map<String, Object> getConsoleUnreadSummary(Long userId) {
        int notificationCount = getUnreadCount(userId);
        int announcementCount = countUnreadAnnouncements(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("notificationUnread", notificationCount);
        result.put("announcementUnread", announcementCount);
        result.put("totalUnread", notificationCount + announcementCount);
        return result;
    }

    @Override
    public List<Map<String, Object>> listConsoleAnnouncements(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        List<Announcement> announcements = announcementMapper.selectList(new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getStatus, "PUBLISHED")
                .and(q -> q.isNull(Announcement::getExpiredAt).or().gt(Announcement::getExpiredAt, now))
                .orderByDesc(Announcement::getPinned)
                .orderByDesc(Announcement::getPublishedAt)
                .last("LIMIT 100"));
        return announcements.stream().map(item -> {
            boolean read = announcementReadMapper.exists(new LambdaQueryWrapper<AnnouncementRead>()
                    .eq(AnnouncementRead::getAnnouncementId, item.getId())
                    .eq(AnnouncementRead::getUserId, userId));
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("title", item.getTitle());
            map.put("content", item.getContent());
            map.put("pinned", item.getPinned());
            map.put("publishedAt", item.getPublishedAt());
            map.put("expiredAt", item.getExpiredAt());
            map.put("read", read);
            return map;
        }).toList();
    }

    @Override
    public Map<String, Object> getConsoleAnnouncementDetail(Long announcementId, Long userId) {
        Announcement item = announcementMapper.selectById(announcementId);
        LocalDateTime now = LocalDateTime.now();
        if (item == null
                || !"PUBLISHED".equals(item.getStatus())
                || (item.getExpiredAt() != null && !item.getExpiredAt().isAfter(now))) {
            throw new BusinessException(404, "公告不存在或已下线");
        }
        boolean read = announcementReadMapper.exists(new LambdaQueryWrapper<AnnouncementRead>()
                .eq(AnnouncementRead::getAnnouncementId, item.getId())
                .eq(AnnouncementRead::getUserId, userId));
        Map<String, Object> map = new HashMap<>();
        map.put("id", item.getId());
        map.put("title", item.getTitle());
        map.put("content", item.getContent());
        map.put("pinned", item.getPinned());
        map.put("publishedAt", item.getPublishedAt());
        map.put("expiredAt", item.getExpiredAt());
        map.put("read", read);
        return map;
    }

    @Override
    @Transactional
    public void markAllNotificationsAsRead(Long userId) {
        notificationMapper.update(null, new LambdaUpdateWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0)
                .set(Notification::getIsRead, 1)
                .set(Notification::getReadAt, LocalDateTime.now()));
    }

    @Override
    @Transactional
    public void markAnnouncementAsRead(Long announcementId, Long userId) {
        if (announcementReadMapper.exists(new LambdaQueryWrapper<AnnouncementRead>()
                .eq(AnnouncementRead::getAnnouncementId, announcementId)
                .eq(AnnouncementRead::getUserId, userId))) {
            return;
        }
        AnnouncementRead read = new AnnouncementRead();
        read.setAnnouncementId(announcementId);
        read.setUserId(userId);
        read.setReadAt(LocalDateTime.now());
        announcementReadMapper.insert(read);
    }

    private boolean isRuleEnabled(Long userId, String ruleType) {
        Long groupId = resolveUserGroupId(userId);
        if (groupId == null) return false;
        NotificationRule rule = ruleMapper.selectOne(new LambdaQueryWrapper<NotificationRule>()
                .eq(NotificationRule::getGroupId, groupId)
                .eq(NotificationRule::getRuleType, ruleType));
        return rule != null && Integer.valueOf(1).equals(rule.getEnabled());
    }

    private Long resolveUserGroupId(Long userId) {
        UserNotificationRuleGroup binding = userRuleGroupMapper.selectOne(new LambdaQueryWrapper<UserNotificationRuleGroup>()
                .eq(UserNotificationRuleGroup::getUserId, userId));
        if (binding != null) return binding.getGroupId();
        NotificationRuleGroup defaultGroup = ruleGroupMapper.selectOne(new LambdaQueryWrapper<NotificationRuleGroup>()
                .eq(NotificationRuleGroup::getIsDefault, 1)
                .last("LIMIT 1"));
        if (defaultGroup == null) return null;
        UserNotificationRuleGroup next = new UserNotificationRuleGroup();
        next.setUserId(userId);
        next.setGroupId(defaultGroup.getId());
        userRuleGroupMapper.insert(next);
        return defaultGroup.getId();
    }

    private int countUnreadAnnouncements(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        List<Announcement> announcements = announcementMapper.selectList(new LambdaQueryWrapper<Announcement>()
                .select(Announcement::getId)
                .eq(Announcement::getStatus, "PUBLISHED")
                .and(q -> q.isNull(Announcement::getExpiredAt).or().gt(Announcement::getExpiredAt, now)));
        if (announcements.isEmpty()) return 0;
        List<Long> ids = announcements.stream().map(Announcement::getId).toList();
        Long readCount = announcementReadMapper.selectCount(new LambdaQueryWrapper<AnnouncementRead>()
                .eq(AnnouncementRead::getUserId, userId)
                .in(AnnouncementRead::getAnnouncementId, ids));
        return Math.max(0, ids.size() - readCount.intValue());
    }
}
