-- =====================================================
-- Skill GitLab 集成 - 补充迁移脚本
-- 扩展 tags 和 description 字段长度
-- =====================================================

SET NAMES utf8mb4;

SET @dbname = DATABASE();
SET @tablename = 'skill';

-- ---------------------------------------------------
-- 1. 扩展 description 字段长度（支持长描述）
-- ---------------------------------------------------
SET @columnname1 = 'description';

SET @preparedStatement1 = (SELECT IF(
    (
        SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = @dbname
        AND TABLE_NAME = @tablename
        AND COLUMN_NAME = @columnname1
    ) > 0,
    CONCAT('ALTER TABLE `', @tablename, '` MODIFY COLUMN `', @columnname1, '` TEXT DEFAULT NULL COMMENT ''描述（从YAML解析）'';'),
    'SELECT 1;'
));

PREPARE alterDesc FROM @preparedStatement1;
EXECUTE alterDesc;
DEALLOCATE PREPARE alterDesc;

-- ---------------------------------------------------
-- 2. 扩展 tags 字段长度（支持数组转字符串）
-- ---------------------------------------------------
SET @columnname2 = 'tags';

SET @preparedStatement2 = (SELECT IF(
    (
        SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = @dbname
        AND TABLE_NAME = @tablename
        AND COLUMN_NAME = @columnname2
    ) > 0,
    CONCAT('ALTER TABLE `', @tablename, '` MODIFY COLUMN `', @columnname2, '` VARCHAR(500) DEFAULT NULL COMMENT ''标签（从YAML metadata解析，逗号分隔）'';'),
    'SELECT 1;'
));

PREPARE alterTags FROM @preparedStatement2;
EXECUTE alterTags;
DEALLOCATE PREPARE alterTags;
