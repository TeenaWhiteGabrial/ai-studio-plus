package com.aistudio.service.controller.portal;

import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.request.ArticleCreateRequest;
import com.aistudio.service.dto.request.ArticleUpdateRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Article;
import com.aistudio.service.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portal/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final SecurityUtils securityUtils;

    @GetMapping("/list")
    public ResponseEntity<PageResult> listArticles(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long tagId,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(articleService.listArticles(keyword, tagId, sort, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getArticle(id));
    }

    @PostMapping
    public ResponseEntity<Long> createArticle(@Valid @RequestBody ArticleCreateRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(articleService.createArticle(request, userId));
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> updateArticle(@PathVariable Long id, @RequestBody ArticleUpdateRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        articleService.updateArticle(id, request, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/delete")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        articleService.deleteArticle(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeArticle(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        articleService.likeArticle(id, userId);
        return ResponseEntity.ok().build();
    }
}
