package com.aistudio.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BrowseHistoryCreateRequest {

    @NotBlank(message = "浏览对象类型不能为空")
    private String targetType;

    @NotNull(message = "浏览对象ID不能为空")
    private Long targetId;
}
