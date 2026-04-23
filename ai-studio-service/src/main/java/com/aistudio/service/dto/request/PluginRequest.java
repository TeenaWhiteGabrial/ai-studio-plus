package com.aistudio.service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PluginRequest {

    @NotBlank(message = "插件名称不能为空")
    private String name;

    private String description;
    private String category;
    private String icon;

    /**
     * OSS key（前端直传后返回）
     */
    @NotBlank(message = "请上传插件文件")
    @JsonProperty("fileOssKey")
    private String fileOssKey;

    /**
     * 文件大小
     */
    @JsonProperty("fileSize")
    private Long fileSize;
}
