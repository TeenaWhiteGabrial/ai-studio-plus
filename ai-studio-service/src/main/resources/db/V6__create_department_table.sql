-- =====================================================
-- 部门管理功能：创建部门表
-- =====================================================

SET NAMES utf8mb4;

-- 创建部门表
CREATE TABLE IF NOT EXISTS `sys_department` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `dept_name`  VARCHAR(50)  NOT NULL COMMENT '部门名称',
    `sort`       INT          DEFAULT 0 COMMENT '排序',
    `status`     TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dept_name` (`dept_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';
