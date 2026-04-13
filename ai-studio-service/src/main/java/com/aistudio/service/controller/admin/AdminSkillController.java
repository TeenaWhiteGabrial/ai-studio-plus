package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
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
 * Admin - Skill 管理（完整CRUD + 版本管理）
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

    @Operation(summary = "创建 Skill")
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Long> create(@RequestBody @Valid SkillCreateRequest request) {
        return Result.success(skillService.createSkill(request, securityUtils.getCurrentUserId(), securityUtils.getCurrentUserDeptId()));
    }

    @Operation(summary = "更新 Skill")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody SkillUpdateRequest request) {
        skillService.updateSkill(id, request, securityUtils.getCurrentUserId(), List.of("SUPER_ADMIN"));
        return Result.success();
    }

    @Operation(summary = "删除 Skill")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        skillService.deleteSkill(id, securityUtils.getCurrentUserId(), List.of("SUPER_ADMIN"));
        return Result.success();
    }

    @Operation(summary = "发布新版本")
    @PostMapping("/{id}/versions")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<String> publishVersion(@PathVariable Long id, @RequestBody @Valid SkillVersionRequest request) {
        return Result.success(skillService.publishVersion(id, request, securityUtils.getCurrentUserId(), securityUtils.getCurrentUserDeptId(), List.of("SUPER_ADMIN")));
    }

    @Operation(summary = "删除版本")
    @DeleteMapping("/{id}/versions/{version}")
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

    @Operation(summary = "上传 Skill 文件到 OSS")
    @PostMapping("/upload")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<UploadResultVO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam String skillName,
            @RequestParam String version) {
        try {
            if (file.isEmpty()) return Result.error(400, "上传文件为空");
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String randomName = System.currentTimeMillis() + "_" + (int)(Math.random() * 10000) + suffix;
            String key = String.format("skills/%s/v%s/%s", skillName, version, randomName);
            String ossKey = ossService.uploadFile(key, file.getInputStream(), file.getSize(), file.getContentType());
            UploadResultVO vo = new UploadResultVO();
            vo.setOssKey(ossKey);
            vo.setOssUrl(ossService.getPublicUrl(ossKey));
            vo.setFileSize(file.getSize());
            return Result.success(vo);
        } catch (IOException e) {
            log.error("上传文件失败", e);
            return Result.error(500, "上传文件失败: " + e.getMessage());
        }
    }

    @lombok.Data
    public static class UploadResultVO {
        private String ossKey;
        private String ossUrl;
        private Long fileSize;
    }
}
