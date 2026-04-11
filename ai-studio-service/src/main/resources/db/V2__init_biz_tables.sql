-- =====================================================
-- 业务表 DDL
-- =====================================================

SET NAMES utf8mb4;

-- ---------------------------------------------------
-- Skill 技能表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS `skill` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`            VARCHAR(100) NOT NULL COMMENT 'Skill名称（唯一）',
    `description`     VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `category`        VARCHAR(50)  DEFAULT NULL COMMENT '分类',
    `source`          VARCHAR(200) DEFAULT NULL COMMENT '来源',
    `content_oss_key` VARCHAR(500) DEFAULT NULL COMMENT 'SKILL.md文件OSS Key',
    `content_url`     VARCHAR(500) DEFAULT NULL COMMENT 'SKILL.md文件访问URL',
    `status`          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    `download_count`  INT          NOT NULL DEFAULT 0 COMMENT '下载次数',
    `created_by`      BIGINT       NOT NULL COMMENT '创建人ID',
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_skill_name` (`name`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`),
    KEY `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Skill技能表';

-- ---------------------------------------------------
-- MCP 服务器表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS `mcp_server` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`         VARCHAR(100) NOT NULL COMMENT '服务器名称',
    `description`  VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `api_endpoint` VARCHAR(500) NOT NULL COMMENT 'API端点地址',
    `auth_type`    VARCHAR(50)  NOT NULL COMMENT '认证类型：Bearer/ApiKey/None',
    `config_json`  TEXT         DEFAULT NULL COMMENT '配置信息（JSON）',
    `status`       TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    `created_by`   BIGINT       NOT NULL COMMENT '创建人ID',
    `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MCP服务器表';

-- ---------------------------------------------------
-- Plugin 插件表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS `plugin` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`           VARCHAR(100) NOT NULL COMMENT '插件名称',
    `description`    VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `type`           VARCHAR(50)  DEFAULT NULL COMMENT '插件类型：editor/build/lint等',
    `version`        VARCHAR(50)  NOT NULL COMMENT '版本号',
    `file_oss_key`   VARCHAR(500) NOT NULL COMMENT '文件OSS Key',
    `file_url`       VARCHAR(500) NOT NULL COMMENT '文件访问URL',
    `file_size`      BIGINT       DEFAULT NULL COMMENT '文件大小（字节）',
    `status`         TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    `download_count` INT          NOT NULL DEFAULT 0 COMMENT '下载次数',
    `created_by`     BIGINT       NOT NULL COMMENT '创建人ID',
    `created_at`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`),
    KEY `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Plugin插件表';

-- ---------------------------------------------------
-- 教程表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS `tutorial` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title`           VARCHAR(200) NOT NULL COMMENT '教程标题',
    `category`        VARCHAR(50)  DEFAULT NULL COMMENT '分类',
    `tags`            VARCHAR(200) DEFAULT NULL COMMENT '标签（逗号分隔）',
    `content_oss_key` VARCHAR(500) NOT NULL COMMENT 'Markdown文件OSS Key',
    `content_url`     VARCHAR(500) NOT NULL COMMENT '文件访问URL',
    `status`          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    `view_count`      INT          NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `created_by`      BIGINT       NOT NULL COMMENT '创建人ID',
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`),
    KEY `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教程表';

-- ---------------------------------------------------
-- 成员产出表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS `member_output` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`        BIGINT       NOT NULL COMMENT '用户ID',
    `stat_date`      DATE         NOT NULL COMMENT '统计日期',
    `prd_count`      INT          DEFAULT 0 COMMENT 'PRD文档数',
    `api_count`      INT          DEFAULT 0 COMMENT 'API接口数',
    `java_lines`     INT          DEFAULT 0 COMMENT 'Java代码行数',
    `frontend_lines` INT          DEFAULT 0 COMMENT '前端代码行数',
    `remark`         VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `created_at`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_user_date` (`user_id`, `stat_date`),
    KEY `idx_stat_date` (`stat_date`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成员产出表';

-- ---------------------------------------------------
-- 操作日志表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_oper_log` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     BIGINT       DEFAULT NULL COMMENT '操作用户ID',
    `username`    VARCHAR(50)  DEFAULT NULL COMMENT '操作用户名',
    `module`      VARCHAR(50)  DEFAULT NULL COMMENT '操作模块',
    `action`      VARCHAR(50)  DEFAULT NULL COMMENT '操作类型：CREATE/UPDATE/DELETE/DOWNLOAD',
    `method`      VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
    `request_url` VARCHAR(500) DEFAULT NULL COMMENT '请求URL',
    `request_param` TEXT       DEFAULT NULL COMMENT '请求参数',
    `result`      VARCHAR(20)  DEFAULT NULL COMMENT '操作结果：SUCCESS/FAIL',
    `error_msg`   VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
    `cost_time`   BIGINT       DEFAULT NULL COMMENT '耗时（ms）',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
