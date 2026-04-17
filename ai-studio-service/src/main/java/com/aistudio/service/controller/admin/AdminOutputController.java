package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.service.MemberOutputService;
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

@Tag(name = "Admin - 产出管理")
@RestController
@RequestMapping("/admin/output")
@RequiredArgsConstructor
public class AdminOutputController {

    private final MemberOutputService memberOutputService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "查询产出明细（多维度筛选）")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<List<Map<String, Object>>> list(
            @Parameter(description = "统计日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "用户ID列表（为空则查全员）") @RequestParam(required = false) List<Long> userIds,
            @Parameter(description = "部门ID列表") @RequestParam(required = false) List<Long> deptIds,
            @Parameter(description = "项目名称列表") @RequestParam(required = false) List<String> projectNames) {
        return Result.success(memberOutputService.getOutputList(date, userIds, deptIds, projectNames));
    }

    @Operation(summary = "按人员查询产出汇总")
    @GetMapping("/by-users")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<List<Map<String, Object>>> byUsers(
            @Parameter(description = "用户ID列表") @RequestParam(required = false) List<Long> userIds,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "项目名称列表") @RequestParam(required = false) List<String> projectNames) {
        return Result.success(memberOutputService.getOutputByUsers(userIds, startDate, endDate, projectNames));
    }

    @Operation(summary = "产出汇总统计")
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<Map<String, Object>> stats(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "用户ID列表") @RequestParam(required = false) List<Long> userIds,
            @Parameter(description = "部门ID列表") @RequestParam(required = false) List<Long> deptIds,
            @Parameter(description = "项目名称列表") @RequestParam(required = false) List<String> projectNames) {
        boolean isAdmin = securityUtils.isAdmin();
        return Result.success(memberOutputService.getStats(
                securityUtils.getCurrentUserId(), startDate, endDate, isAdmin, userIds, deptIds, projectNames));
    }

    @Operation(summary = "按部门汇总产出")
    @GetMapping("/by-department")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<List<Map<String, Object>>> byDepartment(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "部门ID列表") @RequestParam(required = false) List<Long> deptIds) {
        return Result.success(memberOutputService.getOutputByDepartment(startDate, endDate, deptIds));
    }

    @Operation(summary = "按项目汇总产出")
    @GetMapping("/by-project")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<List<Map<String, Object>>> byProject(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "项目名称列表") @RequestParam(required = false) List<String> projectNames) {
        return Result.success(memberOutputService.getOutputByProject(startDate, endDate, projectNames));
    }

    @Operation(summary = "按项目查看成员产出")
    @GetMapping("/project-members")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<List<Map<String, Object>>> projectMembers(
            @Parameter(description = "项目名称") @RequestParam String projectName,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(memberOutputService.getProjectMembers(projectName, startDate, endDate));
    }
}
