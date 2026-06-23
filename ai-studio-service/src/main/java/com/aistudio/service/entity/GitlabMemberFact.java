package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("gitlab_member_fact")
public class GitlabMemberFact {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long gitlabProjectId;
    private Long gitlabUserId;
    private Integer accessLevel;
    private String memberState;
    private LocalDateTime syncedAt;
}
