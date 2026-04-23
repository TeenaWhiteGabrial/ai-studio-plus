-- 修复 skill_version 表结构，添加缺失的字段（幂等）

-- 检查并添加 major 字段
SET @exist = (SELECT COUNT(*) FROM information_schema.COLUMNS
              WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'skill_version' AND COLUMN_NAME = 'major');
SET @sql = IF(@exist = 0, 'ALTER TABLE `skill_version` ADD COLUMN `major` INT DEFAULT NULL COMMENT ''主版本号''', 'SELECT ''major column already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 minor 字段
SET @exist = (SELECT COUNT(*) FROM information_schema.COLUMNS
              WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'skill_version' AND COLUMN_NAME = 'minor');
SET @sql = IF(@exist = 0, 'ALTER TABLE `skill_version` ADD COLUMN `minor` INT DEFAULT NULL COMMENT ''次版本号''', 'SELECT ''minor column already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 patch 字段
SET @exist = (SELECT COUNT(*) FROM information_schema.COLUMNS
              WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'skill_version' AND COLUMN_NAME = 'patch');
SET @sql = IF(@exist = 0, 'ALTER TABLE `skill_version` ADD COLUMN `patch` INT DEFAULT NULL COMMENT ''修订版本号''', 'SELECT ''patch column already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 version_number 字段
SET @exist = (SELECT COUNT(*) FROM information_schema.COLUMNS
              WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'skill_version' AND COLUMN_NAME = 'version_number');
SET @sql = IF(@exist = 0, 'ALTER TABLE `skill_version` ADD COLUMN `version_number` INT DEFAULT NULL COMMENT ''版本数字表示，用于排序''', 'SELECT ''version_number column already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 change_log 字段
SET @exist = (SELECT COUNT(*) FROM information_schema.COLUMNS
              WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'skill_version' AND COLUMN_NAME = 'change_log');
SET @sql = IF(@exist = 0, 'ALTER TABLE `skill_version` ADD COLUMN `change_log` TEXT DEFAULT NULL COMMENT ''版本变更说明''', 'SELECT ''change_log column already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 created_by 字段
SET @exist = (SELECT COUNT(*) FROM information_schema.COLUMNS
              WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'skill_version' AND COLUMN_NAME = 'created_by');
SET @sql = IF(@exist = 0, 'ALTER TABLE `skill_version` ADD COLUMN `created_by` BIGINT DEFAULT NULL COMMENT ''发布者用户ID''', 'SELECT ''created_by column already exists''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 为现有数据填充版本号信息（如果有必要）
-- UPDATE `skill_version`
-- SET
--     `major` = CAST(SUBSTRING_INDEX(version, 1, 1) AS UNSIGNED),
--     `minor` = CASE
--         WHEN LOCATE('.', version, 2) > 0 AND LOCATE('.', version, 3) > LOCATE('.', version, 2)
--         THEN CAST(SUBSTRING_INDEX(version, LOCATE('.', version, 2) + 1, LOCATE('.', version, 3) - LOCATE('.', version, 2) - 1) AS UNSIGNED)
--         ELSE 0
--     END,
--     `patch` = CASE
--         WHEN LOCATE('.', version, 3) > 0
--         THEN CAST(SUBSTRING_INDEX(version, LOCATE('.', version, 3) + 1, LENGTH(version) - LOCATE('.', version, 3)) AS UNSIGNED)
--         ELSE 0
--     END,
--     `version_number` = CAST(SUBSTRING_INDEX(version, 1, 1) AS UNSIGNED) * 10000 +
--         CAST(CASE
--             WHEN LOCATE('.', version, 2) > 0 AND LOCATE('.', version, 3) > LOCATE('.', version, 2)
--             THEN SUBSTRING_INDEX(version, LOCATE('.', version, 2) + 1, LOCATE('.', version, 3) - LOCATE('.', version, 2) - 1)
--             ELSE '0'
--         END AS UNSIGNED) * 100 +
--         CASE
--             WHEN LOCATE('.', version, 3) > 0
--             THEN SUBSTRING_INDEX(version, LOCATE('.', version, 3) + 1, LENGTH(version) - LOCATE('.', version, 3))
--             ELSE '0'
--         END
-- WHERE version IS NOT NULL AND version REGEXP '^[0-9]+\\.[0-9]+\\.[0-9]+$';
