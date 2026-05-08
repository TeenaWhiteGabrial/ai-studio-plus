package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.response.RoleMenuTreeVO;
import com.aistudio.service.entity.SysRole;
import com.aistudio.service.entity.SysUserRole;
import com.aistudio.service.mapper.SysRoleMapper;
import com.aistudio.service.mapper.SysUserRoleMapper;
import com.aistudio.service.service.MenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Admin - 角色管理")
@RestController
@RequestMapping("/admin/role")
@RequiredArgsConstructor
public class AdminRoleController {

    private final SysRoleMapper roleMapper;
    private final MenuService menuService;
    private final SysUserRoleMapper userRoleMapper;

    @Operation(summary = "角色列表")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
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
    @PostMapping("/{id}/menus")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> updateRoleMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) throw new BusinessException(404, "角色不存在");
        if ("SUPER_ADMIN".equals(role.getRoleCode())) throw new BusinessException(403, "超级管理员角色菜单不可编辑");
        menuService.updateRoleMenus(id, menuIds);
        return Result.success();
    }

    @Operation(summary = "获取角色用户")
    @GetMapping("/{id}/users")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<List<Long>> getRoleUsers(@PathVariable Long id) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) throw new BusinessException(404, "角色不存在");
        if ("SUPER_ADMIN".equals(role.getRoleCode())) throw new BusinessException(403, "超级管理员角色用户不可编辑");
        List<Long> userIds = userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, id)
        ).stream().map(SysUserRole::getUserId).toList();
        return Result.success(userIds);
    }

    @Operation(summary = "更新角色用户")
    @PostMapping("/{id}/users")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    public Result<Void> updateRoleUsers(@PathVariable Long id, @RequestBody List<Long> userIds) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) throw new BusinessException(404, "角色不存在");
        if ("SUPER_ADMIN".equals(role.getRoleCode())) throw new BusinessException(403, "超级管理员角色用户不可编辑");

        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, id));
        for (Long userId : Optional.ofNullable(userIds).orElse(List.of()).stream().distinct().toList()) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(id);
            userRoleMapper.insert(userRole);
        }
        return Result.success();
    }
}
