package com.aistudio.service.controller.admin;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.request.TutorialRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Tutorial;
import com.aistudio.service.service.TutorialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin - 教程管理
 */
@Tag(name = "Admin - 教程管理")
@RestController
@RequestMapping("/admin/tutorial")
@RequiredArgsConstructor
public class AdminTutorialController {

    private final TutorialService tutorialService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "教程列表（管理端）")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<PageResult<Tutorial>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) Integer status) {
        return Result.success(tutorialService.listTutorials(page, size, keyword, category, tag, status));
    }

    @Operation(summary = "教程详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','OP_ADMIN')")
    public Result<Tutorial> detail(@PathVariable Long id) {
        return Result.success(tutorialService.getTutorialById(id));
    }

    @Operation(summary = "创建教程")
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Long> create(@Valid @RequestBody TutorialRequest request) {
        return Result.success(tutorialService.createTutorial(request, securityUtils.getCurrentUserId()));
    }

    @Operation(summary = "更新教程")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody TutorialRequest request) {
        tutorialService.updateTutorial(id, request, securityUtils.getCurrentUserId());
        return Result.success();
    }

    @Operation(summary = "删除教程")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        tutorialService.deleteTutorial(id, securityUtils.getCurrentUserId());
        return Result.success();
    }
}
