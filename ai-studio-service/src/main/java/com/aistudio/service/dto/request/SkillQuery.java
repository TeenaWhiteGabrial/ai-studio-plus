package com.aistudio.service.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 技能查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SkillQuery extends PageRequest {

    private String keyword;

    private String category;
}
