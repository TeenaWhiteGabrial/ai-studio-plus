package com.aistudio.service.dto.request;

import lombok.Data;

import java.util.List;

/**
 * 文章列表查询请求 (Console 端使用)
 */
@Data
public class ArticleListRequest {

    /**
     * 关键字搜索
     */
    private String keyword;

    /**
     * 标签 ID
     */
    private Long tagId;

    /**
     * 文件夹 ID
     */
    private Long folderId;

    /**
     * 状态列表筛选: 0-草稿 1-已发布 2-已下架
     */
    private List<Integer> statusList;

    /**
     * 单状态筛选: 0-草稿 1-已发布 2-已下架
     */
    private Integer status;

    /**
     * 排序方式: latest(最新)、hot(热门)
     */
    private String sort = "latest";

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
