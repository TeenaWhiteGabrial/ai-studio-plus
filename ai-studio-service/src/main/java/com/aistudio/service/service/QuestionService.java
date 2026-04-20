package com.aistudio.service.service;

import com.aistudio.service.dto.request.QuestionCreateRequest;
import com.aistudio.service.dto.request.QuestionUpdateRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Question;

public interface QuestionService {

    // Portal端

    PageResult listQuestions(String keyword, Long tagId, String sort, int page, int size);

    Question getQuestion(Long id);

    Long createQuestion(QuestionCreateRequest request, Long authorId);

    void updateQuestion(Long id, QuestionUpdateRequest request, Long userId);

    void deleteQuestion(Long id, Long userId);

    void likeQuestion(Long id, Long userId);

    boolean isLiked(Long id, Long userId);

    // Console端

    PageResult listMyQuestions(Long userId, String keyword, Integer status, int page, int size);

    Question getQuestionForEdit(Long id, Long userId);

    // Admin端

    PageResult listAllQuestions(String keyword, Integer takenDown, int page, int size);

    Question getQuestionForAdmin(Long id);

    void takedownQuestion(Long id, String reason);

    void restoreQuestion(Long id);

    void takedown(Long id);
}
