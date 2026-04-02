-- ========================================================
-- AI生成标记 [Claude Code]
-- 生成时间: 2026-03-31
-- 脚本功能: 添加多维度产出统计查询索引
-- 修改历史:
--   - 2026-03-31: 创建索引，优化部门和项目统计查询性能
-- ========================================================
-- =====================================================
-- 多维度产出统计：添加查询优化索引
-- =====================================================

SET NAMES utf8mb4;

-- 1.1 添加项目统计索引
ALTER TABLE `member_output`
ADD INDEX `idx_project_date` (`project_root_name`, `stat_date`);

-- 1.2 添加用户项目联合查询索引
ALTER TABLE `member_output`
ADD INDEX `idx_user_project_date` (`user_id`, `project_root_name`, `stat_date`);

-- 1.3 添加用于日期范围查询的索引
ALTER TABLE `member_output`
ADD INDEX `idx_stat_date_user` (`stat_date`, `user_id`);

-- 注意：部门统计通过 user_id 关联 sys_user 表获取 dept_id 信息
-- 部门相关查询使用 idx_user_project_date + sys_user 表关联查询
