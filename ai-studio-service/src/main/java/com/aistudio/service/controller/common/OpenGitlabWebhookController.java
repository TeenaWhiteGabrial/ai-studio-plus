package com.aistudio.service.controller.common;

import com.aistudio.service.common.Result;
import com.aistudio.service.service.GitlabActivityAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/open/gitlab/webhook")
@RequiredArgsConstructor
public class OpenGitlabWebhookController {

    private final GitlabActivityAdminService adminService;

    @PostMapping("/events")
    public Result<Map<String, Object>> receive(@RequestHeader(value = "X-Gitlab-Event", required = false) String eventType,
                                               @RequestHeader(value = "X-Gitlab-Event-UUID", required = false) String eventId,
                                               @RequestHeader(value = "X-Gitlab-Token", required = false) String token,
                                               @RequestBody String payload) {
        Long eventLogId = adminService.receiveWebhook(eventType, eventId, token, payload);
        return Result.success(Map.of("event_log_id", eventLogId, "status", "accepted"));
    }
}
