package com.aistudio.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnswerCreateRequest {

    @NotBlank(message = "回答内容不能为空")
    private String content;
}
