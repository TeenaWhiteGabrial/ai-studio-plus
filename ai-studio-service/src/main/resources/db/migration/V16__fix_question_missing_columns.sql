-- ========================================================
-- V16: Fix missing columns on legacy question table
-- Root cause:
-- 1) Old environments may have question table without all V11 columns.
-- 2) V11 uses CREATE TABLE IF NOT EXISTS, which does not alter existing tables.
-- ========================================================

-- question: answers_count
SET @s = (SELECT IF(
  EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'question' AND COLUMN_NAME = 'answers_count'),
  'SELECT 1',
  'ALTER TABLE `question` ADD COLUMN `answers_count` INT DEFAULT 0 COMMENT ''回答数'''
));
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- question: views_count
SET @s = (SELECT IF(
  EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'question' AND COLUMN_NAME = 'views_count'),
  'SELECT 1',
  'ALTER TABLE `question` ADD COLUMN `views_count` INT DEFAULT 0 COMMENT ''浏览数'''
));
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- question: favorite_count
SET @s = (SELECT IF(
  EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'question' AND COLUMN_NAME = 'favorite_count'),
  'SELECT 1',
  'ALTER TABLE `question` ADD COLUMN `favorite_count` INT DEFAULT 0 COMMENT ''收藏数'''
));
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- question: followers_count
SET @s = (SELECT IF(
  EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'question' AND COLUMN_NAME = 'followers_count'),
  'SELECT 1',
  'ALTER TABLE `question` ADD COLUMN `followers_count` INT DEFAULT 0 COMMENT ''粉丝数'''
));
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- question: has_best_answer
SET @s = (SELECT IF(
  EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'question' AND COLUMN_NAME = 'has_best_answer'),
  'SELECT 1',
  'ALTER TABLE `question` ADD COLUMN `has_best_answer` TINYINT DEFAULT 0 COMMENT ''是否有最佳答案: 0-无 1-有'''
));
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- question: best_answer_id
SET @s = (SELECT IF(
  EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'question' AND COLUMN_NAME = 'best_answer_id'),
  'SELECT 1',
  'ALTER TABLE `question` ADD COLUMN `best_answer_id` BIGINT DEFAULT NULL COMMENT ''最佳答案ID'''
));
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- question: taken_down
SET @s = (SELECT IF(
  EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'question' AND COLUMN_NAME = 'taken_down'),
  'SELECT 1',
  'ALTER TABLE `question` ADD COLUMN `taken_down` TINYINT DEFAULT 0 COMMENT ''是否下架: 0-正常 1-已下架'''
));
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- question: is_deleted
SET @s = (SELECT IF(
  EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'question' AND COLUMN_NAME = 'is_deleted'),
  'SELECT 1',
  'ALTER TABLE `question` ADD COLUMN `is_deleted` TINYINT DEFAULT 0 COMMENT ''逻辑删除: 0-正常 1-已删除'''
));
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;
