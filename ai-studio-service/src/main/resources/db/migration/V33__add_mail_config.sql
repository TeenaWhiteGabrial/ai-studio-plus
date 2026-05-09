-- ========================================================
-- V33: 邮件配置
-- ========================================================

CREATE TABLE IF NOT EXISTS `mail_config` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '配置ID',
    `sender_email` VARCHAR(100) DEFAULT NULL COMMENT '发件邮箱',
    `auth_code` VARCHAR(200) DEFAULT NULL COMMENT '邮箱安全码/授权码',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件配置表';
