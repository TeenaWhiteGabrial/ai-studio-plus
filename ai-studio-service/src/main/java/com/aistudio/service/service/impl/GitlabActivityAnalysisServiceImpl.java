package com.aistudio.service.service.impl;

import com.aistudio.service.entity.ActivityDailyAnalysis;
import com.aistudio.service.entity.ActivityDailyReport;
import com.aistudio.service.entity.ActivityStatGroupMember;
import com.aistudio.service.entity.ActivityWorkItem;
import com.aistudio.service.entity.ActivityWorkItemEvidence;
import com.aistudio.service.entity.GitlabCommitFact;
import com.aistudio.service.entity.GitlabProjectConfig;
import com.aistudio.service.entity.SysUser;
import com.aistudio.service.mapper.ActivityDailyAnalysisMapper;
import com.aistudio.service.mapper.ActivityDailyReportMapper;
import com.aistudio.service.mapper.ActivityStatGroupMemberMapper;
import com.aistudio.service.mapper.ActivityWorkItemEvidenceMapper;
import com.aistudio.service.mapper.ActivityWorkItemMapper;
import com.aistudio.service.mapper.GitlabCommitFactMapper;
import com.aistudio.service.mapper.GitlabProjectConfigMapper;
import com.aistudio.service.mapper.SysUserMapper;
import com.aistudio.service.service.AiActivityAnalysisService;
import com.aistudio.service.service.GitlabActivityAnalysisService;
import com.aistudio.service.service.GitlabActivityLinkService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GitlabActivityAnalysisServiceImpl implements GitlabActivityAnalysisService {

    private final GitlabCommitFactMapper commitFactMapper;
    private final ActivityWorkItemMapper workItemMapper;
    private final ActivityWorkItemEvidenceMapper evidenceMapper;
    private final ActivityDailyAnalysisMapper dailyAnalysisMapper;
    private final ActivityDailyReportMapper dailyReportMapper;
    private final ActivityStatGroupMemberMapper statGroupMemberMapper;
    private final GitlabProjectConfigMapper projectConfigMapper;
    private final SysUserMapper userMapper;
    private final AiActivityAnalysisService aiActivityAnalysisService;
    private final GitlabActivityLinkService linkService;

    @Override
    @Transactional
    public void analyzeUserProject(Long userId, Long gitlabProjectId, LocalDate analysisDate) {
        analyzeUserProject(userId, gitlabProjectId, analysisDate, null);
    }

    @Override
    @Transactional
    public void analyzeUserProject(Long userId, Long gitlabProjectId, LocalDate analysisDate, Long eventLogId) {
        LocalDate targetDate = analysisDate == null ? LocalDate.now() : analysisDate;
        List<GitlabCommitFact> commits = commitFactMapper.selectList(new LambdaQueryWrapper<GitlabCommitFact>()
                .eq(gitlabProjectId != null, GitlabCommitFact::getGitlabProjectId, gitlabProjectId)
                .between(GitlabCommitFact::getCommittedAt, targetDate.atStartOfDay(), targetDate.plusDays(1).atStartOfDay()));
        if (userId != null) {
            SysUser user = userMapper.selectById(userId);
            if (user != null && user.getGitName() != null && !user.getGitName().isBlank()) {
                commits = commits.stream()
                        .filter(item -> user.getGitName().equalsIgnoreCase(item.getAuthorName()) || user.getGitName().equalsIgnoreCase(item.getAuthorEmail()))
                        .toList();
            }
        }

        if (commits.isEmpty()) {
            return;
        }

        Long mappedUserId = userId != null ? userId : resolveUserId(commits.get(0));
        Long statGroupId = resolveStatGroupId(mappedUserId);
        Long mappedProjectId = resolveProjectId(gitlabProjectId);

        List<ActivityWorkItem> existingWorkItems = workItemMapper.selectList(new LambdaQueryWrapper<ActivityWorkItem>()
                .eq(ActivityWorkItem::getAnalysisDate, targetDate)
                .eq(mappedUserId != null, ActivityWorkItem::getUserId, mappedUserId)
                .eq(gitlabProjectId != null, ActivityWorkItem::getGitlabProjectId, gitlabProjectId));
        if (!existingWorkItems.isEmpty()) {
            List<Long> workItemIds = existingWorkItems.stream().map(ActivityWorkItem::getId).toList();
            evidenceMapper.delete(new LambdaQueryWrapper<ActivityWorkItemEvidence>().in(ActivityWorkItemEvidence::getWorkItemId, workItemIds));
            workItemMapper.delete(new LambdaQueryWrapper<ActivityWorkItem>().in(ActivityWorkItem::getId, workItemIds));
        }
        dailyAnalysisMapper.delete(new LambdaQueryWrapper<ActivityDailyAnalysis>()
                .eq(ActivityDailyAnalysis::getAnalysisDate, targetDate)
                .eq(mappedUserId != null, ActivityDailyAnalysis::getUserId, mappedUserId)
                .eq(gitlabProjectId != null, ActivityDailyAnalysis::getGitlabProjectId, gitlabProjectId));

        java.util.Optional<AiActivityAnalysisService.AiActivityAnalysisResult> aiResult =
                aiActivityAnalysisService.analyze(mappedUserId, gitlabProjectId, targetDate, commits);
        if (aiResult.isPresent()) {
            persistAiAnalysis(aiResult.get(), commits, targetDate, mappedUserId, statGroupId, mappedProjectId, gitlabProjectId, eventLogId);
            return;
        }

        Map<String, List<GitlabCommitFact>> grouped = commits.stream().collect(Collectors.groupingBy(this::buildWorkKey, LinkedHashMap::new, Collectors.toList()));
        List<String> titles = new ArrayList<>();
        for (Map.Entry<String, List<GitlabCommitFact>> entry : grouped.entrySet()) {
            List<GitlabCommitFact> groupCommits = entry.getValue().stream()
                    .sorted(Comparator.comparing(GitlabCommitFact::getCommittedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                    .toList();
            ActivityWorkItem workItem = new ActivityWorkItem();
            workItem.setAnalysisDate(targetDate);
            workItem.setUserId(mappedUserId);
            workItem.setGitlabUserId(groupCommits.get(0).getAuthorGitlabUserId());
            workItem.setProjectId(mappedProjectId);
            workItem.setGitlabProjectId(groupCommits.get(0).getGitlabProjectId());
            workItem.setStatGroupId(statGroupId);
            workItem.setTitle(generateTitle(groupCommits));
            workItem.setSummary(generateSummary(groupCommits));
            workItem.setWorkType(resolveWorkType(groupCommits));
            workItem.setProgressStatus(resolveProgressStatus(groupCommits));
            workItem.setRiskLevel(resolveRiskLevel(groupCommits));
            workItem.setRiskSummary(resolveRiskSummary(groupCommits));
            workItem.setBlockingStatus(resolveBlockingStatus(groupCommits));
            workItem.setBlockingSummary(resolveBlockingSummary(groupCommits));
            workItem.setModuleNames(resolveModules(groupCommits));
            workItem.setRelatedCommitCount(groupCommits.size());
            workItem.setRelatedMrCount(resolveMrCount(groupCommits));
            workItem.setConfidence(new BigDecimal("0.72"));
            workItem.setEvidenceSummary(resolveEvidenceSummary(groupCommits));
            workItem.setAnalysisEngine("heuristic_v1");
            workItemMapper.insert(workItem);
            linkService.link(eventLogId, "WORK_ITEM", workItem.getId(), "GENERATED_WORK_ITEM");
            titles.add(workItem.getTitle());

            for (GitlabCommitFact commit : groupCommits) {
                ActivityWorkItemEvidence evidence = new ActivityWorkItemEvidence();
                evidence.setWorkItemId(workItem.getId());
                evidence.setEvidenceType("commit");
                evidence.setEvidenceRef(commit.getCommitSha());
                evidence.setSummary(commit.getCommitMessage());
                evidenceMapper.insert(evidence);
            }
        }

        ActivityDailyAnalysis analysis = new ActivityDailyAnalysis();
        analysis.setAnalysisDate(targetDate);
        analysis.setUserId(mappedUserId);
        analysis.setGitlabUserId(commits.get(0).getAuthorGitlabUserId());
        analysis.setProjectId(mappedProjectId);
        analysis.setGitlabProjectId(gitlabProjectId);
        analysis.setStatGroupId(statGroupId);
        analysis.setWorkSummary(String.join("；", titles));
        analysis.setProgressSummary("基于当日 commit 与 MR 线索自动判断推进状态");
        analysis.setRiskSummary("基于提交粒度、模块分散度与持续修改情况生成");
        analysis.setBlockingSummary("当前版本基于活动轨迹识别推进异常");
        analysis.setReportTitle("个人研发活动日报");
        analysis.setReportContent(String.join("；", titles));
        analysis.setAnalysisEngine("heuristic_v1");
        analysis.setStatus("COMPLETED");
        dailyAnalysisMapper.insert(analysis);
        linkService.link(eventLogId, "DAILY_ANALYSIS", analysis.getId(), "GENERATED_ANALYSIS");
    }

    @Override
    @Transactional
    public void generateDailyReport(Long userId, Long projectId, Long statGroupId, LocalDate reportDate) {
        generateDailyReport(userId, projectId, statGroupId, reportDate, null);
    }

    @Override
    @Transactional
    public void generateDailyReport(Long userId, Long projectId, Long statGroupId, LocalDate reportDate, Long eventLogId) {
        LocalDate targetDate = reportDate == null ? LocalDate.now() : reportDate;
        List<ActivityWorkItem> workItems = workItemMapper.selectList(new LambdaQueryWrapper<ActivityWorkItem>()
                .eq(ActivityWorkItem::getAnalysisDate, targetDate)
                .eq(userId != null, ActivityWorkItem::getUserId, userId)
                .eq(projectId != null, ActivityWorkItem::getProjectId, projectId)
                .eq(statGroupId != null, ActivityWorkItem::getStatGroupId, statGroupId));
        if (workItems.isEmpty()) {
            return;
        }

        String scope = userId != null ? "USER" : statGroupId != null ? "GROUP" : "PROJECT";
        ActivityDailyAnalysis latestAnalysis = dailyAnalysisMapper.selectOne(new LambdaQueryWrapper<ActivityDailyAnalysis>()
                .eq(ActivityDailyAnalysis::getAnalysisDate, targetDate)
                .eq(userId != null, ActivityDailyAnalysis::getUserId, userId)
                .eq(projectId != null, ActivityDailyAnalysis::getProjectId, projectId)
                .eq(statGroupId != null, ActivityDailyAnalysis::getStatGroupId, statGroupId)
                .orderByDesc(ActivityDailyAnalysis::getCreatedAt)
                .last("LIMIT 1"));
        String title = defaultString(latestAnalysis == null ? null : latestAnalysis.getReportTitle());
        if (title.isBlank()) {
            title = scope.equals("USER") ? "个人研发活动日报" : scope.equals("GROUP") ? "小组研发活动日报" : "项目研发活动日报";
        }
        String content = defaultString(latestAnalysis == null ? null : latestAnalysis.getReportContent());
        if (content.isBlank()) {
            content = workItems.stream()
                    .map(item -> String.format("%s：%s。进度=%s，风险=%s", item.getTitle(), defaultString(item.getSummary()), item.getProgressStatus(), item.getRiskLevel()))
                    .collect(Collectors.joining("\n"));
        }

        ActivityDailyReport report = dailyReportMapper.selectOne(new LambdaQueryWrapper<ActivityDailyReport>()
                .eq(ActivityDailyReport::getReportDate, targetDate)
                .eq(ActivityDailyReport::getReportScope, scope)
                .eq(userId != null, ActivityDailyReport::getUserId, userId)
                .eq(projectId != null, ActivityDailyReport::getProjectId, projectId)
                .eq(statGroupId != null, ActivityDailyReport::getStatGroupId, statGroupId));
        if (report == null) {
            report = new ActivityDailyReport();
            report.setReportDate(targetDate);
            report.setReportScope(scope);
            report.setUserId(userId);
            report.setProjectId(projectId);
            report.setStatGroupId(statGroupId);
        }
        report.setTitle(title);
        report.setContent(content);
        report.setReportDataJson(String.format("{\"work_item_count\":%d}", workItems.size()));
        report.setGeneratedBy("heuristic_v1");
        if (report.getId() == null) {
            dailyReportMapper.insert(report);
        } else {
            dailyReportMapper.updateById(report);
        }
        linkService.link(eventLogId, "DAILY_REPORT", report.getId(), "GENERATED_REPORT");
    }

    private void persistAiAnalysis(AiActivityAnalysisService.AiActivityAnalysisResult result,
                                   List<GitlabCommitFact> commits,
                                   LocalDate targetDate,
                                   Long mappedUserId,
                                   Long statGroupId,
                                   Long mappedProjectId,
                                   Long gitlabProjectId,
                                   Long eventLogId) {
        for (AiActivityAnalysisService.AiWorkItemResult item : result.getWorkItems()) {
            ActivityWorkItem workItem = new ActivityWorkItem();
            workItem.setAnalysisDate(targetDate);
            workItem.setUserId(mappedUserId);
            workItem.setGitlabUserId(commits.get(0).getAuthorGitlabUserId());
            workItem.setProjectId(mappedProjectId);
            workItem.setGitlabProjectId(gitlabProjectId);
            workItem.setStatGroupId(statGroupId);
            workItem.setTitle(item.getTitle());
            workItem.setSummary(item.getSummary());
            workItem.setWorkType(item.getWorkType());
            workItem.setProgressStatus(item.getProgressStatus());
            workItem.setRiskLevel(item.getRiskLevel());
            workItem.setRiskSummary(item.getRiskSummary());
            workItem.setBlockingStatus(item.getBlockingStatus());
            workItem.setBlockingSummary(item.getBlockingSummary());
            workItem.setModuleNames(item.getModuleNames());
            workItem.setRelatedCommitCount(item.getRelatedCommitCount());
            workItem.setRelatedMrCount(item.getRelatedMrCount());
            workItem.setConfidence(item.getConfidence());
            workItem.setEvidenceSummary(item.getEvidenceSummary());
            workItem.setAnalysisEngine(result.getAnalysisEngine());
            workItemMapper.insert(workItem);
            linkService.link(eventLogId, "WORK_ITEM", workItem.getId(), "GENERATED_WORK_ITEM");

            for (GitlabCommitFact commit : commits) {
                ActivityWorkItemEvidence evidence = new ActivityWorkItemEvidence();
                evidence.setWorkItemId(workItem.getId());
                evidence.setEvidenceType("commit");
                evidence.setEvidenceRef(commit.getCommitSha());
                evidence.setSummary(commit.getCommitMessage());
                evidenceMapper.insert(evidence);
            }
        }

        ActivityDailyAnalysis analysis = new ActivityDailyAnalysis();
        analysis.setAnalysisDate(targetDate);
        analysis.setUserId(mappedUserId);
        analysis.setGitlabUserId(commits.get(0).getAuthorGitlabUserId());
        analysis.setProjectId(mappedProjectId);
        analysis.setGitlabProjectId(gitlabProjectId);
        analysis.setStatGroupId(statGroupId);
        analysis.setWorkSummary(defaultString(result.getWorkSummary()));
        analysis.setProgressSummary(defaultString(result.getProgressSummary()));
        analysis.setRiskSummary(defaultString(result.getRiskSummary()));
        analysis.setBlockingSummary(defaultString(result.getBlockingSummary()));
        analysis.setReportTitle(defaultString(result.getReportTitle()));
        analysis.setReportContent(defaultString(result.getReportContent()));
        analysis.setAnalysisEngine(result.getAnalysisEngine());
        analysis.setStatus("COMPLETED");
        dailyAnalysisMapper.insert(analysis);
        linkService.link(eventLogId, "DAILY_ANALYSIS", analysis.getId(), "GENERATED_ANALYSIS");
    }

    private String buildWorkKey(GitlabCommitFact fact) {
        String message = defaultString(fact.getCommitMessage()).toLowerCase();
        if (message.contains(":")) {
            return message.substring(0, message.indexOf(':')).trim() + "|" + defaultString(fact.getBranchName());
        }
        return defaultString(fact.getBranchName()) + "|" + message.replaceAll("\\s+", " ").trim();
    }

    private String generateTitle(List<GitlabCommitFact> commits) {
        String message = defaultString(commits.get(0).getCommitMessage()).trim();
        if (message.isBlank()) {
            return "代码活动归并项";
        }
        return message.length() > 80 ? message.substring(0, 80) : message;
    }

    private String generateSummary(List<GitlabCommitFact> commits) {
        Set<String> modules = commits.stream()
                .flatMap(item -> parseJsonArray(item.getFilePathsJson()).stream())
                .map(this::resolveModule)
                .filter(text -> !text.isBlank())
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
        String moduleSummary = modules.isEmpty() ? "未识别模块" : String.join("、", modules);
        return "围绕 " + moduleSummary + " 持续提交 " + commits.size() + " 次。";
    }

    private String resolveWorkType(List<GitlabCommitFact> commits) {
        String message = defaultString(commits.get(0).getCommitMessage()).toLowerCase();
        if (message.startsWith("fix")) return "bugfix";
        if (message.startsWith("refactor")) return "refactor";
        if (message.startsWith("docs")) return "docs";
        return "feature";
    }

    private String resolveProgressStatus(List<GitlabCommitFact> commits) {
        if (commits.size() >= 4) return "converging";
        if (commits.size() >= 2) return "in_progress";
        return "started";
    }

    private String resolveRiskLevel(List<GitlabCommitFact> commits) {
        long modules = commits.stream()
                .flatMap(item -> parseJsonArray(item.getFilePathsJson()).stream())
                .map(this::resolveModule)
                .distinct()
                .count();
        if (modules >= 4) return "high";
        if (modules >= 2) return "medium";
        return "low";
    }

    private String resolveRiskSummary(List<GitlabCommitFact> commits) {
        String riskLevel = resolveRiskLevel(commits);
        if ("high".equals(riskLevel)) return "涉及模块较多，可能存在上下文切换和方案发散风险。";
        if ("medium".equals(riskLevel)) return "涉及多个模块，建议关注是否持续收敛。";
        return "当前提交主题相对集中。";
    }

    private String resolveBlockingStatus(List<GitlabCommitFact> commits) {
        return commits.size() == 1 ? "uncertain" : "normal";
    }

    private String resolveBlockingSummary(List<GitlabCommitFact> commits) {
        return commits.size() == 1 ? "单次提交样本较少，阻塞判断置信度有限。" : "未观察到明显推进异常。";
    }

    private String resolveModules(List<GitlabCommitFact> commits) {
        return commits.stream()
                .flatMap(item -> parseJsonArray(item.getFilePathsJson()).stream())
                .map(this::resolveModule)
                .filter(text -> !text.isBlank())
                .distinct()
                .collect(Collectors.joining(","));
    }

    private Integer resolveMrCount(List<GitlabCommitFact> commits) {
        return (int) commits.stream()
                .flatMap(item -> parseJsonArray(item.getMrIdsJson()).stream())
                .distinct()
                .count();
    }

    private String resolveEvidenceSummary(List<GitlabCommitFact> commits) {
        List<String> refs = commits.stream()
                .map(GitlabCommitFact::getCommitSha)
                .filter(text -> text != null && !text.isBlank())
                .map(text -> text.substring(0, Math.min(8, text.length())))
                .toList();
        return "关联 commits: " + String.join(", ", refs);
    }

    private Long resolveUserId(GitlabCommitFact commit) {
        if (commit.getAuthorEmail() != null) {
            SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getEmail, commit.getAuthorEmail()));
            if (user != null) {
                return user.getId();
            }
        }
        if (commit.getAuthorName() != null) {
            SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getGitName, commit.getAuthorName()));
            if (user != null) {
                return user.getId();
            }
        }
        return null;
    }

    private Long resolveStatGroupId(Long userId) {
        if (userId == null) return null;
        ActivityStatGroupMember member = statGroupMemberMapper.selectOne(new LambdaQueryWrapper<ActivityStatGroupMember>()
                .eq(ActivityStatGroupMember::getUserId, userId)
                .last("LIMIT 1"));
        return member == null ? null : member.getStatGroupId();
    }

    private Long resolveProjectId(Long gitlabProjectId) {
        if (gitlabProjectId == null) {
            return null;
        }
        GitlabProjectConfig config = projectConfigMapper.selectOne(new LambdaQueryWrapper<GitlabProjectConfig>()
                .eq(GitlabProjectConfig::getGitlabProjectId, gitlabProjectId)
                .eq(GitlabProjectConfig::getEnabled, 1)
                .last("LIMIT 1"));
        return config == null ? null : config.getProjectId();
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private List<String> parseJsonArray(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        String body = json.trim();
        if (body.startsWith("[") && body.endsWith("]")) {
            body = body.substring(1, body.length() - 1);
        }
        if (body.isBlank()) {
            return List.of();
        }
        return java.util.Arrays.stream(body.split(","))
                .map(item -> item.replace("\"", "").trim())
                .filter(item -> !item.isBlank())
                .toList();
    }

    private String resolveModule(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            return "";
        }
        String normalized = filePath.replace("\\", "/");
        String[] parts = normalized.split("/");
        return parts.length >= 2 ? parts[0] + "/" + parts[1] : parts[0];
    }
}
