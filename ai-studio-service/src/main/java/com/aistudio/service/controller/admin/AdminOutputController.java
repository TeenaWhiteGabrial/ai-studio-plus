package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.request.MemberOutputRequest;
import com.aistudio.service.entity.MemberOutput;
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

    @Operation(summary = "管理员查询全员产出")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<List<Map<String, Object>>> adminList(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "部门ID列表") @RequestParam(required = false) List<Long> deptIds,
            @Parameter(description = "项目名称列表") @RequestParam(required = false) List<String> projectNames) {
        return Result.success(memberOutputService.getAdminList(date, deptIds, projectNames));
    }

    @Operation(summary = "产出汇总统计")
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
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
