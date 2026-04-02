// ========================================================
// AI生成标记 [Claude Code]
// 生成时间: 2026-03-31
// 文件功能: 部门统计控制器，提供部门产出汇总、成员明细、排行等接口
// 修改历史:
//   - 2026-03-31: 创建文件，实现部门维度统计功能
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

@Tag(name = "部门统计")
@RestController
@RequestMapping("/api/output/stats/department")
@RequiredArgsConstructor
public class DepartmentStatsController {

    private final StatsService statsService;

    @Operation(summary = "部门整体产出汇总")
    @GetMapping("/summary")
    public Result<List<Map<String, Object>>> summary(
            @Parameter(description = "部门ID列表") @RequestParam(required = false) List<Long> deptIds,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(statsService.getDepartmentSummary(deptIds, startDate, endDate));
    }

    @Operation(summary = "部门内成员产出明细")
    @GetMapping("/members")
    public Result<List<Map<String, Object>>> members(
            @Parameter(description = "部门ID") @RequestParam Long deptId,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(statsService.getDepartmentMembers(deptId, startDate, endDate));
    }

    @Operation(summary = "部门产出排行")
    @GetMapping("/ranking")
    public Result<List<Map<String, Object>>> ranking(
            @Parameter(description = "部门ID列表") @RequestParam(required = false) List<Long> deptIds,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "排序指标") @RequestParam(defaultValue = "total_code_lines") String sortBy) {
        return Result.success(statsService.getDepartmentRanking(deptIds, startDate, endDate, sortBy));
    }
}
