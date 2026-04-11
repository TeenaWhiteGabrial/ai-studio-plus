package com.aistudio.service.service;

import java.util.Map;

/**
 * Skill 同步服务接口
 */
public interface SkillSyncService {

    /**
     * 执行全量同步
     *
     * @return 同步结果
     */
    SyncResult syncAll();

    /**
     * 同步结果
     */
    record SyncResult(int created, int updated, int deleted, int failed, long durationMs) {
    }
}
