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
import com.aistudio.service.entity.SysUser;
import com.aistudio.service.entity.SysUserRole;
import com.aistudio.service.entity.SysDepartment;
import com.aistudio.service.entity.SysTeam;
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
            wrapper.like(SysUser::getUsername, keyword);
        }
        if (StringUtils.hasText(deptIdOrName)) {
            // 优先尝试作为数字 deptId 查询
            Long deptId = null;
            try {
                deptId = Long.parseLong(deptIdOrName);
            } catch (NumberFormatException ignored) {
                // 不是数字，按部门名称查询
            }
            if (deptId != null) {
                wrapper.eq(SysUser::getDeptId, deptId);
            } else {
                // 根据部门名称查询部门ID，然后筛选用户
                SysDepartment dept = departmentMapper.selectOne(
                    new LambdaQueryWrapper<SysDepartment>().eq(SysDepartment::getDeptName, deptIdOrName));
                if (dept != null) {
                    wrapper.eq(SysUser::getDeptId, dept.getId());
                }
            }
        }
        Page<SysUser> p = userMapper.selectPage(new Page<>(page, size), wrapper);
        // 为每个用户加载角色 ID 列表、部门名称和团队名称
        for (SysUser user : p.getRecords()) {
            List<Long> roleIds = userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, user.getId())
            ).stream().map(SysUserRole::getRoleId).toList();
            user.setRoleIds(roleIds);
            // 加载部门名称
            if (user.getDeptId() != null) {
                SysDepartment dept = departmentMapper.selectById(user.getDeptId());
                if (dept != null) {
                    user.setDeptName(dept.getDeptName());
                }
            }
            // 加载团队名称
            if (user.getTeamId() != null) {
                SysTeam team = teamMapper.selectById(user.getTeamId());
                if (team != null) {
                    user.setTeamName(team.getTeamName());
                }
            }
        }
        return PageResult.of(p.getTotal(), p.getRecords());
    }

    @Override
    @Transactional
    public Long createUser(UserCreateRequest request) {
        if (userMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.getUsername())) > 0) {
            throw new BusinessException("用户名已存在");
        }
        // DEPT_ADMIN 创建用户时，自动将 deptId 设为当前用户的部门
        if (securityUtils.isDeptAdmin()) {
            Long myDeptId = securityUtils.getCurrentUserDeptId();
            if (myDeptId == null) {
                throw new BusinessException(400, "部门管理员未分配部门");
            }
            request.setDeptId(myDeptId);
        }
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setDeptId(request.getDeptId());
        user.setTeamId(request.getTeamId());
        user.setEmail(request.getEmail());
        user.setAvatar(request.getAvatar());
        user.setStatus(1);
        userMapper.insert(user);
        // 插入用户角色关联
        List<Long> roleIds = request.getRoleIds();
        if (roleIds == null || roleIds.isEmpty()) {
            roleIds = List.of(4L); // 默认分配"普通用户"角色
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
        SysUser user = userMapper.selectById(id);
        if (user == null) throw new BusinessException(404, "用户不存在");
        // 部门管理员只能操作自己部门的用户
        if (!securityUtils.canManageDept(user.getDeptId())) {
            throw new BusinessException(403, "无权操作该部门用户");
        }
        if (request.getRealName() != null) user.setRealName(request.getRealName());
        // DEPT_ADMIN 不能修改用户部门
        if (request.getDeptId() != null && !securityUtils.isDeptAdmin()) {
            user.setDeptId(request.getDeptId());
        }
        // 团队ID可以修改
        user.setTeamId(request.getTeamId());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getAvatar() != null) user.setAvatar(request.getAvatar());
        if (request.getStatus() != null) user.setStatus(request.getStatus());
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) throw new BusinessException(404, "用户不存在");
        // 部门管理员只能删除自己部门的用户
        if (!securityUtils.canManageDept(user.getDeptId())) {
            throw new BusinessException(403, "无权操作该部门用户");
        }
        userMapper.deleteById(id);
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        for (Long roleId : roleIds) {
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
                // 检查用户名是否已存在
                if (userMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, request.getUsername())) > 0) {
                    results.add(new UserImportResult(false,
                            new UserImportResult.UserImportData(request.getUsername(),
                                    request.getRealName(), request.getDepartment(), request.getTeam(), request.getEmail()),
                            "用户名已存在"));
                    continue;
                }

                // 根据部门名称查询部门ID
                Long deptId = null;
                if (request.getDepartment() != null && !request.getDepartment().isEmpty()) {
                    SysDepartment dept = departmentMapper.selectOne(
                            new LambdaQueryWrapper<SysDepartment>()
                                    .eq(SysDepartment::getDeptName, request.getDepartment()));
                    if (dept != null) {
                        deptId = dept.getId();
                    }
                }

                // 根据团队名称查询团队ID
                Long teamId = null;
                if (request.getTeam() != null && !request.getTeam().isEmpty()) {
                    SysTeam team = teamMapper.selectOne(
                            new LambdaQueryWrapper<SysTeam>()
                                    .eq(SysTeam::getTeamName, request.getTeam()));
                    if (team != null) {
                        teamId = team.getId();
                    }
                }

                // 创建用户
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
                results.add(new UserImportResult(false,
                        new UserImportResult.UserImportData(request.getUsername(),
                                request.getRealName(), request.getDepartment(), request.getTeam(), request.getEmail()),
                        e.getMessage()));
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
        SysUser user = userMapper.selectById(id);
        if (user == null) throw new BusinessException(404, "用户不存在");
        // 部门管理员只能操作自己部门的用户
        if (!securityUtils.canManageDept(user.getDeptId())) {
            throw new BusinessException(403, "无权操作该部门用户");
        }
        // 不能禁用自己
        if (securityUtils.getCurrentUserId() != null
                && securityUtils.getCurrentUserId().equals(id)) {
            throw new BusinessException(403, "不能禁用当前登录用户");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 只能修改自己的密码
        if (!securityUtils.getCurrentUserId().equals(userId)) {
            throw new BusinessException(403, "只能修改自己的密码");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(400, "旧密码不正确");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userMapper.updateById(user);
    }

    @Override
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 只能修改自己的资料
        if (!securityUtils.getCurrentUserId().equals(userId)) {
            throw new BusinessException(403, "只能修改自己的资料");
        }

        // 更新邮箱
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        // 更新头像
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }

        userMapper.updateById(user);
    }
}
