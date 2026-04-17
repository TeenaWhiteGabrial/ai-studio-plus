package com.aistudio.service.controller.console;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.entity.MemberOutput;
import com.aistudio.service.service.MemberOutputService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Console - 个人产出管理（仅查询）
 */
@Tag(name = "Console - 产出管理")
@RestController
@RequestMapping("/console/output")
@RequiredArgsConstructor
public class ConsoleOutputController {

    private final MemberOutputService memberOutputService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "查询今日产出")
    @GetMapping("/today")
    public Result<MemberOutput> today() {
        return Result.success(memberOutputService.getTodayOutput(securityUtils.getCurrentUserId()));
    }

    @Operation(summary = "查询个人历史产出")
    @GetMapping("/history")
    public Result<List<MemberOutput>> history(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "项目名称列表") @RequestParam(required = false) List<String> projectNames) {
        return Result.success(memberOutputService.getHistory(
                securityUtils.getCurrentUserId(), startDate, endDate, projectNames));
    }
}
