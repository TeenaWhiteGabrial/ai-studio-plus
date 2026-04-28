package com.aistudio.service.service;

import com.aistudio.service.dto.request.ArticleCreateRequest;
import com.aistudio.service.dto.request.ArticleFolderRequest;
import com.aistudio.service.dto.request.ArticleListRequest;
import com.aistudio.service.dto.request.ArticleUpdateRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Article;
import com.aistudio.service.entity.ArticleFolder;

import java.util.List;

public interface ArticleService {

    /**
     * Portal 端 - 文章列表（仅显示已发布状态）
     */
    PageResult listArticles(String keyword, Long tagId, String sort, int page, int size);

    /**
     * Portal 端 - 文章详情
     */
    Article getArticle(Long id);

    /**
     * Console 端 - 我的文章列表（所有状态）
     */
    PageResult listMyArticles(ArticleListRequest request, Long userId);

    /**
     * Console 端 - 我的文章文件夹
     */
    List<ArticleFolder> listMyFolders(Long userId);

    /**
     * Console 端 - 创建文章文件夹
     */
    Long createFolder(ArticleFolderRequest request, Long userId);

    /**
     * Console 端 - 更新文章文件夹
     */
    void updateFolder(Long id, ArticleFolderRequest request, Long userId);

    /**
     * Console 端 - 删除文章文件夹
     */
    void deleteFolder(Long id, Long userId);

    /**
     * Console 端 - 获取文章用于编辑（草稿也可查看）
     */
    Article getArticleForEdit(Long id, Long userId);

    /**
     * Console 端 - 创建文章
     */
    Long createArticle(ArticleCreateRequest request, Long authorId);

    /**
     * Console 端 - 更新文章
     */
    void updateArticle(Long id, ArticleUpdateRequest request, Long userId);

    /**
     * Console 端 - 删除文章
     */
    void deleteArticle(Long id, Long userId);

    /**
     * Console 端 - 立即发布文章
     */
    void publishArticle(Long id, Long userId);

    /**
     * Console 端 - 定时发布文章
     */
    void schedulePublish(Long id, Long userId, java.time.LocalDateTime publishTime);

    /**
     * Console 端 - 取消定时发布
     */
    void cancelSchedule(Long id, Long userId);

    /**
     * Portal 端 - 点赞文章
     */
    void likeArticle(Long id, Long userId);

    /**
     * Portal 端 - 检查是否已点赞
     */
    boolean isLiked(Long id, Long userId);

    /**
     * Admin 端 - 下架文章
     */
    void takedown(Long id);
}
