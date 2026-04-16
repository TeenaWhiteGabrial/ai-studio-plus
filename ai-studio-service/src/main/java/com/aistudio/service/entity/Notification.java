package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("notification")
public class Notification {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    /** 通知类型: comment / reply / accept */
    private String type;
    private String content;
    private Long sourceId;
    private String sourceType;
    /** 是否已读: 0-未读 1-已读 */
    private Integer isRead;
    private LocalDateTime createdAt;
}
