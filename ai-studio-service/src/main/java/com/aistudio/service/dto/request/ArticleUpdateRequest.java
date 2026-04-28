package com.aistudio.service.dto.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleUpdateRequest {

    private String title;
    private String content;
    private String summary;
    private String coverImage;
    private Long folderId;
    private List<Long> tagIds;

    /**
     * 发布类型: 0-草稿 1-立即发布 2-定时发布
     */
    private Integer publishType;

    /**
     * 定时发布时间 (当 publishType=2 时必填)
     */
    private LocalDateTime scheduledPublishTime;
}
