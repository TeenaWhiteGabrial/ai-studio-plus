package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.request.AiModelConfigRequest;
import com.aistudio.service.dto.response.AiModelConfigVO;
import com.aistudio.service.entity.AiModelConfig;
import com.aistudio.service.mapper.AiModelConfigMapper;
import com.aistudio.service.service.AiModelConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AiModelConfigServiceImpl implements AiModelConfigService {

    private static final Long DEFAULT_ID = 1L;

    private final AiModelConfigMapper configMapper;

    @Override
    public AiModelConfigVO getConfig() {
        return toVO(getOrCreateConfig());
    }

    @Override
    @Transactional
    public AiModelConfigVO updateConfig(AiModelConfigRequest request) {
        AiModelConfig config = getOrCreateConfig();
        config.setProviderName(normalize(request.getProvider_name()));
        config.setBaseUrl(normalize(request.getBase_url()));
        config.setModelName(normalize(request.getModel_name()));
        if (StringUtils.hasText(request.getApi_key())) {
            config.setApiKey(request.getApi_key().trim());
        } else if (!StringUtils.hasText(config.getApiKey())) {
            throw new BusinessException(400, "请填写API Key");
        }
        config.setPromptVersion(normalize(request.getPrompt_version()));
        config.setRequestTimeoutMs(request.getRequest_timeout_ms());
        config.setMaxRetries(request.getMax_retries());
        config.setEnabled(request.getEnabled() == null ? 0 : request.getEnabled());
        configMapper.updateById(config);
        return toVO(config);
    }

    @Override
    public AiModelConfig getAiModelConfig() {
        return getOrCreateConfig();
    }

    private AiModelConfig getOrCreateConfig() {
        AiModelConfig config = configMapper.selectById(DEFAULT_ID);
        if (config != null) {
            if (config.getRequestTimeoutMs() == null || config.getMaxRetries() == null || config.getEnabled() == null) {
                if (config.getRequestTimeoutMs() == null) {
                    config.setRequestTimeoutMs(30000);
                }
                if (config.getMaxRetries() == null) {
                    config.setMaxRetries(2);
                }
                if (config.getEnabled() == null) {
                    config.setEnabled(0);
                }
                configMapper.updateById(config);
            }
            return config;
        }
        AiModelConfig defaults = new AiModelConfig();
        defaults.setId(DEFAULT_ID);
        defaults.setRequestTimeoutMs(30000);
        defaults.setMaxRetries(2);
        defaults.setEnabled(0);
        configMapper.insert(defaults);
        return defaults;
    }

    private AiModelConfigVO toVO(AiModelConfig config) {
        AiModelConfigVO vo = new AiModelConfigVO();
        vo.setProvider_name(defaultString(config.getProviderName()));
        vo.setBase_url(defaultString(config.getBaseUrl()));
        vo.setModel_name(defaultString(config.getModelName()));
        vo.setHas_api_key(StringUtils.hasText(config.getApiKey()));
        vo.setPrompt_version(defaultString(config.getPromptVersion()));
        vo.setRequest_timeout_ms(config.getRequestTimeoutMs());
        vo.setMax_retries(config.getMaxRetries());
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
