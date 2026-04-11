package com.aistudio.service.controller;

import com.aistudio.service.common.Result;
import com.aistudio.service.config.RsaConfig;
import com.aistudio.service.dto.request.LoginRequest;
import com.aistudio.service.dto.response.LoginResponse;
import com.aistudio.service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RsaConfig rsaConfig;
    private final PasswordEncoder passwordEncoder;

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
}
