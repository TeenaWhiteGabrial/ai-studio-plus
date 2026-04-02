// ========================================================
// AI生成标记 [Claude Code]
// 生成时间: 2026-03-31
// 文件功能: 项目统计控制器，提供项目产出汇总、成员明细等接口
// 修改历史:
//   - 2026-03-31: 创建文件，实现项目维度统计功能
// ========================================================
package com.aistudio.service.controller;

import com.aistudio.service.common.Result;
import com.aistudio.service.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Tag(name = "项目统计")
@RestController
@RequestMapping("/api/output/stats/project")
@RequiredArgsConstructor
public class ProjectStatsController {

    private final StatsService statsService;

    @Operation(summary = "项目整体产出汇总")
    @GetMapping("/summary")
    public Result<List<Map<String, Object>>> summary(
            @Parameter(description = "项目名称列表") @RequestParam(required = false) List<String> projectNames,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(statsService.getProjectSummary(projectNames, startDate, endDate));
    }

    @Operation(summary = "项目内成员产出明细")
    @GetMapping("/members")
    public Result<List<Map<String, Object>>> members(
            @Parameter(description = "项目名称") @RequestParam String projectName,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(statsService.getProjectMembers(projectName, startDate, endDate));
    }

    @Operation(summary = "个人项目产出分布")
    @GetMapping("/user-distribution")
    public Result<List<Map<String, Object>>> userDistribution(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(statsService.getUserProjectDistribution(userId, startDate, endDate));
    }
}
