package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Admin - 数据导出")
@RestController
@RequestMapping("/admin/export")
@RequiredArgsConstructor
public class AdminExportController {

    private final ExportService exportService;

    @Operation(summary = "导出产出数据")
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public void export(
            @Parameter(description = "导出类型: detail-明细, summary-汇总") @RequestParam String type,
            @Parameter(description = "部门ID列表") @RequestParam(required = false) List<Long> deptIds,
            @Parameter(description = "项目名称列表") @RequestParam(required = false) List<String> projectNames,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletResponse response) throws IOException {

        String filename = URLEncoder.encode(
                String.format("产出统计_%s_%s_%s.xlsx", type, startDate, endDate),
                StandardCharsets.UTF_8
        ).replaceAll("\\+", "%20");

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + filename);

        exportService.export(type, deptIds, projectNames, startDate, endDate, response.getOutputStream());
    }
}
