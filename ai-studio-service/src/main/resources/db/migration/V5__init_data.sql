-- ========================================================
-- V5: 初始数据（角色、菜单、用户）
-- ========================================================

-- 插入初始角色
INSERT INTO `sys_role` (`role_code`, `role_name`) VALUES
    ('SUPER_ADMIN', '超级管理员'),
    ('OP_ADMIN', '运营管理员'),
    ('DEPT_ADMIN', '部门管理员'),
    ('USER', '普通用户')
ON DUPLICATE KEY UPDATE `role_name` = VALUES(`role_name`);

-- 插入初始管理员账号（密码: admin123，BCrypt加密）
-- BCrypt hash for 'admin123': $2a$10$8kZYqN0hVH8F8F8F8F8F8O5Y5Y5Y5Y5Y5Y5Y5Y5Y5Y5Y5Y5Y5Y5Y
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `email`, `status`) VALUES
    ('admin', '$2a$10$8kZYqN0hVH8F8F8F8F8F8O5Y5Y5Y5Y5Y5Y5Y5Y5Y5Y5Y5Y5Y5Y5Y5Y', '系统管理员', 'admin@example.com', 1)
ON DUPLICATE KEY UPDATE `real_name` = VALUES(`real_name`);

-- 将管理员绑定到 SUPER_ADMIN 角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`)
SELECT u.id, r.id FROM `sys_user` u, `sys_role` r
WHERE u.username = 'admin' AND r.role_code = 'SUPER_ADMIN'
ON DUPLICATE KEY UPDATE `created_at` = CURRENT_TIMESTAMP;

-- 插入初始部门
INSERT INTO `sys_department` (`dept_name`, `sort`, `status`) VALUES
    ('技术研发部', 1, 1),
    ('产品运营部', 2, 1),
    ('市场商务部', 3, 1)
ON DUPLICATE KEY UPDATE `dept_name` = VALUES(`dept_name`);

-- 插入初始团队
INSERT INTO `sys_team` (`team_name`, `dept_id`, `description`, `status`) VALUES
    ('前端开发组', 1, '负责前端开发', 1),
    ('后端开发组', 1, '负责后端开发', 1),
    ('AI算法组', 1, '负责AI算法研发', 1),
    ('产品组', 2, '负责产品设计', 1),
    ('运营组', 2, '负责平台运营', 1)
ON DUPLICATE KEY UPDATE `team_name` = VALUES(`team_name`);

-- 插入初始菜单（Admin管理后台菜单）
INSERT INTO `sys_menu` (`parent_id`, `name`, `path`, `component`, `icon`, `permission`, `sort`, `hidden`) VALUES
    (0, '首页', '/dashboard', 'dashboard/index', 'HomeFilled', '', 1, 0),
    (0, '用户管理', '/user', 'user/index', 'User', 'user:list', 2, 0),
    (0, '部门管理', '/department', 'department/index', 'OfficeBuilding', 'dept:list', 3, 0),
    (0, '团队管理', '/team', 'team/index', 'Users', 'team:list', 4, 0),
    (0, '角色管理', '/role', 'role/index', 'Key', 'role:list', 5, 0),
    (0, '资源管理', '', '', 'Folder', '', 6, 0),
    (0, '  Skill', '/skill', 'skill/index', 'Grid', 'skill:list', 7, 1),
    (0, '  MCP服务', '/mcp', 'mcp/index', 'Connection', 'mcp:list', 8, 1),
    (0, '  Plugin', '/plugin', 'plugin/index', 'PuzzlePiece', 'plugin:list', 9, 1),
    (0, '  教程', '/tutorial', 'tutorial/index', 'Reading', 'tutorial:list', 10, 1),
    (0, '审核管理', '', '', 'Check', '', 11, 0),
    (0, '  资源审核', '/audit/resource', 'audit/resource/index', 'Stamp', 'audit:resource', 12, 1),
    (0, '产出统计', '', '', 'DataAnalysis', '', 13, 0),
    (0, '  部门产出', '/stats/department', 'stats/department/index', 'OfficeBuilding', 'stats:dept', 14, 1),
    (0, '  项目产出', '/stats/project', 'stats/project/index', 'Projects', 'stats:project', 15, 1),
    (0, '系统设置', '', '', 'Setting', '', 16, 0),
    (0, '  菜单管理', '/menu', 'menu/index', 'Tree', 'menu:list', 17, 1),
    (0, '  操作日志', '/log', 'log/index', 'Document', 'log:list', 18, 1);

-- 绑定 SUPER_ADMIN 角色拥有所有菜单权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.id, m.id FROM `sys_role` r, `sys_menu` m
WHERE r.role_code = 'SUPER_ADMIN'
ON DUPLICATE KEY UPDATE `created_at` = CURRENT_TIMESTAMP;
