package com.aistudio.service.controller.console;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.response.ActivityDailyReportResponse;
import com.aistudio.service.dto.response.ActivityWorkItemResponse;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.service.GitlabActivityConsoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/console/gitlab-activity")
@RequiredArgsConstructor
public class ConsoleGitlabActivityController {

    private final GitlabActivityConsoleService consoleService;
    private final SecurityUtils securityUtils;

    @GetMapping("/my/work-items")
    public Result<PageResult<ActivityWorkItemResponse>> myWorkItems(@RequestParam(defaultValue = "1") int page,
                                                                    @RequestParam(defaultValue = "10") int size,
                                                                    @RequestParam(required = false) LocalDate analysisDate) {
        return Result.success(consoleService.listMyWorkItems(securityUtils.getCurrentUserId(), page, size, analysisDate));
    }

    @GetMapping("/my/daily-report")
    public Result<ActivityDailyReportResponse> myDailyReport(@RequestParam(required = false) LocalDate reportDate) {
        return Result.success(consoleService.getMyDailyReport(securityUtils.getCurrentUserId(), reportDate));
    }

    @GetMapping("/project/{projectId}/daily-report")
    public Result<ActivityDailyReportResponse> projectDailyReport(@PathVariable Long projectId,
                                                                  @RequestParam(required = false) LocalDate reportDate) {
        return Result.success(consoleService.getProjectDailyReport(securityUtils.getCurrentUserId(), projectId, reportDate));
    }

    @GetMapping("/my/overview")
    public Result<Map<String, Object>> myOverview(@RequestParam(required = false) LocalDate analysisDate) {
        return Result.success(consoleService.getMyOverview(securityUtils.getCurrentUserId(), analysisDate));
    }
}
