package com.aistudio.service.service.impl;

import com.aistudio.service.entity.BrowseHistory;
import com.aistudio.service.mapper.BrowseHistoryMapper;
import com.aistudio.service.service.BrowseHistoryService;
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
public class BrowseHistoryServiceImpl implements BrowseHistoryService {

    private final BrowseHistoryMapper browseHistoryMapper;

    @Override
    @Transactional
    public void addBrowseHistory(Long userId, String targetType, Long targetId) {
        // 先删除同用户同目标的记录（去重）
        LambdaQueryWrapper<BrowseHistory> q = new LambdaQueryWrapper<>();
        q.eq(BrowseHistory::getUserId, userId)
                .eq(BrowseHistory::getTargetType, targetType)
                .eq(BrowseHistory::getTargetId, targetId);
        browseHistoryMapper.delete(q);

        // 插入新记录
        BrowseHistory history = new BrowseHistory();
        history.setUserId(userId);
        history.setTargetType(targetType);
        history.setTargetId(targetId);
        history.setCreatedAt(LocalDateTime.now());
        browseHistoryMapper.insert(history);

        log.info("添加浏览记录: userId={}, targetType={}, targetId={}", userId, targetType, targetId);
    }

    @Override
    public List<BrowseHistory> listBrowseHistories(Long userId) {
        LambdaQueryWrapper<BrowseHistory> q = new LambdaQueryWrapper<>();
        q.eq(BrowseHistory::getUserId, userId)
                .orderByDesc(BrowseHistory::getCreatedAt)
                .last("LIMIT 100");
        return browseHistoryMapper.selectList(q);
    }

    @Override
    @Transactional
    public void clearBrowseHistories(Long userId) {
        LambdaQueryWrapper<BrowseHistory> q = new LambdaQueryWrapper<>();
        q.eq(BrowseHistory::getUserId, userId);
        browseHistoryMapper.delete(q);
        log.info("清空浏览记录: userId={}", userId);
    }
}
