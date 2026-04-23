package com.aistudio.service.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Plugin 版本响应
 */
@Data
public class PluginVersionVO {

    private Long id;
    private String version;
    private String ossUrl;
    private Long fileSize;
    private String changelog;
    private LocalDateTime createdAt;
}
