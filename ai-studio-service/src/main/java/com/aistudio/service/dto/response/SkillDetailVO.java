package com.aistudio.service.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Skill 详情 VO
 */
@Data
public class SkillDetailVO {

    private Long id;
    private String name;
    private String description;
    private String category;

    // 版本信息
    private Long latestVersionId;
    private String latestVersion;
    private Integer totalVersions;

    // 归属信息
    private Long creatorId;
    private String creatorName;
    private Long deptId;
    private String deptName;

    // 审核信息
    private Integer status;
    private LocalDateTime reviewTime;
    private String reviewComment;

    // 统计
    private Integer downloadCount;

    // 时间
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 版本列表（根据权限返回）
    private List<SkillVersionVO> versions;

    // 最新版本下载 URL（列表页直接下载用）
    private String ossUrl;
}
