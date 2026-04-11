package com.aistudio.service.controller;

import com.aistudio.service.common.Result;
import com.aistudio.service.entity.SysDepartment;
import com.aistudio.service.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "部门管理")
@RestController
@RequestMapping("/api/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @Operation(summary = "部门列表")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public Result<List<SysDepartment>> list() {
        return Result.success(departmentService.list());
    }

    @Operation(summary = "启用中的部门列表")
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
    public Result<List<SysDepartment>> listActive() {
        return Result.success(departmentService.listActiveDepartments());
    }

    @Operation(summary = "创建部门")
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Long> create(@RequestBody SysDepartment dept) {
        dept.setId(null); // 确保id为null，使用数据库自增
        departmentService.save(dept);
        return Result.success(dept.getId());
    }

    @Operation(summary = "更新部门")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysDepartment dept) {
        dept.setId(id);
        departmentService.updateById(dept);
        return Result.success();
    }

    @Operation(summary = "删除部门")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        departmentService.removeById(id);
        return Result.success();
    }
}
