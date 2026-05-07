package com.aistudio.service.controller.console;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.entity.SysDepartment;
import com.aistudio.service.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Console - 部门")
@RestController
@RequestMapping("/console/department")
@RequiredArgsConstructor
public class ConsoleDepartmentController {

    private final DepartmentService departmentService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "启用中的部门列表")
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER','SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<List<SysDepartment>> listActive() {
        List<SysDepartment> departments = departmentService.listActiveDepartments();
        if (securityUtils.isDeptAdmin() || securityUtils.isProjectManager()) {
            Long deptId = securityUtils.getCurrentUserDeptId();
            departments = departments.stream()
                    .filter(dept -> dept.getId().equals(deptId))
                    .toList();
        }
        return Result.success(departments);
    }
}
