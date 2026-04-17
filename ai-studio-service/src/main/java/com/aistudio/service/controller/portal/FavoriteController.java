package com.aistudio.service.controller.portal;

import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.request.FavoriteCreateRequest;
import com.aistudio.service.dto.request.FavoriteDeleteRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.service.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portal/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<Void> addFavorite(@Valid @RequestBody FavoriteCreateRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        favoriteService.addFavorite(userId, request.getTargetType(), request.getTargetId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove")
    public ResponseEntity<Void> removeFavorite(@RequestBody FavoriteDeleteRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        favoriteService.removeFavorite(userId, request.getTargetType(), request.getTargetId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public ResponseEntity<PageResult> listFavorites(
            @RequestParam(required = false) String targetType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(favoriteService.listFavorites(userId, targetType, page, size));
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkFavorite(
            @RequestParam String targetType,
            @RequestParam Long targetId) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(favoriteService.checkFavorite(userId, targetType, targetId));
    }
}
