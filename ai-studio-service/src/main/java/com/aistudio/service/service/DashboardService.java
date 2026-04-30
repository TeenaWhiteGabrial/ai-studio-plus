package com.aistudio.service.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DashboardService {
    Map<String, Object> getOverview();
    List<Map<String, Object>> getTrend(String granularity, LocalDate startDate, LocalDate endDate);
    List<Map<String, Object>> getRanking(String period, int topN);
    List<Map<String, Object>> getDetail(String groupBy, LocalDate startDate, LocalDate endDate);
    Map<String, Object> getPortalOverview();
    List<Map<String, Object>> getPortalTrend(LocalDate startDate, LocalDate endDate);
    List<Map<String, Object>> getPortalRanking(String type, int topN);
}
