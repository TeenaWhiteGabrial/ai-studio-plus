package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.request.MailConfigRequest;
import com.aistudio.service.dto.response.MailConfigVO;
import com.aistudio.service.entity.MailConfig;
import com.aistudio.service.mapper.MailConfigMapper;
import com.aistudio.service.service.MailConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class MailConfigServiceImpl implements MailConfigService {

    private static final Long DEFAULT_ID = 1L;

    private final MailConfigMapper mailConfigMapper;

    @Value("${spring.mail.username:}")
    private String legacySenderEmail;

    @Value("${spring.mail.password:}")
    private String legacyAuthCode;

    @Override
    public MailConfigVO getConfig() {
        return toVO(getOrCreateConfig());
    }

    @Override
    @Transactional
    public MailConfigVO updateConfig(MailConfigRequest request) {
        MailConfig config = getOrCreateConfig();
        config.setSenderEmail(normalize(request.getSenderEmail()));
        if (StringUtils.hasText(request.getAuthCode())) {
            config.setAuthCode(request.getAuthCode().trim());
        } else if (!StringUtils.hasText(config.getAuthCode())) {
            throw new BusinessException(400, "请填写安全码");
        }
        mailConfigMapper.updateById(config);
        return toVO(config);
    }

    @Override
    public MailConfig getMailConfig() {
        return getOrCreateConfig();
    }

    private MailConfig getOrCreateConfig() {
        MailConfig config = mailConfigMapper.selectById(DEFAULT_ID);
        if (config != null) {
            boolean changed = false;
            if (!StringUtils.hasText(config.getSenderEmail()) && StringUtils.hasText(legacySenderEmail)) {
                config.setSenderEmail(legacySenderEmail.trim());
                changed = true;
            }
            if (!StringUtils.hasText(config.getAuthCode()) && StringUtils.hasText(legacyAuthCode)) {
                config.setAuthCode(legacyAuthCode.trim());
                changed = true;
            }
            if (changed) {
                mailConfigMapper.updateById(config);
            }
            return config;
        }

        MailConfig defaults = new MailConfig();
        defaults.setId(DEFAULT_ID);
        defaults.setSenderEmail(normalize(legacySenderEmail));
        defaults.setAuthCode(normalize(legacyAuthCode));
        mailConfigMapper.insert(defaults);
        return defaults;
    }

    private MailConfigVO toVO(MailConfig config) {
        MailConfigVO vo = new MailConfigVO();
        BeanUtils.copyProperties(config, vo);
        vo.setSenderEmail(StringUtils.hasText(config.getSenderEmail()) ? config.getSenderEmail() : "");
        vo.setHasAuthCode(StringUtils.hasText(config.getAuthCode()));
        return vo;
    }

    private String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
