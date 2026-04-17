package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.request.AuditRequest;
import com.aistudio.service.dto.request.SkillCreateRequest;
import com.aistudio.service.dto.request.SkillQuery;
import com.aistudio.service.dto.request.SkillUpdateRequest;
import com.aistudio.service.dto.request.SkillVersionRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.dto.response.SkillDetailVO;
import com.aistudio.service.dto.response.SkillVersionVO;
import com.aistudio.service.service.OssService;
import com.aistudio.service.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * Admin - Skill 管理（审核 + 版本管理）
 */
@Slf4j
@Tag(name = "Admin - Skill管理")
@RestController
@RequestMapping("/admin/skill")
@RequiredArgsConstructor
public class AdminSkillController {

    private final SkillService skillService;
    private final SecurityUtils securityUtils;
    private final OssService ossService;

    @Operation(summary = "Skill 列表（管理端）")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<PageResult<SkillDetailVO>> list(SkillQuery query) {
        return Result.success(skillService.listSkills(query, null, null, List.of("SUPER_ADMIN", "OP_ADMIN")));
    }

    @Operation(summary = "Skill 详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<SkillDetailVO> detail(@PathVariable Long id) {
        return Result.success(skillService.getSkillDetail(id, null, List.of("SUPER_ADMIN", "OP_ADMIN")));
    }

    @Operation(summary = "Skill 版本列表")
    @GetMapping("/{id}/versions")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<List<SkillVersionVO>> versions(@PathVariable Long id) {
        return Result.success(skillService.getSkillVersions(id, null, List.of("SUPER_ADMIN", "OP_ADMIN")));
    }

    @Operation(summary = "审核 Skill")
    @PostMapping("/{id}/audit")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Void> audit(@PathVariable Long id, @Valid @RequestBody AuditRequest request) {
        skillService.auditSkill(id, request, securityUtils.getCurrentUserId(), List.of("SUPER_ADMIN", "OP_ADMIN"));
        return Result.success();
    }

    @Operation(summary = "删除版本（仅SUPER_ADMIN）")
    @PostMapping("/{id}/versions/{version}/delete")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> deleteVersion(@PathVariable Long id, @PathVariable String version) {
        skillService.deleteVersion(id, version, securityUtils.getCurrentUserId(), List.of("SUPER_ADMIN"));
        return Result.success();
    }

    @Operation(summary = "下载 Skill")
    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public ResponseEntity<Void> download(@PathVariable Long id, @RequestParam(required = false) String version) {
        String signedUrl = skillService.downloadSkill(id, version, null, List.of("SUPER_ADMIN", "OP_ADMIN"));
        return ResponseEntity.status(302).location(URI.create(signedUrl)).build();
    }

    @lombok.Data
    public static class UploadResultVO {
        private String ossKey;
        private String ossUrl;
        private Long fileSize;
    }
}
