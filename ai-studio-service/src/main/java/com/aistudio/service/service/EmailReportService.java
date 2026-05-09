package com.aistudio.service.service;

import com.aistudio.service.dto.request.EmailReportRuleRequest;
import com.aistudio.service.entity.EmailReportRule;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface EmailReportService {

    List<Map<String, Object>> listRules();

    Long createRule(EmailReportRuleRequest request);

    void updateRule(Long id, EmailReportRuleRequest request);

    void deleteRule(Long id);

    List<Map<String, Object>> listSendLogs(Long ruleId);

    String previewDailyReport(Long id, LocalDate date);

    void sendDailyReport(Long id, LocalDate date);

    void sendDueRules();
}
