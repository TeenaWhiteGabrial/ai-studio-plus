package com.aistudio.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class QuestionCreateRequest {

    @NotBlank(message = "问题标题不能为空")
    @Size(max = 200, message = "标题最多200字")
    private String title;

    @NotBlank(message = "问题内容不能为空")
    private String content;

    private List<String> tags;
}
