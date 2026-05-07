package com.aistudio.service.controller.console;

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

@Tag(name = "Console - 菜单")
@RestController
@RequestMapping("/console/menu")
@RequiredArgsConstructor
public class ConsoleMenuController {

    private final MenuService menuService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "获取当前用户 Console 菜单树")
    @GetMapping("/tree")
    public Result<List<MenuTreeVO>> tree() {
        Long userId = securityUtils.getCurrentUserId();
        return Result.success(menuService.getMenuTree(userId));
    }
}
