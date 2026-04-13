package com.aistudio.service.controller.portal;

import com.aistudio.service.common.Result;
import com.aistudio.service.dto.request.SkillQuery;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.dto.response.SkillDetailVO;
import com.aistudio.service.entity.Plugin;
import com.aistudio.service.entity.Tutorial;
import com.aistudio.service.service.PluginService;
import com.aistudio.service.service.SkillService;
import com.aistudio.service.service.TutorialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Portal - 资源浏览（公开，无需登录）
 */
@Tag(name = "Portal - 资源浏览")
@RestController
@RequestMapping("/portal/resource")
@RequiredArgsConstructor
public class PortalResourceBrowseController {

    private final SkillService skillService;
    private final PluginService pluginService;
    private final TutorialService tutorialService;

    @Operation(summary = "Skill 列表（公开）")
    @GetMapping("/skill/list")
    public Result<PageResult<SkillDetailVO>> skillList(SkillQuery query) {
        return Result.success(skillService.listSkills(query, null, null, null));
    }

    @Operation(summary = "Skill 详情（公开）")
    @GetMapping("/skill/{id}")
    public Result<SkillDetailVO> skillDetail(@PathVariable Long id) {
        return Result.success(skillService.getSkillDetail(id, null, null));
    }

    @Operation(summary = "Plugin 列表（公开）")
    @GetMapping("/plugin/list")
    public Result<PageResult<Plugin>> pluginList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type) {
        return Result.success(pluginService.listPlugins(page, size, keyword, type, 1));
    }

    @Operation(summary = "Plugin 详情（公开）")
    @GetMapping("/plugin/{id}")
    public Result<Plugin> pluginDetail(@PathVariable Long id) {
        return Result.success(pluginService.getPluginById(id));
    }

    @Operation(summary = "教程列表（公开）")
    @GetMapping("/tutorial/list")
    public Result<PageResult<Tutorial>> tutorialList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag) {
        return Result.success(tutorialService.listTutorials(page, size, keyword, category, tag, 1));
    }

    @Operation(summary = "教程详情（公开）")
    @GetMapping("/tutorial/{id}")
    public Result<Tutorial> tutorialDetail(@PathVariable Long id) {
        return Result.success(tutorialService.getTutorialById(id));
    }
}
