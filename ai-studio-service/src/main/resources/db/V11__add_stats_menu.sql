-- ========================================================
-- AI生成标记 [Claude Code]
-- 生成时间: 2026-03-31
-- 脚本功能: 添加统计功能菜单项
-- 修改历史:
--   - 2026-03-31: 创建菜单项，添加部门和项目统计菜单
-- ========================================================
-- =====================================================
-- 多维度产出统计：添加菜单项
-- =====================================================

SET NAMES utf8mb4;

-- 添加部门统计菜单（在产出管理下）
INSERT INTO `sys_menu` (`parent_id`, `name`, `path`, `component`, `icon`, `sort`, `hidden`, `created_at`)
SELECT
    id as parent_id,
    '部门统计' as name,
    '/stats/department' as path,
    'stats/DepartmentStats' as component,
    'TrendCharts' as icon,
    4 as sort,
    0 as hidden,
    NOW() as created_at
FROM `sys_menu` WHERE `name` = '全员产出' AND `parent_id` != 0;

-- 添加项目统计菜单（在产出管理下）
INSERT INTO `sys_menu` (`parent_id`, `name`, `path`, `component`, `icon`, `sort`, `hidden`, `created_at`)
SELECT
    id as parent_id,
    '项目统计' as name,
    '/stats/project' as path,
    'stats/ProjectStats' as component,
    'PieChart' as icon,
    5 as sort,
    0 as hidden,
    NOW() as created_at
FROM `sys_menu` WHERE `name` = '全员产出' AND `parent_id` != 0;
