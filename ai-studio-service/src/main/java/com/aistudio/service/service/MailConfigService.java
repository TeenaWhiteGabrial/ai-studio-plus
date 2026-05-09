package com.aistudio.service.service;

import com.aistudio.service.dto.request.MailConfigRequest;
import com.aistudio.service.dto.response.MailConfigVO;
import com.aistudio.service.entity.MailConfig;

public interface MailConfigService {

    MailConfigVO getConfig();

    MailConfigVO updateConfig(MailConfigRequest request);

    MailConfig getMailConfig();
}
