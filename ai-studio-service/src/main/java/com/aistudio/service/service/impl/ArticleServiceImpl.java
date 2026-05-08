package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.request.ArticleCreateRequest;
import com.aistudio.service.dto.request.ArticleFolderRequest;
import com.aistudio.service.dto.request.ArticleListRequest;
import com.aistudio.service.dto.request.ArticleUpdateRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Article;
import com.aistudio.service.entity.ArticleFolder;
import com.aistudio.service.entity.ArticleLike;
import com.aistudio.service.entity.SysUser;
import com.aistudio.service.mapper.ArticleFolderMapper;
import com.aistudio.service.mapper.ArticleLikeMapper;
import com.aistudio.service.mapper.ArticleMapper;
import com.aistudio.service.mapper.ArticleTagMapper;
import com.aistudio.service.mapper.SysUserMapper;
import com.aistudio.service.service.ArticleService;
import com.aistudio.service.service.NotificationService;
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
    private final ArticleFolderMapper articleFolderMapper;
    private final ArticleLikeMapper articleLikeMapper;
    private final ArticleTagMapper articleTagMapper;
    private final SysUserMapper userMapper;
    private final NotificationService notificationService;

    @Override
    public PageResult listArticles(String keyword, Long tagId, String sort, int page, int size) {
        LambdaQueryWrapper<Article> q = new LambdaQueryWrapper<>();
        q.eq(Article::getStatus, 1).eq(Article::getIsDeleted, 0);

        if (StringUtils.hasText(keyword)) {
            q.and(w -> w.like(Article::getTitle, keyword).or().like(Article::getSummary, keyword));
        }
        applyArticleSort(q, sort, true);

        Page<Article> result = articleMapper.selectPage(new Page<>(page, size), q);
        result.getRecords().forEach(this::fillTagIds);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional
    public Article getArticle(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null || article.getIsDeleted() == 1 || article.getStatus() != 1) {
            throw new BusinessException(404, "文章不存在");
        }
        articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                .eq(Article::getId, id)
                .setSql("views_count = views_count + 1"));
        article.setViewsCount(article.getViewsCount() == null ? 1 : article.getViewsCount() + 1);
        fillTagIds(article);
        return article;
    }

    @Override
    public PageResult listMyArticles(ArticleListRequest request, Long userId) {
        LambdaQueryWrapper<Article> q = new LambdaQueryWrapper<>();
        q.eq(Article::getAuthorId, userId).eq(Article::getIsDeleted, 0);

        if (StringUtils.hasText(request.getKeyword())) {
            q.and(w -> w.like(Article::getTitle, request.getKeyword()).or().like(Article::getSummary, request.getKeyword()));
        }
        if (request.getFolderId() != null) {
            q.eq(Article::getFolderId, request.getFolderId());
        }
        if (request.getStatus() != null) {
            q.eq(Article::getStatus, request.getStatus());
        } else if (request.getStatusList() != null && !request.getStatusList().isEmpty()) {
            q.in(Article::getStatus, request.getStatusList());
        }
        applyArticleSort(q, request.getSort(), false);

        Page<Article> result = articleMapper.selectPage(new Page<>(request.getPage(), request.getSize()), q);
        result.getRecords().forEach(this::fillTagIds);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    public List<ArticleFolder> listMyFolders(Long userId) {
        return articleFolderMapper.selectList(new LambdaQueryWrapper<ArticleFolder>()
                .eq(ArticleFolder::getUserId, userId)
                .eq(ArticleFolder::getIsDeleted, 0)
                .orderByAsc(ArticleFolder::getSortOrder)
                .orderByAsc(ArticleFolder::getCreatedAt));
    }

    @Override
    @Transactional
    public Long createFolder(ArticleFolderRequest request, Long userId) {
        ArticleFolder folder = new ArticleFolder();
        folder.setUserId(userId);
        folder.setParentId(resolveParentFolderId(request.getParentId(), userId));
        folder.setFolderName(request.getFolderName().trim());
        folder.setSortOrder(0);
        folder.setIsDeleted(0);
        folder.setCreatedAt(LocalDateTime.now());
        folder.setUpdatedAt(LocalDateTime.now());
        articleFolderMapper.insert(folder);
        return folder.getId();
    }

    @Override
    @Transactional
    public void updateFolder(Long id, ArticleFolderRequest request, Long userId) {
        ArticleFolder folder = requireOwnedFolder(id, userId);
        Long nextParentId = resolveParentFolderId(request.getParentId(), userId);
        if (id.equals(nextParentId) || isDescendantFolder(nextParentId, id, userId)) {
            throw new BusinessException(400, "不能将文件夹移动到自身或子文件夹下");
        }
        folder.setFolderName(request.getFolderName().trim());
        folder.setParentId(nextParentId);
        folder.setUpdatedAt(LocalDateTime.now());
        articleFolderMapper.updateById(folder);
    }

    @Override
    @Transactional
    public void deleteFolder(Long id, Long userId) {
        requireOwnedFolder(id, userId);
        articleFolderMapper.update(null, new LambdaUpdateWrapper<ArticleFolder>()
                .eq(ArticleFolder::getId, id)
                .eq(ArticleFolder::getUserId, userId)
                .set(ArticleFolder::getIsDeleted, 1));
        articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                .eq(Article::getAuthorId, userId)
                .eq(Article::getFolderId, id)
                .set(Article::getFolderId, null));
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
        fillTagIds(article);
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
        article.setAuthorName(StringUtils.hasText(user.getRealName()) ? user.getRealName() : user.getUsername());
        article.setFolderId(resolveFolderId(request.getFolderId(), authorId));
        article.setViewsCount(0);
        article.setLikesCount(0);
        article.setCommentsCount(0);
        article.setFavoriteCount(0);
        article.setFollowersCount(0);
        article.setIsDeleted(0);
        applyPublishType(article, request.getPublishType(), request.getScheduledPublishTime());

        articleMapper.insert(article);
        replaceTags(article.getId(), request.getTagIds());
        log.info("创建文章成功: {}, id={}, authorId={}, status={}", article.getTitle(), article.getId(), authorId, article.getStatus());
        return article.getId();
    }

    @Override
    @Transactional
    public void updateArticle(Long id, ArticleUpdateRequest request, Long userId) {
        Article article = getOwnedArticle(id, userId);

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
        article.setFolderId(resolveFolderId(request.getFolderId(), userId));
        if (request.getPublishType() != null) {
            applyPublishType(article, request.getPublishType(), request.getScheduledPublishTime());
        }

        articleMapper.updateById(article);
        if (request.getTagIds() != null) {
            replaceTags(id, request.getTagIds());
        }
        log.info("更新文章: id={}", id);
    }

    @Override
    @Transactional
    public void deleteArticle(Long id, Long userId) {
        getOwnedArticle(id, userId);
        articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                .eq(Article::getId, id)
                .set(Article::getIsDeleted, 1));
        log.info("删除文章: id={}", id);
    }

    @Override
    @Transactional
    public void publishArticle(Long id, Long userId) {
        Article article = getOwnedArticle(id, userId);
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
        Article article = getOwnedArticle(id, userId);
        if (article.getStatus() == 2) {
            throw new BusinessException(400, "已下架的文章不能定时发布");
        }
        if (publishTime.isBefore(LocalDateTime.now())) {
            throw new BusinessException(400, "定时发布时间不能早于当前时间");
        }
        articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                .eq(Article::getId, id)
                .set(Article::getStatus, 0)
                .set(Article::getPublishedAt, publishTime));
        log.info("文章定时发布: id={}, publishTime={}", id, publishTime);
    }

    @Override
    @Transactional
    public void cancelSchedule(Long id, Long userId) {
        getOwnedArticle(id, userId);
        articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                .eq(Article::getId, id)
                .set(Article::getStatus, 0)
                .set(Article::getPublishedAt, (LocalDateTime) null));
        log.info("取消文章定时发布: id={}", id);
    }

    @Override
    @Transactional
    public void likeArticle(Long id, Long userId) {
        LambdaQueryWrapper<ArticleLike> q = new LambdaQueryWrapper<>();
        q.eq(ArticleLike::getUserId, userId).eq(ArticleLike::getArticleId, id);
        ArticleLike existing = articleLikeMapper.selectOne(q);

        if (existing != null) {
            articleLikeMapper.delete(q);
            articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                    .eq(Article::getId, id)
                    .setSql("likes_count = GREATEST(likes_count - 1, 0)"));
        } else {
            ArticleLike like = new ArticleLike();
            like.setUserId(userId);
            like.setArticleId(id);
            articleLikeMapper.insert(like);
            articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                    .eq(Article::getId, id)
                    .setSql("likes_count = likes_count + 1"));
            Article article = articleMapper.selectById(id);
            if (article != null && article.getAuthorId() != null && !article.getAuthorId().equals(userId)) {
                notificationService.createRuleNotification(
                        article.getAuthorId(),
                        "ARTICLE_LIKED",
                        "你的文章收到了点赞",
                        "你的文章《" + article.getTitle() + "》收到了一次点赞。",
                        id,
                        "article",
                        "ARTICLE_LIKED:" + id + ":" + userId
                );
            }
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
        Article article = articleMapper.selectById(id);
        articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                .eq(Article::getId, id)
                .set(Article::getStatus, 2));
        if (article != null && article.getAuthorId() != null) {
            notificationService.createRuleNotification(
                    article.getAuthorId(),
                    "ARTICLE_TAKEN_DOWN",
                    "你的文章已被下架",
                    "你的文章《" + article.getTitle() + "》已被管理员下架。",
                    id,
                    "article",
                    "ARTICLE_TAKEN_DOWN:" + id
            );
        }
        log.info("文章下架: id={}", id);
    }

    private Article getOwnedArticle(Long id, Long userId) {
        Article article = articleMapper.selectById(id);
        if (article == null || article.getIsDeleted() == 1) {
            throw new BusinessException(404, "文章不存在");
        }
        if (!article.getAuthorId().equals(userId)) {
            throw new BusinessException(403, "无权限操作他人的文章");
        }
        return article;
    }

    private void applyPublishType(Article article, Integer publishType, LocalDateTime scheduledPublishTime) {
        int type = publishType == null ? 0 : publishType;
        if (type == 1) {
            article.setStatus(1);
            article.setPublishedAt(LocalDateTime.now());
            return;
        }
        if (type == 2) {
            if (scheduledPublishTime == null) {
                throw new BusinessException(400, "定时发布时间不能为空");
            }
            if (scheduledPublishTime.isBefore(LocalDateTime.now())) {
                throw new BusinessException(400, "定时发布时间不能早于当前时间");
            }
            article.setStatus(0);
            article.setPublishedAt(scheduledPublishTime);
            return;
        }
        article.setStatus(0);
        article.setPublishedAt(null);
    }

    private void applyArticleSort(LambdaQueryWrapper<Article> q, String sort, boolean portal) {
        if ("likes".equals(sort)) {
            q.orderByDesc(Article::getLikesCount, Article::getViewsCount);
            return;
        }
        if ("favorites".equals(sort)) {
            q.orderByDesc(Article::getFavoriteCount, Article::getLikesCount);
            return;
        }
        if ("hot".equals(sort)) {
            q.orderByDesc(Article::getFavoriteCount, Article::getLikesCount, Article::getCommentsCount, Article::getViewsCount);
            return;
        }
        if (portal) {
            q.orderByDesc(Article::getPublishedAt, Article::getCreatedAt);
        } else {
            q.orderByDesc(Article::getCreatedAt);
        }
    }

    private Long resolveFolderId(Long folderId, Long userId) {
        if (folderId == null || folderId <= 0) {
            return null;
        }
        return requireOwnedFolder(folderId, userId).getId();
    }

    private Long resolveParentFolderId(Long parentId, Long userId) {
        if (parentId == null || parentId <= 0) {
            return null;
        }
        return requireOwnedFolder(parentId, userId).getId();
    }

    private ArticleFolder requireOwnedFolder(Long folderId, Long userId) {
        ArticleFolder folder = articleFolderMapper.selectById(folderId);
        if (folder == null || folder.getIsDeleted() == 1 || !userId.equals(folder.getUserId())) {
            throw new BusinessException(404, "文件夹不存在或无权限访问");
        }
        return folder;
    }

    private boolean isDescendantFolder(Long candidateParentId, Long folderId, Long userId) {
        Long currentId = candidateParentId;
        while (currentId != null) {
            ArticleFolder current = articleFolderMapper.selectById(currentId);
            if (current == null || current.getIsDeleted() == 1 || !userId.equals(current.getUserId())) {
                return false;
            }
            if (folderId.equals(current.getParentId())) {
                return true;
            }
            currentId = current.getParentId();
        }
        return false;
    }

    private void replaceTags(Long articleId, List<Long> tagIds) {
        articleTagMapper.deleteByArticleId(articleId);
        if (tagIds != null && !tagIds.isEmpty()) {
            articleTagMapper.insertBatch(articleId, tagIds);
        }
    }

    private void fillTagIds(Article article) {
        if (article != null && article.getId() != null) {
            article.setTagIds(articleTagMapper.selectTagIdsByArticleId(article.getId()));
        }
    }
}
