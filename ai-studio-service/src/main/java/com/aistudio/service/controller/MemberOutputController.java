package com.aistudio.service.controller;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.request.MemberOutputRequest;
import com.aistudio.service.entity.MemberOutput;
import com.aistudio.service.service.MemberOutputService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Tag(name = "成员产出统计")
@RestController
@RequestMapping("/api/output")
@RequiredArgsConstructor
public class MemberOutputController {

    private final MemberOutputService memberOutputService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "查询今日产出")
    @GetMapping("/today")
    public Result<MemberOutput> today() {
        return Result.success(memberOutputService.getTodayOutput(securityUtils.getCurrentUserId()));
    }

    @Operation(summary = "提交今日产出")
    @PostMapping
    public Result<Void> submit(@Valid @RequestBody MemberOutputRequest request) {
        memberOutputService.submitOutput(request, securityUtils.getCurrentUserId());
        return Result.success();
    }

    @Operation(summary = "查询个人历史产出")
    @GetMapping("/history")
    public Result<List<MemberOutput>> history(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "项目名称列表") @RequestParam(required = false) List<String> projectNames) {
        return Result.success(memberOutputService.getHistory(
                securityUtils.getCurrentUserId(), startDate, endDate, projectNames));
    }

    @Operation(summary = "管理员查询全员产出")
    @GetMapping("/admin/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public Result<List<Map<String, Object>>> adminList(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "部门ID列表") @RequestParam(required = false) List<Long> deptIds,
            @Parameter(description = "项目名称列表") @RequestParam(required = false) List<String> projectNames) {
        return Result.success(memberOutputService.getAdminList(date, deptIds, projectNames));
    }

    @Operation(summary = "产出汇总统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "部门ID列表") @RequestParam(required = false) List<Long> deptIds,
            @Parameter(description = "项目名称列表") @RequestParam(required = false) List<String> projectNames) {
        boolean isAdmin = securityUtils.isAdmin();
        return Result.success(memberOutputService.getStats(
                securityUtils.getCurrentUserId(), startDate, endDate, isAdmin, deptIds, projectNames));
    }
}
