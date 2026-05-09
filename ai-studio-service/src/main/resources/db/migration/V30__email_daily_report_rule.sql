CREATE TABLE IF NOT EXISTS `email_report_rule` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '邮件日报规则ID',
    `name` VARCHAR(100) NOT NULL COMMENT '规则名称',
    `recipients_json` TEXT NOT NULL COMMENT '接收邮箱JSON数组',
    `user_ids_json` TEXT NOT NULL COMMENT '统计成员ID JSON数组',
    `send_time` VARCHAR(5) NOT NULL DEFAULT '18:00' COMMENT '发送时间 HH:mm',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1启用 0停用',
    `last_sent_at` DATETIME DEFAULT NULL COMMENT '最后发送时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_status_send_time` (`status`, `send_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件日报规则';

INSERT INTO `sys_menu` (`parent_id`, `name`, `path`, `component`, `icon`, `permission`, `app_code`, `sort`, `hidden`)
SELECT parent.id, '邮件日报', '/message/email-report', 'message/email-report', 'Message', 'email-report:list', 'ADMIN', 4, 0
FROM `sys_menu` parent
WHERE parent.path = '/message' AND COALESCE(parent.app_code, 'ADMIN') = 'ADMIN'
  AND NOT EXISTS (
      SELECT 1 FROM `sys_menu` WHERE path = '/message/email-report' AND COALESCE(app_code, 'ADMIN') = 'ADMIN'
  );

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.id, m.id
FROM `sys_role` r
JOIN `sys_menu` m ON m.path = '/message/email-report' AND COALESCE(m.app_code, 'ADMIN') = 'ADMIN'
WHERE r.role_code = 'SUPER_ADMIN'
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);
