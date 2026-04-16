package com.aistudio.service.service;

import com.aistudio.service.entity.CommunityComment;

import java.util.List;

public interface CommentService {

    List<CommunityComment> listComments(String targetType, Long targetId);

    Long createComment(Long userId, String targetType, Long targetId, Long parentId, String content);

    void deleteComment(Long id, Long userId);

    void likeComment(Long id, Long userId);

    boolean isLiked(Long id, Long userId);
}
