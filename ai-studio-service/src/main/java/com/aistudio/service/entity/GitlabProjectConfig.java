package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("gitlab_project_config")
public class GitlabProjectConfig {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long projectId;
    private Long gitlabProjectId;
    private String gitlabProjectName;
    private String gitlabProjectPath;
    private String gitlabGroupName;
    private Integer enabled;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
