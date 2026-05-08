package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_notification_rule_group")
public class UserNotificationRuleGroup {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long groupId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
