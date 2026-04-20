package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.CommunityComment;
import com.aistudio.service.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Admin - 评论管理
 */
@Slf4j
@Tag(name = "Admin - 评论管理")
@RestController
@RequestMapping("/admin/comment")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentService;

    @Operation(summary = "评论列表（管理端）")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<PageResult> list(
            @RequestParam(required = false) String targetType, // article/question/answer
            @RequestParam(required = false) Long targetId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(commentService.listAllComments(targetType, targetId, keyword, page, size));
    }

    @Operation(summary = "评论详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<CommunityComment> detail(@PathVariable Long id) {
        return Result.success(commentService.getCommentForAdmin(id));
    }

    @Operation(summary = "删除评论（管理员操作）")
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Void> delete(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String reason = request.get("reason");
        commentService.adminDeleteComment(id, reason);
        log.info("管理员删除评论: id={}, reason={}", id, reason);
        return Result.success();
    }
}
