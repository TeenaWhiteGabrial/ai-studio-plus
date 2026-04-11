package com.aistudio.service.service;

import com.aistudio.service.dto.request.TutorialRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Tutorial;

public interface TutorialService {
    PageResult<Tutorial> listTutorials(int page, int size, String keyword, String category, String tag, Integer status);
    Long createTutorial(TutorialRequest request, Long userId);
    void updateTutorial(Long id, TutorialRequest request, Long userId);
    void deleteTutorial(Long id, Long userId);
    Tutorial getTutorialById(Long id);
}
