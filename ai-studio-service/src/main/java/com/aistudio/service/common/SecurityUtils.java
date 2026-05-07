package com.aistudio.service.common;

import com.aistudio.service.entity.SysUser;
import com.aistudio.service.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final SysUserMapper userMapper;

    public Long getCurrentUserId() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            Object userId = attrs.getRequest().getAttribute("currentUserId");
            if (userId != null) {
                return (Long) userId;
            }
        }
        // fallback: query by username
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            SysUser user = userMapper.selectOne(
                    new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
            return user != null ? user.getId() : null;
        }
        return null;
    }

    public Long getCurrentUserDeptId() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return null;
        }
        SysUser user = userMapper.selectById(userId);
        return user != null ? user.getDeptId() : null;
    }

    public List<String> getCurrentUserRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return List.of();
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .collect(Collectors.toList());
    }

    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_OP_ADMIN") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));
    }

    public boolean isSuperAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));
    }

    public boolean isDeptAdmin() {
        return getCurrentUserRoles().contains("DEPT_ADMIN");
    }

    public boolean isOpAdmin() {
        return getCurrentUserRoles().contains("OP_ADMIN");
    }

    public boolean isProjectManager() {
        return getCurrentUserRoles().contains("PROJECT_MANAGER");
    }

    /**
     * 判断当前用户是否有权操作指定部门的数据
     * SUPER_ADMIN 和 OP_ADMIN 可以操作任何部门
     * DEPT_ADMIN 和 PROJECT_MANAGER 只能操作自己所属部门的数据
     */
    public boolean canManageDept(Long targetDeptId) {
        if (isSuperAdmin() || isOpAdmin()) return true;
        if (isDeptAdmin() || isProjectManager()) {
            Long myDeptId = getCurrentUserDeptId();
            return myDeptId != null && myDeptId.equals(targetDeptId);
        }
        return false;
    }
}
