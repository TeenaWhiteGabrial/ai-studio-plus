package com.aistudio.service.controller.common;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.config.RsaConfig;
import com.aistudio.service.dto.request.ChangePasswordRequest;
import com.aistudio.service.dto.request.LoginRequest;
import com.aistudio.service.dto.request.UpdateProfileRequest;
import com.aistudio.service.dto.response.LoginResponse;
import com.aistudio.service.dto.response.TokenResponse;
import com.aistudio.service.dto.response.UserInfoResponse;
import com.aistudio.service.service.AuthService;
import com.aistudio.service.service.OssService;
import com.aistudio.service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Tag(name = "认证接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RsaConfig rsaConfig;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final SecurityUtils securityUtils;
    private final OssService ossService;

    @Operation(summary = "获取RSA公钥")
    @GetMapping("/public-key")
    public Result<String> publicKey() {
        return Result.success(rsaConfig.getPublicKeyBase64());
    }

    @Operation(summary = "临时：生成BCrypt hash（调试用，上线前删除）")
    @GetMapping("/gen-hash")
    public Result<String> genHash(@RequestParam String pwd) {
        return Result.success(passwordEncoder.encode(pwd));
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @Operation(summary = "获取Token")
    @PostMapping("/token")
    public Result<TokenResponse> getToken(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.getToken(request));
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/user-info")
    public Result<UserInfoResponse> getUserInfo() {
        Long userId = securityUtils.getCurrentUserId();
        return Result.success(authService.getUserInfo(userId));
    }

    @Operation(summary = "上传头像")
    @PostMapping("/avatar")
    public Result<UploadResultVO> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error(400, "上传文件为空");
            }

            // 校验文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error(400, "只能上传图片文件");
            }

            // 校验文件大小（2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                return Result.error(400, "图片大小不能超过 2MB");
            }

            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";

            // 生成唯一文件名
            String randomName = System.currentTimeMillis() + "_" + (int)(Math.random() * 10000) + suffix;
            String key = String.format("avatars/%s", randomName);

            // 上传到 OSS
            String ossKey = ossService.uploadFile(key, file.getInputStream(), file.getSize(), contentType);
            String ossUrl = ossService.getPublicUrl(ossKey);

            // 更新用户头像
            UpdateProfileRequest updateRequest = new UpdateProfileRequest();
            updateRequest.setAvatar(ossUrl);
            userService.updateProfile(securityUtils.getCurrentUserId(), updateRequest);

            UploadResultVO vo = new UploadResultVO();
            vo.setOss_key(ossKey);
            vo.setOss_url(ossUrl);
            vo.setFile_size(file.getSize());

            log.info("头像上传成功: userId={}, ossKey={}", securityUtils.getCurrentUserId(), ossKey);
            return Result.success(vo);
        } catch (IOException e) {
            log.error("上传头像失败", e);
            return Result.error(500, "上传头像失败: " + e.getMessage());
        }
    }

    @lombok.Data
    public static class UploadResultVO {
        private String oss_key;
        private String oss_url;
        private Long file_size;
    }

    @Operation(summary = "修改密码")
    @PostMapping("/change-password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        userService.changePassword(userId, request);
        return Result.success();
    }

    @Operation(summary = "更新个人资料")
    @PostMapping("/update-profile")
    public Result<Void> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        userService.updateProfile(userId, request);
        return Result.success();
    }
}
