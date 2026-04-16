package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏表
 * 支持 SKILL/PLUGIN/ARTICLE/QUESTION 等多种资源类型
 */
@Data
@TableName("favorite")
public class Favorite {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    /** 资源类型: SKILL / PLUGIN / ARTICLE / QUESTION */
    private String resourceType;
    private Long resourceId;
    private LocalDateTime createdAt;
}
