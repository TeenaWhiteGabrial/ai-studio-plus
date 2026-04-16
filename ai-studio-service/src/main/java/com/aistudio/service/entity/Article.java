package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

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
    private Integer viewsCount;
    private Integer likesCount;
    private Integer commentsCount;
    private Integer favoriteCount;
    private Integer followersCount;
    /** 状态: 0-草稿 1-已发布 2-已下架 */
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    /** 逻辑删除: 0-正常 1-已删除 */
    private Integer isDeleted;
}
