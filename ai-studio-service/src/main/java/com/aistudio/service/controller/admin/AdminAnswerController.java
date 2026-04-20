package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Answer;
import com.aistudio.service.service.AnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Admin - 回答管理（下架等管理功能）
 */
@Slf4j
@Tag(name = "Admin - 回答管理")
@RestController
@RequestMapping("/admin/answer")
@RequiredArgsConstructor
public class AdminAnswerController {

    private final AnswerService answerService;

    @Operation(summary = "回答列表（管理端）")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<PageResult> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long questionId,
            @RequestParam(required = false) Integer isBest, // 0-普通回答, 1-最佳回答, null-全部
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(answerService.listAllAnswers(keyword, questionId, isBest, page, size));
    }

    @Operation(summary = "回答详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Answer> detail(@PathVariable Long id) {
        return Result.success(answerService.getAnswerForAdmin(id));
    }

    @Operation(summary = "下架回答")
    @PostMapping("/{id}/takedown")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Void> takedown(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String reason = request.get("reason");
        answerService.takedownAnswer(id, reason);
        log.info("管理员下架回答: id={}, reason={}", id, reason);
        return Result.success();
    }

    @Operation(summary = "恢复回答（取消下架）")
    @PostMapping("/{id}/restore")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Void> restore(@PathVariable Long id) {
        answerService.restoreAnswer(id);
        log.info("管理员恢复回答: id={}", id);
        return Result.success();
    }
}
