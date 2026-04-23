package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("plugin")
public class Plugin {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String category;
    private String icon;
    private String fileOssKey;
    private String fileUrl;
    private Long fileSize;
    private Integer status;            // 0-待审核 1-通过 2-拒绝
    private Long creatorId;
    private LocalDateTime reviewTime;
    private String reviewComment;
    private Integer downloadCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 版本控制相关字段
    private Long latestVersionId;
    private String latestVersion;
    private Integer totalVersions;
}
