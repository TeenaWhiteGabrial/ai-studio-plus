package com.aistudio.service.controller.console;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.request.FavoriteCreateRequest;
import com.aistudio.service.dto.request.FavoriteDeleteRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Console - 我的收藏管理
 */
@Slf4j
@Tag(name = "Console - 我的收藏管理")
@RestController
@RequestMapping("/console/favorite")
@RequiredArgsConstructor
public class ConsoleFavoriteController {

    private final FavoriteService favoriteService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "我的收藏列表")
    @GetMapping("/list")
    public Result<PageResult> list(
            @RequestParam(required = false) String targetType, // article/question/answer
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = securityUtils.getCurrentUserId();
        return Result.success(favoriteService.listFavorites(userId, targetType, page, size));
    }

    @Operation(summary = "添加收藏")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody FavoriteCreateRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        favoriteService.addFavorite(userId, request.getTargetType(), request.getTargetId());
        return Result.success();
    }

    @Operation(summary = "取消收藏")
    @PostMapping("/remove")
    public Result<Void> remove(@RequestBody FavoriteDeleteRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        favoriteService.removeFavorite(userId, request.getTargetType(), request.getTargetId());
        return Result.success();
    }
}
