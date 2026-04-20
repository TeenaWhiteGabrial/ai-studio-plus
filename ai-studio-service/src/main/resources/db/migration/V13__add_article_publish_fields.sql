-- ========================================================
-- V13: 为 article 表添加定时发布相关字段
-- ========================================================

-- article: published_at (定时发布时间)
SET @s = (SELECT IF(
  EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'article' AND COLUMN_NAME = 'published_at'),
  'SELECT 1',
  'ALTER TABLE `article` ADD COLUMN `published_at` DATETIME DEFAULT NULL COMMENT ''定时发布时间'''
));
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;
