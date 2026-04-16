package com.aistudio.service.service.impl;

import com.aistudio.service.entity.Article;
import com.aistudio.service.entity.Favorite;
import com.aistudio.service.entity.Question;
import com.aistudio.service.mapper.ArticleMapper;
import com.aistudio.service.mapper.FavoriteMapper;
import com.aistudio.service.mapper.QuestionMapper;
import com.aistudio.service.service.FavoriteService;
import com.aistudio.service.dto.response.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        LambdaQueryWrapper<Favorite> q = new LambdaQueryWrapper<>();
        q.eq(Favorite::getUserId, userId)
                .eq(Favorite::getResourceType, targetType)
                .eq(Favorite::getResourceId, targetId);
        Favorite existing = favoriteMapper.selectOne(q);
        if (existing != null) {
            return;
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setResourceType(targetType);
        favorite.setResourceId(targetId);
        favoriteMapper.insert(favorite);

        incrementFavoriteCount(targetType, targetId, 1);
        log.info("添加收藏: userId={}, targetType={}, targetId={}", userId, targetType, targetId);
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, String targetType, Long targetId) {
        LambdaQueryWrapper<Favorite> q = new LambdaQueryWrapper<>();
        q.eq(Favorite::getUserId, userId)
                .eq(Favorite::getResourceType, targetType)
                .eq(Favorite::getResourceId, targetId);
        Favorite existing = favoriteMapper.selectOne(q);
        if (existing == null) {
            return;
        }

        favoriteMapper.delete(q);
        incrementFavoriteCount(targetType, targetId, -1);
        log.info("取消收藏: userId={}, targetType={}, targetId={}", userId, targetType, targetId);
    }

    @Override
    public PageResult listFavorites(Long userId, String targetType, int page, int size) {
        LambdaQueryWrapper<Favorite> q = new LambdaQueryWrapper<>();
        q.eq(Favorite::getUserId, userId);
        if (targetType != null && !targetType.isEmpty()) {
            q.eq(Favorite::getResourceType, targetType);
        }
        q.orderByDesc(Favorite::getCreatedAt);

        Page<Favorite> p = new Page<>(page, size);
        Page<Favorite> result = favoriteMapper.selectPage(p, q);

        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    public boolean checkFavorite(Long userId, String targetType, Long targetId) {
        LambdaQueryWrapper<Favorite> q = new LambdaQueryWrapper<>();
        q.eq(Favorite::getUserId, userId)
                .eq(Favorite::getResourceType, targetType)
                .eq(Favorite::getResourceId, targetId);
        return favoriteMapper.exists(q);
    }

    private void incrementFavoriteCount(String targetType, Long targetId, int delta) {
        if ("ARTICLE".equals(targetType)) {
            articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                    .eq(Article::getId, targetId)
                    .setSql("favorite_count = GREATEST(favorite_count + " + delta + ", 0)"));
        } else if ("QUESTION".equals(targetType)) {
            questionMapper.update(null, new LambdaUpdateWrapper<Question>()
                    .eq(Question::getId, targetId)
                    .setSql("favorite_count = GREATEST(favorite_count + " + delta + ", 0)"));
        }
    }
}
