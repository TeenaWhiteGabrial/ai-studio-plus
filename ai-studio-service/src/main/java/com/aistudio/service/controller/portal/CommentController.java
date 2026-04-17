package com.aistudio.service.controller.portal;

import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.request.CommentCreateRequest;
import com.aistudio.service.entity.CommunityComment;
import com.aistudio.service.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portal/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final SecurityUtils securityUtils;

    @GetMapping("/list")
    public ResponseEntity<List<CommunityComment>> listComments(
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        return ResponseEntity.ok(commentService.listComments(targetType, targetId));
    }

    @PostMapping
    public ResponseEntity<Long> createComment(@RequestBody CommentCreateRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(commentService.createComment(
                userId, request.getTargetType(), request.getTargetId(), request.getParentId(), request.getContent()));
    }

    @PostMapping("/{id}/delete")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        commentService.deleteComment(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeComment(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        commentService.likeComment(id, userId);
        return ResponseEntity.ok().build();
    }
}
