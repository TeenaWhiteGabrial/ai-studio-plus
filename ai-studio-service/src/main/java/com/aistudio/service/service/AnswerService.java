package com.aistudio.service.service;

import com.aistudio.service.dto.request.AnswerCreateRequest;
import com.aistudio.service.entity.Answer;

import java.util.List;

public interface AnswerService {

    List<Answer> listByQuestionId(Long questionId);

    Long createAnswer(Long questionId, AnswerCreateRequest request, Long authorId);

    void updateAnswer(Long id, String content, Long userId);

    void deleteAnswer(Long id, Long userId);

    void acceptAnswer(Long answerId, Long questionId, Long userId);

    void likeAnswer(Long id, Long userId);

    boolean isLiked(Long id, Long userId);
}
