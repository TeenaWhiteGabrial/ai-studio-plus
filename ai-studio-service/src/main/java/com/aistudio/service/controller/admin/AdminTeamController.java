package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.dto.request.TeamCreateRequest;
import com.aistudio.service.dto.request.TeamUpdateRequest;
import com.aistudio.service.entity.SysUser;
import com.aistudio.service.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin - 团队管理")
@RestController
@RequestMapping("/admin/team")
@RequiredArgsConstructor
public class AdminTeamController {

    private final TeamService teamService;

    @Operation(summary = "团队列表")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<List<Object>> list(@RequestParam(required = false) Long deptId) {
        return Result.success(teamService.listTeams(deptId));
    }

    @Operation(summary = "创建团队")
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','DEPT_ADMIN')")
    public Result<Long> create(@RequestBody TeamCreateRequest request) {
        return Result.success(teamService.createTeam(request));
    }

    @Operation(summary = "更新团队")
    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','DEPT_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody TeamUpdateRequest request) {
        teamService.updateTeam(id, request);
        return Result.success();
    }

    @Operation(summary = "删除团队")
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','DEPT_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return Result.success();
    }

    @Operation(summary = "查看团队成员")
    @GetMapping("/{id}/members")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','DEPT_ADMIN')")
    public Result<List<SysUser>> members(@PathVariable Long id) {
        return Result.success(teamService.getTeamMembers(id));
    }

    @Operation(summary = "添加成员到团队")
    @PostMapping("/{id}/members")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','DEPT_ADMIN')")
    public Result<Void> addMember(@PathVariable Long id, @RequestBody List<Long> userIds) {
        teamService.addTeamMembers(id, userIds);
        return Result.success();
    }

    @Operation(summary = "移出团队成员")
    @PostMapping("/{id}/members/{userId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','DEPT_ADMIN')")
    public Result<Void> removeMember(@PathVariable Long id, @PathVariable Long userId) {
        teamService.removeTeamMember(id, userId);
        return Result.success();
    }
}
