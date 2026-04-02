package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.config.JwtTokenProvider;
import com.aistudio.service.config.RsaConfig;
import com.aistudio.service.dto.request.LoginRequest;
import com.aistudio.service.dto.response.LoginResponse;
import com.aistudio.service.entity.SysRole;
import com.aistudio.service.entity.SysUser;
import com.aistudio.service.mapper.SysRoleMapper;
import com.aistudio.service.mapper.SysUserMapper;
import com.aistudio.service.service.AuthService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RsaConfig rsaConfig;

    @Override
    public LoginResponse login(LoginRequest request) {
        // RSA 解密前端传来的密文，得到明文密码
        String plainPassword = rsaConfig.decrypt(request.getPassword());
        log.info("[登录调试] 解密后明文密码: '{}'", plainPassword);

        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, request.getUsername()));
        if (user == null) {
            log.warn("[登录调试] 用户不存在: {}", request.getUsername());
            throw new BusinessException(401, "用户名或密码错误");
        }
        log.info("[登录调试] 数据库密码hash: '{}'", user.getPassword());
        boolean matches = passwordEncoder.matches(plainPassword, user.getPassword());
        log.info("[登录调试] BCrypt匹配结果: {}", matches);
        if (!matches) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(403, "用户已被禁用");
        }
        List<SysRole> roles = roleMapper.selectByUserId(user.getId());
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());
        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .roles(roles.stream().map(SysRole::getRoleCode).collect(Collectors.toList()))
                .build();
    }
}
