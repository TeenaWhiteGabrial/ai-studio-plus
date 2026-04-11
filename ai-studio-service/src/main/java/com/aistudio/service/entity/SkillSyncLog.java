package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("skill_sync_log")
public class SkillSyncLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer createdCount;
    private Integer updatedCount;
    private Integer deletedCount;
    private Integer errorCount;
    private Integer durationMs;
    private String triggerType;
    private String errorMessage;
    private LocalDateTime createdAt;
}
