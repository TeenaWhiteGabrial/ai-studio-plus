package com.aistudio.service.controller.console;

import com.aistudio.service.common.Result;
import com.aistudio.service.dto.request.TutorialRequest;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.entity.Tutorial;
import com.aistudio.service.service.TutorialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Console - 教程浏览（只读）
 */
@Tag(name = "Console - 教程浏览")
@RestController
@RequestMapping("/console/tutorial")
@RequiredArgsConstructor
public class ConsoleTutorialController {

    private final TutorialService tutorialService;

    @Operation(summary = "教程列表")
    @GetMapping("/list")
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
    public Result<Tutorial> detail(@PathVariable Long id) {
        return Result.success(tutorialService.getTutorialById(id));
    }
}
