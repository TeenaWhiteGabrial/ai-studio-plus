package com.aistudio.service.controller.console;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.request.TutorialRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.dto.response.TutorialVersionVO;
import com.aistudio.service.entity.Tutorial;
import com.aistudio.service.service.OssService;
import com.aistudio.service.service.TutorialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Console - 教程上传/管理
 */
@Slf4j
@Tag(name = "Console - 教程管理")
@RestController
@RequestMapping("/console/tutorial")
@RequiredArgsConstructor
public class ConsoleTutorialController {

    private final TutorialService tutorialService;
    private final SecurityUtils securityUtils;
    private final OssService ossService;

    @Operation(summary = "教程列表")
    @GetMapping("/list")
    public Result<PageResult<Tutorial>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) Integer status) {
        return Result.success(tutorialService.listTutorials(page, size, keyword, category, tag, status));
    }

    @Operation(summary = "教程详情")
    @GetMapping("/{id}")
    public Result<Tutorial> detail(@PathVariable Long id) {
        return Result.success(tutorialService.getTutorialById(id));
    }

    @Operation(summary = "创建教程")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody TutorialRequest request) {
        return Result.success(tutorialService.createTutorial(request, securityUtils.getCurrentUserId()));
    }

    @Operation(summary = "更新教程")
    @PostMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody TutorialRequest request) {
        tutorialService.updateTutorial(id, request, securityUtils.getCurrentUserId());
        return Result.success();
    }

    @Operation(summary = "删除教程")
    @PostMapping("/{id}/delete")
    public Result<Void> delete(@PathVariable Long id) {
        tutorialService.deleteTutorial(id, securityUtils.getCurrentUserId());
        return Result.success();
    }

    @Operation(summary = "教程版本列表")
    @GetMapping("/{id}/versions")
    public Result<List<TutorialVersionVO>> versions(@PathVariable Long id) {
        return Result.success(tutorialService.getVersions(id));
    }

    @Operation(summary = "发布新版本")
    @PostMapping("/{id}/versions")
    public Result<String> publishVersion(
            @PathVariable Long id,
            @RequestParam(required = false) String changelog) {
        return Result.success(tutorialService.publishVersion(id, changelog, securityUtils.getCurrentUserId()));
    }

    @Operation(summary = "上传视频到 OSS")
    @PostMapping("/upload/video")
    public Result<UploadResultVO> uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) return Result.error(400, "上传文件为空");
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String randomName = System.currentTimeMillis() + "_" + (int)(Math.random() * 10000) + suffix;
            String key = String.format("tutorials/videos/%s", randomName);
            String ossKey = ossService.uploadFile(key, file.getInputStream(), file.getSize(), file.getContentType());
            UploadResultVO vo = new UploadResultVO();
            vo.setOssKey(ossKey);
            vo.setOssUrl(ossService.getPublicUrl(ossKey));
            vo.setFileSize(file.getSize());
            return Result.success(vo);
        } catch (IOException e) {
            log.error("上传视频失败", e);
            return Result.error(500, "上传视频失败: " + e.getMessage());
        }
    }

    @Operation(summary = "上传 ZIP 附件到 OSS")
    @PostMapping("/upload/zip")
    public Result<UploadResultVO> uploadZip(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) return Result.error(400, "上传文件为空");
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String randomName = System.currentTimeMillis() + "_" + (int)(Math.random() * 10000) + suffix;
            String key = String.format("tutorials/attachments/%s", randomName);
            String ossKey = ossService.uploadFile(key, file.getInputStream(), file.getSize(), file.getContentType());
            UploadResultVO vo = new UploadResultVO();
            vo.setOssKey(ossKey);
            vo.setOssUrl(ossService.getPublicUrl(ossKey));
            vo.setFileSize(file.getSize());
            return Result.success(vo);
        } catch (IOException e) {
            log.error("上传附件失败", e);
            return Result.error(500, "上传附件失败: " + e.getMessage());
        }
    }

    @lombok.Data
    public static class UploadResultVO {
        private String ossKey;
        private String ossUrl;
        private Long fileSize;
    }
}
