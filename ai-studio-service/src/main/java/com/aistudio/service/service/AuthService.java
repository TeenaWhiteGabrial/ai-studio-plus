package com.aistudio.service.service;

import com.aistudio.service.dto.request.LoginRequest;
import com.aistudio.service.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
