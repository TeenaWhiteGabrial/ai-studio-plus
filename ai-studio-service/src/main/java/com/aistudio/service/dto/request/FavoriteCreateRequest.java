package com.aistudio.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavoriteCreateRequest {

    @NotBlank(message = "资源类型不能为空")
    private String targetType;

    @NotNull(message = "资源ID不能为空")
    private Long targetId;
}
