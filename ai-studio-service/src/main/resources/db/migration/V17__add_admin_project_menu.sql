-- Add Admin project management menu entry.
INSERT INTO `sys_menu` (`parent_id`, `name`, `path`, `component`, `icon`, `permission`, `sort`, `hidden`)
SELECT 0, '项目管理', '/project', 'project/index', 'Folder', 'project:list', 5, 0
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `path` = '/project' AND `permission` = 'project:list'
);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.id, m.id
FROM `sys_role` r
JOIN `sys_menu` m ON m.path = '/project' AND m.permission = 'project:list'
WHERE r.role_code IN ('SUPER_ADMIN', 'OP_ADMIN', 'DEPT_ADMIN')
ON DUPLICATE KEY UPDATE `created_at` = CURRENT_TIMESTAMP;
