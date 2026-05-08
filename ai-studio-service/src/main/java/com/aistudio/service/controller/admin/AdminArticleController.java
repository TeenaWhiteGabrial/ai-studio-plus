package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.dto.request.ArticleTakedownRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Article;
import com.aistudio.service.mapper.ArticleMapper;
import com.aistudio.service.service.ArticleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
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
    private final ArticleMapper articleMapper;

    @Operation(summary = "文章列表（管理端，所有状态）")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<PageResult<Article>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        LambdaQueryWrapper<Article> q = new LambdaQueryWrapper<>();
        q.eq(Article::getIsDeleted, 0);
        if (StringUtils.hasText(keyword)) {
            q.and(w -> w.like(Article::getTitle, keyword).or().like(Article::getSummary, keyword));
        }
        if (status != null) {
            q.eq(Article::getStatus, status);
        }
        q.orderByDesc(Article::getCreatedAt);
        Page<Article> result = articleMapper.selectPage(new Page<>(page, size), q);
        return Result.success(PageResult.of(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "文章详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Article> detail(@PathVariable Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null || Integer.valueOf(1).equals(article.getIsDeleted())) {
            return Result.error(404, "文章不存在");
        }
        return Result.success(article);
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
