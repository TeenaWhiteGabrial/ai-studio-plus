package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.response.RoleMenuTreeVO;
import com.aistudio.service.entity.SysRole;
import com.aistudio.service.mapper.SysRoleMapper;
import com.aistudio.service.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin - 角色管理")
@RestController
@RequestMapping("/admin/role")
@RequiredArgsConstructor
public class AdminRoleController {

    private final SysRoleMapper roleMapper;
    private final MenuService menuService;

    @Operation(summary = "角色列表")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','DEPT_ADMIN')")
    public Result<List<SysRole>> list() {
        return Result.success(roleMapper.selectList(null));
    }

    @Operation(summary = "获取角色菜单树")
    @GetMapping("/{id}/menus")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<List<RoleMenuTreeVO>> getRoleMenus(@PathVariable Long id) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) throw new BusinessException(404, "角色不存在");
        if ("SUPER_ADMIN".equals(role.getRoleCode())) throw new BusinessException(403, "超级管理员角色菜单不可编辑");
        return Result.success(menuService.getRoleMenuTree(id));
    }

    @Operation(summary = "更新角色菜单")
    @PutMapping("/{id}/menus")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> updateRoleMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) throw new BusinessException(404, "角色不存在");
        if ("SUPER_ADMIN".equals(role.getRoleCode())) throw new BusinessException(403, "超级管理员角色菜单不可编辑");
        menuService.updateRoleMenus(id, menuIds);
        return Result.success();
    }
}
