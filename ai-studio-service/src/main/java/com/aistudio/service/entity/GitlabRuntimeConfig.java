package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("gitlab_runtime_config")
public class GitlabRuntimeConfig {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String baseUrl;
    private String privateToken;
    private String webhookToken;
    private Integer schedulesEnabled;
    private String dailyAnalyzeCron;
    private String dailyReportCron;
    private String backfillCron;
    private Integer enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
