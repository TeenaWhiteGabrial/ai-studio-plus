package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("gitlab_mr_fact")
public class GitlabMrFact {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long gitlabMrId;
    private Long gitlabProjectId;
    private Long iid;
    private String title;
    private String description;
    private String state;
    private String sourceBranch;
    private String targetBranch;
    private Long authorGitlabUserId;
    private LocalDateTime createdAtGitlab;
    private LocalDateTime updatedAtGitlab;
    private LocalDateTime lastSyncedAt;
}
