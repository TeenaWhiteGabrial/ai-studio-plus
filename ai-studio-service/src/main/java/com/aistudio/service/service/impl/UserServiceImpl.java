package com.aistudio.service.service.impl;

import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.request.BatchUserImportRequest;
import com.aistudio.service.dto.request.ChangePasswordRequest;
import com.aistudio.service.dto.request.UpdateProfileRequest;
import com.aistudio.service.dto.request.UserCreateRequest;
import com.aistudio.service.dto.request.UserUpdateRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.dto.response.UserImportResult;
import com.aistudio.service.entity.SysDepartment;
import com.aistudio.service.entity.SysTeam;
import com.aistudio.service.entity.SysUser;
import com.aistudio.service.entity.SysUserRole;
import com.aistudio.service.mapper.SysDepartmentMapper;
import com.aistudio.service.mapper.SysTeamMapper;
import com.aistudio.service.mapper.SysUserMapper;
import com.aistudio.service.mapper.SysUserRoleMapper;
import com.aistudio.service.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SysUserMapper userMapper;
    private final SecurityUtils securityUtils;
    private final SysUserRoleMapper userRoleMapper;
    private final SysDepartmentMapper departmentMapper;
    private final SysTeamMapper teamMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResult<SysUser> listUsers(int page, int size, String keyword, String deptIdOrName) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or()
                    .like(SysUser::getGitName, keyword)
                    .or()
                    .like(SysUser::getRealName, keyword));
        }
        if (StringUtils.hasText(deptIdOrName)) {
            Long deptId = null;
            try {
                deptId = Long.parseLong(deptIdOrName);
            } catch (NumberFormatException ignored) {
                // Search by department name below.
            }
            if (deptId != null) {
                wrapper.eq(SysUser::getDeptId, deptId);
            } else {
                SysDepartment dept = departmentMapper.selectOne(
                        new LambdaQueryWrapper<SysDepartment>().eq(SysDepartment::getDeptName, deptIdOrName));
                if (dept != null) {
                    wrapper.eq(SysUser::getDeptId, dept.getId());
                }
            }
        }

        Page<SysUser> p = userMapper.selectPage(new Page<>(page, size), wrapper);
        for (SysUser user : p.getRecords()) {
            loadUserRelations(user);
        }
        return PageResult.of(p.getTotal(), p.getRecords());
    }

    @Override
    @Transactional
    public Long createUser(UserCreateRequest request) {
        if (userMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.getUsername())) > 0) {
            throw new BusinessException("username already exists");
        }

        String gitName = normalizeText(request.getGitName());
        validateGitNameAvailable(gitName, null);

        if (securityUtils.isDeptAdmin()) {
            Long myDeptId = securityUtils.getCurrentUserDeptId();
            if (myDeptId == null) {
                throw new BusinessException(400, "department admin has no department");
            }
            request.setDeptId(myDeptId);
        }

        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setGitName(gitName);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setDeptId(request.getDeptId());
        user.setTeamId(request.getTeamId());
        user.setEmail(request.getEmail());
        user.setAvatar(request.getAvatar());
        user.setStatus(1);
        userMapper.insert(user);

        List<Long> roleIds = request.getRoleIds();
        if (roleIds == null || roleIds.isEmpty()) {
            roleIds = List.of(4L);
        }
        for (Long roleId : roleIds) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        }
        return user.getId();
    }

    @Override
    public void updateUser(Long id, UserUpdateRequest request) {
        SysUser user = requireUser(id);
        if (!securityUtils.canManageDept(user.getDeptId())) {
            throw new BusinessException(403, "no permission to manage this department user");
        }

        if (request.getGitName() != null) {
            String gitName = normalizeText(request.getGitName());
            validateGitNameAvailable(gitName, id);
            user.setGitName(gitName);
        }
        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }
        if (request.getDeptId() != null && !securityUtils.isDeptAdmin()) {
            user.setDeptId(request.getDeptId());
        }
        user.setTeamId(request.getTeamId());
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        SysUser user = requireUser(id);
        if (!securityUtils.canManageDept(user.getDeptId())) {
            throw new BusinessException(403, "no permission to delete this department user");
        }
        userMapper.deleteById(id);
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            throw new BusinessException(400, "at least one role is required");
        }
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        for (Long roleId : roleIds.stream().distinct().toList()) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            userRoleMapper.insert(ur);
        }
    }

    @Override
    @Transactional
    public List<UserImportResult> batchImport(List<BatchUserImportRequest> requests) {
        List<UserImportResult> results = new ArrayList<>();
        for (BatchUserImportRequest request : requests) {
            try {
                if (userMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, request.getUsername())) > 0) {
                    results.add(importResult(request, "username already exists"));
                    continue;
                }

                Long deptId = resolveDeptId(request.getDepartment());
                Long teamId = resolveTeamId(request.getTeam());

                SysUser user = new SysUser();
                user.setUsername(request.getUsername());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setRealName(request.getRealName());
                user.setDeptId(deptId);
                user.setTeamId(teamId);
                user.setEmail(request.getEmail());
                user.setStatus(1);
                userMapper.insert(user);

                results.add(new UserImportResult(true,
                        new UserImportResult.UserImportData(request.getUsername(),
                                request.getRealName(), request.getDepartment(), request.getTeam(), request.getEmail()),
                        null));
            } catch (Exception e) {
                results.add(importResult(request, e.getMessage()));
            }
        }
        return results;
    }

    @Override
    public SysUser getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        SysUser user = requireUser(id);
        if (!securityUtils.canManageDept(user.getDeptId())) {
            throw new BusinessException(403, "no permission to update this department user");
        }
        if (securityUtils.getCurrentUserId() != null && securityUtils.getCurrentUserId().equals(id)) {
            throw new BusinessException(403, "can not disable current user");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        SysUser user = requireUser(userId);
        if (!securityUtils.getCurrentUserId().equals(userId)) {
            throw new BusinessException(403, "can only change your own password");
        }
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(400, "old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userMapper.updateById(user);
    }

    @Override
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        SysUser user = requireUser(userId);
        if (!securityUtils.getCurrentUserId().equals(userId)) {
            throw new BusinessException(403, "can only update your own profile");
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getGitName() != null) {
            String gitName = normalizeText(request.getGitName());
            validateGitNameAvailable(gitName, userId);
            user.setGitName(gitName);
        }
        userMapper.updateById(user);
    }

    private void loadUserRelations(SysUser user) {
        List<Long> roleIds = userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, user.getId())
        ).stream().map(SysUserRole::getRoleId).toList();
        user.setRoleIds(roleIds);

        if (user.getDeptId() != null) {
            SysDepartment dept = departmentMapper.selectById(user.getDeptId());
            if (dept != null) {
                user.setDeptName(dept.getDeptName());
            }
        }
        if (user.getTeamId() != null) {
            SysTeam team = teamMapper.selectById(user.getTeamId());
            if (team != null) {
                user.setTeamName(team.getTeamName());
            }
        }
    }

    private SysUser requireUser(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "user does not exist");
        }
        return user;
    }

    private Long resolveDeptId(String department) {
        if (!StringUtils.hasText(department)) {
            return null;
        }
        SysDepartment dept = departmentMapper.selectOne(
                new LambdaQueryWrapper<SysDepartment>().eq(SysDepartment::getDeptName, department));
        return dept == null ? null : dept.getId();
    }

    private Long resolveTeamId(String teamName) {
        if (!StringUtils.hasText(teamName)) {
            return null;
        }
        SysTeam team = teamMapper.selectOne(
                new LambdaQueryWrapper<SysTeam>().eq(SysTeam::getTeamName, teamName));
        return team == null ? null : team.getId();
    }

    private UserImportResult importResult(BatchUserImportRequest request, String error) {
        return new UserImportResult(false,
                new UserImportResult.UserImportData(request.getUsername(),
                        request.getRealName(), request.getDepartment(), request.getTeam(), request.getEmail()),
                error);
    }

    private void validateGitNameAvailable(String gitName, Long excludeUserId) {
        if (!StringUtils.hasText(gitName)) {
            return;
        }
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getGitName, gitName);
        if (excludeUserId != null) {
            wrapper.ne(SysUser::getId, excludeUserId);
        }
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("Git username already exists");
        }
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
