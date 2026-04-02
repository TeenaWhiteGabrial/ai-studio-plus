package com.aistudio.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PluginRequest {

    @NotBlank(message = "插件名称不能为空")
    private String name;

    private String description;
    private String type;

    @NotBlank(message = "版本号不能为空")
    private String version;

    @NotBlank(message = "文件OSS Key不能为空")
    private String fileOssKey;

    @NotBlank(message = "文件URL不能为空")
    private String fileUrl;

    private Long fileSize;
    private Integer status = 1;
}
