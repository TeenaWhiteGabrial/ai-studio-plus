package com.aistudio.service.service;

import com.aistudio.service.dto.request.QuestionCreateRequest;
import com.aistudio.service.dto.request.QuestionUpdateRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Question;

public interface QuestionService {

    PageResult listQuestions(String keyword, Long tagId, String sort, int page, int size);

    Question getQuestion(Long id);

    Long createQuestion(QuestionCreateRequest request, Long authorId);

    void updateQuestion(Long id, QuestionUpdateRequest request, Long userId);

    void deleteQuestion(Long id, Long userId);

    void likeQuestion(Long id, Long userId);

    boolean isLiked(Long id, Long userId);

    void takedown(Long id);
}
