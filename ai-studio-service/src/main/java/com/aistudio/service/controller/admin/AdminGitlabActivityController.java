package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.request.ActivityManualTriggerRequest;
import com.aistudio.service.dto.request.ActivityStatGroupRequest;
import com.aistudio.service.dto.request.GitlabProjectConfigRequest;
import com.aistudio.service.dto.response.ActivityDailyReportResponse;
import com.aistudio.service.dto.response.ActivityTaskExecutionResponse;
import com.aistudio.service.dto.response.ActivityWorkItemResponse;
import com.aistudio.service.dto.response.GitlabEventLogResponse;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.ActivityStatGroup;
import com.aistudio.service.entity.GitlabProjectConfig;
import com.aistudio.service.service.GitlabActivityAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/gitlab-activity")
@RequiredArgsConstructor
public class AdminGitlabActivityController {

    private final GitlabActivityAdminService adminService;
    private final SecurityUtils securityUtils;

    @GetMapping("/project-config/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<PageResult<GitlabProjectConfig>> listProjectConfigs(@RequestParam(defaultValue = "1") int page,
                                                                      @RequestParam(defaultValue = "10") int size) {
        return Result.success(adminService.listProjectConfigs(page, size));
    }

    @PostMapping("/project-config")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Long> createProjectConfig(@Valid @RequestBody GitlabProjectConfigRequest request) {
        return Result.success(adminService.saveProjectConfig(null, request, securityUtils.getCurrentUserId()));
    }

    @PostMapping("/project-config/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Long> updateProjectConfig(@PathVariable Long id, @Valid @RequestBody GitlabProjectConfigRequest request) {
        return Result.success(adminService.saveProjectConfig(id, request, securityUtils.getCurrentUserId()));
    }

    @GetMapping("/stat-group/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<List<ActivityStatGroup>> listStatGroups() {
        return Result.success(adminService.listStatGroups());
    }

    @PostMapping("/stat-group")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<Long> createStatGroup(@Valid @RequestBody ActivityStatGroupRequest request) {
        return Result.success(adminService.saveStatGroup(null, request, securityUtils.getCurrentUserId()));
    }

    @PostMapping("/stat-group/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<Long> updateStatGroup(@PathVariable Long id, @Valid @RequestBody ActivityStatGroupRequest request) {
        return Result.success(adminService.saveStatGroup(id, request, securityUtils.getCurrentUserId()));
    }

    @PostMapping("/tasks/backfill")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Void> triggerBackfill(@Valid @RequestBody ActivityManualTriggerRequest request) {
        adminService.triggerBackfill(request, securityUtils.getCurrentUserId());
        return Result.success();
    }

    @PostMapping("/tasks/analyze")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<Void> triggerAnalyze(@Valid @RequestBody ActivityManualTriggerRequest request) {
        adminService.triggerDailyAnalysis(request, securityUtils.getCurrentUserId());
        return Result.success();
    }

    @PostMapping("/tasks/report")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<Void> triggerReport(@Valid @RequestBody ActivityManualTriggerRequest request) {
        adminService.triggerDailyReport(request, securityUtils.getCurrentUserId());
        return Result.success();
    }

    @GetMapping("/tasks/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<PageResult<ActivityTaskExecutionResponse>> listTasks(@RequestParam(defaultValue = "1") int page,
                                                                       @RequestParam(defaultValue = "10") int size,
                                                                       @RequestParam(required = false) String status) {
        return Result.success(adminService.listTasks(page, size, status));
    }

    @GetMapping("/tasks/{taskId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<ActivityTaskExecutionResponse> getTask(@PathVariable Long taskId) {
        return Result.success(adminService.getTask(taskId));
    }

    @PostMapping("/tasks/{taskId}/retry")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<Void> retryTask(@PathVariable Long taskId) {
        adminService.retryTask(taskId, securityUtils.getCurrentUserId());
        return Result.success();
    }

    @PostMapping("/tasks/{taskId}/run-now")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<Void> runTaskNow(@PathVariable Long taskId) {
        adminService.runTaskNow(taskId, securityUtils.getCurrentUserId());
        return Result.success();
    }

    @GetMapping("/event-logs/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<PageResult<GitlabEventLogResponse>> listEventLogs(@RequestParam(defaultValue = "1") int page,
                                                                    @RequestParam(defaultValue = "10") int size,
                                                                    @RequestParam(required = false) String processStatus,
                                                                    @RequestParam(required = false) Long gitlabProjectId) {
        return Result.success(adminService.listEventLogs(page, size, processStatus, gitlabProjectId));
    }

    @GetMapping("/event-logs/{eventLogId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<GitlabEventLogResponse> getEventLog(@PathVariable Long eventLogId) {
        return Result.success(adminService.getEventLog(eventLogId));
    }

    @GetMapping("/reports/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<PageResult<ActivityDailyReportResponse>> listReports(@RequestParam(defaultValue = "1") int page,
                                                                       @RequestParam(defaultValue = "10") int size,
                                                                       @RequestParam(required = false) LocalDate reportDate,
                                                                       @RequestParam(required = false) Long statGroupId) {
        return Result.success(adminService.listReports(page, size, reportDate, statGroupId));
    }

    @GetMapping("/work-items/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN','DEPT_ADMIN')")
    public Result<PageResult<ActivityWorkItemResponse>> listWorkItems(@RequestParam(defaultValue = "1") int page,
                                                                      @RequestParam(defaultValue = "10") int size,
                                                                      @RequestParam(required = false) LocalDate analysisDate,
                                                                      @RequestParam(required = false) Long statGroupId,
                                                                      @RequestParam(required = false) Long userId) {
        return Result.success(adminService.listWorkItems(page, size, analysisDate, statGroupId, userId));
    }
}
