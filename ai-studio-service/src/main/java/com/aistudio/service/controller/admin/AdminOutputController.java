package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.entity.MemberOutput;
import com.aistudio.service.service.MemberOutputService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Tag(name = "Admin - Output Management")
@RestController
@RequestMapping("/admin/output")
@RequiredArgsConstructor
public class AdminOutputController {

    private final MemberOutputService memberOutputService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "Query current user's today output")
    @GetMapping("/today")
    public Result<MemberOutput> today() {
        return Result.success(memberOutputService.getTodayOutput(securityUtils.getCurrentUserId()));
    }

    @Operation(summary = "Query current user's output history")
    @GetMapping("/history")
    public Result<List<MemberOutput>> history(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Project root names") @RequestParam(required = false) List<String> projectNames) {
        return Result.success(memberOutputService.getHistory(
                securityUtils.getCurrentUserId(), startDate, endDate, projectNames));
    }

    @Operation(summary = "Query output details")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<List<Map<String, Object>>> list(
            @Parameter(description = "Statistic date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "User IDs") @RequestParam(required = false) List<Long> userIds,
            @Parameter(description = "Department IDs") @RequestParam(required = false) List<Long> deptIds,
            @Parameter(description = "Project root names") @RequestParam(required = false) List<String> projectNames) {
        return Result.success(memberOutputService.getOutputList(date, userIds, deptIds, projectNames));
    }

    @Operation(summary = "Query output summary by users")
    @GetMapping("/by-users")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<List<Map<String, Object>>> byUsers(
            @Parameter(description = "User IDs") @RequestParam(required = false) List<Long> userIds,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Project root names") @RequestParam(required = false) List<String> projectNames) {
        return Result.success(memberOutputService.getOutputByUsers(userIds, startDate, endDate, projectNames));
    }

    @Operation(summary = "Query output aggregate stats")
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<Map<String, Object>> stats(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "User IDs") @RequestParam(required = false) List<Long> userIds,
            @Parameter(description = "Department IDs") @RequestParam(required = false) List<Long> deptIds,
            @Parameter(description = "Project root names") @RequestParam(required = false) List<String> projectNames) {
        return Result.success(memberOutputService.getStats(
                securityUtils.getCurrentUserId(), startDate, endDate, securityUtils.isAdmin(), userIds, deptIds, projectNames));
    }

    @Operation(summary = "Query output dashboard details")
    @GetMapping("/dashboard-details")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<List<Map<String, Object>>> dashboardDetails(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "User IDs") @RequestParam(required = false) List<Long> userIds,
            @Parameter(description = "Department IDs") @RequestParam(required = false) List<Long> deptIds,
            @Parameter(description = "Team IDs") @RequestParam(required = false) List<Long> teamIds,
            @Parameter(description = "Project root names") @RequestParam(required = false) List<String> projectNames) {
        return Result.success(memberOutputService.getDashboardDetails(startDate, endDate, userIds, deptIds, teamIds, projectNames));
    }

    @Operation(summary = "Query output summary by department")
    @GetMapping("/by-department")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<List<Map<String, Object>>> byDepartment(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Department IDs") @RequestParam(required = false) List<Long> deptIds) {
        return Result.success(memberOutputService.getOutputByDepartment(startDate, endDate, deptIds));
    }

    @Operation(summary = "Query output summary by project")
    @GetMapping("/by-project")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<List<Map<String, Object>>> byProject(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Project root names") @RequestParam(required = false) List<String> projectNames) {
        return Result.success(memberOutputService.getOutputByProject(startDate, endDate, projectNames));
    }

    @Operation(summary = "Query project members output")
    @GetMapping("/project-members")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<List<Map<String, Object>>> projectMembers(
            @Parameter(description = "Project root name") @RequestParam String projectName,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(memberOutputService.getProjectMembers(projectName, startDate, endDate));
    }
}
