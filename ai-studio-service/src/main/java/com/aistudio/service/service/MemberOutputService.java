package com.aistudio.service.service;

import com.aistudio.service.dto.request.OpenOutputSubmitRequest;
import com.aistudio.service.entity.MemberOutput;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MemberOutputService {
    MemberOutput getTodayOutput(Long userId);
    List<MemberOutput> getHistory(Long userId, LocalDate startDate, LocalDate endDate);
    List<MemberOutput> getHistory(Long userId, LocalDate startDate, LocalDate endDate, List<String> projectNames);

    // 管理员多维度查询
    List<Map<String, Object>> getOutputList(LocalDate date, List<Long> userIds, List<Long> deptIds, List<String> projectNames);
    List<Map<String, Object>> getOutputByUsers(List<Long> userIds, LocalDate startDate, LocalDate endDate, List<String> projectNames);
    Map<String, Object> getStats(Long userId, LocalDate startDate, LocalDate endDate, boolean isAdmin, List<Long> userIds, List<Long> deptIds, List<String> projectNames);
    List<Map<String, Object>> getDashboardDetails(LocalDate startDate, LocalDate endDate, List<Long> userIds, List<Long> deptIds, List<Long> teamIds, List<String> projectNames);
    List<Map<String, Object>> getOutputByDepartment(LocalDate startDate, LocalDate endDate, List<Long> deptIds);
    List<Map<String, Object>> getOutputByProject(LocalDate startDate, LocalDate endDate, List<String> projectNames);
    List<Map<String, Object>> getProjectMembers(String projectName, LocalDate startDate, LocalDate endDate);

    // 开放 API 方法
    void submitOutputByUsername(OpenOutputSubmitRequest request);
}
