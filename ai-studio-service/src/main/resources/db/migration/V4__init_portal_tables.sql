-- ========================================================
-- V4: Portal 门户表（Banner、公告、收藏、评论）
-- ========================================================

-- Banner 表（Portal首页轮播图）
CREATE TABLE IF NOT EXISTS `banner` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Banner ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `image_url` VARCHAR(500) NOT NULL COMMENT '图片URL',
    `link_url` VARCHAR(500) DEFAULT NULL COMMENT '跳转链接',
    `link_type` VARCHAR(20) DEFAULT 'NONE' COMMENT '跳转类型: NONE/INNER/OUTER',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `start_time` DATETIME DEFAULT NULL COMMENT '展示开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '展示结束时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_status` (`status`),
    INDEX `idx_sort` (`sort`),
    INDEX `idx_time_range` (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Banner表';

-- 公告表
CREATE TABLE IF NOT EXISTS `announcement` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '公告ID',
    `title` VARCHAR(200) NOT NULL COMMENT '公告标题',
    `content` TEXT NOT NULL COMMENT '公告内容',
    `type` VARCHAR(20) DEFAULT 'NOTICE' COMMENT '类型: NOTICE/ACTIVITY/UPDATE',
    `priority` TINYINT DEFAULT 0 COMMENT '优先级: 0-普通 1-重要',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `published_at` DATETIME DEFAULT NULL COMMENT '发布时间',
    `expired_at` DATETIME DEFAULT NULL COMMENT '过期时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_status` (`status`),
    INDEX `idx_published_at` (`published_at`),
    INDEX `idx_expired_at` (`expired_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 收藏表
CREATE TABLE IF NOT EXISTS `favorite` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `resource_type` VARCHAR(50) NOT NULL COMMENT '资源类型: SKILL/MCP/PLUGIN/TUTORIAL/INSTALLER/VIDEO',
    `resource_id` BIGINT NOT NULL COMMENT '资源ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    UNIQUE KEY `uk_user_resource` (`user_id`, `resource_type`, `resource_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_resource` (`resource_type`, `resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 点赞表
CREATE TABLE IF NOT EXISTS `like_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点赞ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `resource_type` VARCHAR(50) NOT NULL COMMENT '资源类型: SKILL/MCP/PLUGIN/TUTORIAL/VIDEO',
    `resource_id` BIGINT NOT NULL COMMENT '资源ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY `uk_user_resource` (`user_id`, `resource_type`, `resource_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_resource` (`resource_type`, `resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- 评论表
CREATE TABLE IF NOT EXISTS `comment` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `resource_type` VARCHAR(50) NOT NULL COMMENT '资源类型: SKILL/MCP/PLUGIN/TUTORIAL/VIDEO',
    `resource_id` BIGINT NOT NULL COMMENT '资源ID',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID（回复）',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-删除 1-正常',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_resource` (`resource_type`, `resource_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_parent_id` (`parent_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';
