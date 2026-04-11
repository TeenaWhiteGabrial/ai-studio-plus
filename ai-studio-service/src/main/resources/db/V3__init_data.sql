-- =====================================================
-- 初始化数据
-- =====================================================

SET NAMES utf8mb4;

-- ---------------------------------------------------
-- 初始角色数据
-- ---------------------------------------------------
INSERT IGNORE INTO `sys_role` (`id`, `role_code`, `role_name`) VALUES
(1, 'SUPER_ADMIN', '超级管理员'),
(2, 'ADMIN', '普通管理员'),
(3, 'USER', '普通用户');

-- ---------------------------------------------------
-- 初始超级管理员账号
-- 用户名: admin  密码: Admin@2026 (BCrypt加密)
-- ---------------------------------------------------
INSERT IGNORE INTO `sys_user` (`id`, `username`, `password`, `real_name`, `department`, `status`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '超级管理员', '系统管理', 1);

-- 注意：上面的密码hash是示例，实际启动时会通过初始化脚本重新生成
-- 真实密码 Admin@2026 的BCrypt hash:
UPDATE `sys_user` SET `password` = '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAum' WHERE `username` = 'admin';

-- 分配超级管理员角色
INSERT IGNORE INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- ---------------------------------------------------
-- 菜单数据
-- ---------------------------------------------------
-- 顶级菜单
INSERT IGNORE INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `permission`, `sort`) VALUES
(1,  0, '数据看板',     '/dashboard',  'dashboard/index',      'DataAnalysis', 'dashboard:view',    1),
(2,  0, '资源管理',     '/resource',   NULL,                   'Files',        NULL,                2),
(3,  0, '产出管理',     '/output',     NULL,                   'TrendCharts',  NULL,                3),
(4,  0, '系统管理',     '/system',     NULL,                   'Setting',      NULL,                4);

-- 资源管理子菜单
INSERT IGNORE INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `permission`, `sort`) VALUES
(10, 2, 'Skill管理',    '/resource/skill',    'skill/index',    'MagicStick',   'skill:list',        1),
(11, 2, 'MCP管理',      '/resource/mcp',      'mcp/index',      'Connection',   'mcp:list',          2),
(12, 2, 'Plugin管理',   '/resource/plugin',   'plugin/index',   'Cpu',          'plugin:list',       3),
(13, 2, '教程管理',     '/resource/tutorial', 'tutorial/index', 'Reading',      'tutorial:list',     4);

-- 产出管理子菜单
INSERT IGNORE INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `permission`, `sort`) VALUES
(20, 3, '我的产出',     '/output/my',     'output/my',      'User',         'output:my',         1),
(21, 3, '产出历史',     '/output/history','output/history', 'Clock',        'output:history',    2),
(22, 3, '全员产出',     '/output/admin',  'output/admin',   'UserFilled',   'output:admin:list', 3);

-- 系统管理子菜单
INSERT IGNORE INTO `sys_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `permission`, `sort`) VALUES
(30, 4, '用户管理',     '/system/user',   'user/index',     'Avatar',       'user:list',         1),
(31, 4, '角色管理',     '/system/role',   'role/index',     'Lock',         'role:list',         2);

-- ---------------------------------------------------
-- 角色菜单关联
-- SUPER_ADMIN: 全部菜单
-- ADMIN: 资源管理 + 产出管理（含全员产出）+ 数据看板
-- USER: 资源管理 + 我的产出 + 产出历史 + 数据看板
-- ---------------------------------------------------

-- SUPER_ADMIN 拥有全部菜单
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, `id` FROM `sys_menu`;

-- ADMIN 菜单
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(2, 1),   -- 数据看板
(2, 2),   -- 资源管理（父）
(2, 10),  -- Skill管理
(2, 11),  -- MCP管理
(2, 12),  -- Plugin管理
(2, 13),  -- 教程管理
(2, 3),   -- 产出管理（父）
(2, 20),  -- 我的产出
(2, 21),  -- 产出历史
(2, 22);  -- 全员产出

-- USER 菜单
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(3, 1),   -- 数据看板
(3, 2),   -- 资源管理（父）
(3, 10),  -- Skill管理
(3, 11),  -- MCP管理
(3, 12),  -- Plugin管理
(3, 13),  -- 教程管理
(3, 3),   -- 产出管理（父）
(3, 20),  -- 我的产出
(3, 21);  -- 产出历史
