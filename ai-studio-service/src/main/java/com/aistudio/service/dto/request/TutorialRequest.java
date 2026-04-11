package com.aistudio.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TutorialRequest {

    @NotBlank(message = "教程标题不能为空")
    private String title;

    private String category;
    private String tags;

    @NotBlank(message = "内容OSS Key不能为空")
    private String contentOssKey;

    @NotBlank(message = "内容URL不能为空")
    private String contentUrl;

    private Integer status = 1;
}
