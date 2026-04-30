package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.request.OpenOutputSubmitRequest;
import com.aistudio.service.entity.MemberOutput;
import com.aistudio.service.entity.SysUser;
import com.aistudio.service.mapper.MemberOutputMapper;
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

    private static final String DEFAULT_OUTPUT_TYPE = "DEV_WORK_SUMMARY";

    private final MemberOutputMapper memberOutputMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public MemberOutput getTodayOutput(Long userId) {
        List<MemberOutput> outputs = memberOutputMapper.selectList(new LambdaQueryWrapper<MemberOutput>()
                .eq(MemberOutput::getUserId, userId)
                .eq(MemberOutput::getStatDate, LocalDate.now()));
        return aggregateOutputs(userId, LocalDate.now(), outputs);
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
    public Map<String, Object> getStats(Long userId, LocalDate startDate, LocalDate endDate, boolean isAdmin,
                                        List<Long> userIds, List<Long> deptIds, List<String> projectNames) {
        if (isAdmin) {
            return memberOutputMapper.selectStatsAllWithFilters(startDate, endDate, userIds, deptIds, projectNames);
        }
        return memberOutputMapper.selectStatsByUser(userId, startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> getDashboardDetails(LocalDate startDate, LocalDate endDate, List<Long> userIds,
                                                         List<Long> deptIds, List<Long> teamIds, List<String> projectNames) {
        return memberOutputMapper.selectDashboardDetails(startDate, endDate, userIds, deptIds, teamIds, projectNames);
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
        String gitName = normalizeText(request.getGitName());
        if (gitName == null) {
            throw new BusinessException("git_name can not be blank");
        }

        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getGitName, gitName));
        if (user == null) {
            throw new BusinessException("git_name is not bound to a user: " + gitName);
        }

        if (request.getStatDate() == null) {
            request.setStatDate(LocalDate.now());
        }

        String projectRootName = normalizeText(request.getProjectRootName());
        request.setProjectRootName(projectRootName);

        String outputType = normalizeText(request.getOutputType());
        if (outputType == null) {
            outputType = DEFAULT_OUTPUT_TYPE;
            request.setOutputType(outputType);
        }

        LambdaQueryWrapper<MemberOutput> wrapper = new LambdaQueryWrapper<MemberOutput>()
                .eq(MemberOutput::getUserId, user.getId())
                .eq(MemberOutput::getStatDate, request.getStatDate())
                .eq(MemberOutput::getOutputType, outputType);

        if (projectRootName == null) {
            wrapper.isNull(MemberOutput::getProjectRootName);
        } else {
            wrapper.eq(MemberOutput::getProjectRootName, projectRootName);
        }

        MemberOutput existing = memberOutputMapper.selectOne(wrapper);
        if (existing != null) {
            updateOutputFromRequest(existing, request, user, gitName);
            memberOutputMapper.updateById(existing);
            return;
        }

        MemberOutput output = new MemberOutput();
        output.setUserId(user.getId());
        output.setStatDate(request.getStatDate());
        updateOutputFromRequest(output, request, user, gitName);
        memberOutputMapper.insert(output);
    }

    private void updateOutputFromRequest(MemberOutput output, OpenOutputSubmitRequest request, SysUser user, String gitName) {
        output.setUserId(user.getId());
        output.setGitName(gitName);
        output.setProjectId(request.getProjectId());
        output.setProjectRootName(request.getProjectRootName());
        output.setOutputType(request.getOutputType());
        output.setResourceId(request.getResourceId());

        output.setPrdDocCount(value(request.getPrdDocCount()));
        output.setDataModelDocCount(value(request.getDataModelDocCount()));
        output.setApiDocCount(value(request.getApiDocCount()));
        output.setJavaFileCount(value(request.getJavaFileCount()));
        output.setJavaCodeLines(value(request.getJavaCodeLines()));
        output.setApiCount(value(request.getApiCount()));
        output.setCoreBizServiceCount(value(request.getCoreBizServiceCount()));
        output.setEntityCount(value(request.getEntityCount()));
        output.setFrontendComponentCount(value(request.getFrontendComponentCount()));
        output.setFrontendPageCount(value(request.getFrontendPageCount()));
        output.setFrontendCommonComponentCount(value(request.getFrontendCommonComponentCount()));
        output.setTsCodeLines(value(request.getTsCodeLines()));
        output.setFrontendCodeLines(value(request.getFrontendCodeLines()));
        output.setSqlScriptCount(value(request.getSqlScriptCount()));
        output.setTestFileCount(value(request.getTestFileCount()));
        output.setTotalCodeLines(value(request.getTotalCodeLines()));
        output.setRemark(request.getRemark());
    }

    private MemberOutput aggregateOutputs(Long userId, LocalDate statDate, List<MemberOutput> outputs) {
        if (outputs == null || outputs.isEmpty()) {
            return null;
        }

        MemberOutput total = new MemberOutput();
        total.setUserId(userId);
        total.setStatDate(statDate);
        total.setOutputType("SUMMARY");

        if (outputs.size() == 1) {
            total.setProjectId(outputs.get(0).getProjectId());
            total.setProjectRootName(outputs.get(0).getProjectRootName());
            total.setResourceId(outputs.get(0).getResourceId());
            total.setOutputType(outputs.get(0).getOutputType());
            total.setRemark(outputs.get(0).getRemark());
        }

        for (MemberOutput output : outputs) {
            if (total.getGitName() == null) {
                total.setGitName(output.getGitName());
            }
            total.setPrdDocCount(add(total.getPrdDocCount(), output.getPrdDocCount()));
            total.setDataModelDocCount(add(total.getDataModelDocCount(), output.getDataModelDocCount()));
            total.setApiDocCount(add(total.getApiDocCount(), output.getApiDocCount()));
            total.setJavaFileCount(add(total.getJavaFileCount(), output.getJavaFileCount()));
            total.setJavaCodeLines(add(total.getJavaCodeLines(), output.getJavaCodeLines()));
            total.setApiCount(add(total.getApiCount(), output.getApiCount()));
            total.setCoreBizServiceCount(add(total.getCoreBizServiceCount(), output.getCoreBizServiceCount()));
            total.setEntityCount(add(total.getEntityCount(), output.getEntityCount()));
            total.setFrontendComponentCount(add(total.getFrontendComponentCount(), output.getFrontendComponentCount()));
            total.setFrontendPageCount(add(total.getFrontendPageCount(), output.getFrontendPageCount()));
            total.setFrontendCommonComponentCount(add(total.getFrontendCommonComponentCount(), output.getFrontendCommonComponentCount()));
            total.setTsCodeLines(add(total.getTsCodeLines(), output.getTsCodeLines()));
            total.setFrontendCodeLines(add(total.getFrontendCodeLines(), output.getFrontendCodeLines()));
            total.setSqlScriptCount(add(total.getSqlScriptCount(), output.getSqlScriptCount()));
            total.setTestFileCount(add(total.getTestFileCount(), output.getTestFileCount()));
            total.setTotalCodeLines(add(total.getTotalCodeLines(), output.getTotalCodeLines()));
        }
        return total;
    }

    private Integer add(Integer left, Integer right) {
        return value(left) + value(right);
    }

    private Integer value(Integer value) {
        return value == null ? 0 : value;
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
