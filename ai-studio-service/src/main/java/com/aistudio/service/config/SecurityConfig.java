package com.aistudio.service.config;

import com.aistudio.service.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                // Public endpoints
                .requestMatchers(
                    "/auth/login", "/auth/public-key", "/auth/gen-hash", "/auth/token",
                    "/admin/auth/login", "/admin/auth/public-key", "/admin/auth/gen-hash", "/admin/auth/token",
                    "/console/auth/login", "/console/auth/public-key", "/console/auth/token",
                    "/open/**",
                    "/portal/open/**",
                    "/portal/site/config",
                    "/doc.html", "/swagger-ui/**", "/v3/api-docs/**", "/webjars/**"
                ).permitAll()
                // Authenticated profile/account endpoints
                .requestMatchers(
                    "/auth/user-info", "/auth/avatar", "/auth/update-profile", "/auth/change-password",
                    "/admin/auth/user-info",
                    "/console/auth/user-info", "/console/auth/avatar", "/console/auth/update-profile", "/console/auth/change-password"
                ).authenticated()
                // Portal public read endpoints
                .requestMatchers(HttpMethod.GET,
                    "/portal/article/list",
                    "/portal/article/*",
                    "/portal/question/list",
                    "/portal/question/*",
                    "/portal/question/*/answers",
                    "/portal/comment/list",
                    "/portal/tag/list"
                ).permitAll()
                // Admin role endpoints
                .requestMatchers("/admin/**").hasAnyRole("SUPER_ADMIN", "OP_ADMIN", "DEPT_ADMIN")
                // Console role endpoints
                .requestMatchers("/console/**").hasAnyRole("USER", "PROJECT_MANAGER", "OP_ADMIN", "DEPT_ADMIN", "SUPER_ADMIN")
                // Portal endpoints requiring login
                .requestMatchers(
                    "/portal/article/*/like",
                    "/portal/article/*/is-liked",
                    "/portal/question",
                    "/portal/question/*",
                    "/portal/question/*/delete",
                    "/portal/question/*/answer",
                    "/portal/answer/**",
                    "/portal/comment",
                    "/portal/comment/*/delete",
                    "/portal/comment/*/like",
                    "/portal/favorite/**",
                    "/portal/browse-history/**",
                    "/portal/notification/**",
                    "/portal/tag",
                    "/portal/user/profile",
                    "/oss/**"
                ).authenticated()
                // Only super admin
                .requestMatchers("/api/admin/**").hasRole("SUPER_ADMIN")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> {
                    res.setStatus(401);
                    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    res.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    res.getWriter().write(objectMapper.writeValueAsString(Result.error(401, "Unauthorized, please login first")));
                })
                .accessDeniedHandler((req, res, e) -> {
                    res.setStatus(403);
                    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    res.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    res.getWriter().write(objectMapper.writeValueAsString(Result.error(403, "Forbidden")));
                })
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .authenticationProvider(authProvider());
        return http.build();
    }
}
