package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin - 团队管理")
@RestController
@RequestMapping("/admin/team")
@RequiredArgsConstructor
public class AdminTeamController {

    private final DepartmentService departmentService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "团队列表")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','DEPT_ADMIN')")
    public Result<List<Object>> list(@RequestParam(required = false) Long deptId) {
        // TODO: 实现团队列表查询
        return Result.success(List.of());
    }

    @Operation(summary = "创建团队")
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Long> create(@RequestBody Object team) {
        // TODO: 实现团队创建
        return Result.success(1L);
    }

    @Operation(summary = "更新团队")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody Object team) {
        return Result.success();
    }

    @Operation(summary = "查看团队成员")
    @GetMapping("/{id}/members")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','DEPT_ADMIN')")
    public Result<List<Object>> members(@PathVariable Long id) {
        // TODO: 实现团队成员查询
        return Result.success(List.of());
    }

    @Operation(summary = "添加成员到团队")
    @PostMapping("/{id}/members")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','DEPT_ADMIN')")
    public Result<Void> addMember(@PathVariable Long id, @RequestBody List<Long> userIds) {
        return Result.success();
    }

    @Operation(summary = "移出团队成员")
    @DeleteMapping("/{id}/members/{userId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','DEPT_ADMIN')")
    public Result<Void> removeMember(@PathVariable Long id, @PathVariable Long userId) {
        return Result.success();
    }
}
