package com.aistudio.service.common;

import com.aistudio.service.entity.SysOperLog;
import com.aistudio.service.mapper.SysOperLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperLogAspect {

    private final SysOperLogMapper operLogMapper;
    private final SecurityUtils securityUtils;

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface OperLog {
        String module() default "";
        String action() default "";
    }

    @Around("@annotation(operLog)")
    public Object around(ProceedingJoinPoint pjp, OperLog operLog) throws Throwable {
        long start = System.currentTimeMillis();
        SysOperLog logEntity = new SysOperLog();
        logEntity.setModule(operLog.module());
        logEntity.setAction(operLog.action());
        logEntity.setCreatedAt(LocalDateTime.now());

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                logEntity.setUsername(auth.getName());
                Long userId = securityUtils.getCurrentUserId();
                logEntity.setUserId(userId);
            }
        } catch (Exception ignored) {}

        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest req = attrs.getRequest();
                logEntity.setMethod(req.getMethod());
                logEntity.setRequestUrl(req.getRequestURI());
            }
        } catch (Exception ignored) {}

        Object result;
        try {
            result = pjp.proceed();
            logEntity.setResult("SUCCESS");
        } catch (Throwable e) {
            logEntity.setResult("FAIL");
            logEntity.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            logEntity.setCostTime(System.currentTimeMillis() - start);
            saveLogAsync(logEntity);
        }
        return result;
    }

    @Async
    public void saveLogAsync(SysOperLog logEntity) {
        try {
            operLogMapper.insert(logEntity);
        } catch (Exception e) {
            log.warn("保存操作日志失败: {}", e.getMessage());
        }
    }
}
