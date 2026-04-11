-- =====================================================
-- 成员产出表扩展：新增项目根目录名字段
-- =====================================================

SET NAMES utf8mb4;

-- 添加项目根目录名字段
ALTER TABLE `member_output`
ADD COLUMN `project_root_name` VARCHAR(255) DEFAULT NULL COMMENT '项目根目录名（可选，用于标识产出所属项目）' AFTER `remark`;
