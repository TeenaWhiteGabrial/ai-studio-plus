-- =============================================
-- AI-Studio MCP 审核功能数据库更新脚本
-- 版本: V20260423__update_mcp_audit.sql
-- 说明: 为 MCP 服务器表添加审核相关字段
-- 幂等性: 可多次执行，每次结果一致
-- 兼容性: MySQL 5.6+ / 8.x
-- =============================================

-- =============================================
-- MCP 服务器表审核字段更新
-- =============================================

-- 1. 添加 creator_id（如果不存在）
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'mcp_server' AND COLUMN_NAME = 'creator_id');
SET @sqlstmt := IF(@exist = 0, 'ALTER TABLE `mcp_server` ADD COLUMN `creator_id` BIGINT DEFAULT NULL COMMENT ''创建人ID'' AFTER `dept_id`', 'SELECT ''creator_id already exists in mcp_server''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2. 修改 status 字段语义: 0-禁用 -> 0-待审核, 1-启用 -> 1-通过(发布)
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'mcp_server' AND COLUMN_NAME = 'status');
SET @sqlstmt := IF(@exist > 0, 'ALTER TABLE `mcp_server` MODIFY COLUMN `status` TINYINT DEFAULT 0 COMMENT ''状态: 0-待审核 1-通过 2-拒绝''', 'SELECT ''status does not exist in mcp_server''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3. 添加 review_time（如果不存在）
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'mcp_server' AND COLUMN_NAME = 'review_time');
SET @sqlstmt := IF(@exist = 0, 'ALTER TABLE `mcp_server` ADD COLUMN `review_time` DATETIME DEFAULT NULL COMMENT ''审核时间'' AFTER `status`', 'SELECT ''review_time already exists in mcp_server''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 4. 将 reject_reason 重命名为 review_comment（如果 reject_reason 存在且 review_comment 不存在）
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'mcp_server' AND COLUMN_NAME = 'review_comment');
SET @exist_old := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'mcp_server' AND COLUMN_NAME = 'reject_reason');
SET @sqlstmt := IF(@exist = 0 AND @exist_old > 0, 'ALTER TABLE `mcp_server` CHANGE COLUMN `reject_reason` `review_comment` VARCHAR(500) DEFAULT NULL COMMENT ''审核备注''', 'SELECT ''review_comment already exists or reject_reason does not exist in mcp_server''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 5. 如果两个都不存在，添加 review_comment
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'mcp_server' AND COLUMN_NAME = 'review_comment');
SET @exist_old := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'mcp_server' AND COLUMN_NAME = 'reject_reason');
SET @sqlstmt := IF(@exist = 0 AND @exist_old = 0, 'ALTER TABLE `mcp_server` ADD COLUMN `review_comment` VARCHAR(500) DEFAULT NULL COMMENT ''审核备注'' AFTER `review_time`', 'SELECT ''review_comment already exists in mcp_server''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 6. 修改 reject_comment 字段注释（如果存在）
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'mcp_server' AND COLUMN_NAME = 'review_comment');
SET @sqlstmt := IF(@exist > 0, 'ALTER TABLE `mcp_server` MODIFY COLUMN `review_comment` VARCHAR(500) DEFAULT NULL COMMENT ''审核备注''', 'SELECT ''review_comment does not exist in mcp_server''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;


-- =============================================
-- 状态值说明
-- =============================================
-- status 字段语义：
--   0-待审核
--   1-通过(已发布)
--   2-拒绝


-- =============================================
-- 回滚脚本（如果需要）
-- =============================================
/*
-- 回滚 MCP 表
ALTER TABLE `mcp_server` DROP COLUMN `creator_id`;
ALTER TABLE `mcp_server` DROP COLUMN `review_time`;
ALTER TABLE `mcp_server` DROP COLUMN `review_comment`;
ALTER TABLE `mcp_server` MODIFY COLUMN `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用';

-- 如果需要恢复 reject_reason
ALTER TABLE `mcp_server` CHANGE COLUMN `review_comment` `reject_reason` VARCHAR(500) DEFAULT NULL COMMENT '审核拒绝原因';
*/