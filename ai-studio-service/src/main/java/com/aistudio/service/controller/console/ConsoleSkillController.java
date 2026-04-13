package com.aistudio.service.controller.console;

import com.aistudio.service.common.Result;
import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.request.SkillQuery;
import com.aistudio.service.dto.response.PageResult;
import com.aistudio.service.dto.response.SkillDetailVO;
import com.aistudio.service.dto.response.SkillVersionVO;
import com.aistudio.service.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Console - Skill 浏览（只读）
 */
@Tag(name = "Console - Skill浏览")
@RestController
@RequestMapping("/console/skill")
@RequiredArgsConstructor
public class ConsoleSkillController {

    private final SkillService skillService;
    private final SecurityUtils securityUtils;

    @Operation(summary = "Skill 列表")
    @GetMapping("/list")
    public Result<PageResult<SkillDetailVO>> list(SkillQuery query) {
        Long userId = securityUtils.getCurrentUserId();
        Long deptId = securityUtils.getCurrentUserDeptId();
        List<String> roles = securityUtils.getCurrentUserRoles();
        return Result.success(skillService.listSkills(query, userId, deptId, roles));
    }

    @Operation(summary = "Skill 详情")
    @GetMapping("/{id}")
    public Result<SkillDetailVO> detail(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        List<String> roles = securityUtils.getCurrentUserRoles();
        return Result.success(skillService.getSkillDetail(id, userId, roles));
    }

    @Operation(summary = "Skill 版本列表")
    @GetMapping("/{id}/versions")
    public Result<List<SkillVersionVO>> versions(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        List<String> roles = securityUtils.getCurrentUserRoles();
        return Result.success(skillService.getSkillVersions(id, userId, roles));
    }

    @Operation(summary = "下载 Skill")
    @GetMapping("/{id}/download")
    public ResponseEntity<Void> download(@PathVariable Long id, @RequestParam(required = false) String version) {
        Long userId = securityUtils.getCurrentUserId();
        List<String> roles = securityUtils.getCurrentUserRoles();
        String signedUrl = skillService.downloadSkill(id, version, userId, roles);
        return ResponseEntity.status(302).location(URI.create(signedUrl)).build();
    }
}
