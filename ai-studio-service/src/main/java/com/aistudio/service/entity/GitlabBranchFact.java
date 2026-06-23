package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("gitlab_branch_fact")
public class GitlabBranchFact {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long gitlabProjectId;
    private String branchName;
    private Integer isDefaultBranch;
    private String lastCommitSha;
    private LocalDateTime syncedAt;
}
