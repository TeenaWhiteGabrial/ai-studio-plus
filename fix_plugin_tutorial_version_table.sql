-- 为 Plugin 表添加版本控制字段（幂等）
DROP PROCEDURE IF EXISTS `add_plugin_version_columns`;
DELIMITER $$
CREATE PROCEDURE `add_plugin_version_columns`()
BEGIN
    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'plugin' AND COLUMN_NAME = 'latest_version_id') THEN
        ALTER TABLE `plugin` ADD COLUMN `latest_version_id` BIGINT COMMENT '最新版本ID' AFTER `updated_at`;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'plugin' AND COLUMN_NAME = 'latest_version') THEN
        ALTER TABLE `plugin` ADD COLUMN `latest_version` VARCHAR(50) COMMENT '最新版本号' AFTER `latest_version_id`;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'plugin' AND COLUMN_NAME = 'total_versions') THEN
        ALTER TABLE `plugin` ADD COLUMN `total_versions` INT DEFAULT 0 COMMENT '版本总数' AFTER `latest_version`;
    END IF;
END$$
DELIMITER ;
CALL `add_plugin_version_columns`;
DROP PROCEDURE IF EXISTS `add_plugin_version_columns`;

-- 为 Tutorial 表添加版本控制字段（幂等）
DROP PROCEDURE IF EXISTS `add_tutorial_version_columns`;
DELIMITER $$
CREATE PROCEDURE `add_tutorial_version_columns`()
BEGIN
    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'tutorial' AND COLUMN_NAME = 'latest_version_id') THEN
        ALTER TABLE `tutorial` ADD COLUMN `latest_version_id` BIGINT COMMENT '最新版本ID' AFTER `updated_at`;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'tutorial' AND COLUMN_NAME = 'latest_version') THEN
        ALTER TABLE `tutorial` ADD COLUMN `latest_version` VARCHAR(50) COMMENT '最新版本号' AFTER `latest_version_id`;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'tutorial' AND COLUMN_NAME = 'total_versions') THEN
        ALTER TABLE `tutorial` ADD COLUMN `total_versions` INT DEFAULT 0 COMMENT '版本总数' AFTER `latest_version`;
    END IF;
END$$
DELIMITER ;
CALL `add_tutorial_version_columns`;
DROP PROCEDURE IF EXISTS `add_tutorial_version_columns`;

-- 创建 Plugin 版本表（幂等）
CREATE TABLE IF NOT EXISTS `plugin_version` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '版本ID',
    `plugin_id` BIGINT NOT NULL COMMENT '插件ID',
    `version` VARCHAR(50) NOT NULL COMMENT '版本号',
    `major` INT NOT NULL COMMENT '主版本号',
    `minor` INT NOT NULL COMMENT '次版本号',
    `patch` INT NOT NULL COMMENT '修订版本号',
    `version_number` INT NOT NULL COMMENT '版本数字表示（major*10000 + minor*100 + patch）',
    `oss_key` VARCHAR(500) COMMENT 'OSS存储key',
    `oss_url` VARCHAR(500) COMMENT 'OSS访问URL',
    `file_size` BIGINT COMMENT '文件大小（字节）',
    `change_log` TEXT COMMENT '变更日志',
    `created_by` BIGINT COMMENT '发布者用户ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY `idx_plugin_id` (`plugin_id`),
    KEY `idx_version_number` (`version_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='插件版本表';

-- 创建 Tutorial 版本表（幂等）
CREATE TABLE IF NOT EXISTS `tutorial_version` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '版本ID',
    `tutorial_id` BIGINT NOT NULL COMMENT '教程ID',
    `version` VARCHAR(50) NOT NULL COMMENT '版本号',
    `major` INT NOT NULL COMMENT '主版本号',
    `minor` INT NOT NULL COMMENT '次版本号',
    `patch` INT NOT NULL COMMENT '修订版本号',
    `version_number` INT NOT NULL COMMENT '版本数字表示（major*10000 + minor*100 + patch）',
    `change_log` TEXT COMMENT '变更日志',
    `created_by` BIGINT COMMENT '发布者用户ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY `idx_tutorial_id` (`tutorial_id`),
    KEY `idx_version_number` (`version_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教程版本表';
