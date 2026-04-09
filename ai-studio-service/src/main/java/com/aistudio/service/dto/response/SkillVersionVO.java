package com.aistudio.service.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Skill 版本 VO
 */
@Data
public class SkillVersionVO {

    private Long id;
    private Long skillId;

    // 版本号
    private String version;
    private Integer major;
    private Integer minor;
    private Integer patch;

    // OSS
    private String ossUrl;
    private Long fileSize;
    private String fileSizeText;  // 格式化后的文件大小，如 "12.5 MB"

    // 变更日志
    private String changelog;

    // 发布者
    private Long createdBy;
    private String creatorName;

    // 时间
    private LocalDateTime createdAt;
}
