package com.aistudio.service.controller.portal;

import com.aistudio.service.dto.request.TagCreateRequest;
import com.aistudio.service.entity.Tag;
import com.aistudio.service.mapper.TagMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portal/tag")
@RequiredArgsConstructor
public class TagController {

    private final TagMapper tagMapper;

    @GetMapping("/list")
    public ResponseEntity<List<Tag>> listTags(@RequestParam(required = false) String type) {
        return ResponseEntity.ok(tagMapper.selectTagsByType(type));
    }

    @PostMapping
    public ResponseEntity<Void> createTag(@Valid @RequestBody TagCreateRequest request) {
        Tag tag = new Tag();
        tag.setName(request.getName());
        tag.setType(request.getType());
        tag.setUseCount(0);
        tagMapper.insert(tag);
        return ResponseEntity.ok().build();
    }
}
