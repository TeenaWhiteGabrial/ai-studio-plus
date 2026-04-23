package com.aistudio.service.controller.console;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.request.PluginRequest;
import com.aistudio.service.dto.request.PluginVersionRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.dto.response.PluginVersionVO;
import com.aistudio.service.entity.Plugin;
import com.aistudio.service.entity.PluginVersion;
import com.aistudio.service.service.OssService;
import com.aistudio.service.service.PluginService;
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
 * Console - Plugin 上传/管理
 */
@Slf4j
@Tag(name = "Console - Plugin管理")
@RestController
@RequestMapping("/console/plugin")
@RequiredArgsConstructor
public class ConsolePluginController {

    private final PluginService pluginService;
    private final SecurityUtils securityUtils;
    private final OssService ossService;

    @Operation(summary = "Plugin 列表")
    @GetMapping("/list")
    public Result<PageResult<Plugin>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        return Result.success(pluginService.listPlugins(page, size, keyword, category, status));
    }

    @Operation(summary = "Plugin 详情")
    @GetMapping("/{id}")
    public Result<Plugin> detail(@PathVariable Long id) {
        return Result.success(pluginService.getPluginById(id));
    }

    @Operation(summary = "创建 Plugin")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody PluginRequest request) {
        return Result.success(pluginService.createPlugin(request, securityUtils.getCurrentUserId()));
    }

    @Operation(summary = "更新 Plugin")
    @PostMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody PluginRequest request) {
        pluginService.updatePlugin(id, request, securityUtils.getCurrentUserId());
        return Result.success();
    }

    @Operation(summary = "删除 Plugin")
    @PostMapping("/{id}/delete")
    public Result<Void> delete(@PathVariable Long id) {
        pluginService.deletePlugin(id, securityUtils.getCurrentUserId());
        return Result.success();
    }

    @Operation(summary = "下载 Plugin")
    @GetMapping("/{id}/download")
    public Result<String> download(@PathVariable Long id) {
        return Result.success(pluginService.downloadPlugin(id));
    }

    @Operation(summary = "Plugin 版本列表")
    @GetMapping("/{id}/versions")
    public Result<List<PluginVersionVO>> versions(@PathVariable Long id) {
        return Result.success(pluginService.getVersions(id));
    }

    @Operation(summary = "发布新版本")
    @PostMapping("/{id}/versions")
    public Result<String> publishVersion(
            @PathVariable Long id,
            @Valid @RequestBody PluginVersionRequest request) {
        return Result.success(pluginService.publishVersion(id, request, securityUtils.getCurrentUserId()));
    }

    @Operation(summary = "上传 Plugin 文件到 OSS")
    @PostMapping("/upload")
    public Result<UploadResultVO> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) return Result.error(400, "上传文件为空");
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String randomName = System.currentTimeMillis() + "_" + (int)(Math.random() * 10000) + suffix;
            String key = String.format("plugins/%s", randomName);
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
