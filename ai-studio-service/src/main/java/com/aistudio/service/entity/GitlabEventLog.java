package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("gitlab_event_log")
public class GitlabEventLog {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String eventUid;
    private String eventType;
    private Long projectId;
    private Long gitlabProjectId;
    private Long gitlabUserId;
    private String refName;
    private LocalDateTime eventTime;
    @TableField("payload_json")
    private String payloadJson;
    private String processStatus;
    private String processMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
