-- ========================================================
-- V9: 修复菜单路径，与前端路由保持一致
-- 前端路由使用 /admin/system/xxx, /admin/resource/xxx 等路径
-- ========================================================

-- 用户管理 -> /system/user
UPDATE sys_menu SET path = '/system/user' WHERE path = '/user' AND name = '用户管理';

-- 部门管理 -> /system/department
UPDATE sys_menu SET path = '/system/department' WHERE path = '/department' AND name = '部门管理';

-- 角色管理 -> /system/role
UPDATE sys_menu SET path = '/system/role' WHERE path = '/role' AND name = '角色管理';

-- 团队管理 -> /system/team
UPDATE sys_menu SET path = '/system/team' WHERE path = '/team' AND name = '团队管理';

-- Skill -> /resource/skill
UPDATE sys_menu SET path = '/resource/skill' WHERE path = '/skill' AND name = '  Skill';

-- MCP服务 -> /resource/mcp
UPDATE sys_menu SET path = '/resource/mcp' WHERE path = '/mcp' AND name = '  MCP服务';

-- Plugin -> /resource/plugin
UPDATE sys_menu SET path = '/resource/plugin' WHERE path = '/plugin' AND name = '  Plugin';

-- 教程 -> /resource/tutorial
UPDATE sys_menu SET path = '/resource/tutorial' WHERE path = '/tutorial' AND name = '  教程';

-- 资源审核 -> /audit/resource
UPDATE sys_menu SET path = '/audit/resource' WHERE path = '/audit/resource' AND name = '  资源审核';

-- 部门产出 -> /stats/department
UPDATE sys_menu SET path = '/stats/department' WHERE path = '/stats/department' AND name = '  部门产出';

-- 项目产出 -> /stats/project
UPDATE sys_menu SET path = '/stats/project' WHERE path = '/stats/project' AND name = '  项目产出';

-- 菜单管理 -> /system/menu
UPDATE sys_menu SET path = '/system/menu' WHERE path = '/menu' AND name = '  菜单管理';

-- 操作日志 -> /system/log
UPDATE sys_menu SET path = '/system/log' WHERE path = '/log' AND name = '  操作日志';
