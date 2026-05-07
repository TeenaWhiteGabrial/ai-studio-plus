package com.aistudio.service.service.impl;

import com.aistudio.service.dto.request.SiteConfigRequest;
import com.aistudio.service.dto.response.SiteConfigVO;
import com.aistudio.service.entity.SiteConfig;
import com.aistudio.service.mapper.SiteConfigMapper;
import com.aistudio.service.service.SiteConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SiteConfigServiceImpl implements SiteConfigService {

    private static final Long DEFAULT_ID = 1L;
    private static final String DEFAULT_FOOTER_LINKS = "[{\"name\":\"首页\",\"url\":\"/\"},{\"name\":\"社区\",\"url\":\"/community\"},{\"name\":\"资源中心\",\"url\":\"/resources\"},{\"name\":\"个人中心\",\"url\":\"/profile\"}]";

    private final SiteConfigMapper siteConfigMapper;

    @Override
    public SiteConfigVO getConfig() {
        SiteConfig config = getOrCreateConfig();
        return toVO(config);
    }

    @Override
    @Transactional
    public SiteConfigVO updateConfig(SiteConfigRequest request) {
        SiteConfig config = getOrCreateConfig();
        config.setSiteName(request.getSiteName());
        config.setSiteDescription(request.getSiteDescription());
        config.setLogoUrl(request.getLogoUrl());
        config.setIconUrl(request.getIconUrl());
        config.setFooterText(request.getFooterText());
        config.setFooterCopyright(request.getFooterCopyright());
        config.setFooterRecord(request.getFooterRecord());
        config.setFooterLinks(request.getFooterLinks());
        config.setContacts(request.getContacts());
        siteConfigMapper.updateById(config);
        return toVO(config);
    }

    private SiteConfig getOrCreateConfig() {
        SiteConfig config = siteConfigMapper.selectById(DEFAULT_ID);
        if (config != null) {
            return config;
        }

        SiteConfig defaults = new SiteConfig();
        defaults.setId(DEFAULT_ID);
        defaults.setSiteName("AI Studio");
        defaults.setSiteDescription("AI 应用开发平台");
        defaults.setLogoUrl("/ai-studio-logo.svg");
        defaults.setIconUrl("/favicon.png");
        defaults.setFooterText("面向研发团队的 AI 技术社区与资源平台。");
        defaults.setFooterCopyright("Copyright © 2026 AI Studio");
        defaults.setFooterRecord("");
        defaults.setFooterLinks(DEFAULT_FOOTER_LINKS);
        defaults.setContacts("");
        siteConfigMapper.insert(defaults);
        return defaults;
    }

    private SiteConfigVO toVO(SiteConfig config) {
        SiteConfigVO vo = new SiteConfigVO();
        BeanUtils.copyProperties(config, vo);
        return vo;
    }
}
