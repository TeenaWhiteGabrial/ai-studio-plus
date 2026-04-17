package com.aistudio.service.controller.console;

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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * Console - Skill 上传/管理
 */
@Slf4j
@Tag(name = "Console - Skill管理")
@RestController
@RequestMapping("/console/skill")
@RequiredArgsConstructor
public class ConsoleSkillController {

    private final SkillService skillService;
    private final SecurityUtils securityUtils;
    private final OssService ossService;

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

    @Operation(summary = "创建 Skill")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody SkillCreateRequest request) {
        return Result.success(skillService.createSkill(request, securityUtils.getCurrentUserId(), securityUtils.getCurrentUserDeptId()));
    }

    @Operation(summary = "更新 Skill")
    @PostMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SkillUpdateRequest request) {
        skillService.updateSkill(id, request, securityUtils.getCurrentUserId(), securityUtils.getCurrentUserRoles());
        return Result.success();
    }

    @Operation(summary = "删除 Skill")
    @PostMapping("/{id}/delete")
    public Result<Void> delete(@PathVariable Long id) {
        skillService.deleteSkill(id, securityUtils.getCurrentUserId(), securityUtils.getCurrentUserRoles());
        return Result.success();
    }

    @Operation(summary = "发布新版本")
    @PostMapping("/{id}/versions")
    public Result<String> publishVersion(@PathVariable Long id, @Valid @RequestBody SkillVersionRequest request) {
        return Result.success(skillService.publishVersion(id, request, securityUtils.getCurrentUserId(), securityUtils.getCurrentUserDeptId(), securityUtils.getCurrentUserRoles()));
    }

    @Operation(summary = "下载 Skill")
    @GetMapping("/{id}/download")
    public ResponseEntity<Void> download(@PathVariable Long id, @RequestParam(required = false) String version) {
        Long userId = securityUtils.getCurrentUserId();
        List<String> roles = securityUtils.getCurrentUserRoles();
        String signedUrl = skillService.downloadSkill(id, version, userId, roles);
        return ResponseEntity.status(302).location(URI.create(signedUrl)).build();
    }

    @Operation(summary = "上传 Skill 文件到 OSS")
    @PostMapping("/upload")
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
