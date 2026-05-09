package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mail_config")
public class MailConfig {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String senderEmail;
    private String authCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
