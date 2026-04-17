package com.aistudio.service.controller.portal;

import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.request.AnswerCreateRequest;
import com.aistudio.service.dto.request.AnswerUpdateRequest;
import com.aistudio.service.entity.Answer;
import com.aistudio.service.service.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portal")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;
    private final SecurityUtils securityUtils;

    @GetMapping("/question/{questionId}/answers")
    public ResponseEntity<List<Answer>> listAnswers(@PathVariable Long questionId) {
        return ResponseEntity.ok(answerService.listByQuestionId(questionId));
    }

    @PostMapping("/question/{questionId}/answer")
    public ResponseEntity<Long> createAnswer(@PathVariable Long questionId, @Valid @RequestBody AnswerCreateRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(answerService.createAnswer(questionId, request, userId));
    }

    @PostMapping("/answer/{id}")
    public ResponseEntity<Void> updateAnswer(@PathVariable Long id, @RequestBody AnswerUpdateRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        answerService.updateAnswer(id, request.getContent(), userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/answer/{id}/delete")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        answerService.deleteAnswer(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/answer/{id}/accept")
    public ResponseEntity<Void> acceptAnswer(@PathVariable Long id, @RequestParam Long questionId) {
        Long userId = securityUtils.getCurrentUserId();
        answerService.acceptAnswer(id, questionId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/answer/{id}/like")
    public ResponseEntity<Void> likeAnswer(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        answerService.likeAnswer(id, userId);
        return ResponseEntity.ok().build();
    }
}
