package com.aistudio.service.controller.common;

import com.aistudio.service.common.Result;
import com.aistudio.service.service.OssService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "OSS 文件存储")
@RestController
@RequestMapping("/oss")
@RequiredArgsConstructor
public class OssController {

    private final OssService ossService;

    @Operation(summary = "获取七牛云上传凭证")
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
}
