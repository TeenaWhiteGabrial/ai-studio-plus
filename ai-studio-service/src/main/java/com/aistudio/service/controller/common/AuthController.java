package com.aistudio.service.controller.common;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.config.RsaConfig;
import com.aistudio.service.dto.request.LoginRequest;
import com.aistudio.service.dto.response.LoginResponse;
import com.aistudio.service.dto.response.TokenResponse;
import com.aistudio.service.dto.response.UserInfoResponse;
import com.aistudio.service.service.AuthService;
import com.aistudio.service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "认证接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RsaConfig rsaConfig;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

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
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(authService.getUserInfo(userId));
    }

    @Operation(summary = "上传头像")
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Long userId = SecurityUtils.getCurrentUserId();
        // TODO: 实现文件上传逻辑，这里暂时返回文件名
        String fileName = file.getOriginalFilename();
        // 实际项目中应该上传到对象存储（如OSS、MinIO等），并返回URL
        return Result.success("/uploads/avatar/" + fileName);
    }
}
