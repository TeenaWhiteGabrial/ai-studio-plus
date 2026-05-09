package com.aistudio.service.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MailConfigVO {

    private String senderEmail;
    private Boolean hasAuthCode;
    private LocalDateTime updatedAt;
}
