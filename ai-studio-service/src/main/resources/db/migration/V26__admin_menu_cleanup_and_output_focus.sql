-- Align Admin menus with the simplified output and community management scope.

DELETE rm FROM `sys_role_menu` rm
JOIN `sys_menu` m ON rm.menu_id = m.id
WHERE COALESCE(m.app_code, 'ADMIN') = 'ADMIN'
  AND (
    m.path IN ('/output/my', '/output/history', '/stats/department', '/stats/project', '/community/question', '/community/answer')
    OR m.name IN ('我的产出', '产出历史', '部门统计', '项目统计', '问题管理', '回答管理', '统计分析')
  );

DELETE FROM `sys_menu`
WHERE COALESCE(app_code, 'ADMIN') = 'ADMIN'
  AND (
    path IN ('/output/my', '/output/history', '/stats/department', '/stats/project', '/community/question', '/community/answer')
    OR name IN ('我的产出', '产出历史', '部门统计', '项目统计', '问题管理', '回答管理', '统计分析')
  );

UPDATE `sys_menu`
SET name = '全员产出',
    path = '/output/admin',
    component = 'output/admin',
    icon = 'Document',
    permission = 'output:list',
    parent_id = 0,
    hidden = 0,
    sort = 13,
    app_code = 'ADMIN'
WHERE COALESCE(app_code, 'ADMIN') = 'ADMIN'
  AND (path = '/output/admin' OR name = '全员产出');

INSERT INTO `sys_menu` (`parent_id`, `name`, `path`, `component`, `icon`, `permission`, `app_code`, `sort`, `hidden`)
SELECT 0, '全员产出', '/output/admin', 'output/admin', 'Document', 'output:list', 'ADMIN', 13, 0
WHERE NOT EXISTS (
  SELECT 1 FROM `sys_menu`
  WHERE COALESCE(app_code, 'ADMIN') = 'ADMIN'
    AND path = '/output/admin'
);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.id, m.id
FROM `sys_role` r
JOIN `sys_menu` m ON COALESCE(m.app_code, 'ADMIN') = 'ADMIN' AND m.path = '/output/admin'
WHERE r.role_code IN ('SUPER_ADMIN', 'OP_ADMIN', 'DEPT_ADMIN')
ON DUPLICATE KEY UPDATE `created_at` = CURRENT_TIMESTAMP;
