-- ========================================================
-- V24: 确保网站设置表存在
-- ========================================================

CREATE TABLE IF NOT EXISTS `site_config` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '配置ID',
    `site_name` VARCHAR(100) NOT NULL COMMENT '网站标题',
    `site_description` VARCHAR(500) DEFAULT NULL COMMENT '网站描述',
    `logo_url` VARCHAR(500) DEFAULT NULL COMMENT '网站Logo地址',
    `icon_url` VARCHAR(500) DEFAULT NULL COMMENT '浏览器图标地址',
    `footer_text` VARCHAR(500) DEFAULT NULL COMMENT '底部说明',
    `footer_copyright` VARCHAR(300) DEFAULT NULL COMMENT '版权信息',
    `footer_record` VARCHAR(200) DEFAULT NULL COMMENT '备案信息',
    `footer_links` TEXT DEFAULT NULL COMMENT '底部链接JSON',
    `contacts` VARCHAR(500) DEFAULT NULL COMMENT '联系方式',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网站设置表';

INSERT INTO `site_config` (
    `id`,
    `site_name`,
    `site_description`,
    `logo_url`,
    `icon_url`,
    `footer_text`,
    `footer_copyright`,
    `footer_record`,
    `footer_links`,
    `contacts`
) VALUES (
    1,
    'AI Studio',
    'AI 应用开发平台',
    '/ai-studio-logo.svg',
    '/favicon.png',
    '面向研发团队的 AI 技术社区与资源平台。',
    'Copyright © 2026 AI Studio',
    '',
    '[{"name":"首页","url":"/"},{"name":"社区","url":"/community"},{"name":"资源中心","url":"/resources"},{"name":"个人中心","url":"/profile"}]',
    ''
) ON DUPLICATE KEY UPDATE `id` = `id`;
