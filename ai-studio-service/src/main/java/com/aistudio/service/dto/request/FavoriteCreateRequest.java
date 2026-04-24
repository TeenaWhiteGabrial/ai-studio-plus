package com.aistudio.service.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavoriteCreateRequest {

    @JsonAlias({"targetType", "target_type", "resourceType", "resource_type"})
    @NotBlank(message = "资源类型不能为空")
    private String targetType;

    @JsonAlias({"targetId", "target_id", "resourceId", "resource_id"})
    @NotNull(message = "资源ID不能为空")
    private Long targetId;
}
