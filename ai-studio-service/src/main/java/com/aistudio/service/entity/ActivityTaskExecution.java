package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("activity_task_execution")
public class ActivityTaskExecution {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String taskType;
    private LocalDate targetDate;
    private Long userId;
    private Long projectId;
    private String targetRef;
    private String status;
    private Integer attemptCount;
    private LocalDateTime nextRunAt;
    private String errorMessage;
    @TableField("payload_json")
    private String payloadJson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
