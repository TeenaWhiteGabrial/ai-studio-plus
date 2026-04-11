// ========================================================
// AI生成标记 [Claude Code]
// 生成时间: 2026-03-31
// 文件功能: 统计服务接口，定义部门和项目统计方法
// 修改历史:
//   - 2026-03-31: 创建文件，定义统计服务接口
// ========================================================
package com.aistudio.service.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StatsService {

    /**
     * 部门整体产出汇总
     */
    List<Map<String, Object>> getDepartmentSummary(List<Long> deptIds, LocalDate startDate, LocalDate endDate);

    /**
     * 部门内成员产出明细
     */
    List<Map<String, Object>> getDepartmentMembers(Long deptId, LocalDate startDate, LocalDate endDate);

    /**
     * 部门产出排行
     */
    List<Map<String, Object>> getDepartmentRanking(List<Long> deptIds, LocalDate startDate, LocalDate endDate, String sortBy);

    /**
     * 项目整体产出汇总
     */
    List<Map<String, Object>> getProjectSummary(List<String> projectNames, LocalDate startDate, LocalDate endDate);

    /**
     * 项目内成员产出明细
     */
    List<Map<String, Object>> getProjectMembers(String projectName, LocalDate startDate, LocalDate endDate);

    /**
     * 个人项目产出分布
     */
    List<Map<String, Object>> getUserProjectDistribution(Long userId, LocalDate startDate, LocalDate endDate);
}
