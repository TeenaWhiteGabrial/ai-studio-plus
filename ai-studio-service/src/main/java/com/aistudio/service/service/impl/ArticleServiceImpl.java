package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.request.ArticleCreateRequest;
import com.aistudio.service.dto.request.ArticleListRequest;
import com.aistudio.service.dto.request.ArticleUpdateRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Article;
import com.aistudio.service.entity.ArticleLike;
import com.aistudio.service.entity.SysUser;
import com.aistudio.service.mapper.*;
import com.aistudio.service.service.ArticleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final ArticleLikeMapper articleLikeMapper;
    private final ArticleTagMapper articleTagMapper;
    private final SysUserMapper userMapper;

    @Override
    public PageResult listArticles(String keyword, Long tagId, String sort, int page, int size) {
        LambdaQueryWrapper<Article> q = new LambdaQueryWrapper<>();
        q.eq(Article::getStatus, 1).eq(Article::getIsDeleted, 0);

        if (StringUtils.hasText(keyword)) {
            q.and(w -> w.like(Article::getTitle, keyword).or().like(Article::getSummary, keyword));
        }

        if ("hot".equals(sort)) {
            q.orderByDesc(Article::getViewsCount, Article::getLikesCount);
        } else {
            q.orderByDesc(Article::getCreatedAt);
        }

        Page<Article> p = new Page<>(page, size);
        Page<Article> result = articleMapper.selectPage(p, q);

        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional
    public Article getArticle(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null || article.getIsDeleted() == 1) {
            throw new BusinessException(404, "文章不存在");
        }
        // 阅读数 +1
        articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                .eq(Article::getId, id)
                .setSql("views_count = views_count + 1"));
        article.setViewsCount(article.getViewsCount() + 1);
        return article;
    }

    @Override
    @Transactional
    public Long createArticle(ArticleCreateRequest request, Long authorId) {
        SysUser user = userMapper.selectById(authorId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setSummary(request.getSummary());
        article.setCoverImage(request.getCoverImage());
        article.setAuthorId(authorId);
        article.setAuthorName(user.getRealName() != null ? user.getRealName() : user.getUsername());
        article.setViewsCount(0);
        article.setLikesCount(0);
        article.setCommentsCount(0);
        article.setFavoriteCount(0);
        article.setFollowersCount(0);
        article.setIsDeleted(0);

        // 处理发布类型
        Integer publishType = request.getPublishType() == null ? 0 : request.getPublishType();
        if (publishType == 1) {
            // 立即发布
            article.setStatus(1);
            article.setPublishedAt(LocalDateTime.now());
        } else if (publishType == 2) {
            // 定时发布
            if (request.getScheduledPublishTime() == null) {
                throw new BusinessException(400, "定时发布时间不能为空");
            }
            if (request.getScheduledPublishTime().isBefore(LocalDateTime.now())) {
                throw new BusinessException(400, "定时发布时间不能早于当前时间");
            }
            article.setStatus(0); // 草稿状态
            article.setPublishedAt(request.getScheduledPublishTime());
        } else {
            // 默认草稿
            article.setStatus(0);
        }

        articleMapper.insert(article);

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            articleTagMapper.insertBatch(article.getId(), request.getTagIds());
        }

        log.info("创建文章成功: {}, id={}, authorId={}, status={}", article.getTitle(), article.getId(), authorId, article.getStatus());
        return article.getId();
    }

    @Override
    @Transactional
    public void updateArticle(Long id, ArticleUpdateRequest request, Long userId) {
        Article article = articleMapper.selectById(id);
        if (article == null || article.getIsDeleted() == 1) {
            throw new BusinessException(404, "文章不存在");
        }
        if (!article.getAuthorId().equals(userId)) {
            throw new BusinessException(403, "无权限修改他人的文章");
        }

        if (StringUtils.hasText(request.getTitle())) {
            article.setTitle(request.getTitle());
        }
        if (StringUtils.hasText(request.getContent())) {
            article.setContent(request.getContent());
        }
        if (request.getSummary() != null) {
            article.setSummary(request.getSummary());
        }
        if (request.getCoverImage() != null) {
            article.setCoverImage(request.getCoverImage());
        }

        // 处理发布类型更新
        Integer publishType = request.getPublishType();
        if (publishType != null) {
            if (publishType == 1) {
                // 立即发布
                article.setStatus(1);
                article.setPublishedAt(LocalDateTime.now());
            } else if (publishType == 2) {
                // 定时发布
                if (request.getScheduledPublishTime() == null) {
                    throw new BusinessException(400, "定时发布时间不能为空");
                }
                if (request.getScheduledPublishTime().isBefore(LocalDateTime.now())) {
                    throw new BusinessException(400, "定时发布时间不能早于当前时间");
                }
                article.setStatus(0); // 草稿状态
                article.setPublishedAt(request.getScheduledPublishTime());
            } else if (publishType == 0) {
                // 保存为草稿
                article.setStatus(0);
                article.setPublishedAt(null);
            }
        }

        articleMapper.updateById(article);
        log.info("更新文章: id={}", id);
    }

    @Override
    @Transactional
    public void deleteArticle(Long id, Long userId) {
        Article article = articleMapper.selectById(id);
        if (article == null || article.getIsDeleted() == 1) {
            throw new BusinessException(404, "文章不存在");
        }
        if (!article.getAuthorId().equals(userId)) {
            throw new BusinessException(403, "无权限删除他人的文章");
        }

        articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                .eq(Article::getId, id)
                .set(Article::getIsDeleted, 1));
        log.info("删除文章: id={}", id);
    }

    @Override
    @Transactional
    public void likeArticle(Long id, Long userId) {
        // 检查是否已点赞
        LambdaQueryWrapper<ArticleLike> q = new LambdaQueryWrapper<>();
        q.eq(ArticleLike::getUserId, userId).eq(ArticleLike::getArticleId, id);
        ArticleLike existing = articleLikeMapper.selectOne(q);

        if (existing != null) {
            // 取消点赞
            articleLikeMapper.delete(q);
            articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                    .eq(Article::getId, id)
                    .setSql("likes_count = GREATEST(likes_count - 1, 0)"));
        } else {
            // 点赞
            ArticleLike like = new ArticleLike();
            like.setUserId(userId);
            like.setArticleId(id);
            articleLikeMapper.insert(like);
            articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                    .eq(Article::getId, id)
                    .setSql("likes_count = likes_count + 1"));
        }
    }

    @Override
    public boolean isLiked(Long id, Long userId) {
        LambdaQueryWrapper<ArticleLike> q = new LambdaQueryWrapper<>();
        q.eq(ArticleLike::getUserId, userId).eq(ArticleLike::getArticleId, id);
        return articleLikeMapper.exists(q);
    }

    @Override
    @Transactional
    public void takedown(Long id) {
        articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                .eq(Article::getId, id)
                .set(Article::getStatus, 2));
        log.info("文章下架: id={}", id);
    }

    @Override
    public PageResult listMyArticles(ArticleListRequest request, Long userId) {
        LambdaQueryWrapper<Article> q = new LambdaQueryWrapper<>();
        q.eq(Article::getAuthorId, userId)
                .eq(Article::getIsDeleted, 0);

        if (StringUtils.hasText(request.getKeyword())) {
            q.and(w -> w.like(Article::getTitle, request.getKeyword())
                    .or().like(Article::getSummary, request.getKeyword()));
        }

        if (request.getTagId() != null) {
            // 这里需要关联查询 article_tag 表
            // 暂时简化处理，后续可以优化
        }

        if (request.getStatusList() != null && !request.getStatusList().isEmpty()) {
            q.in(Article::getStatus, request.getStatusList());
        }

        if ("hot".equals(request.getSort())) {
            q.orderByDesc(Article::getViewsCount, Article::getLikesCount);
        } else {
            q.orderByDesc(Article::getCreatedAt);
        }

        Page<Article> p = new Page<>(request.getPage(), request.getSize());
        Page<Article> result = articleMapper.selectPage(p, q);

        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    public Article getArticleForEdit(Long id, Long userId) {
        Article article = articleMapper.selectById(id);
        if (article == null || article.getIsDeleted() == 1) {
            throw new BusinessException(404, "文章不存在");
        }
        if (!article.getAuthorId().equals(userId)) {
            throw new BusinessException(403, "无权限查看他人的文章");
        }
        // 编辑时不增加阅读数
        return article;
    }

    @Override
    @Transactional
    public void publishArticle(Long id, Long userId) {
        Article article = articleMapper.selectById(id);
        if (article == null || article.getIsDeleted() == 1) {
            throw new BusinessException(404, "文章不存在");
        }
        if (!article.getAuthorId().equals(userId)) {
            throw new BusinessException(403, "无权限发布他人的文章");
        }
        if (article.getStatus() == 2) {
            throw new BusinessException(400, "已下架的文章不能发布");
        }

        articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                .eq(Article::getId, id)
                .set(Article::getStatus, 1)
                .set(Article::getPublishedAt, LocalDateTime.now()));
        log.info("文章发布: id={}", id);
    }

    @Override
    @Transactional
    public void schedulePublish(Long id, Long userId, LocalDateTime publishTime) {
        Article article = articleMapper.selectById(id);
        if (article == null || article.getIsDeleted() == 1) {
            throw new BusinessException(404, "文章不存在");
        }
        if (!article.getAuthorId().equals(userId)) {
            throw new BusinessException(403, "无权限发布他人的文章");
        }
        if (article.getStatus() == 2) {
            throw new BusinessException(400, "已下架的文章不能定时发布");
        }
        if (publishTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException(400, "定时发布时间不能早于当前时间");
        }

        articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                .eq(Article::getId, id)
                .set(Article::getStatus, 0) // 草稿状态
                .set(Article::getPublishedAt, publishTime));
        log.info("文章定时发布: id={}, publishTime={}", id, publishTime);
    }

    @Override
    @Transactional
    public void cancelSchedule(Long id, Long userId) {
        Article article = articleMapper.selectById(id);
        if (article == null || article.getIsDeleted() == 1) {
            throw new BusinessException(404, "文章不存在");
        }
        if (!article.getAuthorId().equals(userId)) {
            throw new BusinessException(403, "无权限操作他人的文章");
        }

        articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                .eq(Article::getId, id)
                .set(Article::getStatus, 0) // 草稿状态
                .set(Article::getPublishedAt, (LocalDateTime) null));
        log.info("取消文章区定时发布: id={}", id);
    }
}
