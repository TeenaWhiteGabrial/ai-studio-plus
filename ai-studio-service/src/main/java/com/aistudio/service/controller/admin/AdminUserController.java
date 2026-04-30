package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.dto.request.BatchUserImportRequest;
import com.aistudio.service.dto.request.UserCreateRequest;
import com.aistudio.service.dto.request.UserUpdateRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.dto.response.UserImportResult;
import com.aistudio.service.entity.SysRole;
import com.aistudio.service.entity.SysUser;
import com.aistudio.service.mapper.SysRoleMapper;
import com.aistudio.service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin - 用户管理")
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;
    private final SysRoleMapper roleMapper;

    @Operation(summary = "用户列表")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<PageResult<SysUser>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, name = "dept_id") String deptId) {
        return Result.success(userService.listUsers(page, size, keyword, deptId));
    }

    @Operation(summary = "创建用户", description = "创建用户时可指定 roleIds 分配角色，若不指定则默认分配\"普通用户\"角色（roleId=4）")
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','DEPT_ADMIN')")
    public Result<Long> create(@Valid @RequestBody UserCreateRequest request) {
        return Result.success(userService.createUser(request));
    }

    @Operation(summary = "更新用户")
    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','DEPT_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        userService.updateUser(id, request);
        return Result.success();
    }

    @Operation(summary = "删除用户")
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','DEPT_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @Operation(summary = "分配角色")
    @PostMapping("/{id}/roles")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return Result.success();
    }

    @Operation(summary = "角色列表")
    @GetMapping("/roles")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<List<SysRole>> roles() {
        return Result.success(roleMapper.selectList(null));
    }

    @Operation(summary = "更新用户状态")
    @PostMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','DEPT_ADMIN')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "批量导入用户")
    @PostMapping("/batch")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<List<UserImportResult>> batchImport(@Valid @RequestBody List<BatchUserImportRequest> requests) {
        return Result.success(userService.batchImport(requests));
    }

}
