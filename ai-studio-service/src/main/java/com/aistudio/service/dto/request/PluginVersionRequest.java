package com.aistudio.service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 发布新版本请求
 */
@Data
public class PluginVersionRequest {

    /**
     * 版本升级类型
     */
    @NotNull(message = "请选择版本升级类型")
    @JsonProperty("bumpType")
    private VersionBumpType bumpType;

    @NotBlank(message = "请上传插件文件")
    @JsonProperty("ossKey")
    private String ossKey;

    @NotNull(message = "文件大小不能为空")
    @JsonProperty("fileSize")
    private Long fileSize;

    @JsonProperty("changelog")
    private String changelog;

    /**
     * 版本升级类型枚举
     */
    public enum VersionBumpType {
        PATCH,  // 修订版本 1.0.0 -> 1.0.1
        MINOR,  // 次要版本 1.0.0 -> 1.1.0
        MAJOR   // 主要版本 1.0.0 -> 2.0.0
    }
}
