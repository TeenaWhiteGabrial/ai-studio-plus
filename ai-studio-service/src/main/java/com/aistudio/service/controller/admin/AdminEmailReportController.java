package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.dto.request.EmailReportRuleRequest;
import com.aistudio.service.service.EmailReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/email-report")
@RequiredArgsConstructor
public class AdminEmailReportController {

    private final EmailReportService emailReportService;

    @GetMapping("/rules")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<List<Map<String, Object>>> rules() {
        return Result.success(emailReportService.listRules());
    }

    @PostMapping("/rules")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Long> create(@Valid @RequestBody EmailReportRuleRequest request) {
        return Result.success(emailReportService.createRule(request));
    }

    @PostMapping("/rules/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody EmailReportRuleRequest request) {
        emailReportService.updateRule(id, request);
        return Result.success();
    }

    @PostMapping("/rules/{id}/delete")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        emailReportService.deleteRule(id);
        return Result.success();
    }

    @GetMapping("/rules/{id}/logs")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<List<Map<String, Object>>> logs(@PathVariable Long id) {
        return Result.success(emailReportService.listSendLogs(id));
    }

    @GetMapping("/rules/{id}/preview")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<String> preview(@PathVariable Long id,
                                  @RequestParam(required = false)
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return Result.success(emailReportService.previewDailyReport(id, date));
    }

    @PostMapping("/rules/{id}/send")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Void> send(@PathVariable Long id,
                             @RequestParam(required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        emailReportService.sendDailyReport(id, date);
        return Result.success();
    }
}
