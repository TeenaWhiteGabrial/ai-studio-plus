package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.CommunityComment;
import com.aistudio.service.entity.Article;
import com.aistudio.service.entity.SysUser;
import com.aistudio.service.mapper.ArticleMapper;
import com.aistudio.service.mapper.CommunityCommentMapper;
import com.aistudio.service.mapper.SysUserMapper;
import com.aistudio.service.service.CommentService;
import com.aistudio.service.service.NotificationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommunityCommentMapper commentMapper;
    private final SysUserMapper userMapper;
    private final ArticleMapper articleMapper;
    private final NotificationService notificationService;

    @Override
    public List<CommunityComment> listComments(String targetType, Long targetId) {
        LambdaQueryWrapper<CommunityComment> q = new LambdaQueryWrapper<>();
        q.eq(CommunityComment::getCommentType, targetType)
                .eq(CommunityComment::getTargetId, targetId)
                .eq(CommunityComment::getIsDeleted, 0)
                .orderByAsc(CommunityComment::getCreatedAt);
        return commentMapper.selectList(q);
    }

    @Override
    @Transactional
    public Long createComment(Long userId, String targetType, Long targetId, Long parentId, String content) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        CommunityComment comment = new CommunityComment();
        comment.setCommentType(targetType);
        comment.setTargetId(targetId);
        comment.setParentId(parentId);
        comment.setRootId(parentId != null ? parentId : 0L);
        comment.setAuthorId(userId);
        comment.setAuthorName(user.getRealName() != null ? user.getRealName() : user.getUsername());
        comment.setContent(content);
        comment.setLikesCount(0);
        comment.setIsDeleted(0);
        commentMapper.insert(comment);

        if ("article".equals(targetType)) {
            Article article = articleMapper.selectById(targetId);
            if (article != null && article.getAuthorId() != null && !article.getAuthorId().equals(userId)) {
                notificationService.createRuleNotification(
                        article.getAuthorId(),
                        "ARTICLE_COMMENTED",
                        "你的文章收到了评论",
                        "你的文章《" + article.getTitle() + "》收到了一条评论。",
                        comment.getId(),
                        "comment",
                        "ARTICLE_COMMENTED:" + comment.getId()
                );
            }
        }

        log.info("创建评论: id={}, targetType={}, targetId={}, userId={}", comment.getId(), targetType, targetId, userId);
        return comment.getId();
    }

    @Override
    @Transactional
    public void deleteComment(Long id, Long userId) {
        CommunityComment comment = commentMapper.selectById(id);
        if (comment == null || comment.getIsDeleted() == 1) {
            throw new BusinessException(404, "评论不存在");
        }
        if (!comment.getAuthorId().equals(userId)) {
            throw new BusinessException(403, "无权限删除他人的评论");
        }

        commentMapper.update(null, new LambdaUpdateWrapper<CommunityComment>()
                .eq(CommunityComment::getId, id)
                .set(CommunityComment::getIsDeleted, 1));
        log.info("删除评论: id={}", id);
    }

    @Override
    @Transactional
    public void likeComment(Long id, Long userId) {
        log.info("评论点赞: id={}, userId={}", id, userId);
    }

    @Override
    public boolean isLiked(Long id, Long userId) {
        return false;
    }

    // ========== Console端 ==========
    @Override
    public PageResult listMyComments(Long userId, String targetType, int page, int size) {
        LambdaQueryWrapper<CommunityComment> q = new LambdaQueryWrapper<>();
        q.eq(CommunityComment::getAuthorId, userId)
                .eq(CommunityComment::getIsDeleted, 0);

        if (StringUtils.hasText(targetType)) {
            q.eq(CommunityComment::getCommentType, targetType);
        }

        q.orderByDesc(CommunityComment::getCreatedAt);

        Page<CommunityComment> p = new Page<>(page, size);
        Page<CommunityComment> result = commentMapper.selectPage(p, q);

        return PageResult.of(result.getTotal(), result.getRecords());
    }

    // ========== Admin端 ==========
    @Override
    public PageResult listAllComments(String targetType, Long targetId, String keyword, int page, int size) {
        LambdaQueryWrapper<CommunityComment> q = new LambdaQueryWrapper<>();
        q.eq(CommunityComment::getIsDeleted, 0);

        if (StringUtils.hasText(targetType)) {
            q.eq(CommunityComment::getCommentType, targetType);
        }

        if (targetId != null) {
            q.eq(CommunityComment::getTargetId, targetId);
        }

        if (StringUtils.hasText(keyword)) {
            q.like(CommunityComment::getContent, keyword);
        }

        q.orderByDesc(CommunityComment::getCreatedAt);

        Page<CommunityComment> p = new Page<>(page, size);
        Page<CommunityComment> result = commentMapper.selectPage(p, q);

        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    public CommunityComment getCommentForAdmin(Long id) {
        CommunityComment comment = commentMapper.selectById(id);
        if (comment == null || comment.getIsDeleted() == 1) {
            throw new BusinessException(404, "评论不存在");
        }
        return comment;
    }

    @Override
    @Transactional
    public void adminDeleteComment(Long id, String reason) {
        commentMapper.update(null, new LambdaUpdateWrapper<CommunityComment>()
                .eq(CommunityComment::getId, id)
                .set(CommunityComment::getIsDeleted, 1));
        log.info("管理员删除评论: id={}, reason={}", id, reason);
    }
}
