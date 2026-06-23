package com.aistudio.service.service.impl;

import com.aistudio.service.config.GitlabActivityProperties;
import com.aistudio.service.dto.request.GitlabRuntimeConfigRequest;
import com.aistudio.service.dto.response.GitlabRuntimeConfigVO;
import com.aistudio.service.entity.GitlabRuntimeConfig;
import com.aistudio.service.mapper.GitlabRuntimeConfigMapper;
import com.aistudio.service.service.GitlabRuntimeConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class GitlabRuntimeConfigServiceImpl implements GitlabRuntimeConfigService {

    private static final Long DEFAULT_ID = 1L;

    private final GitlabRuntimeConfigMapper configMapper;
    private final GitlabActivityProperties properties;

    @Value("${gitlab.activity.api-base-url:}")
    private String legacyBaseUrl;

    @Value("${gitlab.activity.private-token:}")
    private String legacyPrivateToken;

    @Value("${gitlab.activity.webhook-token:}")
    private String legacyWebhookToken;

    @Override
    public GitlabRuntimeConfigVO getConfig() {
        return toVO(getOrCreateConfig());
    }

    @Override
    @Transactional
    public GitlabRuntimeConfigVO updateConfig(GitlabRuntimeConfigRequest request) {
        GitlabRuntimeConfig config = getOrCreateConfig();
        config.setBaseUrl(normalize(request.getBase_url()));
        if (StringUtils.hasText(request.getPrivate_token())) {
            config.setPrivateToken(request.getPrivate_token().trim());
        }
        if (StringUtils.hasText(request.getWebhook_token())) {
            config.setWebhookToken(request.getWebhook_token().trim());
        }
        config.setSchedulesEnabled(Boolean.TRUE.equals(request.getSchedules_enabled()) ? 1 : 0);
        config.setDailyAnalyzeCron(normalize(request.getDaily_analyze_cron()));
        config.setDailyReportCron(normalize(request.getDaily_report_cron()));
        config.setBackfillCron(normalize(request.getBackfill_cron()));
        config.setEnabled(request.getEnabled() == null ? 1 : request.getEnabled());
        configMapper.updateById(config);
        return toVO(config);
    }

    @Override
    public GitlabRuntimeConfig getRuntimeConfig() {
        return getOrCreateConfig();
    }

    private GitlabRuntimeConfig getOrCreateConfig() {
        GitlabRuntimeConfig config = configMapper.selectById(DEFAULT_ID);
        if (config != null) {
            boolean changed = false;
            if (!StringUtils.hasText(config.getBaseUrl()) && StringUtils.hasText(legacyBaseUrl)) {
                config.setBaseUrl(legacyBaseUrl.trim());
                changed = true;
            }
            if (!StringUtils.hasText(config.getPrivateToken()) && StringUtils.hasText(legacyPrivateToken)) {
                config.setPrivateToken(legacyPrivateToken.trim());
                changed = true;
            }
            if (!StringUtils.hasText(config.getWebhookToken()) && StringUtils.hasText(legacyWebhookToken)) {
                config.setWebhookToken(legacyWebhookToken.trim());
                changed = true;
            }
            if (config.getDailyAnalyzeCron() == null) {
                config.setDailyAnalyzeCron(properties.getDailyAnalyzeCron());
                changed = true;
            }
            if (config.getDailyReportCron() == null) {
                config.setDailyReportCron(properties.getDailyReportCron());
                changed = true;
            }
            if (config.getBackfillCron() == null) {
                config.setBackfillCron(properties.getBackfillCron());
                changed = true;
            }
            if (config.getSchedulesEnabled() == null) {
                config.setSchedulesEnabled(properties.isSchedulesEnabled() ? 1 : 0);
                changed = true;
            }
            if (config.getEnabled() == null) {
                config.setEnabled(1);
                changed = true;
            }
            if (changed) {
                configMapper.updateById(config);
            }
            return config;
        }

        GitlabRuntimeConfig defaults = new GitlabRuntimeConfig();
        defaults.setId(DEFAULT_ID);
        defaults.setBaseUrl(normalize(legacyBaseUrl));
        defaults.setPrivateToken(normalize(legacyPrivateToken));
        defaults.setWebhookToken(normalize(legacyWebhookToken));
        defaults.setSchedulesEnabled(properties.isSchedulesEnabled() ? 1 : 0);
        defaults.setDailyAnalyzeCron(properties.getDailyAnalyzeCron());
        defaults.setDailyReportCron(properties.getDailyReportCron());
        defaults.setBackfillCron(properties.getBackfillCron());
        defaults.setEnabled(1);
        configMapper.insert(defaults);
        return defaults;
    }

    private GitlabRuntimeConfigVO toVO(GitlabRuntimeConfig config) {
        GitlabRuntimeConfigVO vo = new GitlabRuntimeConfigVO();
        vo.setBase_url(defaultString(config.getBaseUrl()));
        vo.setHas_private_token(StringUtils.hasText(config.getPrivateToken()));
        vo.setHas_webhook_token(StringUtils.hasText(config.getWebhookToken()));
        vo.setSchedules_enabled(config.getSchedulesEnabled() != null && config.getSchedulesEnabled() == 1);
        vo.setDaily_analyze_cron(defaultString(config.getDailyAnalyzeCron()));
        vo.setDaily_report_cron(defaultString(config.getDailyReportCron()));
        vo.setBackfill_cron(defaultString(config.getBackfillCron()));
        vo.setEnabled(config.getEnabled());
        return vo;
    }

    private String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }
}
