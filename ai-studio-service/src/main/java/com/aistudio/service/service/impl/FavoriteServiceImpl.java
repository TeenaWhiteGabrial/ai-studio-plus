package com.aistudio.service.service.impl;

import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Article;
import com.aistudio.service.entity.Favorite;
import com.aistudio.service.entity.Question;
import com.aistudio.service.mapper.ArticleMapper;
import com.aistudio.service.mapper.FavoriteMapper;
import com.aistudio.service.mapper.QuestionMapper;
import com.aistudio.service.service.FavoriteService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final ArticleMapper articleMapper;
    private final QuestionMapper questionMapper;

    @Override
    @Transactional
    public void addFavorite(Long userId, String targetType, Long targetId) {
        String normalizedType = normalizeTargetType(targetType);

        LambdaQueryWrapper<Favorite> q = new LambdaQueryWrapper<>();
        q.eq(Favorite::getUserId, userId)
            .eq(Favorite::getResourceId, targetId);
        appendResourceTypeFilter(q, normalizedType);

        Favorite existing = favoriteMapper.selectOne(q);
        if (existing != null) {
            return;
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setResourceType(normalizedType);
        favorite.setResourceId(targetId);
        favoriteMapper.insert(favorite);

        incrementFavoriteCount(normalizedType, targetId, 1);
        log.info("Add favorite: userId={}, targetType={}, targetId={}", userId, normalizedType, targetId);
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, String targetType, Long targetId) {
        String normalizedType = normalizeTargetType(targetType);

        LambdaQueryWrapper<Favorite> q = new LambdaQueryWrapper<>();
        q.eq(Favorite::getUserId, userId)
            .eq(Favorite::getResourceId, targetId);
        appendResourceTypeFilter(q, normalizedType);

        Favorite existing = favoriteMapper.selectOne(q);
        if (existing == null) {
            return;
        }

        favoriteMapper.delete(q);
        incrementFavoriteCount(normalizedType, targetId, -1);
        log.info("Remove favorite: userId={}, targetType={}, targetId={}", userId, normalizedType, targetId);
    }

    @Override
    public PageResult listFavorites(Long userId, String targetType, int page, int size) {
        String normalizedType = normalizeTargetType(targetType);

        LambdaQueryWrapper<Favorite> q = new LambdaQueryWrapper<>();
        q.eq(Favorite::getUserId, userId);
        if (normalizedType != null && !normalizedType.isEmpty()) {
            appendResourceTypeFilter(q, normalizedType);
        }
        q.orderByDesc(Favorite::getCreatedAt);

        Page<Favorite> p = new Page<>(page, size);
        Page<Favorite> result = favoriteMapper.selectPage(p, q);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    public boolean checkFavorite(Long userId, String targetType, Long targetId) {
        String normalizedType = normalizeTargetType(targetType);

        LambdaQueryWrapper<Favorite> q = new LambdaQueryWrapper<>();
        q.eq(Favorite::getUserId, userId)
            .eq(Favorite::getResourceId, targetId);
        appendResourceTypeFilter(q, normalizedType);
        return favoriteMapper.exists(q);
    }

    private void incrementFavoriteCount(String targetType, Long targetId, int delta) {
        if ("ARTICLE".equalsIgnoreCase(targetType)) {
            articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                .eq(Article::getId, targetId)
                .setSql("favorite_count = GREATEST(favorite_count + " + delta + ", 0)"));
            return;
        }

        if ("QUESTION".equalsIgnoreCase(targetType)) {
            questionMapper.update(null, new LambdaUpdateWrapper<Question>()
                .eq(Question::getId, targetId)
                .setSql("favorite_count = GREATEST(favorite_count + " + delta + ", 0)"));
        }
    }

    private String normalizeTargetType(String targetType) {
        if (targetType == null) {
            return null;
        }
        return targetType.trim().toUpperCase(Locale.ROOT);
    }

    private void appendResourceTypeFilter(LambdaQueryWrapper<Favorite> q, String normalizedType) {
        if (normalizedType == null || normalizedType.isEmpty()) {
            return;
        }
        q.apply("UPPER(resource_type) = {0}", normalizedType);
    }
}
