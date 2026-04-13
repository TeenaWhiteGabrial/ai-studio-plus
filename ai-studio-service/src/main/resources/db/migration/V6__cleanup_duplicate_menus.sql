-- ========================================================
-- V6: 清理重复菜单数据
-- ========================================================

-- 1. 创建临时表存储要保留的 sys_role_menu 记录
CREATE TEMPORARY TABLE tmp_keep_role_menu AS
SELECT MIN(id) as id FROM sys_role_menu GROUP BY role_id, menu_id;

-- 2. 删除不在保留列表中的记录
DELETE FROM sys_role_menu WHERE id NOT IN (SELECT id FROM tmp_keep_role_menu);

-- 3. 清理临时表
DROP TEMPORARY TABLE tmp_keep_role_menu;

-- 4. 创建临时表存储要保留的 sys_menu 记录
CREATE TEMPORARY TABLE tmp_keep_menu AS
SELECT MIN(id) as id FROM sys_menu GROUP BY name, COALESCE(path, '');

-- 5. 删除不在保留列表中的菜单
DELETE FROM sys_menu WHERE id NOT IN (SELECT id FROM tmp_keep_menu);

-- 6. 清理临时表
DROP TEMPORARY TABLE tmp_keep_menu;

-- 7. 修复图标名称（Element Plus 中不存在这些图标）
UPDATE sys_menu SET icon = 'Avatar' WHERE icon = 'Users';
UPDATE sys_menu SET icon = 'Operation' WHERE icon = 'PuzzlePiece';
UPDATE sys_menu SET icon = 'PieChart' WHERE icon = 'Projects';
UPDATE sys_menu SET icon = 'List' WHERE icon = 'Tree';
