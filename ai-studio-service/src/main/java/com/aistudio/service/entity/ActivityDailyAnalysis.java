package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("activity_daily_analysis")
public class ActivityDailyAnalysis {

    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDate analysisDate;
    private Long userId;
    private Long gitlabUserId;
    private Long projectId;
    private Long gitlabProjectId;
    private Long statGroupId;
    private String workSummary;
    private String progressSummary;
    private String riskSummary;
    private String blockingSummary;
    private String reportTitle;
    private String reportContent;
    private String analysisEngine;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
