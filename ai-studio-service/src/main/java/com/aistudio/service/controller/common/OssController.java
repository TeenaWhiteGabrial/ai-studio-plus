package com.aistudio.service.controller.common;

import com.aistudio.service.common.Result;
import com.aistudio.service.service.OssService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Tag(name = "OSS File Storage")
@RestController
@RequestMapping("/oss")
@RequiredArgsConstructor
public class OssController {

    private final OssService ossService;

    @Operation(summary = "Get upload token")
    @GetMapping("/token")
    public Result<Map<String, String>> getUpToken(
            @RequestParam(required = false, defaultValue = "") String keyPrefix) {
        String token = ossService.generateUpToken(keyPrefix);
        return Result.success(Map.of(
                "token", token,
                "domain", ossService.getDomain(),
                "keyPrefix", keyPrefix
        ));
    }

    @Operation(summary = "Upload file")
    @PostMapping("/upload")
    public Result<UploadResultVO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false, defaultValue = "portal/images") String keyPrefix) {
        if (file == null || file.isEmpty()) {
            return Result.error(400, "上传文件不能为空");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String suffix = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String normalizedPrefix = normalizePrefix(keyPrefix);
            String randomName = System.currentTimeMillis() + "_" + ThreadLocalRandom.current().nextInt(10000) + suffix;
            String key = normalizedPrefix + "/" + randomName;

            String ossKey = ossService.uploadFile(key, file.getInputStream(), file.getSize(), file.getContentType());
            String ossUrl = ossService.getPublicUrl(ossKey);

            UploadResultVO vo = new UploadResultVO();
            vo.setFileName(originalFilename);
            vo.setOssKey(ossKey);
            vo.setOssUrl(ossUrl);
            vo.setFileSize(file.getSize());
            return Result.success(vo);
        } catch (IOException e) {
            return Result.error(500, "上传失败: " + e.getMessage());
        }
    }

    private String normalizePrefix(String keyPrefix) {
        String prefix = keyPrefix == null ? "" : keyPrefix.trim();
        while (prefix.startsWith("/")) {
            prefix = prefix.substring(1);
        }
        while (prefix.endsWith("/")) {
            prefix = prefix.substring(0, prefix.length() - 1);
        }
        if (prefix.isEmpty()) {
            return "portal/images";
        }
        return prefix;
    }

    @Data
    public static class UploadResultVO {
        private String fileName;
        private String ossKey;
        private String ossUrl;
        private Long fileSize;
    }
}
