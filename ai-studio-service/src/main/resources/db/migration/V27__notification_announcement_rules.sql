-- Notification, announcement, and per-user rule group support.

ALTER TABLE `notification`
    ADD COLUMN `title` VARCHAR(120) DEFAULT NULL COMMENT '通知标题' AFTER `type`,
    ADD COLUMN `dedupe_key` VARCHAR(160) DEFAULT NULL COMMENT '去重键' AFTER `source_type`,
    ADD COLUMN `read_at` DATETIME DEFAULT NULL COMMENT '已读时间' AFTER `is_read`,
    ADD UNIQUE KEY `uk_notification_dedupe` (`dedupe_key`),
    ADD INDEX `idx_notification_type` (`type`);

CREATE TABLE IF NOT EXISTS `announcement` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '公告ID',
    `title` VARCHAR(200) NOT NULL COMMENT '公告标题',
    `content` TEXT NOT NULL COMMENT '公告内容',
    `publisher_id` BIGINT DEFAULT NULL COMMENT '发布人ID',
    `status` VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT/PUBLISHED/OFFLINE',
    `pinned` TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶',
    `published_at` DATETIME DEFAULT NULL COMMENT '发布时间',
    `expired_at` DATETIME DEFAULT NULL COMMENT '过期时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_status` (`status`),
    INDEX `idx_published_at` (`published_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

CREATE TABLE IF NOT EXISTS `announcement_read` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '公告阅读ID',
    `announcement_id` BIGINT NOT NULL COMMENT '公告ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `read_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
    UNIQUE KEY `uk_announcement_user` (`announcement_id`, `user_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告阅读状态表';

CREATE TABLE IF NOT EXISTS `notification_rule_group` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '规则组ID',
    `name` VARCHAR(100) NOT NULL COMMENT '规则组名称',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '规则组说明',
    `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认规则组',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1启用 0停用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_rule_group_name` (`name`),
    INDEX `idx_is_default` (`is_default`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知规则组';

CREATE TABLE IF NOT EXISTS `notification_rule` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '规则ID',
    `group_id` BIGINT NOT NULL COMMENT '规则组ID',
    `rule_type` VARCHAR(50) NOT NULL COMMENT '规则类型',
    `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
    `config_json` TEXT DEFAULT NULL COMMENT '规则配置JSON',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_group_rule` (`group_id`, `rule_type`),
    INDEX `idx_rule_type` (`rule_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知规则';

CREATE TABLE IF NOT EXISTS `user_notification_rule_group` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户规则组绑定ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `group_id` BIGINT NOT NULL COMMENT '规则组ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_user_rule_group` (`user_id`),
    INDEX `idx_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户通知规则组绑定';

INSERT INTO `notification_rule_group` (`name`, `description`, `is_default`, `status`)
VALUES ('默认规则组', '系统默认通知规则组，适用于未单独配置的用户', 1, 1)
ON DUPLICATE KEY UPDATE `description` = VALUES(`description`), `is_default` = 1, `status` = 1;

INSERT INTO `notification_rule` (`group_id`, `rule_type`, `enabled`)
SELECT g.id, t.rule_type, 1
FROM `notification_rule_group` g
JOIN (
    SELECT 'DAILY_TASK_MISSING' AS rule_type
    UNION ALL SELECT 'TASK_ASSIGNED'
    UNION ALL SELECT 'ARTICLE_LIKED'
    UNION ALL SELECT 'ARTICLE_COMMENTED'
    UNION ALL SELECT 'ARTICLE_TAKEN_DOWN'
    UNION ALL SELECT 'RESOURCE_APPROVED'
    UNION ALL SELECT 'RESOURCE_TAKEN_DOWN'
) t
WHERE g.is_default = 1
ON DUPLICATE KEY UPDATE `enabled` = VALUES(`enabled`);

INSERT INTO `user_notification_rule_group` (`user_id`, `group_id`)
SELECT u.id, g.id
FROM `sys_user` u
JOIN `notification_rule_group` g ON g.is_default = 1
WHERE u.status = 1
ON DUPLICATE KEY UPDATE `group_id` = VALUES(`group_id`);
