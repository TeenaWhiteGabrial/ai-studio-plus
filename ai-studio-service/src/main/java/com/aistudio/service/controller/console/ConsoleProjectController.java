package com.aistudio.service.controller.console;

import com.aistudio.service.common.Result;
import com.aistudio.service.dto.request.ProjectCreateRequest;
import com.aistudio.service.dto.request.ProjectStatusRequest;
import com.aistudio.service.dto.request.ProjectUpdateRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.dto.response.ProjectVO;
import com.aistudio.service.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.util.List;

@Tag(name = "Console - 项目")
@RestController
@RequestMapping("/console/project")
@RequiredArgsConstructor
public class ConsoleProjectController {

    private final ProjectService projectService;

    @Operation(summary = "查询进行中的项目")
    @GetMapping("/active")
    public Result<List<ProjectVO>> activeProjects() {
        return Result.success(projectService.listActiveProjectsForCurrentUserTeam());
    }

    @Operation(summary = "项目列表", description = "响应字段采用 snake_case，例如 project_name、owner_id、started_at。")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER','SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<PageResult<ProjectVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, name = "owner_id") Long ownerId) {
        return Result.success(projectService.listProjects(page, size, keyword, status, ownerId));
    }

    @Operation(summary = "项目详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER','SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<ProjectVO> detail(@PathVariable Long id) {
        return Result.success(projectService.getProject(id));
    }

    @Operation(summary = "创建项目", description = "请求字段采用 snake_case：project_name、description、dept_id、team_id、owner_id、status、started_at。")
    @PostMapping
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER','SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<Long> create(@Valid @RequestBody ProjectCreateRequest request) {
        return Result.success(projectService.createProject(request));
    }

    @Operation(summary = "更新项目", description = "请求字段采用 snake_case：project_name、description、dept_id、team_id、owner_id、status、started_at、ended_at。")
    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER','SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody ProjectUpdateRequest request) {
        projectService.updateProject(id, request);
        return Result.success();
    }

    @Operation(summary = "删除项目")
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER','SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        projectService.deleteProject(id);
        return Result.success();
    }

    @Operation(summary = "更新项目状态", description = "请求字段采用 snake_case：status，取值 ACTIVE 或 ENDED。")
    @PostMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('PROJECT_MANAGER','SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody ProjectStatusRequest request) {
        projectService.updateProjectStatus(id, request.getStatus());
        return Result.success();
    }
}
