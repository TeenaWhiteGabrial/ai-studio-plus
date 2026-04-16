package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("question")
public class Question {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String content;
    private Long authorId;
    private String authorName;
    private String tags;
    private Integer answersCount;
    private Integer viewsCount;
    private Integer favoriteCount;
    private Integer followersCount;
    /** 是否有最佳答案: 0-无 1-有 */
    private Integer hasBestAnswer;
    private Long bestAnswerId;
    /** 是否下架: 0-正常 1-已下架 */
    private Integer takenDown;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    /** 逻辑删除: 0-正常 1-已删除 */
    private Integer isDeleted;
}
