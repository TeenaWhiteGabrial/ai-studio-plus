package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Plugin 版本实体
 */
@Data
@TableName("plugin_version")
public class PluginVersion {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联 plugin.id
     */
    private Long pluginId;

    /**
     * 版本号，如：1.2.3
     */
    private String version;

    /**
     * ZIP包OSS存储key
     */
    private String ossKey;

    /**
     * OSS访问URL
     */
    private String ossUrl;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * MD5校验码
     */
    private String md5;

    /**
     * 版本变更说明
     */
    @TableField("change_log")
    private String changelog;

    /**
     * 状态: 0-禁用 1-启用
     */
    private Integer status;

    /**
     * 发布者用户ID
     */
    @TableField("published_by")
    private Long publishedBy;

    /**
     * 发布时间
     */
    @TableField("published_at")
    private LocalDateTime publishedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}