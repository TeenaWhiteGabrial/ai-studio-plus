package com.aistudio.service.service;

import com.aistudio.service.dto.request.SiteConfigRequest;
import com.aistudio.service.dto.response.SiteConfigVO;

public interface SiteConfigService {

    SiteConfigVO getConfig();

    SiteConfigVO updateConfig(SiteConfigRequest request);
}
