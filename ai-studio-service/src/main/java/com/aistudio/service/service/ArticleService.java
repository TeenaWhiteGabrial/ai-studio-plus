package com.aistudio.service.service;

import com.aistudio.service.dto.request.ArticleCreateRequest;
import com.aistudio.service.dto.request.ArticleUpdateRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Article;

public interface ArticleService {

    PageResult listArticles(String keyword, Long tagId, String sort, int page, int size);

    Article getArticle(Long id);

    Long createArticle(ArticleCreateRequest request, Long authorId);

    void updateArticle(Long id, ArticleUpdateRequest request, Long userId);

    void deleteArticle(Long id, Long userId);

    void likeArticle(Long id, Long userId);

    boolean isLiked(Long id, Long userId);

    void takedown(Long id);
}
