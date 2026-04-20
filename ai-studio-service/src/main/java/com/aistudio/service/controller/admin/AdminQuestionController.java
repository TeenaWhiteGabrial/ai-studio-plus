package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Question;
import com.aistudio.service.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Admin - 问题管理（下架等管理功能）
 */
@Slf4j
@Tag(name = "Admin - 问题管理")
@RestController
@RequestMapping("/admin/question")
@RequiredArgsConstructor
public class AdminQuestionController {

    private final QuestionService questionService;

    @Operation(summary = "问题列表（管理端，所有状态）")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<PageResult> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer takenDown, // 0-正常, 1-已下架, null-全部
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(questionService.listAllQuestions(keyword, takenDown, page, size));
    }

    @Operation(summary = "问题详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Question> detail(@PathVariable Long id) {
        return Result.success(questionService.getQuestionForAdmin(id));
    }

    @Operation(summary = "下架问题")
    @PostMapping("/{id}/takedown")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Void> takedown(@PathVariable Long id, @RequestBody @Valid Map<String, String> request) {
        String reason = request.get("reason");
        questionService.takedownQuestion(id, reason);
        log.info("管理员下架问题: id={}, reason={}", id, reason);
        return Result.success();
    }

    @Operation(summary = "恢复问题（取消下架）")
    @PostMapping("/{id}/restore")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Void> restore(@PathVariable Long id) {
        questionService.restoreQuestion(id);
        log.info("管理员恢复问题: id={}", id);
        return Result.success();
    }
}
