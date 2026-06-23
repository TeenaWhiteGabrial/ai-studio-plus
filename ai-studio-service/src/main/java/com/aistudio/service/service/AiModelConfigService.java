package com.aistudio.service.service;

import com.aistudio.service.dto.request.AiModelConfigRequest;
import com.aistudio.service.dto.response.AiModelConfigVO;
import com.aistudio.service.entity.AiModelConfig;

public interface AiModelConfigService {
    AiModelConfigVO getConfig();
    AiModelConfigVO updateConfig(AiModelConfigRequest request);
    AiModelConfig getAiModelConfig();
}
