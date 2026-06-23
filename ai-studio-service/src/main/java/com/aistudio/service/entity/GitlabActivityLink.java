package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("gitlab_activity_link")
public class GitlabActivityLink {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long eventLogId;
    private String targetType;
    private Long targetId;
    private String relationType;
    private LocalDateTime createdAt;
}
