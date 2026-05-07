package com.aistudio.service.controller.console;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Console - 团队")
@RestController
@RequestMapping("/console/team")
@RequiredArgsConstructor
public class ConsoleTeamController {

    private final TeamService teamService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "团队列表")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER','SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<List<Object>> list(@RequestParam(required = false) Long deptId) {
        if (securityUtils.isDeptAdmin() || securityUtils.isProjectManager()) {
            Long currentDeptId = securityUtils.getCurrentUserDeptId();
            deptId = currentDeptId == null ? -1L : currentDeptId;
        }
        return Result.success(teamService.listTeams(deptId));
    }
}
