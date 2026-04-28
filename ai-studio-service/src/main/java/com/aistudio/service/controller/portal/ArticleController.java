package com.aistudio.service.controller.portal;

import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Article;
import com.aistudio.service.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Portal - 文章展示与互动
 */
@Tag(name = "Portal - 文章展示")
@RestController
@RequestMapping("/portal/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "文章列表（仅显示已发布状态）")
    @GetMapping("/list")
    public ResponseEntity<PageResult> listArticles(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long tagId,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(articleService.listArticles(keyword, tagId, sort, page, size));
    }

    @Operation(summary = "文章详情")
    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getArticle(id));
    }

    @Operation(summary = "点赞/取消点赞文章")
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeArticle(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        articleService.likeArticle(id, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "检查是否已点赞")
    @GetMapping("/{id}/is-liked")
    public ResponseEntity<Boolean> isLiked(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(articleService.isLiked(id, userId));
    }
}
