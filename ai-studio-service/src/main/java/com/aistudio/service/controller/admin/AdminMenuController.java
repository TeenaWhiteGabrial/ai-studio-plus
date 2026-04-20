package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.request.MenuRequest;
import com.aistudio.service.dto.response.MenuTreeVO;
import com.aistudio.service.entity.SysMenu;
import com.aistudio.service.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin - 菜单管理")
@RestController
@RequestMapping("/admin/menu")
@RequiredArgsConstructor
public class AdminMenuController {

    private final MenuService menuService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "获取当前用户菜单树")
    @GetMapping("/tree")
    public Result<List<MenuTreeVO>> tree() {
        Long userId = securityUtils.getCurrentUserId();
        return Result.success(menuService.getMenuTree(userId));
    }

    @Operation(summary = "菜单列表")
    @GetMapping("/list")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<List<SysMenu>> list() {
        return Result.success(menuService.listAllMenus());
    }

    @Operation(summary = "菜单详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<SysMenu> detail(@PathVariable Long id) {
        return Result.success(menuService.getMenuById(id));
    }

    @Operation(summary = "创建菜单")
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Long> create(@Valid @RequestBody MenuRequest request) {
        return Result.success(menuService.createMenu(request));
    }

    @Operation(summary = "更新菜单")
    @PostMapping("/update/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody MenuRequest request) {
        menuService.updateMenu(id, request);
        return Result.success();
    }

    @Operation(summary = "删除菜单")
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return Result.success();
    }
}
