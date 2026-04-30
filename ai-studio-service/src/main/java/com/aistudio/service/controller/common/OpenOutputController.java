package com.aistudio.service.controller.common;

import com.aistudio.service.common.Result;
import com.aistudio.service.dto.request.OpenOutputSubmitRequest;
import com.aistudio.service.service.MemberOutputService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Open - 开放产出接口（仅提交，无需认证）
 */
@Tag(name = "Open - 产出开放接口")
@RestController
@RequestMapping("/open/output")
@RequiredArgsConstructor
public class OpenOutputController {

    private final MemberOutputService memberOutputService;

    @Operation(summary = "提交产出数据（开放接口，无需认证）")
    @PostMapping("/submit")
    public Result<Map<String, Object>> submit(@Valid @RequestBody OpenOutputSubmitRequest request) {
        memberOutputService.submitOutputByUsername(request);
        Map<String, Object> response = new HashMap<>();
        response.put("git_name", request.getGitName());
        response.put("stat_date", request.getStatDate());
        response.put("project_root_name", request.getProjectRootName());
        response.put("output_type", request.getOutputType());
        return Result.success(response);
    }
}
