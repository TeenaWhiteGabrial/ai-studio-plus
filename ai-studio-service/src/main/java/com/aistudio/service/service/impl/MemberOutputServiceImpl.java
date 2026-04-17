package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.request.OpenOutputSubmitRequest;
import com.aistudio.service.entity.MemberOutput;
import com.aistudio.service.entity.SysDepartment;
import com.aistudio.service.entity.SysUser;
import com.aistudio.service.mapper.MemberOutputMapper;
import com.aistudio.service.mapper.SysDepartmentMapper;
import com.aistudio.service.mapper.SysUserMapper;
import com.aistudio.service.service.MemberOutputService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberOutputServiceImpl implements MemberOutputService {

    private final MemberOutputMapper memberOutputMapper;
    private final SysUserMapper sysUserMapper;
    private final SysDepartmentMapper departmentMapper;

    @Override
    public MemberOutput getTodayOutput(Long userId) {
        return memberOutputMapper.selectOne(new LambdaQueryWrapper<MemberOutput>()
                .eq(MemberOutput::getUserId, userId)
                .eq(MemberOutput::getStatDate, LocalDate.now()));
    }

    @Override
    public List<MemberOutput> getHistory(Long userId, LocalDate startDate, LocalDate endDate) {
        return memberOutputMapper.selectHistoryWithUserInfo(userId, startDate, endDate);
    }

    @Override
    public List<MemberOutput> getHistory(Long userId, LocalDate startDate, LocalDate endDate, List<String> projectNames) {
        LambdaQueryWrapper<MemberOutput> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberOutput::getUserId, userId)
                .between(MemberOutput::getStatDate, startDate, endDate)
                .orderByDesc(MemberOutput::getStatDate);
        if (projectNames != null && !projectNames.isEmpty()) {
            wrapper.in(MemberOutput::getProjectRootName, projectNames);
        }
        return memberOutputMapper.selectList(wrapper);
    }

    @Override
    public List<Map<String, Object>> getOutputList(LocalDate date, List<Long> userIds, List<Long> deptIds, List<String> projectNames) {
        return memberOutputMapper.selectAllByDateWithFilters(date, userIds, deptIds, projectNames);
    }

    @Override
    public List<Map<String, Object>> getOutputByUsers(List<Long> userIds, LocalDate startDate, LocalDate endDate, List<String> projectNames) {
        return memberOutputMapper.selectOutputByUsers(userIds, startDate, endDate, projectNames);
    }

    @Override
    public Map<String, Object> getStats(Long userId, LocalDate startDate, LocalDate endDate, boolean isAdmin, List<Long> userIds, List<Long> deptIds, List<String> projectNames) {
        if (isAdmin) {
            return memberOutputMapper.selectStatsAllWithFilters(startDate, endDate, userIds, deptIds, projectNames);
        }
        return memberOutputMapper.selectStatsByUser(userId, startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> getOutputByDepartment(LocalDate startDate, LocalDate endDate, List<Long> deptIds) {
        return memberOutputMapper.selectDepartmentSummary(deptIds, startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> getOutputByProject(LocalDate startDate, LocalDate endDate, List<String> projectNames) {
        return memberOutputMapper.selectProjectSummary(projectNames, startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> getProjectMembers(String projectName, LocalDate startDate, LocalDate endDate) {
        return memberOutputMapper.selectProjectMembers(projectName, startDate, endDate);
    }

    @Override
    public void submitOutputByUsername(OpenOutputSubmitRequest request) {
        // 1. 校验 username 不能为空
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new BusinessException("username 不能为空");
        }

        // 2. 校验用户是否存在
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.getUsername().trim()));
        if (user == null) {
            throw new BusinessException("用户不存在：" + request.getUsername());
        }

        // 3. 如果 stat_date 为空，默认使用当前日期
        if (request.getStatDate() == null) {
            request.setStatDate(LocalDate.now());
        }

        // 4. 规范化 project_root_name（空值统一为 null）
        String projectRootName = request.getProjectRootName();
        if (projectRootName != null && projectRootName.trim().isEmpty()) {
            projectRootName = null;
            request.setProjectRootName(null);
        }

        // 5. 按 (username, stat_date, project_root_name) 查询记录
        LambdaQueryWrapper<MemberOutput> wrapper = new LambdaQueryWrapper<MemberOutput>()
                .eq(MemberOutput::getUserName, request.getUsername().trim())
                .eq(MemberOutput::getStatDate, request.getStatDate());

        // project_root_name 为 null 时按 null 查询，不为 null 时按值查询
        if (projectRootName == null) {
            wrapper.isNull(MemberOutput::getProjectRootName);
        } else {
            wrapper.eq(MemberOutput::getProjectRootName, projectRootName);
        }

        MemberOutput existing = memberOutputMapper.selectOne(wrapper);

        if (existing != null) {
            // 更新已有数据
            updateOutputFromRequest(existing, request);
            memberOutputMapper.updateById(existing);
        } else {
            // 新建数据
            MemberOutput output = new MemberOutput();
            output.setUserId(user.getId());
            output.setUserName(request.getUsername().trim());
            output.setStatDate(request.getStatDate());
            updateOutputFromRequest(output, request);
            memberOutputMapper.insert(output);
        }
    }

    private void updateOutputFromRequest(MemberOutput output, OpenOutputSubmitRequest request) {
        output.setPrdDocCount(request.getPrdDocCount());
        output.setDataModelDocCount(request.getDataModelDocCount());
        output.setApiDocCount(request.getApiDocCount());
        output.setJavaFileCount(request.getJavaFileCount());
        output.setJavaCodeLines(request.getJavaCodeLines());
        output.setApiCount(request.getApiCount());
        output.setCoreBizServiceCount(request.getCoreBizServiceCount());
        output.setEntityCount(request.getEntityCount());
        output.setFrontendComponentCount(request.getFrontendComponentCount());
        output.setFrontendPageCount(request.getFrontendPageCount());
        output.setFrontendCommonComponentCount(request.getFrontendCommonComponentCount());
        output.setTsCodeLines(request.getTsCodeLines());
        output.setFrontendCodeLines(request.getFrontendCodeLines());
        output.setSqlScriptCount(request.getSqlScriptCount());
        output.setTestFileCount(request.getTestFileCount());
        output.setTotalCodeLines(request.getTotalCodeLines());
        output.setRemark(request.getRemark());
        output.setProjectRootName(request.getProjectRootName());
    }
}
