package com.aistudio.service.service;

import com.aistudio.service.dto.request.MemberOutputRequest;
import com.aistudio.service.dto.request.OpenOutputSubmitRequest;
import com.aistudio.service.entity.MemberOutput;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MemberOutputService {
    MemberOutput getTodayOutput(Long userId);
    void submitOutput(MemberOutputRequest request, Long userId);
    List<MemberOutput> getHistory(Long userId, LocalDate startDate, LocalDate endDate);
    List<MemberOutput> getHistory(Long userId, LocalDate startDate, LocalDate endDate, List<String> projectNames);
    List<Map<String, Object>> getAdminList(LocalDate date);
    List<Map<String, Object>> getAdminList(LocalDate date, List<Long> deptIds, List<String> projectNames);
    Map<String, Object> getStats(Long userId, LocalDate startDate, LocalDate endDate, boolean isAdmin);
    Map<String, Object> getStats(Long userId, LocalDate startDate, LocalDate endDate, boolean isAdmin, List<Long> deptIds, List<String> projectNames);

    // 开放 API 方法
    void submitOutputByUsername(OpenOutputSubmitRequest request);
    List<MemberOutput> getTodayOutputByUsername(String username);
    List<MemberOutput> getHistoryByUsername(String username, LocalDate startDate, LocalDate endDate);
}
