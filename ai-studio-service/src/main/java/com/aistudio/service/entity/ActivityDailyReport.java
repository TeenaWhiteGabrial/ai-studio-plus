package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("activity_daily_report")
public class ActivityDailyReport {

    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDate reportDate;
    private String reportScope;
    private Long userId;
    private Long projectId;
    private Long statGroupId;
    private String title;
    private String content;
    @TableField("report_data_json")
    private String reportDataJson;
    private String generatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
