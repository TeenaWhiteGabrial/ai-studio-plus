-- Normalize legacy announcement.status from numeric values to the string enum used by the API.

SET @status_needs_normalize := (
    SELECT IF(COUNT(*) = 0, 0, MAX(DATA_TYPE NOT IN ('varchar', 'char')))
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'announcement'
      AND COLUMN_NAME = 'status'
);

SET @sqlstmt := IF(
    @status_needs_normalize = 1,
    'ALTER TABLE `announcement` MODIFY COLUMN `status` VARCHAR(20) NOT NULL DEFAULT ''DRAFT'' COMMENT ''状态: DRAFT/PUBLISHED/OFFLINE''',
    'SELECT ''announcement.status already normalized'''
);
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE `announcement`
SET `status` = CASE `status`
    WHEN '0' THEN 'DRAFT'
    WHEN '1' THEN 'PUBLISHED'
    WHEN '2' THEN 'OFFLINE'
    ELSE `status`
END
WHERE `status` IN ('0', '1', '2');
