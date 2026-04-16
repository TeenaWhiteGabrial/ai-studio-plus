package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 社区评论表（区别于资源评论 comment 表，用于文章/问答/回答下的评论）
 */
@Data
@TableName("community_comment")
public class CommunityComment {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 评论类型: article / question / answer */
    private String commentType;
    private Long targetId;
    private Long parentId;
    private Long rootId;
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    private String content;
    private Integer likesCount;
    private LocalDateTime createdAt;
    /** 逻辑删除: 0-正常 1-已删除 */
    private Integer isDeleted;
}
