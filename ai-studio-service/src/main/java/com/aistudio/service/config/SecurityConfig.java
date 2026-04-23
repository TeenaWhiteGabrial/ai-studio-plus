package com.aistudio.service.config;

import com.aistudio.service.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final ObjectMapper objectMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 公开接口 - Spring Security 使用不含 context-path 的路径
                .requestMatchers(
                    // 基础公开接口
                    "/auth/login", "/auth/public-key", "/auth/gen-hash", "/auth/token", "/auth/user-info",
                    // Admin 端公开接口
                    "/admin/auth/login", "/admin/auth/public-key", "/admin/auth/gen-hash", "/admin/auth/token", "/admin/auth/user-info",
                    // Console 端公开接口
                    "/console/auth/login", "/console/auth/public-key", "/console/auth/token", "/console/auth/user-info",
                    // Open 开放接口
                    "/open/**",
                    // Portal 公开接口（无需认证）
                    "/portal/open/**",
                    // Swagger
                    "/doc.html", "/swagger-ui/**", "/v3/api-docs/**", "/webjars/**"
                ).permitAll()
                // 认证接口（需登录但不限角色）
                .requestMatchers(
                    "/auth/avatar", "/auth/update-profile", "/auth/change-password",
                    "/console/auth/avatar", "/console/auth/update-profile", "/console/auth/change-password"
                ).authenticated()
                // Admin 端接口（需特定角色）
                .requestMatchers("/admin/**").hasAnyRole("SUPER_ADMIN", "OP_ADMIN", "DEPT_ADMIN")
                // Console 端接口
                .requestMatchers("/console/**").hasRole("USER")
                // Portal 社区认证操作（需登录但不限角色）
                .requestMatchers(
                    "/portal/article", "/portal/article/{id}",
                    "/portal/question", "/portal/question/{id}",
                    "/portal/question/{questionId}/answer",
                    "/portal/question/{questionId}/answers",
                    "/portal/answer/**",
                    "/portal/comment/**",
                    "/portal/favorite/**",
                    "/portal/browse-history/**",
                    "/portal/notification/**",
                    "/portal/tag",
                    "/portal/user/profile",
                    "/oss/**"
                ).authenticated()
                // Admin 下架接口（仅超级管理员）
                .requestMatchers("/api/admin/**").hasRole("SUPER_ADMIN")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> {
                    res.setStatus(401);
                    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    res.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    res.getWriter().write(objectMapper.writeValueAsString(Result.error(401, "未认证，请先登录")));
                })
                .accessDeniedHandler((req, res, e) -> {
                    res.setStatus(403);
                    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    res.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    res.getWriter().write(objectMapper.writeValueAsString(Result.error(403, "无权限访问")));
                })
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .authenticationProvider(authProvider());
        return http.build();
    }
}
