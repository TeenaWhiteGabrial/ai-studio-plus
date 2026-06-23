package com.aistudio.service.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AiModelConfigRequest {

    @NotBlank(message = "模型提供方不能为空")
    @JsonAlias("providerName")
    private String provider_name;

    @NotBlank(message = "模型服务URL不能为空")
    @JsonAlias("baseUrl")
    private String base_url;

    @NotBlank(message = "模型名称不能为空")
    @JsonAlias("modelName")
    private String model_name;

    @JsonAlias("apiKey")
    private String api_key;

    @JsonAlias("promptVersion")
    private String prompt_version;

    @NotNull(message = "请求超时时间不能为空")
    @JsonAlias("requestTimeoutMs")
    private Integer request_timeout_ms;

    @NotNull(message = "最大重试次数不能为空")
    @JsonAlias("maxRetries")
    private Integer max_retries;

    private Integer enabled;
}
