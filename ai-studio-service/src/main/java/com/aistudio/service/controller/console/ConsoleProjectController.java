package com.aistudio.service.controller.console;

import com.aistudio.service.common.Result;
import com.aistudio.service.dto.response.ProjectVO;
import com.aistudio.service.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Console - 项目")
@RestController
@RequestMapping("/console/project")
@RequiredArgsConstructor
public class ConsoleProjectController {

    private final ProjectService projectService;

    @Operation(summary = "查询进行中的项目")
    @GetMapping("/active")
    public Result<List<ProjectVO>> activeProjects() {
        return Result.success(projectService.listActiveProjectsForCurrentUserTeam());
    }
}
