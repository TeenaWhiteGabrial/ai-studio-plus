package com.aistudio.service.controller.common;

import com.aistudio.service.common.Result;
import com.aistudio.service.dto.request.OpenOutputSubmitRequest;
import com.aistudio.service.entity.MemberOutput;
import com.aistudio.service.service.MemberOutputService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Open - 开放产出接口（无需认证）
 */
@Tag(name = "Open - 产出开放接口")
@RestController
@RequestMapping("/open/output")
@RequiredArgsConstructor
public class OpenOutputController {

    private final MemberOutputService memberOutputService;

    @Operation(summary = "提交产出数据（开放接口，无需认证）")
    @PostMapping("/submit")
    public Result<Map<String, Object>> submit(@Valid @RequestBody OpenOutputSubmitRequest request) {
        memberOutputService.submitOutputByUsername(request);
        Map<String, Object> response = new HashMap<>();
        response.put("username", request.getUsername());
        response.put("statDate", request.getStatDate());
        return Result.success(response);
    }

    @Operation(summary = "查询今日产出（开放接口，无需认证）")
    @GetMapping("/today")
    public Result<List<MemberOutput>> getToday(@RequestParam String username) {
        return Result.success(memberOutputService.getTodayOutputByUsername(username));
    }

    @Operation(summary = "查询时间范围产出（开放接口，无需认证）")
    @GetMapping("/history")
    public Result<List<MemberOutput>> getHistory(
            @Parameter(description = "用户名") @RequestParam String username,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(memberOutputService.getHistoryByUsername(username, startDate, endDate));
    }
}
