-- 为 sys_user 表添加 team_id 字段（一个用户只能属于一个团队）
-- 幂等处理：若列已存在则跳过
SET @col_exists := (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'sys_user'
    AND COLUMN_NAME = 'team_id');
SET @sql_add_col := IF(@col_exists = 0,
    'ALTER TABLE `sys_user` ADD COLUMN `team_id` BIGINT DEFAULT NULL COMMENT \'团队ID\' AFTER `dept_id`',
    'SELECT 1');
PREPARE stmt_col FROM @sql_add_col;
EXECUTE stmt_col;
DEALLOCATE PREPARE stmt_col;

-- 幂等处理：若索引已存在则跳过
SET @idx_exists := (SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'sys_user'
    AND INDEX_NAME = 'idx_team_id');
SET @sql_add_idx := IF(@idx_exists = 0,
    'ALTER TABLE `sys_user` ADD KEY `idx_team_id` (`team_id`)',
    'SELECT 1');
PREPARE stmt_idx FROM @sql_add_idx;
EXECUTE stmt_idx;
DEALLOCATE PREPARE stmt_idx;
