-- =====================================================
-- Skill OSS 版本管理迁移脚本
-- 将 Skill 从 GitLab 同步模式改为 OSS + 版本控制模式
-- 注意：本脚本设计为幂等，可安全重复执行
-- =====================================================

SET NAMES utf8mb4;

-- ---------------------------------------------------
-- 1. 创建 skill_version 版本表（幂等）
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS `skill_version` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `skill_id`        BIGINT       NOT NULL COMMENT '关联skill.id',

    -- 版本号
    `version`         VARCHAR(20)  NOT NULL COMMENT '版本号（如：1.2.3）',
    `major`           INT          NOT NULL COMMENT '主版本号',
    `minor`           INT          NOT NULL COMMENT '次版本号',
    `patch`           INT          NOT NULL COMMENT '修订版本号',
    `version_number`  INT          NOT NULL COMMENT '版本数字表示，用于排序（major*10000 + minor*100 + patch）',

    -- OSS 存储信息
    `oss_key`         VARCHAR(500) NOT NULL COMMENT 'ZIP包OSS存储key',
    `oss_url`         VARCHAR(500) DEFAULT NULL COMMENT 'OSS访问URL',
    `file_size`       BIGINT       DEFAULT NULL COMMENT '文件大小（字节）',

    -- 发布信息
    `changelog`       TEXT         DEFAULT NULL COMMENT '版本变更说明',
    `created_by`      BIGINT       NOT NULL COMMENT '发布者用户ID',
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_skill_version` (`skill_id`, `version`),
    KEY `idx_skill_id` (`skill_id`),
    KEY `idx_version_number` (`version_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Skill版本表';

-- ---------------------------------------------------
-- 2. 修改 skill 表：添加版本管理字段（幂等）
-- ---------------------------------------------------

-- 添加 latest_version_id 字段（如果不存在）
SET @column_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'skill'
    AND COLUMN_NAME = 'latest_version_id');

SET @sql = IF(@column_exists = 0,
    'ALTER TABLE `skill` ADD COLUMN `latest_version_id` BIGINT DEFAULT NULL COMMENT "最新版本ID" AFTER `source`',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 latest_version 字段（如果不存在）
SET @column_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'skill'
    AND COLUMN_NAME = 'latest_version');

SET @sql = IF(@column_exists = 0,
    'ALTER TABLE `skill` ADD COLUMN `latest_version` VARCHAR(20) DEFAULT NULL COMMENT "最新版本号" AFTER `latest_version_id`',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 total_versions 字段（如果不存在）
SET @column_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'skill'
    AND COLUMN_NAME = 'total_versions');

SET @sql = IF(@column_exists = 0,
    'ALTER TABLE `skill` ADD COLUMN `total_versions` INT DEFAULT 0 COMMENT "版本总数" AFTER `latest_version`',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 dept_id 字段（如果不存在）
SET @column_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'skill'
    AND COLUMN_NAME = 'dept_id');

SET @sql = IF(@column_exists = 0,
    'ALTER TABLE `skill` ADD COLUMN `dept_id` BIGINT DEFAULT NULL COMMENT "创建者部门ID" AFTER `created_by`',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 is_deleted 字段（如果不存在）
SET @column_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'skill'
    AND COLUMN_NAME = 'is_deleted');

SET @sql = IF(@column_exists = 0,
    'ALTER TABLE `skill` ADD COLUMN `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT "0-正常 1-已删除" AFTER `download_count`',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 deleted_at 字段（如果不存在）
SET @column_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'skill'
    AND COLUMN_NAME = 'deleted_at');

SET @sql = IF(@column_exists = 0,
    'ALTER TABLE `skill` ADD COLUMN `deleted_at` DATETIME DEFAULT NULL COMMENT "删除时间" AFTER `is_deleted`',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 deleted_by 字段（如果不存在）
SET @column_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'skill'
    AND COLUMN_NAME = 'deleted_by');

SET @sql = IF(@column_exists = 0,
    'ALTER TABLE `skill` ADD COLUMN `deleted_by` BIGINT DEFAULT NULL COMMENT "删除者ID" AFTER `deleted_at`',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ---------------------------------------------------
-- 3. 修改 skill 表：添加索引（幂等）
-- ---------------------------------------------------

-- 添加 idx_dept_id 索引（如果不存在）
SET @index_exists = (SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'skill'
    AND INDEX_NAME = 'idx_dept_id');

SET @sql = IF(@index_exists = 0,
    'ALTER TABLE `skill` ADD KEY `idx_dept_id` (`dept_id`)',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 idx_is_deleted 索引（如果不存在）
SET @index_exists = (SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'skill'
    AND INDEX_NAME = 'idx_is_deleted');

SET @sql = IF(@index_exists = 0,
    'ALTER TABLE `skill` ADD KEY `idx_is_deleted` (`is_deleted`)',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ---------------------------------------------------
-- 4. 初始化现有数据的 dept_id（幂等，只更新空值）
-- ---------------------------------------------------
-- 将现有技能的 dept_id 设置为创建者的部门
UPDATE `skill` s
SET s.`dept_id` = (
    SELECT u.`dept_id` FROM `sys_user` u WHERE u.`id` = s.`created_by`
)
WHERE s.`dept_id` IS NULL;
