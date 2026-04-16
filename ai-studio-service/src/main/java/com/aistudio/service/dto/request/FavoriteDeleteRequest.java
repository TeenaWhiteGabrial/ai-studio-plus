package com.aistudio.service.dto.request;

import lombok.Data;

@Data
public class FavoriteDeleteRequest {
    private String targetType;
    private Long targetId;
}
