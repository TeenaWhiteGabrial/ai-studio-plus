package com.aistudio.service.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class QuestionUpdateRequest {

    private String title;
    private String content;
    private List<String> tags;
}
