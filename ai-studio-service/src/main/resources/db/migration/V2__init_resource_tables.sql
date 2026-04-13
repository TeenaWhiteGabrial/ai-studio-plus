-- ========================================================
-- V2: 资源表（Skill、Plugin、教程、MCP服务器）
-- ========================================================

-- Skill 表
CREATE TABLE IF NOT EXISTS `skill` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Skill ID',
    `name` VARCHAR(100) NOT NULL COMMENT 'Skill名称',
    `description` TEXT DEFAULT NULL COMMENT 'Skill描述',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '分类',
    `source` VARCHAR(50) DEFAULT NULL COMMENT '来源',
    `latest_version_id` BIGINT DEFAULT NULL COMMENT '最新版本ID',
    `latest_version` VARCHAR(50) DEFAULT NULL COMMENT '最新版本号',
    `total_versions` INT DEFAULT 0 COMMENT '版本总数',
    `author` VARCHAR(100) DEFAULT NULL COMMENT '作者',
    `tags` VARCHAR(500) DEFAULT NULL COMMENT '标签，逗号分隔',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `dept_id` BIGINT DEFAULT NULL COMMENT '所属部门ID',
    `download_count` INT DEFAULT 0 COMMENT '下载次数',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '软删除: 0-正常 1-已删除',
    `deleted_at` DATETIME DEFAULT NULL COMMENT '删除时间',
    `deleted_by` BIGINT DEFAULT NULL COMMENT '删除人ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_category` (`category`),
    INDEX `idx_created_by` (`created_by`),
    INDEX `idx_dept_id` (`dept_id`),
    INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Skill表';

