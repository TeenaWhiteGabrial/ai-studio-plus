-- =====================================================
-- 开放产出接口：修改唯一索引支持多项目
-- 将唯一键从 (user_id, stat_date) 改为 (user_name, stat_date, project_root_name)
-- =====================================================

SET NAMES utf8mb4;

-- 删除旧唯一索引（如果存在）
ALTER TABLE `member_output`
DROP INDEX IF EXISTS `uq_user_date`;

-- 添加新唯一索引：支持每人每天多项目
-- user_name 为空字符串时视为空项目
ALTER TABLE `member_output`
ADD UNIQUE KEY `uq_user_date_project` (`user_name`, `stat_date`, `project_root_name`);

-- 添加普通索引加速查询
ALTER TABLE `member_output`
ADD KEY `idx_user_name_date` (`user_name`, `stat_date`);
