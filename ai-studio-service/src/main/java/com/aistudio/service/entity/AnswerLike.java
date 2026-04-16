package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("answer_like")
public class AnswerLike {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long answerId;
    private LocalDateTime createdAt;
}
