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

    // GitLab 相关字段
    private String gitlabRepo;
    private String gitlabPath;
    private String gitlabCommitSha;

    // 元数据字段（从 YAML frontmatter 解析）
    private String author;
    private String tags;

    // 同步状态
    private Integer syncStatus;    // 0-待同步 1-成功 2-失败
    private LocalDateTime lastSyncAt;
    private String syncError;
    private Integer isDeleted;     // 0-正常 1-已删除

    // 统计
    private Integer downloadCount;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 已废弃字段（保留但不使用）
    @Deprecated
    private String contentOssKey;
    @Deprecated
    private String contentUrl;
    @Deprecated
    private Integer status;
}
