package com.aistudio.service.controller.console;

import com.aistudio.service.common.Result;
import com.aistudio.service.config.RsaConfig;
import com.aistudio.service.dto.request.LoginRequest;
import com.aistudio.service.dto.response.LoginResponse;
import com.aistudio.service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Console认证接口")
@RestController
@RequestMapping("/console/auth")
@RequiredArgsConstructor
public class ConsoleAuthController {

    private final AuthService authService;
    private final RsaConfig rsaConfig;

    @Operation(summary = "获取RSA公钥")
    @GetMapping("/public-key")
    public Result<String> publicKey() {
        return Result.success(rsaConfig.getPublicKeyBase64());
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }
}
