package com.aistudio.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建技能请求
 */
@Data
public class SkillCreateRequest {

    @NotBlank(message = "技能名称不能为空")
    private String name;

    private String description;

    @NotBlank(message = "分类不能为空")
    private String category;

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
     * 初始版本变更日志
     */
    private String changelog = "初始版本";
}
