-- ========================================================
-- V3: Console 控制台表（项目、每日任务、API Key）
-- ========================================================

-- 项目表
CREATE TABLE IF NOT EXISTS `project` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '项目ID',
    `project_name` VARCHAR(200) NOT NULL COMMENT '项目名称',
    `description` TEXT DEFAULT NULL COMMENT '项目描述',
    `owner_id` BIGINT NOT NULL COMMENT '项目负责人ID',
    `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE/ENDED',
    `started_at` DATETIME DEFAULT NULL COMMENT '开始时间',
    `ended_at` DATETIME DEFAULT NULL COMMENT '结束时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_owner_id` (`owner_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_started_at` (`started_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目表';

-- 每日任务表
CREATE TABLE IF NOT EXISTS `daily_task` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '任务ID',
    `project_id` BIGINT DEFAULT NULL COMMENT '所属项目ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `task_date` DATE NOT NULL COMMENT '任务日期',
    `content` TEXT NOT NULL COMMENT '任务内容',
    `hours` DECIMAL(5,2) DEFAULT 0 COMMENT '工时（小时）',
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING/COMPLETED/CANCELLED',
    `completed_at` DATETIME DEFAULT NULL COMMENT '完成时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_project_id` (`project_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_task_date` (`task_date`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日任务表';

-- API Key 表
CREATE TABLE IF NOT EXISTS `api_key` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'API Key ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `api_key` VARCHAR(100) NOT NULL UNIQUE COMMENT 'API Key',
    `secret_key` VARCHAR(255) NOT NULL COMMENT 'Secret Key（加密存储）',
    `name` VARCHAR(100) DEFAULT NULL COMMENT 'Key名称',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `last_used_at` DATETIME DEFAULT NULL COMMENT '最后使用时间',
    `expires_at` DATETIME DEFAULT NULL COMMENT '过期时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_api_key` (`api_key`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API Key表';

-- 成员产出记录表（用于统计）
CREATE TABLE IF NOT EXISTS `member_output` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '产出ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `project_id` BIGINT DEFAULT NULL COMMENT '项目ID',
    `output_type` VARCHAR(50) NOT NULL COMMENT '产出类型: SKILL/MCP/PLUGIN/TUTORIAL/VIDEO',
    `resource_id` BIGINT NOT NULL COMMENT '资源ID',
    `output_date` DATE NOT NULL COMMENT '产出日期',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_project_id` (`project_id`),
    INDEX `idx_output_type` (`output_type`),
    INDEX `idx_output_date` (`output_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成员产出记录表';
