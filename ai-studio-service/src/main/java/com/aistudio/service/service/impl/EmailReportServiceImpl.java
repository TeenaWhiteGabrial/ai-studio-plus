package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.request.EmailReportRuleRequest;
import com.aistudio.service.entity.DailyTask;
import com.aistudio.service.entity.EmailReportRule;
import com.aistudio.service.entity.EmailReportSendLog;
import com.aistudio.service.entity.MemberOutput;
import com.aistudio.service.entity.Project;
import com.aistudio.service.entity.SysUser;
import com.aistudio.service.mapper.DailyTaskMapper;
import com.aistudio.service.mapper.EmailReportRuleMapper;
import com.aistudio.service.mapper.EmailReportSendLogMapper;
import com.aistudio.service.mapper.MemberOutputMapper;
import com.aistudio.service.mapper.ProjectMapper;
import com.aistudio.service.mapper.SysUserMapper;
import com.aistudio.service.service.EmailReportService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmailReportServiceImpl implements EmailReportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-M-d");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final int MAX_ERROR_MESSAGE_LENGTH = 2000;

    private final EmailReportRuleMapper ruleMapper;
    private final EmailReportSendLogMapper sendLogMapper;
    private final DailyTaskMapper dailyTaskMapper;
    private final ProjectMapper projectMapper;
    private final SysUserMapper userMapper;
    private final MemberOutputMapper outputMapper;
    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper;

    @Value("${spring.mail.username:}")
    private String sender;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    @Override
    public List<Map<String, Object>> listRules() {
        return ruleMapper.selectList(new LambdaQueryWrapper<EmailReportRule>().orderByDesc(EmailReportRule::getCreatedAt))
                .stream().map(this::toMap).toList();
    }

    @Override
    @Transactional
    public Long createRule(EmailReportRuleRequest request) {
        EmailReportRule rule = new EmailReportRule();
        fillRule(rule, request);
        rule.setCreatedAt(LocalDateTime.now());
        rule.setUpdatedAt(LocalDateTime.now());
        ruleMapper.insert(rule);
        return rule.getId();
    }

    @Override
    @Transactional
    public void updateRule(Long id, EmailReportRuleRequest request) {
        EmailReportRule rule = requireRule(id);
        fillRule(rule, request);
        rule.setUpdatedAt(LocalDateTime.now());
        ruleMapper.updateById(rule);
    }

    @Override
    @Transactional
    public void deleteRule(Long id) {
        ruleMapper.deleteById(id);
    }

    @Override
    public List<Map<String, Object>> listSendLogs(Long ruleId) {
        requireRule(ruleId);
        return sendLogMapper.selectList(new LambdaQueryWrapper<EmailReportSendLog>()
                        .eq(EmailReportSendLog::getRuleId, ruleId)
                        .orderByDesc(EmailReportSendLog::getSentAt)
                        .last("LIMIT 100"))
                .stream().map(this::logToMap).toList();
    }

    @Override
    public String previewDailyReport(Long id, LocalDate date) {
        return buildDailyReportHtml(requireRule(id), date == null ? LocalDate.now() : date);
    }

    @Override
    public void sendDailyReport(Long id, LocalDate date) {
        EmailReportRule rule = requireRule(id);
        sendRule(rule, date == null ? LocalDate.now() : date, "MANUAL");
        rule.setLastSentAt(LocalDateTime.now());
        ruleMapper.updateById(rule);
    }

    @Override
    public void sendDueRules() {
        String nowTime = LocalTime.now().format(TIME_FORMATTER);
        LocalDate today = LocalDate.now();
        List<EmailReportRule> rules = ruleMapper.selectList(new LambdaQueryWrapper<EmailReportRule>()
                .eq(EmailReportRule::getStatus, 1)
                .eq(EmailReportRule::getSendTime, nowTime));
        for (EmailReportRule rule : rules) {
            if (rule.getLastSentAt() != null && today.equals(rule.getLastSentAt().toLocalDate())) {
                continue;
            }
            try {
                sendRule(rule, today, "SCHEDULED");
                rule.setLastSentAt(LocalDateTime.now());
                ruleMapper.updateById(rule);
            } catch (BusinessException ignored) {
                // Failure details are persisted to email_report_send_log.
            }
        }
    }

    private void fillRule(EmailReportRule rule, EmailReportRuleRequest request) {
        List<Long> toRecipientUserIds = normalizeIds(request.getToRecipientUserIds());
        List<Long> ccRecipientUserIds = normalizeIds(request.getCcRecipientUserIds());
        List<String> toEmails = resolveRecipientEmails(toRecipientUserIds, "主送接收人");
        if (toEmails.isEmpty() && request.getRecipients() != null) {
            toEmails = request.getRecipients().stream()
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .distinct()
                    .toList();
        }
        if (toEmails.isEmpty()) {
            throw new BusinessException(400, "请选择主送接收人");
        }
        List<Long> reportUserIds = normalizeIds(request.getUserIds());
        if (reportUserIds.isEmpty()) {
            throw new BusinessException(400, "请选择统计成员");
        }
        if (!toRecipientUserIds.isEmpty() && !ccRecipientUserIds.isEmpty()) {
            List<Long> duplicates = ccRecipientUserIds.stream().filter(toRecipientUserIds::contains).toList();
            if (!duplicates.isEmpty()) {
                throw new BusinessException(400, "主送和抄送接收人不能重复");
            }
        }
        resolveRecipientEmails(ccRecipientUserIds, "抄送接收人");
        rule.setName(request.getName().trim());
        rule.setToRecipientUserIdsJson(writeJson(toRecipientUserIds));
        rule.setCcRecipientUserIdsJson(writeJson(ccRecipientUserIds));
        rule.setRecipientsJson(writeJson(toEmails));
        rule.setUserIdsJson(writeJson(reportUserIds));
        rule.setSendTime(request.getSendTime());
        rule.setStatus(request.getStatus() == null ? 1 : request.getStatus());
    }

    private void sendRule(EmailReportRule rule, LocalDate date, String triggerType) {
        EmailReportSendLog log = new EmailReportSendLog();
        log.setRuleId(rule.getId());
        log.setRuleName(rule.getName());
        log.setReportDate(date);
        log.setTriggerType(triggerType);
        log.setSentAt(LocalDateTime.now());
        log.setCreatedAt(LocalDateTime.now());
        if (!StringUtils.hasText(mailPassword)) {
            logFailedSend(log, "QQ 邮箱授权码未配置，请设置环境变量 QQ_MAIL_AUTH_CODE");
            throw new BusinessException(500, "QQ 邮箱授权码未配置，请设置环境变量 QQ_MAIL_AUTH_CODE");
        }
        try {
            List<String> toRecipients = ruleToRecipientEmails(rule);
            List<String> ccRecipients = resolveRecipientEmails(readLongList(rule.getCcRecipientUserIdsJson()), "抄送接收人");
            if (toRecipients.isEmpty()) {
                throw new BusinessException(400, "主送接收人邮箱不能为空");
            }
            String subject = "产品研发日报-" + date.format(DATE_FORMATTER);
            String html = buildDailyReportHtml(rule, date);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(sender);
            helper.setTo(toRecipients.toArray(new String[0]));
            if (!ccRecipients.isEmpty()) {
                helper.setCc(ccRecipients.toArray(new String[0]));
            }
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.setStatus("SUCCESS");
            log.setToRecipientsJson(writeJson(toRecipients));
            log.setCcRecipientsJson(writeJson(ccRecipients));
            log.setSubject(subject);
            sendLogMapper.insert(log);
        } catch (BusinessException e) {
            logFailedSend(log, e.getMessage());
            throw e;
        } catch (Exception e) {
            logFailedSend(log, "邮件发送失败: " + e.getMessage());
            throw new BusinessException(500, "邮件发送失败: " + e.getMessage());
        }
    }

    private String buildDailyReportHtml(EmailReportRule rule, LocalDate date) {
        List<Long> userIds = readLongList(rule.getUserIdsJson());
        List<DailyTask> tasks = userIds.isEmpty() ? List.of() : dailyTaskMapper.selectList(new LambdaQueryWrapper<DailyTask>()
                .eq(DailyTask::getTaskDate, date)
                .in(DailyTask::getUserId, userIds)
                .orderByAsc(DailyTask::getProjectId)
                .orderByAsc(DailyTask::getUserId)
                .orderByAsc(DailyTask::getId));

        Map<Long, Project> projects = loadProjects(tasks);
        Map<Long, SysUser> users = loadUsers(userIds);
        Map<Long, MemberOutput> outputs = loadOutputs(tasks);
        Map<String, List<DailyTask>> grouped = tasks.stream().collect(Collectors.groupingBy(
                task -> projectName(task, projects),
                LinkedHashMap::new,
                Collectors.toList()
        ));

        StringBuilder html = new StringBuilder();
        html.append("<!doctype html><html><body style='font-family:Arial,\"Microsoft YaHei\",sans-serif;color:#111827;'>");
        html.append("<p style='font-size:16px;line-height:1.8;margin:0 0 16px;'>各位领导、同事好：<br>")
                .append("&nbsp;&nbsp;&nbsp;&nbsp;云平台产品处 ")
                .append(escape(date.format(DATE_FORMATTER)))
                .append("工作总结如下，请收悉，谢谢！</p>");
        html.append("<h2 style='font-size:26px;margin:22px 0 18px;'>产品研发日报-")
                .append(escape(date.format(DATE_FORMATTER))).append("</h2>");
        html.append("<table cellspacing='0' cellpadding='0' style='width:100%;border-collapse:collapse;font-size:14px;'>")
                .append("<thead><tr>")
                .append(th("项目名称", "18%"))
                .append(th("今日任务", "42%"))
                .append(th("参与人", "14%"))
                .append(th("关联产出", "26%"))
                .append("</tr></thead><tbody>");
        if (grouped.isEmpty()) {
            html.append("<tr><td colspan='4' style='border:1px solid #d1d5db;padding:14px;text-align:center;color:#6b7280;'>暂无日报任务</td></tr>");
        } else {
            grouped.forEach((projectName, projectTasks) -> {
                for (int i = 0; i < projectTasks.size(); i++) {
                    DailyTask task = projectTasks.get(i);
                    html.append("<tr>");
                    if (i == 0) {
                        html.append("<td rowspan='").append(projectTasks.size()).append("' style='")
                                .append(tdStyle()).append("font-weight:600;background:#fafafa;'>")
                                .append(escape(projectName)).append("</td>");
                    }
                    html.append("<td style='").append(tdStyle()).append("'>").append(formatTask(task)).append("</td>");
                    html.append("<td style='").append(tdStyle()).append("white-space:nowrap;'>")
                            .append(escape(userName(users.get(task.getUserId())))).append("</td>");
                    html.append("<td style='").append(tdStyle()).append("'>")
                            .append(formatOutput(outputs.get(task.getOutputId()))).append("</td>");
                    html.append("</tr>");
                }
            });
        }
        html.append("</tbody></table></body></html>");
        return html.toString();
    }

    private Map<Long, Project> loadProjects(List<DailyTask> tasks) {
        List<Long> ids = tasks.stream().map(DailyTask::getProjectId).filter(Objects::nonNull).distinct().toList();
        if (ids.isEmpty()) return Map.of();
        return projectMapper.selectBatchIds(ids).stream().collect(Collectors.toMap(Project::getId, item -> item));
    }

    private Map<Long, SysUser> loadUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return Map.of();
        return userMapper.selectBatchIds(userIds).stream().collect(Collectors.toMap(SysUser::getId, item -> item));
    }

    private Map<Long, MemberOutput> loadOutputs(List<DailyTask> tasks) {
        List<Long> ids = tasks.stream().map(DailyTask::getOutputId).filter(Objects::nonNull).distinct().toList();
        if (ids.isEmpty()) return Map.of();
        return outputMapper.selectBatchIds(ids).stream().collect(Collectors.toMap(MemberOutput::getId, item -> item));
    }

    private String projectName(DailyTask task, Map<Long, Project> projects) {
        Project project = task.getProjectId() == null ? null : projects.get(task.getProjectId());
        return project == null ? "未关联项目" : project.getProjectName();
    }

    private String userName(SysUser user) {
        if (user == null) return "-";
        if (StringUtils.hasText(user.getRealName())) return user.getRealName();
        if (StringUtils.hasText(user.getUsername())) return user.getUsername();
        return "-";
    }

    private String formatTask(DailyTask task) {
        return escape(task.getContent()).replace("\n", "<br>");
    }

    private String formatOutput(MemberOutput output) {
        if (output == null) return "-";
        List<String> parts = new ArrayList<>();
        addPart(parts, "PRD", output.getPrdDocCount());
        addPart(parts, "API文档", output.getApiDocCount());
        addPart(parts, "Java行", output.getJavaCodeLines());
        addPart(parts, "前端行", output.getFrontendCodeLines());
        addPart(parts, "API", output.getApiCount());
        addPart(parts, "SQL", output.getSqlScriptCount());
        addPart(parts, "测试", output.getTestFileCount());
        addPart(parts, "总行数", output.getTotalCodeLines());
        return parts.isEmpty() ? "-" : escape(String.join("；", parts));
    }

    private void addPart(List<String> parts, String label, Integer value) {
        if (value != null && value > 0) parts.add(label + ": " + value);
    }

    private String th(String text, String width) {
        return "<th style='width:" + width + ";border:1px solid #d1d5db;padding:10px 8px;text-align:left;background:#f3f4f6;'>" + text + "</th>";
    }

    private String tdStyle() {
        return "border:1px solid #d1d5db;padding:10px 8px;vertical-align:top;line-height:1.7;";
    }

    private EmailReportRule requireRule(Long id) {
        EmailReportRule rule = ruleMapper.selectById(id);
        if (rule == null) throw new BusinessException(404, "日报规则不存在");
        return rule;
    }

    private Map<String, Object> toMap(EmailReportRule rule) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", rule.getId());
        map.put("name", rule.getName());
        List<Long> toRecipientUserIds = readLongList(rule.getToRecipientUserIdsJson());
        List<Long> ccRecipientUserIds = readLongList(rule.getCcRecipientUserIdsJson());
        map.put("toRecipientUserIds", toRecipientUserIds);
        map.put("ccRecipientUserIds", ccRecipientUserIds);
        map.put("recipients", safeRuleRecipientEmails(rule));
        map.put("ccRecipients", safeRecipientEmails(ccRecipientUserIds));
        map.put("userIds", readLongList(rule.getUserIdsJson()));
        map.put("sendTime", rule.getSendTime());
        map.put("status", rule.getStatus());
        map.put("lastSentAt", rule.getLastSentAt());
        map.put("createdAt", rule.getCreatedAt());
        return map;
    }

    private Map<String, Object> logToMap(EmailReportSendLog log) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", log.getId());
        map.put("ruleId", log.getRuleId());
        map.put("ruleName", log.getRuleName());
        map.put("reportDate", log.getReportDate());
        map.put("triggerType", log.getTriggerType());
        map.put("status", log.getStatus());
        map.put("toRecipients", readStringList(log.getToRecipientsJson()));
        map.put("ccRecipients", readStringList(log.getCcRecipientsJson()));
        map.put("subject", log.getSubject());
        map.put("errorMessage", log.getErrorMessage());
        map.put("sentAt", log.getSentAt());
        return map;
    }

    private void logFailedSend(EmailReportSendLog log, String message) {
        log.setStatus("FAILED");
        log.setErrorMessage(truncate(message));
        sendLogMapper.insert(log);
    }

    private String truncate(String value) {
        if (value == null || value.length() <= MAX_ERROR_MESSAGE_LENGTH) return value;
        return value.substring(0, MAX_ERROR_MESSAGE_LENGTH);
    }

    private List<Long> normalizeIds(List<Long> ids) {
        if (ids == null) return List.of();
        return ids.stream().filter(Objects::nonNull).distinct().toList();
    }

    private List<String> ruleToRecipientEmails(EmailReportRule rule) {
        List<Long> toRecipientUserIds = readLongList(rule.getToRecipientUserIdsJson());
        if (!toRecipientUserIds.isEmpty()) {
            return resolveRecipientEmails(toRecipientUserIds, "主送接收人");
        }
        return readStringList(rule.getRecipientsJson());
    }

    private List<String> safeRuleRecipientEmails(EmailReportRule rule) {
        try {
            return ruleToRecipientEmails(rule);
        } catch (BusinessException e) {
            return readStringList(rule.getRecipientsJson());
        }
    }

    private List<String> safeRecipientEmails(List<Long> userIds) {
        try {
            return resolveRecipientEmails(userIds, "抄送接收人");
        } catch (BusinessException e) {
            return List.of();
        }
    }

    private List<String> resolveRecipientEmails(List<Long> userIds, String label) {
        List<Long> ids = normalizeIds(userIds);
        if (ids.isEmpty()) return List.of();
        Map<Long, SysUser> users = userMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(SysUser::getId, item -> item));
        List<String> emails = new ArrayList<>();
        for (Long id : ids) {
            SysUser user = users.get(id);
            if (user == null) {
                throw new BusinessException(400, label + "不存在: " + id);
            }
            if (!StringUtils.hasText(user.getEmail())) {
                throw new BusinessException(400, label + "「" + userName(user) + "」未配置邮箱");
            }
            emails.add(user.getEmail().trim());
        }
        return emails.stream().distinct().toList();
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new BusinessException(500, "JSON 序列化失败");
        }
    }

    private List<String> readStringList(String json) {
        try {
            return StringUtils.hasText(json) ? objectMapper.readValue(json, new TypeReference<>() {}) : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<Long> readLongList(String json) {
        try {
            return StringUtils.hasText(json) ? objectMapper.readValue(json, new TypeReference<>() {}) : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
