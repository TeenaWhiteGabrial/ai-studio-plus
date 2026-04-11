-- =====================================================
-- 部门管理功能：数据迁移脚本
-- =====================================================

SET NAMES utf8mb4;

-- 步骤1：从现有用户数据提取部门名称到部门表
INSERT IGNORE INTO `sys_department` (`dept_name`)
SELECT DISTINCT `department` FROM `sys_user`
WHERE `department` IS NOT NULL AND `department` != '';

-- 步骤2：在用户表添加 dept_id 字段
ALTER TABLE `sys_user` ADD COLUMN `dept_id` BIGINT DEFAULT NULL COMMENT '部门ID' AFTER `department`;

-- 步骤3：迁移数据：将用户的 department 字符串关联到 dept_id
UPDATE `sys_user` u
SET `dept_id` = (SELECT `id` FROM `sys_department` d WHERE d.`dept_name` = u.`department`)
WHERE u.`department` IS NOT NULL AND u.`department` != '';

-- 步骤4：删除原 department 字符串字段
-- 注意：先确认迁移成功后再执行此步骤
-- ALTER TABLE `sys_user` DROP COLUMN `department`;

-- 步骤5：添加外键索引（可选，根据性能需求）
ALTER TABLE `sys_user` ADD KEY `idx_dept_id` (`dept_id`);
