-- V25: Console 菜单动态化，Admin 菜单改为前端固定配置。

SET @exist := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'sys_menu'
      AND COLUMN_NAME = 'app_code'
);
SET @sqlstmt := IF(@exist = 0, 'ALTER TABLE `sys_menu` ADD COLUMN `app_code` VARCHAR(20) NOT NULL DEFAULT ''ADMIN'' COMMENT ''菜单所属应用: ADMIN/CONSOLE'' AFTER `permission`', 'SELECT ''app_code already exists in sys_menu''');
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE `sys_menu`
SET `app_code` = 'ADMIN'
WHERE `app_code` IS NULL OR `app_code` = '';

INSERT INTO `sys_role` (`role_code`, `role_name`) VALUES
    ('PROJECT_MANAGER', '项目经理')
ON DUPLICATE KEY UPDATE `role_name` = VALUES(`role_name`);

DELETE rm FROM `sys_role_menu` rm
JOIN `sys_menu` m ON rm.menu_id = m.id
WHERE m.app_code = 'CONSOLE';

DELETE FROM `sys_menu`
WHERE `app_code` = 'CONSOLE';

INSERT INTO `sys_menu` (`parent_id`, `name`, `path`, `component`, `icon`, `permission`, `app_code`, `sort`, `hidden`) VALUES
    (0, '工作台', '/dashboard', 'Dashboard', 'HomeFilled', 'console:dashboard', 'CONSOLE', 1, 0),
    (0, 'API Key', '/apikey', 'ApiKey', 'Key', 'console:apikey', 'CONSOLE', 2, 0),
    (0, '每日任务', '/task', 'DailyTask', 'Calendar', 'console:task', 'CONSOLE', 3, 0),
    (0, '文章管理', '/article', 'Article', 'Document', 'console:article', 'CONSOLE', 4, 0),
    (0, '项目管理', '/project', 'Project', 'Folder', 'console:project', 'CONSOLE', 5, 0),
    (0, '资源中心', '/resource', 'ResourceCenter', 'Box', 'console:resource', 'CONSOLE', 6, 0),
    (0, '产出统计', '/stats', 'OutputStats', 'DataLine', 'console:stats', 'CONSOLE', 7, 0),
    (0, '个人设置', '/settings', 'Settings', 'Setting', 'console:settings', 'CONSOLE', 99, 0);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.id, m.id
FROM `sys_role` r
JOIN `sys_menu` m ON m.path IN ('/dashboard', '/apikey', '/task', '/article', '/resource', '/stats', '/settings')
WHERE r.role_code = 'USER' AND m.app_code = 'CONSOLE'
ON DUPLICATE KEY UPDATE `created_at` = CURRENT_TIMESTAMP;

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.id, m.id
FROM `sys_role` r
JOIN `sys_menu` m ON m.path IN ('/dashboard', '/project', '/settings')
WHERE r.role_code = 'PROJECT_MANAGER' AND m.app_code = 'CONSOLE'
ON DUPLICATE KEY UPDATE `created_at` = CURRENT_TIMESTAMP;

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.id, m.id
FROM `sys_role` r
JOIN `sys_menu` m ON m.path IN ('/dashboard', '/article', '/settings')
WHERE r.role_code = 'OP_ADMIN' AND m.app_code = 'CONSOLE'
ON DUPLICATE KEY UPDATE `created_at` = CURRENT_TIMESTAMP;

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.id, m.id
FROM `sys_role` r
JOIN `sys_menu` m ON m.path IN ('/dashboard', '/project', '/settings')
WHERE r.role_code = 'DEPT_ADMIN' AND m.app_code = 'CONSOLE'
ON DUPLICATE KEY UPDATE `created_at` = CURRENT_TIMESTAMP;

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.id, m.id
FROM `sys_role` r
JOIN `sys_menu` m
WHERE r.role_code = 'SUPER_ADMIN' AND m.app_code = 'CONSOLE'
ON DUPLICATE KEY UPDATE `created_at` = CURRENT_TIMESTAMP;
