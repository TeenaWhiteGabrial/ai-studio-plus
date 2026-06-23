package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("gitlab_commit_fact")
public class GitlabCommitFact {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long gitlabProjectId;
    private String commitSha;
    private String shortSha;
    private Long authorGitlabUserId;
    private String authorName;
    private String authorEmail;
    private String commitMessage;
    private String branchName;
    private LocalDateTime committedAt;
    private LocalDateTime pushedAt;
    @TableField("file_paths_json")
    private String filePathsJson;
    private String diffSummary;
    @TableField("mr_ids_json")
    private String mrIdsJson;
    private LocalDateTime lastSyncedAt;
}
