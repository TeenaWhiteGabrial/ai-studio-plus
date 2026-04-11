package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.request.BatchUserImportRequest;
import com.aistudio.service.dto.request.UserCreateRequest;
import com.aistudio.service.dto.request.UserUpdateRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.dto.response.UserImportResult;
import com.aistudio.service.entity.SysUser;
import com.aistudio.service.entity.SysUserRole;
import com.aistudio.service.entity.SysDepartment;
import com.aistudio.service.mapper.SysDepartmentMapper;
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
    private final SysUserRoleMapper userRoleMapper;
    private final SysDepartmentMapper departmentMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResult<SysUser> listUsers(int page, int size, String keyword, String department) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SysUser::getUsername, keyword);
        }
        if (StringUtils.hasText(department)) {
            // 根据部门名称查询部门ID，然后筛选用户
            SysDepartment dept = departmentMapper.selectOne(
                new LambdaQueryWrapper<SysDepartment>().eq(SysDepartment::getDeptName, department));
            if (dept != null) {
                wrapper.eq(SysUser::getDeptId, dept.getId());
            }
        }
        Page<SysUser> p = userMapper.selectPage(new Page<>(page, size), wrapper);
        // 为每个用户加载角色 ID 列表和部门名称
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
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setDeptId(request.getDeptId());
        user.setEmail(request.getEmail());
        user.setStatus(1);
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public void updateUser(Long id, UserUpdateRequest request) {
        SysUser user = userMapper.selectById(id);
        if (user == null) throw new BusinessException(404, "用户不存在");
        if (request.getRealName() != null) user.setRealName(request.getRealName());
        if (request.getDeptId() != null) user.setDeptId(request.getDeptId());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getStatus() != null) user.setStatus(request.getStatus());
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
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
                                    request.getRealName(), request.getDepartment(), request.getEmail()),
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

                // 创建用户
                SysUser user = new SysUser();
                user.setUsername(request.getUsername());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setRealName(request.getRealName());
                user.setDeptId(deptId);
                user.setEmail(request.getEmail());
                user.setStatus(1);
                userMapper.insert(user);

                results.add(new UserImportResult(true,
                        new UserImportResult.UserImportData(request.getUsername(),
                                request.getRealName(), request.getDepartment(), request.getEmail()),
                        null));
            } catch (Exception e) {
                results.add(new UserImportResult(false,
                        new UserImportResult.UserImportData(request.getUsername(),
                                request.getRealName(), request.getDepartment(), request.getEmail()),
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
        user.setStatus(status);
        userMapper.updateById(user);
    }
}
