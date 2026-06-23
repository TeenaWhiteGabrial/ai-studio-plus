package com.aistudio.service.schedule;

import com.aistudio.service.config.GitlabActivityProperties;
import com.aistudio.service.service.GitlabRuntimeConfigService;
import com.aistudio.service.service.GitlabActivityTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GitlabActivityScheduler {

    private final GitlabActivityTaskService taskService;
    private final GitlabRuntimeConfigService runtimeConfigService;
    private final GitlabActivityProperties properties;

    @Scheduled(cron = "0 */10 * * * ?")
    public void processPendingTasks() {
        boolean schedulesEnabled = runtimeConfigService.getRuntimeConfig().getSchedulesEnabled() != null
                ? runtimeConfigService.getRuntimeConfig().getSchedulesEnabled() == 1
                : properties.isSchedulesEnabled();
        if (!schedulesEnabled) {
            return;
        }
        log.debug("开始处理 GitLab 活动任务");
        taskService.processPendingTasks();
    }
}
