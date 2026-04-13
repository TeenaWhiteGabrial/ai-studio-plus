package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.response.MenuTreeVO;
import com.aistudio.service.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
