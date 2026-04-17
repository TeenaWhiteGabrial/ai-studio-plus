package com.aistudio.service.controller.portal;

import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.request.QuestionCreateRequest;
import com.aistudio.service.dto.request.QuestionUpdateRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Question;
import com.aistudio.service.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portal/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final SecurityUtils securityUtils;

    @GetMapping("/list")
    public ResponseEntity<PageResult> listQuestions(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long tagId,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(questionService.listQuestions(keyword, tagId, sort, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestion(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestion(id));
    }

    @PostMapping
    public ResponseEntity<Long> createQuestion(@Valid @RequestBody QuestionCreateRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(questionService.createQuestion(request, userId));
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> updateQuestion(@PathVariable Long id, @RequestBody QuestionUpdateRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        questionService.updateQuestion(id, request, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/delete")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        questionService.deleteQuestion(id, userId);
        return ResponseEntity.ok().build();
    }
}
