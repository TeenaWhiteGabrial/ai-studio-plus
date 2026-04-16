package com.aistudio.service.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ArticleUpdateRequest {

    private String title;
    private String content;
    private String summary;
    private String coverImage;
    private List<Long> tagIds;
}
