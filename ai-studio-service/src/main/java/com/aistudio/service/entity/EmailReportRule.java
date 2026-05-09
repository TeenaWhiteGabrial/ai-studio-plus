package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("email_report_rule")
public class EmailReportRule {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String toRecipientUserIdsJson;
    private String ccRecipientUserIdsJson;
    private String recipientsJson;
    private String userIdsJson;
    private String sendTime;
    private Integer status;
    private LocalDateTime lastSentAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
