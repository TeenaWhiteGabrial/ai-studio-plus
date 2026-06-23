package com.aistudio.service.service;

import com.aistudio.service.dto.response.ActivityDailyReportResponse;
import com.aistudio.service.dto.response.ActivityWorkItemResponse;
import com.aistudio.service.dto.response.PageResult;

import java.time.LocalDate;
import java.util.Map;

public interface GitlabActivityConsoleService {
    PageResult<ActivityWorkItemResponse> listMyWorkItems(Long userId, int page, int size, LocalDate analysisDate);
    ActivityDailyReportResponse getMyDailyReport(Long userId, LocalDate reportDate);
    ActivityDailyReportResponse getProjectDailyReport(Long userId, Long projectId, LocalDate reportDate);
    Map<String, Object> getMyOverview(Long userId, LocalDate analysisDate);
}
