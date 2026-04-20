package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.dto.request.ArticleTakedownRequest;
import com.aistudio.service.entity.Article;
import com.aistudio.service.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin - 文章管理（下架等管理功能）
 */
@Slf4j
@Tag(name = "Admin - 文章管理")
@RestController
@RequestMapping("/admin/article")
@RequiredArgsConstructor
public class AdminArticleController {

    private final ArticleService articleService;

    @Operation(summary = "文章列表（管理端，所有状态）")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Article> list() {
        // 暂时返回空，后续可根据需要实现分页列表
        return Result.success();
    }

    @Operation(summary = "文章详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Article> detail(@PathVariable Long id) {
        // TODO: 实现详情查询，需要修改 ArticleService 添加无需权限的方法
        return Result.success();
    }

    @Operation(summary = "下架文章")
    @PostMapping("/{id}/takedown")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Void> takedown(@PathVariable Long id, @Valid @RequestBody ArticleTakedownRequest request) {
        articleService.takedown(id);
        log.info("管理员下架文章: id={}, reason={}", id, request.getReason());
        return Result.success();
    }
}
