package com.aistudio.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("community_comment_like")
public class CommunityCommentLike {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long commentId;
    private LocalDateTime createdAt;
}
