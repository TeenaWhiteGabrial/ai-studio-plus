package com.aistudio.service.dto.response;

import lombok.Data;

@Data
public class AiModelConfigVO {
    private String provider_name;
    private String base_url;
    private String model_name;
    private Boolean has_api_key;
    private String prompt_version;
    private Integer request_timeout_ms;
    private Integer max_retries;
    private Integer enabled;
}
