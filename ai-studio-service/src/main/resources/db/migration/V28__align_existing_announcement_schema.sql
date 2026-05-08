-- Align older announcement tables with the notification/announcement feature schema.
-- Some environments already had an announcement table before V27, so CREATE TABLE IF NOT EXISTS
-- did not add the columns expected by the new backend code.

SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'announcement'
      AND COLUMN_NAME = 'content'
);
SET @sqlstmt := IF(
    @column_exists = 0,
    'ALTER TABLE `announcement` ADD COLUMN `content` TEXT NULL COMMENT ''公告内容'' AFTER `title`',
    'SELECT ''announcement.content exists'''
);
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'announcement'
      AND COLUMN_NAME = 'publisher_id'
);
SET @sqlstmt := IF(
    @column_exists = 0,
    'ALTER TABLE `announcement` ADD COLUMN `publisher_id` BIGINT DEFAULT NULL COMMENT ''发布人ID'' AFTER `content`',
    'SELECT ''announcement.publisher_id exists'''
);
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'announcement'
      AND COLUMN_NAME = 'status'
);
SET @sqlstmt := IF(
    @column_exists = 0,
    'ALTER TABLE `announcement` ADD COLUMN `status` VARCHAR(20) NOT NULL DEFAULT ''DRAFT'' COMMENT ''状态: DRAFT/PUBLISHED/OFFLINE'' AFTER `publisher_id`',
    'SELECT ''announcement.status exists'''
);
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'announcement'
      AND COLUMN_NAME = 'pinned'
);
SET @sqlstmt := IF(
    @column_exists = 0,
    'ALTER TABLE `announcement` ADD COLUMN `pinned` TINYINT NOT NULL DEFAULT 0 COMMENT ''是否置顶'' AFTER `status`',
    'SELECT ''announcement.pinned exists'''
);
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'announcement'
      AND COLUMN_NAME = 'published_at'
);
SET @sqlstmt := IF(
    @column_exists = 0,
    'ALTER TABLE `announcement` ADD COLUMN `published_at` DATETIME DEFAULT NULL COMMENT ''发布时间'' AFTER `pinned`',
    'SELECT ''announcement.published_at exists'''
);
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'announcement'
      AND COLUMN_NAME = 'expired_at'
);
SET @sqlstmt := IF(
    @column_exists = 0,
    'ALTER TABLE `announcement` ADD COLUMN `expired_at` DATETIME DEFAULT NULL COMMENT ''过期时间'' AFTER `published_at`',
    'SELECT ''announcement.expired_at exists'''
);
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'announcement'
      AND COLUMN_NAME = 'created_at'
);
SET @sqlstmt := IF(
    @column_exists = 0,
    'ALTER TABLE `announcement` ADD COLUMN `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'' AFTER `expired_at`',
    'SELECT ''announcement.created_at exists'''
);
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'announcement'
      AND COLUMN_NAME = 'updated_at'
);
SET @sqlstmt := IF(
    @column_exists = 0,
    'ALTER TABLE `announcement` ADD COLUMN `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'' AFTER `created_at`',
    'SELECT ''announcement.updated_at exists'''
);
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists := (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'announcement'
      AND INDEX_NAME = 'idx_status'
);
SET @sqlstmt := IF(
    @index_exists = 0,
    'ALTER TABLE `announcement` ADD INDEX `idx_status` (`status`)',
    'SELECT ''announcement.idx_status exists'''
);
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists := (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'announcement'
      AND INDEX_NAME = 'idx_published_at'
);
SET @sqlstmt := IF(
    @index_exists = 0,
    'ALTER TABLE `announcement` ADD INDEX `idx_published_at` (`published_at`)',
    'SELECT ''announcement.idx_published_at exists'''
);
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
