-- ========================================================
-- V12: 为现有表添加 Portal 社区所需字段（幂等）
-- 使用 PREPARE/EXECUTE 方式检查列是否存在，避免重复添加报错
-- ========================================================

-- 添加列的辅助宏（MySQL 预处理语句不支持直接IF NOT EXISTS，这是标准幂等写法）
-- sys_user: avatar
SET @s = (SELECT IF(
  EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'sys_user' AND COLUMN_NAME = 'avatar'),
  'SELECT 1',
  'ALTER TABLE `sys_user` ADD COLUMN `avatar` VARCHAR(500) DEFAULT NULL COMMENT ''用户头像OSS Key'''
));
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- sys_user: bio
SET @s = (SELECT IF(
  EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'sys_user' AND COLUMN_NAME = 'bio'),
  'SELECT 1',
  'ALTER TABLE `sys_user` ADD COLUMN `bio` VARCHAR(200) DEFAULT NULL COMMENT ''个人简介'''
));
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- skill: favorite_count
SET @s = (SELECT IF(
  EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'skill' AND COLUMN_NAME = 'favorite_count'),
  'SELECT 1',
  'ALTER TABLE `skill` ADD COLUMN `favorite_count` INT DEFAULT 0 COMMENT ''收藏数'''
));
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- skill: followers_count
SET @s = (SELECT IF(
  EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'skill' AND COLUMN_NAME = 'followers_count'),
  'SELECT 1',
  'ALTER TABLE `skill` ADD COLUMN `followers_count` INT DEFAULT 0 COMMENT ''粉丝数'''
));
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- plugin: favorite_count
SET @s = (SELECT IF(
  EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'plugin' AND COLUMN_NAME = 'favorite_count'),
  'SELECT 1',
  'ALTER TABLE `plugin` ADD COLUMN `favorite_count` INT DEFAULT 0 COMMENT ''收藏数'''
));
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- plugin: followers_count
SET @s = (SELECT IF(
  EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'plugin' AND COLUMN_NAME = 'followers_count'),
  'SELECT 1',
  'ALTER TABLE `plugin` ADD COLUMN `followers_count` INT DEFAULT 0 COMMENT ''粉丝数'''
));
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;
