package com.aistudio.service.controller.console;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.request.DailyTaskRequest;
import com.aistudio.service.dto.response.DailyTaskStatsVO;
import com.aistudio.service.entity.DailyTask;
import com.aistudio.service.service.DailyTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Console - 任务管理")
@RestController
@RequestMapping("/console/task")
@RequiredArgsConstructor
public class ConsoleTaskController {

    private final DailyTaskService dailyTaskService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "查询本人任务清单")
    @GetMapping("/list")
    public Result<List<DailyTask>> list(
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "任务状态") @RequestParam(required = false) String status) {
        return Result.success(dailyTaskService.list(securityUtils.getCurrentUserId(), startDate, endDate, status));
    }

    @Operation(summary = "录入任务")
    @PostMapping
    public Result<DailyTask> create(@Valid @RequestBody DailyTaskRequest request) {
        return Result.success(dailyTaskService.create(securityUtils.getCurrentUserId(), request));
    }

    @Operation(summary = "编辑任务")
    @PutMapping("/{id}")
    public Result<DailyTask> update(@PathVariable Long id, @Valid @RequestBody DailyTaskRequest request) {
        return Result.success(dailyTaskService.update(securityUtils.getCurrentUserId(), id, request));
    }

    @Operation(summary = "删除任务")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dailyTaskService.delete(securityUtils.getCurrentUserId(), id);
        return Result.success();
    }

    @Operation(summary = "查询本人任务统计")
    @GetMapping("/stats")
    public Result<DailyTaskStatsVO> stats() {
        return Result.success(dailyTaskService.stats(securityUtils.getCurrentUserId()));
    }
}
