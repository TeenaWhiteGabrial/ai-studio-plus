package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.entity.SysDepartment;
import com.aistudio.service.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin - 部门管理")
@RestController
@RequestMapping("/admin/department")
@RequiredArgsConstructor
public class AdminDepartmentController {

    private final DepartmentService departmentService;

    @Operation(summary = "部门列表")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','DEPT_ADMIN')")
    public Result<List<SysDepartment>> list() {
        return Result.success(departmentService.list());
    }

    @Operation(summary = "启用中的部门列表")
    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    public Result<List<SysDepartment>> listActive() {
        return Result.success(departmentService.listActiveDepartments());
    }

    @Operation(summary = "创建部门")
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Long> create(@RequestBody SysDepartment dept) {
        dept.setId(null);
        departmentService.save(dept);
        return Result.success(dept.getId());
    }

    @Operation(summary = "更新部门")
    @PostMapping("/update/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysDepartment dept) {
        dept.setId(id);
        departmentService.updateById(dept);
        return Result.success();
    }

    @Operation(summary = "删除部门")
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        departmentService.removeById(id);
        return Result.success();
    }
}
