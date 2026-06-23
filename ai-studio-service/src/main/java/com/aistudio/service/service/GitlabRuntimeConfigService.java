package com.aistudio.service.service;

import com.aistudio.service.dto.request.GitlabRuntimeConfigRequest;
import com.aistudio.service.dto.response.GitlabRuntimeConfigVO;
import com.aistudio.service.entity.GitlabRuntimeConfig;

public interface GitlabRuntimeConfigService {
    GitlabRuntimeConfigVO getConfig();
    GitlabRuntimeConfigVO updateConfig(GitlabRuntimeConfigRequest request);
    GitlabRuntimeConfig getRuntimeConfig();
}
