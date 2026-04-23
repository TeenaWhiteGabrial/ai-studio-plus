package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Tutorial 版本实体
 */
@Data
@TableName("tutorial_version")
public class TutorialVersion {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联 tutorial.id
     */
    private Long tutorialId;

    /**
     * 版本号，如：1.2.3
     */
    private String version;

    /**
     * 主版本号
     */
    private Integer major;

    /**
     * 次版本号
     */
    private Integer minor;

    /**
     * 修订版本号
     */
    private Integer patch;

    /**
     * 版本数字表示，用于排序（major*10000 + minor*100 + patch）
     */
    private Integer versionNumber;

    /**
     * 版本变更说明
     */
    @TableField("change_log")
    private String changelog;

    /**
     * 发布者用户ID
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 获取版本号数字表示
     */
    public static int calculateVersionNumber(int major, int minor, int patch) {
        return major * 10000 + minor * 100 + patch;
    }

    /**
     * 从版本号字符串解析
     */
    public static VersionParts parseVersion(String version) {
        String[] parts = version.split("\\.");
        int major = Integer.parseInt(parts[0]);
        int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
        return new VersionParts(major, minor, patch);
    }

    /**
     * 版本号各部分
     */
    public record VersionParts(int major, int minor, int patch) {
        public int toNumber() {
            return calculateVersionNumber(major, minor, patch);
        }

        public String toVersionString() {
            return major + "." + minor + "." + patch;
        }
    }
}
