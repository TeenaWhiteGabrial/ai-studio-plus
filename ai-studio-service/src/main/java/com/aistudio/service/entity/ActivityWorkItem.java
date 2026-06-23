package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("activity_work_item")
public class ActivityWorkItem {

    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDate analysisDate;
    private Long userId;
    private Long gitlabUserId;
    private Long projectId;
    private Long gitlabProjectId;
    private Long statGroupId;
    private String title;
    private String summary;
    private String workType;
    private String progressStatus;
    private String riskLevel;
    private String riskSummary;
    private String blockingStatus;
    private String blockingSummary;
    private String moduleNames;
    private Integer relatedCommitCount;
    private Integer relatedMrCount;
    private BigDecimal confidence;
    private String evidenceSummary;
    private String analysisEngine;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
