package com.aistudio.service.controller.console;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.SysUser;
import com.aistudio.service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Console - 用户")
@RestController
@RequestMapping("/console/user")
@RequiredArgsConstructor
public class ConsoleUserController {

    private final UserService userService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "项目负责人候选用户列表")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER','SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<PageResult<SysUser>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, name = "dept_id") String deptId) {
        if (securityUtils.isDeptAdmin() || securityUtils.isProjectManager()) {
            Long currentDeptId = securityUtils.getCurrentUserDeptId();
            deptId = currentDeptId == null ? "-1" : String.valueOf(currentDeptId);
        }
        return Result.success(userService.listUsers(page, size, keyword, deptId));
    }
}
