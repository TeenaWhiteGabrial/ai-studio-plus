package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Tag(name = "Admin - 产出统计")
@RestController
@RequestMapping("/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final StatsService statsService;

    // ========== 项目统计 ==========

    @Operation(summary = "项目整体产出汇总")
    @GetMapping("/project/summary")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<List<Map<String, Object>>> projectSummary(
            @Parameter(description = "项目名称列表") @RequestParam(required = false) List<String> projectNames,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(statsService.getProjectSummary(projectNames, startDate, endDate));
    }

    @Operation(summary = "项目内成员产出明细")
    @GetMapping("/project/members")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<List<Map<String, Object>>> projectMembers(
            @Parameter(description = "项目名称") @RequestParam String projectName,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(statsService.getProjectMembers(projectName, startDate, endDate));
    }

    @Operation(summary = "个人项目产出分布")
    @GetMapping("/project/user-distribution")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<List<Map<String, Object>>> userDistribution(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(statsService.getUserProjectDistribution(userId, startDate, endDate));
    }

    // ========== 部门统计 ==========

    @Operation(summary = "部门整体产出汇总")
    @GetMapping("/department/summary")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<List<Map<String, Object>>> departmentSummary(
            @Parameter(description = "部门ID列表") @RequestParam(required = false) List<Long> deptIds,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(statsService.getDepartmentSummary(deptIds, startDate, endDate));
    }

    @Operation(summary = "部门内成员产出明细")
    @GetMapping("/department/members")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<List<Map<String, Object>>> departmentMembers(
            @Parameter(description = "部门ID") @RequestParam Long deptId,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(statsService.getDepartmentMembers(deptId, startDate, endDate));
    }

    @Operation(summary = "部门产出排行")
    @GetMapping("/department/ranking")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<List<Map<String, Object>>> departmentRanking(
            @Parameter(description = "部门ID列表") @RequestParam(required = false) List<Long> deptIds,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "排序指标") @RequestParam(defaultValue = "total_code_lines") String sortBy) {
        return Result.success(statsService.getDepartmentRanking(deptIds, startDate, endDate, sortBy));
    }
}
