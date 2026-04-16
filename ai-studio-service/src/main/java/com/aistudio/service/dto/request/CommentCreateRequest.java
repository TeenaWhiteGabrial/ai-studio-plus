package com.aistudio.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentCreateRequest {

    @NotBlank(message = "评论类型不能为空")
    private String targetType;

    private Long targetId;

    private Long parentId;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 2000, message = "评论最多2000字")
    private String content;
}
