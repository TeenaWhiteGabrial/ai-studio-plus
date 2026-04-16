package com.aistudio.service.service.impl;

import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.request.TeamCreateRequest;
import com.aistudio.service.dto.request.TeamUpdateRequest;
import com.aistudio.service.entity.SysTeam;
import com.aistudio.service.entity.SysTeamMember;
import com.aistudio.service.entity.SysUser;
import com.aistudio.service.mapper.SysTeamMapper;
import com.aistudio.service.mapper.SysTeamMemberMapper;
import com.aistudio.service.mapper.SysUserMapper;
import com.aistudio.service.service.TeamService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final SysTeamMapper teamMapper;
    private final SysTeamMemberMapper teamMemberMapper;
    private final SysUserMapper userMapper;
    private final SecurityUtils securityUtils;

    @Override
    public List<Object> listTeams(Long deptId) {
        LambdaQueryWrapper<SysTeam> wrapper = new LambdaQueryWrapper<>();
        if (deptId != null) {
            wrapper.eq(SysTeam::getDeptId, deptId);
        }
        return new ArrayList<>(teamMapper.selectList(wrapper));
    }

    @Override
    @Transactional
    public Long createTeam(TeamCreateRequest request) {
        // DEPT_ADMIN 创建团队时，自动将 deptId 设为当前用户的部门
        if (securityUtils.isDeptAdmin()) {
            Long myDeptId = securityUtils.getCurrentUserDeptId();
            if (myDeptId == null) {
                throw new BusinessException(400, "部门管理员未分配部门");
            }
            request.setDeptId(myDeptId);
        }
        SysTeam team = new SysTeam();
        team.setTeamName(request.getTeamName());
        team.setDeptId(request.getDeptId());
        team.setDescription(request.getDescription());
        team.setStatus(1);
        team.setCreatedAt(LocalDateTime.now());
        team.setUpdatedAt(LocalDateTime.now());
        teamMapper.insert(team);
        return team.getId();
    }

    @Override
    public void updateTeam(Long id, TeamUpdateRequest request) {
        SysTeam team = teamMapper.selectById(id);
        if (team == null) throw new BusinessException(404, "团队不存在");
        // 部门管理员只能操作自己部门的团队
        if (!securityUtils.canManageDept(team.getDeptId())) {
            throw new BusinessException(403, "无权操作该部门团队");
        }
        if (request.getTeamName() != null) team.setTeamName(request.getTeamName());
        if (request.getDescription() != null) team.setDescription(request.getDescription());
        if (request.getStatus() != null) team.setStatus(request.getStatus());
        team.setUpdatedAt(LocalDateTime.now());
        teamMapper.updateById(team);
    }

    @Override
    @Transactional
    public void deleteTeam(Long id) {
        SysTeam team = teamMapper.selectById(id);
        if (team == null) throw new BusinessException(404, "团队不存在");
        // 部门管理员只能操作自己部门的团队
        if (!securityUtils.canManageDept(team.getDeptId())) {
            throw new BusinessException(403, "无权操作该部门团队");
        }
        // 删除团队成员
        teamMemberMapper.delete(new LambdaQueryWrapper<SysTeamMember>().eq(SysTeamMember::getTeamId, id));
        // 删除团队
        teamMapper.deleteById(id);
    }

    @Override
    public List<SysUser> getTeamMembers(Long teamId) {
        SysTeam team = teamMapper.selectById(teamId);
        if (team == null) throw new BusinessException(404, "团队不存在");
        // 部门管理员只能查看自己部门的团队成员
        if (!securityUtils.canManageDept(team.getDeptId())) {
            throw new BusinessException(403, "无权操作该部门团队");
        }
        List<SysTeamMember> members = teamMemberMapper.selectList(
                new LambdaQueryWrapper<SysTeamMember>().eq(SysTeamMember::getTeamId, teamId));
        List<SysUser> users = new ArrayList<>();
        for (SysTeamMember member : members) {
            SysUser user = userMapper.selectById(member.getUserId());
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }

    @Override
    @Transactional
    public void addTeamMembers(Long teamId, List<Long> userIds) {
        SysTeam team = teamMapper.selectById(teamId);
        if (team == null) throw new BusinessException(404, "团队不存在");
        // 部门管理员只能操作自己部门的团队
        if (!securityUtils.canManageDept(team.getDeptId())) {
            throw new BusinessException(403, "无权操作该部门团队");
        }
        // 校验所有成员属于同一部门
        for (Long userId : userIds) {
            SysUser user = userMapper.selectById(userId);
            if (user == null) throw new BusinessException(404, "用户不存在: " + userId);
            if (!team.getDeptId().equals(user.getDeptId())) {
                throw new BusinessException(400, "只能添加同部门成员到团队");
            }
            // 检查是否已存在
            long count = teamMemberMapper.selectCount(
                    new LambdaQueryWrapper<SysTeamMember>()
                            .eq(SysTeamMember::getTeamId, teamId)
                            .eq(SysTeamMember::getUserId, userId));
            if (count == 0) {
                SysTeamMember member = new SysTeamMember();
                member.setTeamId(teamId);
                member.setUserId(userId);
                member.setRole("MEMBER");
                member.setJoinedAt(LocalDateTime.now());
                teamMemberMapper.insert(member);
            }
        }
    }

    @Override
    @Transactional
    public void removeTeamMember(Long teamId, Long userId) {
        SysTeam team = teamMapper.selectById(teamId);
        if (team == null) throw new BusinessException(404, "团队不存在");
        // 部门管理员只能操作自己部门的团队
        if (!securityUtils.canManageDept(team.getDeptId())) {
            throw new BusinessException(403, "无权操作该部门团队");
        }
        teamMemberMapper.delete(
                new LambdaQueryWrapper<SysTeamMember>()
                        .eq(SysTeamMember::getTeamId, teamId)
                        .eq(SysTeamMember::getUserId, userId));
    }
}
