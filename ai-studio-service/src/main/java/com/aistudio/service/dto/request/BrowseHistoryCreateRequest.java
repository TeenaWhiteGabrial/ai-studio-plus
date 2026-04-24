package com.aistudio.service.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BrowseHistoryCreateRequest {

    @JsonAlias({"targetType", "target_type", "resourceType", "resource_type"})
    @NotBlank(message = "浏览对象类型不能为空")
    private String targetType;

    @JsonAlias({"targetId", "target_id", "resourceId", "resource_id"})
    @NotNull(message = "浏览对象ID不能为空")
    private Long targetId;
}
