package com.aistudio.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class McpServerRequest {

    @NotBlank(message = "名称不能为空")
    private String name;

    private String description;

    @NotBlank(message = "API端点不能为空")
    private String apiEndpoint;

    @NotBlank(message = "认证类型不能为空")
    private String authType;

    private String configJson;
    private Integer status = 1;
}
