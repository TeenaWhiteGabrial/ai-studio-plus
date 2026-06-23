package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("activity_stat_group")
public class ActivityStatGroup {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String groupName;
    private String groupKey;
    private String description;
    private String scopeType;
    private Integer enabled;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
