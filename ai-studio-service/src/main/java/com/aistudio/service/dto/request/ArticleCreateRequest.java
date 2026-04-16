package com.aistudio.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ArticleCreateRequest {

    @NotBlank(message = "文章标题不能为空")
    @Size(max = 200, message = "标题最多200字")
    private String title;

    @NotBlank(message = "文章内容不能为空")
    private String content;

    private String summary;

    private String coverImage;

    private List<Long> tagIds;
}
