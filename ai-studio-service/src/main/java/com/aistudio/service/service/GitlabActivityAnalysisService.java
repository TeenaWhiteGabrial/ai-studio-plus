package com.aistudio.service.service;

import java.time.LocalDate;

public interface GitlabActivityAnalysisService {
    void analyzeUserProject(Long userId, Long gitlabProjectId, LocalDate analysisDate);
    void generateDailyReport(Long userId, Long projectId, Long statGroupId, LocalDate reportDate);
    void analyzeUserProject(Long userId, Long gitlabProjectId, LocalDate analysisDate, Long eventLogId);
    void generateDailyReport(Long userId, Long projectId, Long statGroupId, LocalDate reportDate, Long eventLogId);
}
