package com.aistudio.service.service.impl;

import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.request.ProjectCreateRequest;
import com.aistudio.service.dto.request.ProjectUpdateRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.dto.response.ProjectVO;
import com.aistudio.service.entity.Project;
import com.aistudio.service.entity.SysDepartment;
import com.aistudio.service.entity.SysTeam;
import com.aistudio.service.entity.SysUser;
import com.aistudio.service.mapper.ProjectMapper;
import com.aistudio.service.mapper.SysDepartmentMapper;
import com.aistudio.service.mapper.SysTeamMapper;
import com.aistudio.service.mapper.SysUserMapper;
import com.aistudio.service.service.ProjectService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_ENDED = "ENDED";

    private final ProjectMapper projectMapper;
    private final SysUserMapper userMapper;
    private final SysDepartmentMapper departmentMapper;
    private final SysTeamMapper teamMapper;
    private final SecurityUtils securityUtils;

    @Override
    public PageResult<ProjectVO> listProjects(int page, int size, String keyword, String status, Long ownerId) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Project::getProjectName, keyword);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Project::getStatus, normalizeStatus(status));
        }
        if (ownerId != null) {
            wrapper.eq(Project::getOwnerId, ownerId);
        }
        applyDeptScope(wrapper);
        wrapper.orderByDesc(Project::getCreatedAt);

        Page<Project> result = projectMapper.selectPage(new Page<>(page, size), wrapper);
        List<ProjectVO> records = result.getRecords().stream().map(this::toVO).toList();
        return PageResult.of(result.getTotal(), records);
    }

    @Override
    public List<ProjectVO> listActiveProjects() {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<Project>()
                .eq(Project::getStatus, STATUS_ACTIVE)
                .orderByDesc(Project::getStartedAt)
                .orderByDesc(Project::getCreatedAt);
        applyDeptScope(wrapper);
        return projectMapper.selectList(wrapper).stream().map(this::toVO).toList();
    }

    @Override
    public List<ProjectVO> listActiveProjectsForCurrentUserTeam() {
        Long userId = securityUtils.getCurrentUserId();
        SysUser user = userMapper.selectById(userId);
        if (user == null || user.getTeamId() == null) {
            return List.of();
        }
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<Project>()
                .eq(Project::getStatus, STATUS_ACTIVE)
                .eq(Project::getTeamId, user.getTeamId())
                .orderByDesc(Project::getStartedAt)
                .orderByDesc(Project::getCreatedAt);
        applyDeptScope(wrapper);
        return projectMapper.selectList(wrapper).stream().map(this::toVO).toList();
    }

    @Override
    public ProjectVO getProject(Long id) {
        Project project = requireProject(id);
        assertCanManageProject(project);
        return toVO(project);
    }

    @Override
    @Transactional
    public Long createProject(ProjectCreateRequest request) {
        SysUser currentUser = requireCurrentUser();
        if (currentUser.getDeptId() == null) {
            throw new BusinessException(400, "当前登录用户未设置所属部门，无法创建项目");
        }
        if (currentUser.getTeamId() == null) {
            throw new BusinessException(400, "当前登录用户未设置所属团队，无法创建项目");
        }
        SysDepartment department = requireDepartment(currentUser.getDeptId());
        SysTeam team = requireTeam(currentUser.getTeamId(), department.getId());
        SysUser owner = requireOwner(request.getOwnerId());
        assertCanManageDept(department.getId());
        assertOwnerMatchesProjectScope(owner, department.getId(), team.getId());

        Project project = new Project();
        project.setProjectName(request.getProjectName());
        project.setDescription(request.getDescription());
        project.setOwnerId(request.getOwnerId());
        project.setDeptId(department.getId());
        project.setTeamId(team.getId());
        project.setStatus(StringUtils.hasText(request.getStatus()) ? normalizeStatus(request.getStatus()) : STATUS_ACTIVE);
        project.setStartedAt(request.getStartedAt() != null ? request.getStartedAt() : LocalDateTime.now());
        if (STATUS_ENDED.equals(project.getStatus())) {
            project.setEndedAt(LocalDateTime.now());
        }
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        projectMapper.insert(project);
        return project.getId();
    }

    @Override
    @Transactional
    public void updateProject(Long id, ProjectUpdateRequest request) {
        Project project = requireProject(id);
        assertCanManageProject(project);

        if (StringUtils.hasText(request.getProjectName())) {
            project.setProjectName(request.getProjectName());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }
        if (request.getOwnerId() != null) {
            requireOwner(request.getOwnerId());
            project.setOwnerId(request.getOwnerId());
        }
        Long nextDeptId = request.getDeptId() != null ? request.getDeptId() : project.getDeptId();
        Long nextTeamId = request.getTeamId() != null ? request.getTeamId() : project.getTeamId();
        if (nextDeptId != null) {
            SysDepartment department = requireDepartment(nextDeptId);
            assertCanManageDept(department.getId());
            project.setDeptId(department.getId());
        }
        if (nextTeamId != null) {
            if (project.getDeptId() == null) {
                throw new BusinessException(400, "请选择所属部门");
            }
            SysTeam team = requireTeam(nextTeamId, project.getDeptId());
            project.setTeamId(team.getId());
        }
        SysUser owner = requireOwner(project.getOwnerId());
        if (project.getDeptId() != null && project.getTeamId() != null) {
            assertOwnerMatchesProjectScope(owner, project.getDeptId(), project.getTeamId());
        } else {
            assertCanManageOwner(owner);
        }
        if (StringUtils.hasText(request.getStatus())) {
            applyStatus(project, normalizeStatus(request.getStatus()));
        }
        if (request.getStartedAt() != null) {
            project.setStartedAt(request.getStartedAt());
        }
        if (request.getEndedAt() != null) {
            project.setEndedAt(request.getEndedAt());
        }
        project.setUpdatedAt(LocalDateTime.now());
        projectMapper.updateById(project);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        Project project = requireProject(id);
        assertCanManageProject(project);
        projectMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateProjectStatus(Long id, String status) {
        Project project = requireProject(id);
        assertCanManageProject(project);
        applyStatus(project, normalizeStatus(status));
        project.setUpdatedAt(LocalDateTime.now());
        projectMapper.updateById(project);
    }

    private void applyDeptScope(LambdaQueryWrapper<Project> wrapper) {
        if ((!securityUtils.isDeptAdmin() && !securityUtils.isProjectManager()) || securityUtils.isSuperAdmin() || securityUtils.isOpAdmin()) {
            return;
        }
        Long deptId = securityUtils.getCurrentUserDeptId();
        if (deptId == null) {
            wrapper.eq(Project::getDeptId, -1L);
            return;
        }
        wrapper.eq(Project::getDeptId, deptId);
    }

    private Project requireProject(Long id) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new BusinessException(404, "项目不存在");
        }
        return project;
    }

    private SysUser requireOwner(Long ownerId) {
        SysUser owner = userMapper.selectById(ownerId);
        if (owner == null) {
            throw new BusinessException(404, "负责人不存在");
        }
        return owner;
    }

    private SysUser requireCurrentUser() {
        Long userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(401, "当前登录用户不存在");
        }
        return user;
    }

    private SysDepartment requireDepartment(Long deptId) {
        SysDepartment department = departmentMapper.selectById(deptId);
        if (department == null || (department.getStatus() != null && department.getStatus() != 1)) {
            throw new BusinessException(404, "所属部门不存在或未启用");
        }
        return department;
    }

    private SysTeam requireTeam(Long teamId, Long deptId) {
        SysTeam team = teamMapper.selectById(teamId);
        if (team == null || (team.getStatus() != null && team.getStatus() != 1)) {
            throw new BusinessException(404, "所属团队不存在或未启用");
        }
        if (!deptId.equals(team.getDeptId())) {
            throw new BusinessException(400, "所属团队不属于所选部门");
        }
        return team;
    }

    private void assertCanManageProject(Project project) {
        if (project.getDeptId() != null) {
            assertCanManageDept(project.getDeptId());
            return;
        }
        assertCanManageOwner(requireOwner(project.getOwnerId()));
    }

    private void assertCanManageOwner(SysUser owner) {
        assertCanManageDept(owner.getDeptId());
    }

    private void assertCanManageDept(Long deptId) {
        if (!securityUtils.canManageDept(deptId)) {
            throw new BusinessException(403, "无权操作该项目");
        }
    }

    private void assertOwnerMatchesProjectScope(SysUser owner, Long deptId, Long teamId) {
        assertCanManageOwner(owner);
        if (!deptId.equals(owner.getDeptId())) {
            throw new BusinessException(400, "负责人不属于所选部门");
        }
        if (!teamId.equals(owner.getTeamId())) {
            throw new BusinessException(400, "负责人不属于所选团队");
        }
    }

    private String normalizeStatus(String status) {
        String normalized = status.trim().toUpperCase();
        if (!STATUS_ACTIVE.equals(normalized) && !STATUS_ENDED.equals(normalized)) {
            throw new BusinessException(400, "项目状态仅支持 ACTIVE 或 ENDED");
        }
        return normalized;
    }

    private void applyStatus(Project project, String status) {
        project.setStatus(status);
        if (STATUS_ENDED.equals(status) && project.getEndedAt() == null) {
            project.setEndedAt(LocalDateTime.now());
        }
        if (STATUS_ACTIVE.equals(status)) {
            project.setEndedAt(null);
        }
    }

    private ProjectVO toVO(Project project) {
        ProjectVO vo = new ProjectVO();
        BeanUtils.copyProperties(project, vo);
        if (project.getDeptId() != null) {
            SysDepartment department = departmentMapper.selectById(project.getDeptId());
            if (department != null) {
                vo.setDeptName(department.getDeptName());
            }
        }
        if (project.getTeamId() != null) {
            SysTeam team = teamMapper.selectById(project.getTeamId());
            if (team != null) {
                vo.setTeamName(team.getTeamName());
            }
        }
        SysUser owner = userMapper.selectById(project.getOwnerId());
        if (owner != null) {
            vo.setOwnerName(StringUtils.hasText(owner.getRealName()) ? owner.getRealName() : owner.getUsername());
            vo.setOwnerDeptId(owner.getDeptId());
            if (owner.getDeptId() != null) {
                SysDepartment department = departmentMapper.selectById(owner.getDeptId());
                if (department != null) {
                    vo.setOwnerDeptName(department.getDeptName());
                }
            }
        }
        return vo;
    }
}
