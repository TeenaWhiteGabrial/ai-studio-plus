package com.aistudio.service.controller;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Skill;
import com.aistudio.service.entity.SkillSyncLog;
import com.aistudio.service.service.SkillService;
import com.aistudio.service.service.SkillSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Tag(name = "Skill 管理")
@RestController
@RequestMapping("/api/skill")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;
    private final SkillSyncService skillSyncService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "Skill 列表")
    @GetMapping("/list")
    public Result<PageResult<Skill>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        return Result.success(skillService.listSkills(page, size, keyword, category, status));
    }

    @Operation(summary = "Skill 详情")
    @GetMapping("/{id}")
    public Result<Skill> detail(@PathVariable Long id) {
        return Result.success(skillService.getSkillById(id));
    }

    @Operation(summary = "下载 Skill")
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        return skillService.downloadSkillAsZip(id);
    }

    @Operation(summary = "获取 Skill Raw 内容")
    @GetMapping("/{id}/raw")
    public ResponseEntity<String> raw(@PathVariable Long id) {
        String content = skillService.getSkillRawContent(id);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_MARKDOWN)
                .body(content);
    }

    @Operation(summary = "手动同步 Skill（异步执行，立即返回）")
    @PostMapping("/sync")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Result<String> sync() {
        // 异步执行同步，立即返回
        new Thread(skillSyncService::syncAll).start();
        return Result.success("同步任务已启动，请稍后刷新查看结果");
    }

    @Operation(summary = "同步日志")
    @GetMapping("/sync-log")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Result<List<SkillSyncLog>> syncLog(
            @RequestParam(defaultValue = "20") int limit) {
        return Result.success(skillService.getSyncLogs(limit));
    }
}
