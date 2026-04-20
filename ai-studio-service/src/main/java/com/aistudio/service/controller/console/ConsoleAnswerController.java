package com.aistudio.service.controller.console;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.request.AnswerCreateRequest;
import com.aistudio.service.dto.request.AnswerUpdateRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Answer;
import com.aistudio.service.service.AnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Console - 我的回答管理
 */
@Slf4j
@Tag(name = "Console - 我的回答管理")
@RestController
@RequestMapping("/console/answer")
@RequiredArgsConstructor
public class ConsoleAnswerController {

    private final AnswerService answerService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "我的回答列表")
    @GetMapping("/list")
    public Result<PageResult> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status, // 0-全部, 1-普通回答, 2-最佳回答
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = securityUtils.getCurrentUserId();
        return Result.success(answerService.listMyAnswers(userId, keyword, status, page, size));
    }

    @Operation(summary = "回答详情（编辑使用）")
    @GetMapping("/{id}")
    public Result<Answer> detail(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        return Result.success(answerService.getAnswerForEdit(id, userId));
    }

    @Operation(summary = "创建回答")
    @PostMapping
    public Result<Long> create(@RequestParam Long questionId, @Valid @RequestBody AnswerCreateRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        return Result.success(answerService.createAnswer(questionId, request, userId));
    }

    @Operation(summary = "更新回答")
    @PostMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody AnswerUpdateRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        answerService.updateAnswer(id, request.getContent(), userId);
        return Result.success();
    }

    @Operation(summary = "删除回答")
    @PostMapping("/{id}/delete")
    public Result<Void> delete(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        answerService.deleteAnswer(id, userId);
        return Result.success();
    }
}
