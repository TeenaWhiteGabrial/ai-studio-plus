package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("activity_stat_group_member")
public class ActivityStatGroupMember {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long statGroupId;
    private Long userId;
    private LocalDateTime createdAt;
}
