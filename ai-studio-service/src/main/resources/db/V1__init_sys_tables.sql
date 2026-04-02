-- =====================================================
-- AI Studio 平台数据库初始化脚本
-- 数据库: dev_db
-- =====================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------
-- 用户表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名（唯一）',
    `password`    VARCHAR(200) NOT NULL COMMENT '密码（BCrypt加密）',
    `real_name`   VARCHAR(50)  DEFAULT NULL COMMENT '真实姓名',
    `department`  VARCHAR(100) DEFAULT NULL COMMENT '所属部门',
    `email`       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ---------------------------------------------------
-- 角色表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id`         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `role_code`  VARCHAR(50) NOT NULL COMMENT '角色编码（唯一）',
    `role_name`  VARCHAR(50) NOT NULL COMMENT '角色名称',
    `created_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ---------------------------------------------------
-- 用户角色关联表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `id`      BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ---------------------------------------------------
-- 菜单表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_menu` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `parent_id`  BIGINT       DEFAULT 0 COMMENT '父菜单ID，0表示顶级',
    `name`       VARCHAR(50)  NOT NULL COMMENT '菜单名称',
    `path`       VARCHAR(200) DEFAULT NULL COMMENT '路由路径',
    `component`  VARCHAR(200) DEFAULT NULL COMMENT '组件路径',
    `icon`       VARCHAR(100) DEFAULT NULL COMMENT '菜单图标',
    `permission` VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
    `sort`       INT          DEFAULT 0 COMMENT '排序号',
    `hidden`     TINYINT      DEFAULT 0 COMMENT '是否隐藏：0-显示，1-隐藏',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- ---------------------------------------------------
-- 角色菜单关联表
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
    `id`      BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';
