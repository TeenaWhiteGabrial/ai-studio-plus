CREATE TABLE IF NOT EXISTS `email_report_send_log` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '邮件日报发送记录ID',
    `rule_id` BIGINT NOT NULL COMMENT '邮件日报规则ID',
    `rule_name` VARCHAR(100) NOT NULL COMMENT '发送时规则名称',
    `report_date` DATE NOT NULL COMMENT '日报日期',
    `trigger_type` VARCHAR(20) NOT NULL COMMENT '触发方式: MANUAL/SCHEDULED',
    `status` VARCHAR(20) NOT NULL COMMENT '发送状态: SUCCESS/FAILED',
    `to_recipients_json` TEXT NULL COMMENT '主送邮箱JSON数组',
    `cc_recipients_json` TEXT NULL COMMENT '抄送邮箱JSON数组',
    `subject` VARCHAR(200) NULL COMMENT '邮件标题',
    `error_message` TEXT NULL COMMENT '失败原因',
    `sent_at` DATETIME NOT NULL COMMENT '发送尝试时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_rule_sent_at` (`rule_id`, `sent_at`),
    INDEX `idx_status_sent_at` (`status`, `sent_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件日报发送记录';