-- Skill 版本表
CREATE TABLE IF NOT EXISTS `skill_version` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '版本ID',
    `skill_id` BIGINT NOT NULL COMMENT '所属Skill ID',
    `version` VARCHAR(50) NOT NULL COMMENT '版本号',
    `oss_key` VARCHAR(500) NOT NULL COMMENT 'OSS存储Key',
    `oss_url` VARCHAR(500) DEFAULT NULL COMMENT 'OSS访问URL',
    `file_size` BIGINT DEFAULT NULL COMMENT '文件大小(字节)',
    `change_log` TEXT DEFAULT NULL COMMENT '版本变更日志',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `published_by` BIGINT DEFAULT NULL COMMENT '发布人ID',
    `published_at` DATETIME DEFAULT NULL COMMENT '发布时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_skill_id` (`skill_id`),
    INDEX `idx_version` (`version`),
    INDEX `idx_status` (`status`),
    UNIQUE KEY `uk_skill_version` (`skill_id`, `version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Skill版本表';

-- Plugin 表
CREATE TABLE IF NOT EXISTS `plugin` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Plugin ID',
    `name` VARCHAR(100) NOT NULL COMMENT 'Plugin名称',
    `description` TEXT DEFAULT NULL COMMENT 'Plugin描述',
    `type` VARCHAR(50) DEFAULT NULL COMMENT '类型',
    `latest_version_id` BIGINT DEFAULT NULL COMMENT '最新版本ID',
    `latest_version` VARCHAR(50) DEFAULT NULL COMMENT '最新版本号',
    `total_versions` INT DEFAULT 0 COMMENT '版本总数',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `reject_reason` VARCHAR(500) DEFAULT NULL COMMENT '审核拒绝原因',
    `download_count` INT DEFAULT 0 COMMENT '下载次数',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `dept_id` BIGINT DEFAULT NULL COMMENT '所属部门ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_type` (`type`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_by` (`created_by`),
    INDEX `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Plugin表';

-- 教程表
CREATE TABLE IF NOT EXISTS `tutorial` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '教程ID',
    `title` VARCHAR(200) NOT NULL COMMENT '教程标题',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '分类',
    `tag` VARCHAR(200) DEFAULT NULL COMMENT '标签，逗号分隔',
    `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
    `author` VARCHAR(100) DEFAULT NULL COMMENT '作者',
    `latest_version_id` BIGINT DEFAULT NULL COMMENT '最新版本ID',
    `latest_version` VARCHAR(50) DEFAULT NULL COMMENT '最新版本号',
    `total_versions` INT DEFAULT 0 COMMENT '版本总数',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `reject_reason` VARCHAR(500) DEFAULT NULL COMMENT '审核拒绝原因',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `dept_id` BIGINT DEFAULT NULL COMMENT '所属部门ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_category` (`category`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_by` (`created_by`),
    INDEX `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教程表';

-- MCP 服务器表
CREATE TABLE IF NOT EXISTS `mcp_server` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'MCP服务器ID',
    `name` VARCHAR(100) NOT NULL COMMENT '服务器名称',
    `description` TEXT DEFAULT NULL COMMENT '服务器描述',
    `server_type` VARCHAR(50) DEFAULT NULL COMMENT '服务器类型',
    `latest_version_id` BIGINT DEFAULT NULL COMMENT '最新版本ID',
    `latest_version` VARCHAR(50) DEFAULT NULL COMMENT '最新版本号',
    `total_versions` INT DEFAULT 0 COMMENT '版本总数',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `reject_reason` VARCHAR(500) DEFAULT NULL COMMENT '审核拒绝原因',
    `last_test_at` DATETIME DEFAULT NULL COMMENT '最后测试时间',
    `last_test_result` VARCHAR(50) DEFAULT NULL COMMENT '最后测试结果: SUCCESS/FAILED',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `dept_id` BIGINT DEFAULT NULL COMMENT '所属部门ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_server_type` (`server_type`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_by` (`created_by`),
    INDEX `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MCP服务器表';

-- Banner 已移至 V4__init_portal_tables.sql

-- 视频表（产品视频/教程视频）
CREATE TABLE IF NOT EXISTS `video` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '视频ID',
    `title` VARCHAR(200) NOT NULL COMMENT '视频标题',
    `description` TEXT DEFAULT NULL COMMENT '视频描述',
    `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '封面URL',
    `video_url` VARCHAR(500) NOT NULL COMMENT '视频URL',
    `duration` INT DEFAULT 0 COMMENT '时长(秒)',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '分类',
    `tag` VARCHAR(200) DEFAULT NULL COMMENT '标签',
    `view_count` INT DEFAULT 0 COMMENT '播放次数',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_category` (`category`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频表';

-- MCP 服务器版本表
CREATE TABLE IF NOT EXISTS `mcp_server_version` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '版本ID',
    `mcp_server_id` BIGINT NOT NULL COMMENT '所属MCP服务器ID',
    `version` VARCHAR(50) NOT NULL COMMENT '版本号',
    `endpoint` VARCHAR(500) DEFAULT NULL COMMENT '服务端点URL',
    `auth_config` TEXT DEFAULT NULL COMMENT '认证配置(JSON)',
    `change_log` TEXT DEFAULT NULL COMMENT '版本变更日志',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `published_by` BIGINT DEFAULT NULL COMMENT '发布人ID',
    `published_at` DATETIME DEFAULT NULL COMMENT '发布时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_mcp_server_id` (`mcp_server_id`),
    INDEX `idx_version` (`version`),
    INDEX `idx_status` (`status`),
    UNIQUE KEY `uk_server_version` (`mcp_server_id`, `version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MCP服务器版本表';

-- Plugin 版本表
CREATE TABLE IF NOT EXISTS `plugin_version` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '版本ID',
    `plugin_id` BIGINT NOT NULL COMMENT '所属Plugin ID',
    `version` VARCHAR(50) NOT NULL COMMENT '版本号',
    `oss_key` VARCHAR(500) NOT NULL COMMENT 'OSS存储Key',
    `oss_url` VARCHAR(500) DEFAULT NULL COMMENT 'OSS访问URL',
    `file_size` BIGINT DEFAULT NULL COMMENT '文件大小(字节)',
    `md5` VARCHAR(32) DEFAULT NULL COMMENT 'MD5校验码',
    `change_log` TEXT DEFAULT NULL COMMENT '版本变更日志',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `published_by` BIGINT DEFAULT NULL COMMENT '发布人ID',
    `published_at` DATETIME DEFAULT NULL COMMENT '发布时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_plugin_id` (`plugin_id`),
    INDEX `idx_version` (`version`),
    INDEX `idx_status` (`status`),
    UNIQUE KEY `uk_plugin_version` (`plugin_id`, `version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Plugin版本表';

-- 教程版本表
CREATE TABLE IF NOT EXISTS `tutorial_version` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '版本ID',
    `tutorial_id` BIGINT NOT NULL COMMENT '所属教程ID',
    `version` VARCHAR(50) NOT NULL COMMENT '版本号',
    `content` LONGTEXT DEFAULT NULL COMMENT '教程内容(Markdown/HTML)',
    `change_log` TEXT DEFAULT NULL COMMENT '版本变更日志',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `published_by` BIGINT DEFAULT NULL COMMENT '发布人ID',
    `published_at` DATETIME DEFAULT NULL COMMENT '发布时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_tutorial_id` (`tutorial_id`),
    INDEX `idx_version` (`version`),
    INDEX `idx_status` (`status`),
    UNIQUE KEY `uk_tutorial_version` (`tutorial_id`, `version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教程版本表';

-- 安装包版本表
CREATE TABLE IF NOT EXISTS `installer_version` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '版本ID',
    `installer_id` BIGINT NOT NULL COMMENT '所属安装包ID',
    `version` VARCHAR(50) NOT NULL COMMENT '版本号',
    `os_type` VARCHAR(50) DEFAULT NULL COMMENT '操作系统: WINDOWS/MAC/LINUX',
    `architecture` VARCHAR(20) DEFAULT NULL COMMENT '架构: X64/ARM64',
    `file_oss_key` VARCHAR(500) NOT NULL COMMENT '文件OSS Key',
    `file_url` VARCHAR(500) DEFAULT NULL COMMENT '文件访问URL',
    `file_size` BIGINT DEFAULT NULL COMMENT '文件大小(字节)',
    `md5` VARCHAR(32) DEFAULT NULL COMMENT 'MD5校验码',
    `change_log` TEXT DEFAULT NULL COMMENT '版本变更日志',
    `is_latest` TINYINT DEFAULT 0 COMMENT '是否最新版本: 0-否 1-是',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `published_at` DATETIME DEFAULT NULL COMMENT '发布时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_installer_id` (`installer_id`),
    INDEX `idx_version` (`version`),
    INDEX `idx_is_latest` (`is_latest`),
    INDEX `idx_status` (`status`),
    UNIQUE KEY `uk_installer_version` (`installer_id`, `version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='安装包版本表';

-- 安装包表（Installer下载）- 主表记录基本信息
CREATE TABLE IF NOT EXISTS `installer` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '安装包ID',
    `name` VARCHAR(200) NOT NULL COMMENT '安装包名称',
    `description` TEXT DEFAULT NULL COMMENT '更新说明',
    `latest_version_id` BIGINT DEFAULT NULL COMMENT '最新版本ID',
    `latest_version` VARCHAR(50) DEFAULT NULL COMMENT '最新版本号',
    `total_versions` INT DEFAULT 0 COMMENT '版本总数',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `reject_reason` VARCHAR(500) DEFAULT NULL COMMENT '审核拒绝原因',
    `download_count` INT DEFAULT 0 COMMENT '下载次数',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `dept_id` BIGINT DEFAULT NULL COMMENT '所属部门ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_status` (`status`),
    INDEX `idx_created_by` (`created_by`),
    INDEX `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='安装包表';
