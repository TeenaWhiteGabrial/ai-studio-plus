-- =============================================
-- AI-Studio 资源审核模块数据库变更脚本
-- 版本: V20260417
-- 说明: Skill/Plugin/Tutorial 添加审核字段，调整表结构
-- 幂等性: 可多次执行，每次结果一致
-- 兼容性: MySQL 5.6+ / 8.x
-- =============================================

-- =============================================
-- 辅助函数：添加列（如果不存在）
-- =============================================

-- =============================================
-- 1. Skill 表变更
-- =============================================
-- 1.1 添加 creator_id（如果不存在）
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'skill' AND COLUMN_NAME = 'creator_id');
SET @sqlstmt := IF(@exist = 0, 'ALTER TABLE `skill` ADD COLUMN `creator_id` BIGINT DEFAULT NULL COMMENT ''创建人ID'' AFTER `dept_id`', 'SELECT ''creator_id already exists in skill''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 1.2 添加 status 字段用于审核流程: 0-待审核 1-通过 2-拒绝
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'skill' AND COLUMN_NAME = 'status');
SET @sqlstmt := IF(@exist = 0, 'ALTER TABLE `skill` ADD COLUMN `status` TINYINT DEFAULT 0 COMMENT ''状态: 0-待审核 1-通过 2-拒绝'' AFTER `creator_id`', 'SELECT ''status already exists in skill''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 1.3 添加 review_time（如果不存在）
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'skill' AND COLUMN_NAME = 'review_time');
SET @sqlstmt := IF(@exist = 0, 'ALTER TABLE `skill` ADD COLUMN `review_time` DATETIME DEFAULT NULL COMMENT ''审核时间'' AFTER `status`', 'SELECT ''review_time already exists in skill''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 1.4 添加 review_comment（如果不存在）
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'skill' AND COLUMN_NAME = 'review_comment');
SET @sqlstmt := IF(@exist = 0, 'ALTER TABLE `skill` ADD COLUMN `review_comment` VARCHAR(500) DEFAULT NULL COMMENT ''审核备注'' AFTER `review_time`', 'SELECT ''review_comment already exists in skill''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;


-- =============================================
-- 2. Plugin 表变更
-- =============================================
-- 2.1 添加 icon 字段（如果不存在）
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'plugin' AND COLUMN_NAME = 'icon');
SET @sqlstmt := IF(@exist = 0, 'ALTER TABLE `plugin` ADD COLUMN `icon` VARCHAR(500) DEFAULT NULL COMMENT ''图标URL'' AFTER `description`', 'SELECT ''icon already exists in plugin''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2.2 重命名 type -> category（如果 type 列存在）
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'plugin' AND COLUMN_NAME = 'type');
SET @sqlstmt := IF(@exist > 0, 'ALTER TABLE `plugin` CHANGE COLUMN `type` `category` VARCHAR(50) DEFAULT NULL COMMENT ''分类''', 'SELECT ''type already renamed or does not exist in plugin''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2.3 添加 creator_id（如果不存在）
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'plugin' AND COLUMN_NAME = 'creator_id');
SET @sqlstmt := IF(@exist = 0, 'ALTER TABLE `plugin` ADD COLUMN `creator_id` BIGINT DEFAULT NULL COMMENT ''创建人ID'' AFTER `dept_id`', 'SELECT ''creator_id already exists in plugin''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2.4 修改 status 字段语义: 0-禁用 -> 0-待审核, 1-启用 -> 1-通过(发布)
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'plugin' AND COLUMN_NAME = 'status');
SET @sqlstmt := IF(@exist > 0, 'ALTER TABLE `plugin` MODIFY COLUMN `status` TINYINT DEFAULT 0 COMMENT ''状态: 0-待审核 1-通过 2-拒绝''', 'SELECT ''status does not exist in plugin''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2.5 添加 review_time（如果不存在）
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'plugin' AND COLUMN_NAME = 'review_time');
SET @sqlstmt := IF(@exist = 0, 'ALTER TABLE `plugin` ADD COLUMN `review_time` DATETIME DEFAULT NULL COMMENT ''审核时间'' AFTER `status`', 'SELECT ''review_time already exists in plugin''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2.6 添加 review_comment（如果不存在）
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'plugin' AND COLUMN_NAME = 'review_comment');
SET @sqlstmt := IF(@exist = 0, 'ALTER TABLE `plugin` ADD COLUMN `review_comment` VARCHAR(500) DEFAULT NULL COMMENT ''审核备注'' AFTER `review_time`', 'SELECT ''review_comment already exists in plugin''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2.7 添加 file_oss_key（如果不存在）
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'plugin' AND COLUMN_NAME = 'file_oss_key');
SET @sqlstmt := IF(@exist = 0, 'ALTER TABLE `plugin` ADD COLUMN `file_oss_key` VARCHAR(500) DEFAULT NULL COMMENT ''OSS存储Key'' AFTER `total_versions`', 'SELECT ''file_oss_key already exists in plugin''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2.8 添加 file_url（如果不存在）
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'plugin' AND COLUMN_NAME = 'file_url');
SET @sqlstmt := IF(@exist = 0, 'ALTER TABLE `plugin` ADD COLUMN `file_url` VARCHAR(500) DEFAULT NULL COMMENT ''OSS访问URL'' AFTER `file_oss_key`', 'SELECT ''file_url already exists in plugin''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2.9 添加 file_size（如果不存在）
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'plugin' AND COLUMN_NAME = 'file_size');
SET @sqlstmt := IF(@exist = 0, 'ALTER TABLE `plugin` ADD COLUMN `file_size` BIGINT DEFAULT NULL COMMENT ''文件大小(字节)'' AFTER `file_url`', 'SELECT ''file_size already exists in plugin''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;


-- =============================================
-- 3. Tutorial 表变更
-- =============================================
-- 3.1 添加 description 简介字段（如果不存在）
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'tutorial' AND COLUMN_NAME = 'description');
SET @sqlstmt := IF(@exist = 0, 'ALTER TABLE `tutorial` ADD COLUMN `description` TEXT DEFAULT NULL COMMENT ''简介'' AFTER `title`', 'SELECT ''description already exists in tutorial''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3.2 添加 content_type 内容类型字段（如果不存在）
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'tutorial' AND COLUMN_NAME = 'content_type');
SET @sqlstmt := IF(@exist = 0, 'ALTER TABLE `tutorial` ADD COLUMN `content_type` VARCHAR(20) DEFAULT NULL COMMENT ''内容类型: richText/markdown'' AFTER `cover_image`', 'SELECT ''content_type already exists in tutorial''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3.3 添加 content 正文内容字段（如果不存在）
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'tutorial' AND COLUMN_NAME = 'content');
SET @sqlstmt := IF(@exist = 0, 'ALTER TABLE `tutorial` ADD COLUMN `content` LONGTEXT DEFAULT NULL COMMENT ''正文内容（富文本或Markdown）'' AFTER `content_type`', 'SELECT ''content already exists in tutorial''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3.4 添加 video_url 视频地址字段（如果不存在）
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'tutorial' AND COLUMN_NAME = 'video_url');
SET @sqlstmt := IF(@exist = 0, 'ALTER TABLE `tutorial` ADD COLUMN `video_url` VARCHAR(500) DEFAULT NULL COMMENT ''视频OSS地址'' AFTER `content`', 'SELECT ''video_url already exists in tutorial''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3.5 添加 zip_file_url（如果不存在）
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'tutorial' AND COLUMN_NAME = 'zip_file_url');
SET @sqlstmt := IF(@exist = 0, 'ALTER TABLE `tutorial` ADD COLUMN `zip_file_url` VARCHAR(500) DEFAULT NULL COMMENT ''附件ZIP OSS地址'' AFTER `video_url`', 'SELECT ''zip_file_url already exists in tutorial''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3.6 添加 zip_file_name（如果不存在）
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'tutorial' AND COLUMN_NAME = 'zip_file_name');
SET @sqlstmt := IF(@exist = 0, 'ALTER TABLE `tutorial` ADD COLUMN `zip_file_name` VARCHAR(255) DEFAULT NULL COMMENT ''附件文件名'' AFTER `zip_file_url`', 'SELECT ''zip_file_name already exists in tutorial''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3.7 添加 creator_id（如果不存在）
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'tutorial' AND COLUMN_NAME = 'creator_id');
SET @sqlstmt := IF(@exist = 0, 'ALTER TABLE `tutorial` ADD COLUMN `creator_id` BIGINT DEFAULT NULL COMMENT ''创建人ID'' AFTER `dept_id`', 'SELECT ''creator_id already exists in tutorial''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3.8 修改 status 字段语义: 0-禁用 -> 0-待审核, 1-启用 -> 1-通过(发布)
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'tutorial' AND COLUMN_NAME = 'status');
SET @sqlstmt := IF(@exist > 0, 'ALTER TABLE `tutorial` MODIFY COLUMN `status` TINYINT DEFAULT 0 COMMENT ''状态: 0-待审核 1-通过 2-拒绝''', 'SELECT ''status does not exist in tutorial''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3.9 添加 review_time（如果不存在）
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'tutorial' AND COLUMN_NAME = 'review_time');
SET @sqlstmt := IF(@exist = 0, 'ALTER TABLE `tutorial` ADD COLUMN `review_time` DATETIME DEFAULT NULL COMMENT ''审核时间'' AFTER `status`', 'SELECT ''review_time already exists in tutorial''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3.10 添加 review_comment（如果不存在）
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'tutorial' AND COLUMN_NAME = 'review_comment');
SET @sqlstmt := IF(@exist = 0, 'ALTER TABLE `tutorial` ADD COLUMN `review_comment` VARCHAR(500) DEFAULT NULL COMMENT ''审核备注'' AFTER `review_time`', 'SELECT ''review_comment already exists in tutorial''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;


-- =============================================
-- 4. 状态值说明
-- =============================================
-- status 字段语义变更：
--   旧: 0-禁用 1-启用
--   新: 0-待审核 1-通过(已发布) 2-拒绝


-- =============================================
-- 5. 回滚脚本（如果需要）
-- =============================================
/*
-- 回滚 Skill 表
ALTER TABLE `skill` DROP COLUMN `creator_id`;
ALTER TABLE `skill` DROP COLUMN `status`;
ALTER TABLE `skill` DROP COLUMN `review_time`;
ALTER TABLE `skill` DROP COLUMN `review_comment`;

-- 回滚 Tutorial 表
ALTER TABLE `tutorial` DROP COLUMN `description`;
ALTER TABLE `tutorial` DROP COLUMN `content_type`;
ALTER TABLE `tutorial` DROP COLUMN `content`;
ALTER TABLE `tutorial` DROP COLUMN `video_url`;
ALTER TABLE `tutorial` DROP COLUMN `zip_file_url`;
ALTER TABLE `tutorial` DROP COLUMN `zip_file_name`;
ALTER TABLE `tutorial` DROP COLUMN `creator_id`;
ALTER TABLE `tutorial` DROP COLUMN `review_time`;
ALTER TABLE `tutorial` DROP COLUMN `review_comment`;
ALTER TABLE `tutorial` MODIFY COLUMN `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用';

-- 回滚 Plugin 表
ALTER TABLE `plugin` DROP COLUMN `icon`;
ALTER TABLE `plugin` DROP COLUMN `creator_id`;
ALTER TABLE `plugin` DROP COLUMN `review_time`;
ALTER TABLE `plugin` DROP COLUMN `review_comment`;
ALTER TABLE `plugin` DROP COLUMN `file_oss_key`;
ALTER TABLE `plugin` DROP COLUMN `file_url`;
ALTER TABLE `plugin` DROP COLUMN `file_size`;
ALTER TABLE `plugin` MODIFY COLUMN `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用';
ALTER TABLE `plugin` CHANGE COLUMN `category` `type` VARCHAR(50) DEFAULT NULL COMMENT '类型';
*/
