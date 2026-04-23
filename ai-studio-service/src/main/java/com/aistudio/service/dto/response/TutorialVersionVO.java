package com.aistudio.service.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Tutorial 版本响应
 */
@Data
public class TutorialVersionVO {

    private Long id;
    private String version;
    private String changelog;
    private LocalDateTime createdAt;
}
