package com.aistudio.service.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class FavoriteDeleteRequest {
    @JsonAlias({"targetType", "target_type", "resourceType", "resource_type"})
    private String targetType;

    @JsonAlias({"targetId", "target_id", "resourceId", "resource_id"})
    private Long targetId;
}
