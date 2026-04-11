package com.aistudio.service.job;

import com.aistudio.service.service.SkillSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class SkillSyncJob {

    private final SkillSyncService skillSyncService;

    // 同步锁，防止并发执行
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    /**
     * 定时同步任务
     * 默认每 30 分钟执行一次
     * 可通过配置 skill.gitlab.sync-cron 修改
     */
    @Scheduled(cron = "${skill.gitlab.sync-cron:0 */30 * * * *}")
    public void scheduledSync() {
        // 检查是否正在运行
        if (isRunning.get()) {
            log.warn("上一次同步尚未完成，跳过本次定时同步");
            return;
        }

        try {
            isRunning.set(true);
            log.info("开始定时同步 Skill...");
            SkillSyncService.SyncResult result = skillSyncService.syncAll();
            log.info("定时同步完成: 新增 {}, 更新 {}, 删除 {}, 失败 {}, 耗时 {}ms",
                    result.created(), result.updated(), result.deleted(), result.failed(), result.durationMs());
        } catch (Exception e) {
            log.error("定时同步失败", e);
        } finally {
            isRunning.set(false);
        }
    }

    /**
     * 检查同步是否正在运行
     */
    public boolean isRunning() {
        return isRunning.get();
    }
}
