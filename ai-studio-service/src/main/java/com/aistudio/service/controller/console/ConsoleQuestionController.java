package com.aistudio.service.controller.console;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.request.QuestionCreateRequest;
import com.aistudio.service.dto.request.QuestionUpdateRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Question;
import com.aistudio.service.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Console - 我的问题管理
 */
@Slf4j
@Tag(name = "Console - 我的问题管理")
@RestController
@RequestMapping("/console/question")
@RequiredArgsConstructor
public class ConsoleQuestionController {

    private final QuestionService questionService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "我的问题列表")
    @GetMapping("/list")
    public Result<PageResult> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status, // 0-全部, 1-待解决, 2-已解决
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = securityUtils.getCurrentUserId();
        return Result.success(questionService.listMyQuestions(userId, keyword, status, page, size));
    }

    @Operation(summary = "问题详情（编辑使用）")
    @GetMapping("/{id}")
    public Result<Question> detail(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        return Result.success(questionService.getQuestionForEdit(id, userId));
    }

    @Operation(summary = "创建问题")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody QuestionCreateRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        return Result.success(questionService.createQuestion(request, userId));
    }

    @Operation(summary = "更新问题")
    @PostMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody QuestionUpdateRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        questionService.updateQuestion(id, request, userId);
        return Result.success();
    }

    @Operation(summary = "删除问题")
    @PostMapping("/{id}/delete")
    public Result<Void> delete(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        questionService.deleteQuestion(id, userId);
        return Result.success();
    }
}
