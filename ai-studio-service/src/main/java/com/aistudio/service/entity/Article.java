package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("article")
public class Article {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String content;
    private String summary;
    private String coverImage;
    private Long authorId;
    private String authorName;
    private Long folderId;
    private Integer viewsCount;
    private Integer likesCount;
    private Integer commentsCount;
    private Integer favoriteCount;
    private Integer followersCount;
    /** 状态: 0-草稿 1-已发布 2-已下架 */
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    /** 定时发布时间 */
    private LocalDateTime publishedAt;
    /** 逻辑删除: 0-正常 1-已删除 */
    private Integer isDeleted;

    @TableField(exist = false)
    private List<Long> tagIds;
}
