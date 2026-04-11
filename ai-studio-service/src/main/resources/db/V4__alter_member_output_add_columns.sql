-- =====================================================
-- 成员产出表扩展：新增 16 个指标字段
-- =====================================================

SET NAMES utf8mb4;

-- 添加新字段
ALTER TABLE `member_output`
ADD COLUMN `user_name` VARCHAR(50) DEFAULT NULL COMMENT '用户名（开放 API 使用）' AFTER `user_id`;

-- 文档类指标
ALTER TABLE `member_output`
ADD COLUMN `prd_doc_count` INT DEFAULT 0 COMMENT 'PRD 文档数量' AFTER `remark`,
ADD COLUMN `data_model_doc_count` INT DEFAULT 0 COMMENT '数据模型文档数量',
ADD COLUMN `api_doc_count` INT DEFAULT 0 COMMENT 'API 接口文档数量';

-- Java 后端指标
ALTER TABLE `member_output`
ADD COLUMN `java_file_count` INT DEFAULT 0 COMMENT 'Java 文件数量',
ADD COLUMN `java_code_lines` INT DEFAULT 0 COMMENT 'Java 代码行数',
ADD COLUMN `core_biz_service_count` INT DEFAULT 0 COMMENT '核心业务服务数量',
ADD COLUMN `entity_count` INT DEFAULT 0 COMMENT '数据库实体数量';

-- 前端指标
ALTER TABLE `member_output`
ADD COLUMN `frontend_component_count` INT DEFAULT 0 COMMENT '前端组件数量',
ADD COLUMN `frontend_page_count` INT DEFAULT 0 COMMENT '前端页面数量',
ADD COLUMN `frontend_common_component_count` INT DEFAULT 0 COMMENT '前端公共组件数量',
ADD COLUMN `ts_code_lines` INT DEFAULT 0 COMMENT 'TypeScript 代码行数',
ADD COLUMN `frontend_code_lines` INT DEFAULT 0 COMMENT '前端代码行数';

-- 其他指标
ALTER TABLE `member_output`
ADD COLUMN `sql_script_count` INT DEFAULT 0 COMMENT 'SQL 脚本数量',
ADD COLUMN `test_file_count` INT DEFAULT 0 COMMENT '测试文件数量',
ADD COLUMN `total_code_lines` INT DEFAULT 0 COMMENT '总代码行数';
