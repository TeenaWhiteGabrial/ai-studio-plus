package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("skill")
public class Skill {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String category;
    private String source;

    // 版本管理
    private Long latestVersionId;
    private String latestVersion;
    private Integer totalVersions;

    // 元数据字段
    private String author;
    private String tags;

    // 归属信息
    private Long creatorId;
    private Long deptId;

    // 审核信息
    private Integer status;            // 0-待审核 1-通过 2-拒绝
    private LocalDateTime reviewTime;
    private String reviewComment;

    // 统计
    private Integer downloadCount;

    // 软删除
    private Integer isDeleted;     // 0-正常 1-已删除
    private LocalDateTime deletedAt;
    private Long deletedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
