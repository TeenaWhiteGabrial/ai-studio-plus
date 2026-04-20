package com.aistudio.service.service;

import com.aistudio.service.dto.request.AnswerCreateRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Answer;

import java.util.List;

public interface AnswerService {

    // Portal端

    List<Answer> listByQuestionId(Long questionId);

    Long createAnswer(Long questionId, AnswerCreateRequest request, Long authorId);

    void updateAnswer(Long id, String content, Long userId);

    void deleteAnswer(Long id, Long userId);

    void acceptAnswer(Long answerId, Long questionId, Long userId);

    void likeAnswer(Long id, Long userId);

    boolean isLiked(Long id, Long userId);

    // Console端

    PageResult listMyAnswers(Long userId, String keyword, Integer status, int page, int size);

    Answer getAnswerForEdit(Long id, Long userId);

    // Admin端

    PageResult listAllAnswers(String keyword, Long questionId, Integer isBest, int page, int size);

    Answer getAnswerForAdmin(Long id);

    void takedownAnswer(Long id, String reason);

    void restoreAnswer(Long id);
}
