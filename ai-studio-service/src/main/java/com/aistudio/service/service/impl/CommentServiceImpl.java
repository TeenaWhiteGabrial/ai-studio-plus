package com.aistudio.service.service.impl;

import com.aistudio.service.common.exception.BusinessException;
import com.aistudio.service.entity.CommunityComment;
import com.aistudio.service.entity.SysUser;
import com.aistudio.service.mapper.CommunityCommentMapper;
import com.aistudio.service.mapper.SysUserMapper;
import com.aistudio.service.service.CommentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommunityCommentMapper commentMapper;
    private final SysUserMapper userMapper;

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
}
