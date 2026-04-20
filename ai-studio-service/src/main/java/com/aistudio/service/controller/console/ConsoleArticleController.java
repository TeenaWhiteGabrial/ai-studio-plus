package com.aistudio.service.controller.console;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.request.ArticleCreateRequest;
import com.aistudio.service.dto.request.ArticleListRequest;
import com.aistudio.service.dto.request.ArticleUpdateRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Article;
import com.aistudio.service.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Console - 文章管理
 */
@Slf4j
@Tag(name = "Console - 文章管理")
@RestController
@RequestMapping("/console/article")
@RequiredArgsConstructor
public class ConsoleArticleController {

    private final ArticleService articleService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "我的文章列表")
    @GetMapping("/list")
    public Result<PageResult<Article>> list(ArticleListRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        return Result.success(articleService.listMyArticles(request, userId));
    }

    @Operation(summary = "文章详情（编辑使用）")
    @GetMapping("/{id}")
    public Result<Article> detail(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        return Result.success(articleService.getArticleForEdit(id, userId));
    }

    @Operation(summary = "创建文章")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody ArticleCreateRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        return Result.success(articleService.createArticle(request, userId));
    }

    @Operation(summary = "更新文章")
    @PostMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ArticleUpdateRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        articleService.updateArticle(id, request, userId);
        return Result.success();
    }

    @Operation(summary = "删除文章")
    @PostMapping("/{id}/delete")
    public Result<Void> delete(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        articleService.deleteArticle(id, userId);
        return Result.success();
    }

    @Operation(summary = "立即发布文章")
    @PostMapping("/{id}/publish")
    public Result<Void> publish(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        articleService.publishArticle(id, userId);
        return Result.success();
    }

    @Operation(summary = "定时发布文章")
    @PostMapping("/{id}/schedule")
    public Result<Void> schedule(@PathVariable Long id, @RequestParam LocalDateTime publishTime) {
        Long userId = securityUtils.getCurrentUserId();
        articleService.schedulePublish(id, userId, publishTime);
        return Result.success();
    }

    @Operation(summary = "取消定时发布")
    @PostMapping("/{id}/cancel-schedule")
    public Result<Void> cancelSchedule(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        articleService.cancelSchedule(id, userId);
        return Result.success();
    }
}
