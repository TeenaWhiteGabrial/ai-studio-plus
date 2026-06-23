package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("gitlab_user_snapshot")
public class GitlabUserSnapshot {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long gitlabUserId;
    private String username;
    private String name;
    private String email;
    private String avatarUrl;
    private String state;
    private LocalDateTime syncedAt;
}
