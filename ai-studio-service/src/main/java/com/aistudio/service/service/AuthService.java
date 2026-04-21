package com.aistudio.service.service;

import com.aistudio.service.dto.request.LoginRequest;
import com.aistudio.service.dto.response.LoginResponse;
import com.aistudio.service.dto.response.TokenResponse;
import com.aistudio.service.dto.response.UserInfoResponse;

public interface AuthService {
    @Deprecated
    LoginResponse login(LoginRequest request);

    // 获取Token
    TokenResponse getToken(LoginRequest request);

    // 获取用户信息
    UserInfoResponse getUserInfo(Long userId);
}
