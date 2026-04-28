package com.aistudio.service.service;

import com.aistudio.service.dto.request.DailyTaskRequest;
import com.aistudio.service.dto.response.DailyTaskStatsVO;
import com.aistudio.service.entity.DailyTask;

import java.time.LocalDate;
import java.util.List;

public interface DailyTaskService {
    List<DailyTask> list(Long userId, LocalDate startDate, LocalDate endDate, String status);
    DailyTask create(Long userId, DailyTaskRequest request);
    DailyTask update(Long userId, Long id, DailyTaskRequest request);
    void delete(Long userId, Long id);
    DailyTaskStatsVO stats(Long userId);
}
