package com.aistudio.service.service;

import com.aistudio.service.dto.request.GitlabWebhookRequest;
import com.aistudio.service.entity.ActivityTaskExecution;

import java.time.LocalDate;

public interface GitlabActivityTaskService {
    Long recordWebhookEvent(String eventType, String eventId, String payload, GitlabWebhookRequest request);
    void enqueueTask(String taskType, LocalDate targetDate, Long userId, Long projectId, String targetRef, String payloadJson);
    void processPendingTasks();
    ActivityTaskExecution getTask(Long taskId);
    void retryTask(Long taskId);
    void runTaskNow(Long taskId);
}
