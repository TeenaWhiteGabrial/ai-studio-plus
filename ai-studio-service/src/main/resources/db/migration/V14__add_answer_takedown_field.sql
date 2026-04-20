-- ========================================================
-- V14: 为 answer 表添加下架字段
-- ========================================================

-- answer: taken_down (是否下架: 0-正常 1-已下架)
SET @s = (SELECT IF(
  EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'answer' AND COLUMN_NAME = 'taken_down'),
  'SELECT 1',
  'ALTER TABLE `answer` ADD COLUMN `taken_down` TINYINT DEFAULT 0 COMMENT ''是否下架: 0-正常 1-已下架'', ADD INDEX `idx_taken_down` (`taken_down`)'
));
PREPARE stmt FROM @s; EXECUTE stmt; DEALLOCATE PREPARE stmt;
