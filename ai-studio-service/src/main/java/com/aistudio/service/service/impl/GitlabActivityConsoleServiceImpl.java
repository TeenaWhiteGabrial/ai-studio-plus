package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.response.ActivityDailyReportResponse;
import com.aistudio.service.dto.response.ActivityWorkItemResponse;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.ActivityDailyReport;
import com.aistudio.service.entity.ActivityWorkItem;
import com.aistudio.service.entity.ActivityWorkItemEvidence;
import com.aistudio.service.entity.Project;
import com.aistudio.service.entity.SysUser;
import com.aistudio.service.mapper.ActivityDailyReportMapper;
import com.aistudio.service.mapper.ActivityWorkItemEvidenceMapper;
import com.aistudio.service.mapper.ActivityWorkItemMapper;
import com.aistudio.service.mapper.ProjectMapper;
import com.aistudio.service.mapper.SysUserMapper;
import com.aistudio.service.service.GitlabActivityConsoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GitlabActivityConsoleServiceImpl implements GitlabActivityConsoleService {

    private final ActivityWorkItemMapper workItemMapper;
    private final ActivityWorkItemEvidenceMapper evidenceMapper;
    private final ActivityDailyReportMapper dailyReportMapper;
    private final ProjectMapper projectMapper;
    private final SysUserMapper userMapper;

    @Override
    public PageResult<ActivityWorkItemResponse> listMyWorkItems(Long userId, int page, int size, LocalDate analysisDate) {
        Page<ActivityWorkItem> result = workItemMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<ActivityWorkItem>()
                        .eq(ActivityWorkItem::getUserId, userId)
                        .eq(analysisDate != null, ActivityWorkItem::getAnalysisDate, analysisDate)
                        .orderByDesc(ActivityWorkItem::getAnalysisDate)
                        .orderByDesc(ActivityWorkItem::getCreatedAt));
        return PageResult.of(result.getTotal(), result.getRecords().stream().map(this::toWorkItemResponse).toList());
    }

    @Override
    public ActivityDailyReportResponse getMyDailyReport(Long userId, LocalDate reportDate) {
        ActivityDailyReport report = dailyReportMapper.selectOne(new LambdaQueryWrapper<ActivityDailyReport>()
                .eq(ActivityDailyReport::getReportScope, "USER")
                .eq(ActivityDailyReport::getUserId, userId)
                .eq(ActivityDailyReport::getReportDate, reportDate == null ? LocalDate.now() : reportDate)
                .last("LIMIT 1"));
        return report == null ? null : toReportResponse(report);
    }

    @Override
    public ActivityDailyReportResponse getProjectDailyReport(Long userId, Long projectId, LocalDate reportDate) {
        Project project = projectMapper.selectById(projectId);
        SysUser user = userMapper.selectById(userId);
        if (project == null || user == null || user.getTeamId() == null || !user.getTeamId().equals(project.getTeamId())) {
            throw new BusinessException(403, "无权查看该项目日报");
        }
        ActivityDailyReport report = dailyReportMapper.selectOne(new LambdaQueryWrapper<ActivityDailyReport>()
                .eq(ActivityDailyReport::getReportScope, "PROJECT")
                .eq(ActivityDailyReport::getProjectId, projectId)
                .eq(ActivityDailyReport::getReportDate, reportDate == null ? LocalDate.now() : reportDate)
                .last("LIMIT 1"));
        return report == null ? null : toReportResponse(report);
    }

    @Override
    public Map<String, Object> getMyOverview(Long userId, LocalDate analysisDate) {
        LocalDate targetDate = analysisDate == null ? LocalDate.now() : analysisDate;
        PageResult<ActivityWorkItemResponse> page = listMyWorkItems(userId, 1, 20, targetDate);
        Map<String, Object> result = new HashMap<>();
        result.put("analysis_date", targetDate);
        result.put("work_item_count", page.getTotal());
        result.put("top_work_items", page.getRecords().stream().limit(3).toList());
        result.put("daily_report", getMyDailyReport(userId, targetDate));
        return result;
    }

    private ActivityWorkItemResponse toWorkItemResponse(ActivityWorkItem item) {
        ActivityWorkItemResponse response = new ActivityWorkItemResponse();
        response.setId(item.getId());
        response.setAnalysis_date(item.getAnalysisDate());
        response.setUser_id(item.getUserId());
        response.setGitlab_user_id(item.getGitlabUserId());
        response.setProject_id(item.getProjectId());
        response.setGitlab_project_id(item.getGitlabProjectId());
        response.setStat_group_id(item.getStatGroupId());
        response.setTitle(item.getTitle());
        response.setSummary(item.getSummary());
        response.setWork_type(item.getWorkType());
        response.setProgress_status(item.getProgressStatus());
        response.setRisk_level(item.getRiskLevel());
        response.setRisk_summary(item.getRiskSummary());
        response.setBlocking_status(item.getBlockingStatus());
        response.setBlocking_summary(item.getBlockingSummary());
        response.setModule_names(item.getModuleNames());
        response.setRelated_commit_count(item.getRelatedCommitCount());
        response.setRelated_mr_count(item.getRelatedMrCount());
        response.setConfidence(item.getConfidence());
        response.setEvidence_summary(item.getEvidenceSummary());
        response.setEvidence_refs(evidenceMapper.selectList(new LambdaQueryWrapper<ActivityWorkItemEvidence>()
                        .eq(ActivityWorkItemEvidence::getWorkItemId, item.getId()))
                .stream()
                .map(ActivityWorkItemEvidence::getEvidenceRef)
                .collect(Collectors.toList()));
        return response;
    }

    private ActivityDailyReportResponse toReportResponse(ActivityDailyReport item) {
        ActivityDailyReportResponse response = new ActivityDailyReportResponse();
        response.setId(item.getId());
        response.setReport_date(item.getReportDate());
        response.setReport_scope(item.getReportScope());
        response.setUser_id(item.getUserId());
        response.setProject_id(item.getProjectId());
        response.setStat_group_id(item.getStatGroupId());
        response.setTitle(item.getTitle());
        response.setContent(item.getContent());
        response.setReport_data_json(item.getReportDataJson());
        response.setGenerated_by(item.getGeneratedBy());
        response.setCreated_at(item.getCreatedAt());
        return response;
    }
}
