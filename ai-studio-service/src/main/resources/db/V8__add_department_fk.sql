-- =====================================================
-- 部门管理功能：添加外键约束
-- =====================================================

SET NAMES utf8mb4;

-- 步骤1：检查并删除旧字段（如果存在）
-- 注意：此步骤在确认数据迁移成功后执行
SET @dbname = DATABASE();
SET @tablename = 'sys_user';
SET @columnname = 'department';

SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = @dbname
              AND table_name = @tablename
              AND column_name = @columnname
        ),
        'ALTER TABLE sys_user DROP COLUMN department;',
        'SELECT 1;'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 步骤2：添加外键索引（如果不存在）
SET @indexname = 'idx_dept_id';
SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1 FROM information_schema.statistics
            WHERE table_schema = @dbname
              AND table_name = @tablename
              AND index_name = @indexname
        ),
        'SELECT 1;',
        'ALTER TABLE sys_user ADD KEY idx_dept_id (dept_id);'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 步骤3：添加外键约束（如果不存在）
SET @fkname = 'fk_user_department';
SET @sql = (
    SELECT IF(
        EXISTS(
            SELECT 1 FROM information_schema.table_constraints
            WHERE table_schema = @dbname
              AND table_name = @tablename
              AND constraint_name = @fkname
        ),
        'SELECT 1;',
        'ALTER TABLE sys_user ADD CONSTRAINT fk_user_department FOREIGN KEY (dept_id) REFERENCES sys_department(id) ON DELETE SET NULL ON UPDATE CASCADE;'
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
