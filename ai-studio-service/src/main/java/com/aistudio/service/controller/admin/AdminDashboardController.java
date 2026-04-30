package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Tag(name = "Admin - 数据看板")
@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "概览数据")
    @GetMapping("/overview")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<Map<String, Object>> overview() {
        return Result.success(dashboardService.getOverview());
    }

    @Operation(summary = "产出趋势")
    @GetMapping("/trend")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<List<Map<String, Object>>> trend(
            @RequestParam(defaultValue = "day") String granularity,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(dashboardService.getTrend(granularity, startDate, endDate));
    }

    @Operation(summary = "成员排行榜")
    @GetMapping("/ranking")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<List<Map<String, Object>>> ranking(
            @RequestParam(defaultValue = "week") String period,
            @RequestParam(defaultValue = "10") int topN) {
        return Result.success(dashboardService.getRanking(period, topN));
    }

    @Operation(summary = "详细统计")
    @GetMapping("/detail")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<List<Map<String, Object>>> detail(
            @RequestParam(defaultValue = "user") String groupBy,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(dashboardService.getDetail(groupBy, startDate, endDate));
    }

    @Operation(summary = "Portal 网站概览")
    @GetMapping("/portal-overview")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<Map<String, Object>> portalOverview() {
        return Result.success(dashboardService.getPortalOverview());
    }

    @Operation(summary = "Portal 网站趋势")
    @GetMapping("/portal-trend")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<List<Map<String, Object>>> portalTrend(
            @RequestParam(name = "start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(dashboardService.getPortalTrend(startDate, endDate));
    }

    @Operation(summary = "Portal 网站内容排行")
    @GetMapping("/portal-ranking")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<List<Map<String, Object>>> portalRanking(
            @RequestParam(defaultValue = "article") String type,
            @RequestParam(name = "top_n", defaultValue = "10") int topN) {
        return Result.success(dashboardService.getPortalRanking(type, topN));
    }
}
