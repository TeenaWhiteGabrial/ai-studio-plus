package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("email_report_send_log")
public class EmailReportSendLog {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long ruleId;
    private String ruleName;
    private LocalDate reportDate;
    private String triggerType;
    private String status;
    private String toRecipientsJson;
    private String ccRecipientsJson;
    private String subject;
    private String errorMessage;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
}
