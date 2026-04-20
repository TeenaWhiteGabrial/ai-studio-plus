package com.aistudio.service.controller.console;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.request.CommentCreateRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.CommunityComment;
import com.aistudio.service.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Console - 我的评论管理
 */
@Slf4j
@Tag(name = "Console - 我的评论管理")
@RestController
@RequestMapping("/console/comment")
@RequiredArgsConstructor
public class ConsoleCommentController {

    private final CommentService commentService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "我的评论列表")
    @GetMapping("/list")
    public Result<PageResult> list(
            @RequestParam(required = false) String targetType, // article/question/answer
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = securityUtils.getCurrentUserId();
        return Result.success(commentService.listMyComments(userId, targetType, page, size));
    }

    @Operation(summary = "创建评论")
    @PostMapping
    public Result<Long> create(@RequestBody CommentCreateRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        return Result.success(commentService.createComment(
                userId, request.getTargetType(), request.getTargetId(), request.getParentId(), request.getContent()));
    }

    @Operation(summary = "删除评论")
    @PostMapping("/{id}/delete")
    public Result<Void> delete(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        commentService.deleteComment(id, userId);
        return Result.success();
    }
}
