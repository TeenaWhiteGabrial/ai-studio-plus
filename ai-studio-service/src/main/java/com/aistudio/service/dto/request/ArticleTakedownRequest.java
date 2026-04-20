package com.aistudio.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 文章下架请求 (Admin 端使用)
 */
@Data
public class ArticleTakedownRequest {

    /**
     * 下架原因
     */
    @NotBlank(message = "下架原因不能为空")
    @Size(max = 500, message = "下架原因最多500字")
    private String reason;
}
