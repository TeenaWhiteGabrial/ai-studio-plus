-- =====================================================
-- Skill GitLab 集成迁移脚本
-- =====================================================

SET NAMES utf8mb4;

-- ---------------------------------------------------
-- 1. 修改 skill 表：新增 GitLab 相关字段
-- ---------------------------------------------------
ALTER TABLE `skill`
    ADD COLUMN `gitlab_repo` VARCHAR(100) NOT NULL DEFAULT 'yunzhougyhlw/yunzhoupingtai/iip/foundation/base/aicoding' COMMENT 'GitLab仓库名' AFTER `source`,
    ADD COLUMN `gitlab_path` VARCHAR(500) DEFAULT NULL COMMENT 'GitLab文件路径' AFTER `gitlab_repo`,
    ADD COLUMN `gitlab_commit_sha` VARCHAR(40) DEFAULT NULL COMMENT '最新commit SHA' AFTER `gitlab_path`,
    ADD COLUMN `author` VARCHAR(50) DEFAULT NULL COMMENT '作者（从YAML metadata解析）' AFTER `gitlab_commit_sha`,
    ADD COLUMN `tags` VARCHAR(500) DEFAULT NULL COMMENT '标签（从YAML metadata解析，逗号分隔）' AFTER `author`,
    ADD COLUMN `sync_status` TINYINT NOT NULL DEFAULT 0 COMMENT '同步状态：0-待同步 1-成功 2-失败' AFTER `tags`,
    ADD COLUMN `last_sync_at` DATETIME DEFAULT NULL COMMENT '最后同步时间' AFTER `sync_status`,
    ADD COLUMN `sync_error` VARCHAR(500) DEFAULT NULL COMMENT '同步错误信息' AFTER `last_sync_at`,
    ADD COLUMN `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已删除：0-正常 1-已删除' AFTER `sync_error`;

-- ---------------------------------------------------
-- 2. 添加索引
-- ---------------------------------------------------
ALTER TABLE `skill`
    ADD UNIQUE KEY `uk_gitlab_path` (`gitlab_path`),
    ADD KEY `idx_sync_status` (`sync_status`),
    ADD KEY `idx_is_deleted` (`is_deleted`);

-- ---------------------------------------------------
-- 3. 修改 content_oss_key 和 content_url 字段注释，标记为废弃
-- 同时扩展 description 字段为 TEXT 支持长描述
-- ---------------------------------------------------
ALTER TABLE `skill`
    MODIFY COLUMN `description` TEXT DEFAULT NULL COMMENT '描述（从YAML解析）',
    MODIFY COLUMN `content_ss_key` VARCHAR(500) DEFAULT NULL COMMENT 'SKILL.md文件OSS Key（已废弃，保留用于数据迁移）',
    MODIFY COLUMN `content_url` VARCHAR(500) DEFAULT NULL COMMENT 'SKILL.md文件访问URL（已废弃，保留用于数据迁移）';

-- ---------------------------------------------------
-- 4. 创建 skill_sync_log 同步日志表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS `skill_sync_log` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `created_count`   INT          DEFAULT 0 COMMENT '新增数',
    `updated_count`   INT          DEFAULT 0 COMMENT '更新数',
    `deleted_count`   INT          DEFAULT 0 COMMENT '删除数',
    `error_count`     INT          DEFAULT 0 COMMENT '失败数',
    `duration_ms`     INT          DEFAULT NULL COMMENT '耗时ms',
    `trigger_type`    VARCHAR(20)  DEFAULT NULL COMMENT '触发方式: manual/cron/webhook',
    `error_message`   VARCHAR(500) DEFAULT NULL COMMENT '错误信息（同步失败时）',
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Skill同步日志表';
