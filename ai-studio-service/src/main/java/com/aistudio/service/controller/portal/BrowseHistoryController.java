package com.aistudio.service.controller.portal;

import com.aistudio.service.common.SecurityUtils;
import com.aistudio.service.dto.request.BrowseHistoryCreateRequest;
import com.aistudio.service.entity.BrowseHistory;
import com.aistudio.service.service.BrowseHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portal/browse-history")
@RequiredArgsConstructor
public class BrowseHistoryController {

    private final BrowseHistoryService browseHistoryService;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<Void> addBrowseHistory(@Valid @RequestBody BrowseHistoryCreateRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        browseHistoryService.addBrowseHistory(userId, request.getTargetType(), request.getTargetId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public ResponseEntity<List<BrowseHistory>> listBrowseHistories() {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(browseHistoryService.listBrowseHistories(userId));
    }

    @PostMapping("/clear")
    public ResponseEntity<Void> clearBrowseHistories() {
        Long userId = securityUtils.getCurrentUserId();
        browseHistoryService.clearBrowseHistories(userId);
        return ResponseEntity.ok().build();
    }
}
