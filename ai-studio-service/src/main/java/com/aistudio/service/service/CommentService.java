package com.aistudio.service.service;

import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.CommunityComment;

import java.util.List;

public interface CommentService {

    // Portal端

    List<CommunityComment> listComments(String targetType, Long targetId);

    Long createComment(Long userId, String targetType, Long targetId, Long parentId, String content);

    void deleteComment(Long id, Long userId);

    void likeComment(Long id, Long userId);

    boolean isLiked(Long id, Long userId);

    // Console端

    PageResult listMyComments(Long userId, String targetType, int page, int size);

    // Admin端

    PageResult listAllComments(String targetType, Long targetId, String keyword, int page, int size);

    CommunityComment getCommentForAdmin(Long id);

    void adminDeleteComment(Long id, String reason);
}
