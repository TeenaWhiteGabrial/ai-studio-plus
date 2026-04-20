package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("answer")
public class Answer {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long questionId;
    private Long authorId;
    private String authorName;
    private String content;
    private Integer likesCount;
    /** 是否最佳答案: 0-否 1-是 */
    private Integer isBest;
    /** 是否下架: 0-正常 1-已下架 */
    private Integer takenDown;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    /** 逻辑删除: 0-正常 1-已删除 */
    private Integer isDeleted;
}
