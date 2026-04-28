package com.aistudio.service.controller.console;

import com.aistudio.service.common.Result;
import com.aistudio.service.entity.Tag;
import com.aistudio.service.mapper.TagMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Console - 标签")
@RestController
@RequestMapping("/console/tag")
@RequiredArgsConstructor
public class ConsoleTagController {

    private final TagMapper tagMapper;

    @Operation(summary = "标签列表")
    @GetMapping("/list")
    public Result<List<Tag>> listTags(@RequestParam(required = false) String type) {
        return Result.success(tagMapper.selectTagsByType(type));
    }
}
