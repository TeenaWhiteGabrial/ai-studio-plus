package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("gitlab_project_snapshot")
public class GitlabProjectSnapshot {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long gitlabProjectId;
    private String name;
    private String pathWithNamespace;
    private String webUrl;
    private String defaultBranch;
    private LocalDateTime syncedAt;
}
