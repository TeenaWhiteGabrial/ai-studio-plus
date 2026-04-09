package com.aistudio.service.controller;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.request.SkillCreateRequest;
import com.aistudio.service.dto.request.SkillQuery;
import com.aistudio.service.dto.request.SkillUpdateRequest;
import com.aistudio.service.dto.request.SkillVersionRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.dto.response.SkillDetailVO;
import com.aistudio.service.dto.response.SkillVersionVO;
import com.aistudio.service.entity.SkillVersion;
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
 * Skill 管理控制器（OSS + 版本控制模式）
 */
@Slf4j
@Tag(name = "Skill 管理")
@RestController
@RequestMapping("/api/skill")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;
    private final SecurityUtils securityUtils;
    private final OssService ossService;

    // ========== 查询接口 ==========

    @Operation(summary = "Skill 列表")
    @GetMapping("/list")
    public Result<PageResult<SkillDetailVO>> list(SkillQuery query) {
        Long userId = securityUtils.getCurrentUserId();
        Long deptId = securityUtils.getCurrentUserDeptId();
        List<String> roles = securityUtils.getCurrentUserRoles();

        return Result.success(skillService.listSkills(query, userId, deptId, roles));
    }

    @Operation(summary = "Skill 详情")
    @GetMapping("/{id}")
    public Result<SkillDetailVO> detail(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        List<String> roles = securityUtils.getCurrentUserRoles();

        return Result.success(skillService.getSkillDetail(id, userId, roles));
    }

    @Operation(summary = "Skill 版本列表")
    @GetMapping("/{id}/versions")
    public Result<List<SkillVersionVO>> versions(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        List<String> roles = securityUtils.getCurrentUserRoles();

        return Result.success(skillService.getSkillVersions(id, userId, roles));
    }

    // ========== 管理接口 ==========

    @Operation(summary = "创建 Skill")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Result<Long> create(@RequestBody @Valid SkillCreateRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        Long deptId = securityUtils.getCurrentUserDeptId();

        Long skillId = skillService.createSkill(request, userId, deptId);
        return Result.success(skillId);
    }

    @Operation(summary = "更新 Skill")
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Result<Void> update(@PathVariable Long id, @RequestBody SkillUpdateRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        List<String> roles = securityUtils.getCurrentUserRoles();

        skillService.updateSkill(id, request, userId, roles);
        return Result.success();
    }

    @Operation(summary = "删除 Skill")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Result<Void> delete(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        List<String> roles = securityUtils.getCurrentUserRoles();

        skillService.deleteSkill(id, userId, roles);
        return Result.success();
    }

    // ========== 版本管理接口 ==========

    @Operation(summary = "发布新版本")
    @PostMapping("/{id}/versions")
    @PreAuthorize("isAuthenticated()")
    public Result<String> publishVersion(@PathVariable Long id,
                                          @RequestBody @Valid SkillVersionRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        Long deptId = securityUtils.getCurrentUserDeptId();
        List<String> roles = securityUtils.getCurrentUserRoles();

        String version = skillService.publishVersion(id, request, userId, deptId, roles);
        return Result.success(version);
    }

    @Operation(summary = "删除版本（仅超级管理员）")
    @DeleteMapping("/{id}/versions/{version}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> deleteVersion(@PathVariable Long id, @PathVariable String version) {
        Long userId = securityUtils.getCurrentUserId();
        List<String> roles = securityUtils.getCurrentUserRoles();

        skillService.deleteVersion(id, version, userId, roles);
        return Result.success();
    }

    // ========== 下载接口 ==========

    @Operation(summary = "下载 Skill")
    @GetMapping("/{id}/download")
    public ResponseEntity<Void> download(@PathVariable Long id,
                                          @RequestParam(required = false) String version) {
        Long userId = securityUtils.getCurrentUserId();
        List<String> roles = securityUtils.getCurrentUserRoles();

        String signedUrl = skillService.downloadSkill(id, version, userId, roles);

        return ResponseEntity.status(302)
                .location(URI.create(signedUrl))
                .build();
    }

    // ========== OSS 上传接口 ==========

    @Operation(summary = "上传 Skill 文件到 OSS")
    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public Result<UploadResultVO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam String skillName,
            @RequestParam String version) {
        try {
            if (file.isEmpty()) {
                return Result.error(400, "上传文件为空");
            }

            // 生成 OSS key: skills/{skillName}/v{version}/{randomFileName}
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String randomName = System.currentTimeMillis() + "_" + (int)(Math.random() * 10000) + suffix;
            String key = String.format("skills/%s/v%s/%s", skillName, version, randomName);

            // 上传文件到 OSS
            String ossKey = ossService.uploadFile(
                    key,
                    file.getInputStream(),
                    file.getSize(),
                    file.getContentType()
            );

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

    /**
     * 上传结果 VO
     */
    @lombok.Data
    public static class UploadResultVO {
        private String ossKey;
        private String ossUrl;
        private Long fileSize;
    }
}
