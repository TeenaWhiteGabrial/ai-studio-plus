package com.aistudio.service.service;

import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Favorite;

import java.util.Map;

public interface FavoriteService {

    void addFavorite(Long userId, String targetType, Long targetId);

    void removeFavorite(Long userId, String targetType, Long targetId);

    PageResult listFavorites(Long userId, String targetType, int page, int size);

    boolean checkFavorite(Long userId, String targetType, Long targetId);
}
