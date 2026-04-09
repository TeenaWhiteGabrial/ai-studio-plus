package com.aistudio.service.dto.request;

import lombok.Data;

/**
 * 更新技能请求
 */
@Data
public class SkillUpdateRequest {

    private String description;

    private String category;
}
