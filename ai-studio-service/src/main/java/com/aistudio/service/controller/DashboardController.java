package com.aistudio.service.controller;

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

@Tag(name = "数据看板")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "概览数据")
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        return Result.success(dashboardService.getOverview());
    }

    @Operation(summary = "产出趋势")
    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> trend(
            @RequestParam(defaultValue = "day") String granularity,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(dashboardService.getTrend(granularity, startDate, endDate));
    }

    @Operation(summary = "成员排行榜")
    @GetMapping("/ranking")
    public Result<List<Map<String, Object>>> ranking(
            @RequestParam(defaultValue = "week") String period,
            @RequestParam(defaultValue = "10") int topN) {
        return Result.success(dashboardService.getRanking(period, topN));
    }

    @Operation(summary = "详细统计（管理员）")
    @GetMapping("/detail")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public Result<List<Map<String, Object>>> detail(
            @RequestParam(defaultValue = "user") String groupBy,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(dashboardService.getDetail(groupBy, startDate, endDate));
    }
}
