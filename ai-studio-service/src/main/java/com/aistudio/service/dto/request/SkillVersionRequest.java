package com.aistudio.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 发布新版本请求
 */
@Data
public class SkillVersionRequest {

    /**
     * 版本升级类型
     */
    @NotNull(message = "请选择版本升级类型")
    private VersionBumpType bumpType;

    /**
     * OSS key（前端直传后返回）
     */
    @NotBlank(message = "请上传技能文件")
    private String ossKey;

    /**
     * 文件大小
     */
    @NotNull(message = "文件大小不能为空")
    private Long fileSize;

    /**
     * 变更日志
     */
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
