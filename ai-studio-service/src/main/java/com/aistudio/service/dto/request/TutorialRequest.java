package com.aistudio.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TutorialRequest {

    @NotBlank(message = "教程标题不能为空")
    private String title;

    private String description;
    private String category;
    private String coverImage;
    private String contentType;      // richText/markdown
    private String content;          // 富文本或Markdown内容
    private String videoUrl;         // 视频OSS地址
    private String zipFileUrl;      // 附件ZIP OSS地址
    private String zipFileName;      // 附件文件名
}
