package com.aistudio.service.service;

import com.aistudio.service.entity.BrowseHistory;

import java.util.List;

public interface BrowseHistoryService {

    void addBrowseHistory(Long userId, String targetType, Long targetId);

    List<BrowseHistory> listBrowseHistories(Long userId);

    void clearBrowseHistories(Long userId);
}
