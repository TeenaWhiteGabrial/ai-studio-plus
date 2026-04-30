-- Migrated from C:\Users\zhangkunlun01\Desktop\1.sql
-- Target: `ai-studio-plus`.`member_output`
-- Old compatibility columns removed: prd_count, java_lines, frontend_lines
-- git_name is copied from old dev_db.member_output.user_name.
-- user_id is resolved from current `ai-studio-plus`.`sys_user` by git_name.
-- output_type is fixed to DEV_WORK_SUMMARY for migrated historical Skill output.
-- If old total_code_lines is 0, it is recalculated as java_code_lines + frontend_code_lines + ts_code_lines.
-- After execution, expected inserted/updated rows: 182. Verify with: SELECT COUNT(*) FROM `ai-studio-plus`.`member_output`;

INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 3, u.id, 'wutiancheng', '2026-03-27', 'DEV_WORK_SUMMARY', 1, NULL, 'technical-community', 0, '2026-03-27 09:55:15', '2026-03-27 09:55:15', 0, 0, 3, 43, 1, 0, 0, 0, 0, 0, 0, 0, 0, 43
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'wutiancheng'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 4, u.id, 'shichao', '2026-03-27', 'DEV_WORK_SUMMARY', 25, NULL, 'zhongshi-unified-configuration', 0, '2026-03-27 15:48:42', '2026-03-27 15:48:42', 0, 0, 10, 967, 3, 1, 0, 0, 0, 0, 0, 0, 0, 967
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 5, u.id, 'liuchong', '2026-03-27', 'DEV_WORK_SUMMARY', 0, NULL, 'monitor-alarm-center', 0, '2026-03-27 17:26:20', '2026-03-27 17:26:20', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'liuchong'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 6, u.id, 'zhangdi', '2026-03-27', 'DEV_WORK_SUMMARY', 0, NULL, 'portal-web', 0, '2026-03-27 17:26:48', '2026-03-27 17:26:48', 0, 0, 0, 0, 0, 0, 6, 0, 6, 3156, 2199, 0, 0, 5355
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 7, u.id, 'fanqi', '2026-03-27', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation', 0, '2026-03-27 17:29:05', '2026-03-27 17:29:05', 0, 0, 1, 146, 0, 0, 0, 0, 0, 0, 0, 0, 0, 146
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanqi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 8, u.id, 'yuyang', '2026-03-27', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web', 0, '2026-03-27 17:33:58', '2026-03-27 17:33:58', 0, 0, 0, 0, 0, 0, 6, 6, 0, 0, 3155, 0, 0, 3155
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'yuyang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 9, u.id, 'chenzhiyuan02', '2026-03-27', 'DEV_WORK_SUMMARY', 20, NULL, 'apsp-unified-operations-center-manage-rest', 0, '2026-03-27 17:41:27', '2026-03-27 17:41:27', 0, 0, 7, 654, 3, 0, 0, 0, 0, 0, 0, 0, 0, 654
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'chenzhiyuan02'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 10, u.id, 'kunlun zhang', '2026-03-27', 'DEV_WORK_SUMMARY', 47, NULL, 'AI Studio', 1, '2026-03-27 17:49:27', '2026-03-27 17:49:27', 1, 1, 81, 3256, 11, 11, 13, 11, 2, 527, 2976, 5, 0, 6759
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'kunlun zhang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 11, u.id, 'fanhailong01', '2026-03-27', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-fhl-center', 0, '2026-03-27 18:31:54', '2026-03-27 18:31:54', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanhailong01'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 12, u.id, 'xuchangpeng', '2026-03-27', 'DEV_WORK_SUMMARY', 11, NULL, 'unified-operation', 2, '2026-03-27 15:58:54', '2026-03-30 14:20:27', 0, 0, 20, 1450, 4, 0, 0, 0, 0, 0, 0, 0, 0, 1450
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'xuchangpeng'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 13, u.id, 'liuchong', '2026-03-30', 'DEV_WORK_SUMMARY', 0, NULL, 'monitor-alarm-center', 0, '2026-03-30 14:46:12', '2026-03-30 14:46:12', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'liuchong'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 15, u.id, 'yuyang', '2026-03-30', 'DEV_WORK_SUMMARY', 30, NULL, 'unified-operation-manage-web', 0, '2026-03-30 17:47:54', '2026-03-30 17:47:54', 0, 0, 0, 0, 0, 0, 4, 2, 2, 0, 673, 0, 0, 673
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'yuyang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 16, u.id, 'chenzhiyuan02', '2026-03-30', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operations-center-manage-rest', 0, '2026-03-30 17:53:05', '2026-03-30 17:53:05', 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'chenzhiyuan02'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 17, u.id, 'zhangdi', '2026-03-30', 'DEV_WORK_SUMMARY', 0, NULL, 'portal-web', 0, '2026-03-30 17:53:30', '2026-03-30 17:53:30', 0, 0, 0, 0, 0, 0, 7, 0, 7, 0, 846, 0, 0, 846
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 18, u.id, 'kunlun zhang', '2026-03-30', 'DEV_WORK_SUMMARY', 19, NULL, 'AI Studio', 5, '2026-03-30 17:57:35', '2026-03-31 18:25:42', 2, 4, 20, 396, 4, 1, 4, 4, 0, 800, 2096, 4, 0, 3312
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'kunlun zhang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 19, u.id, 'fanhailong01', '2026-03-30', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-fhl-center', 0, '2026-03-30 18:26:47', '2026-03-30 18:26:47', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanhailong01'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 27, u.id, 'yuyang', '2026-03-31', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web', 0, '2026-03-31 17:12:37', '2026-03-31 17:12:37', 0, 0, 0, 0, 0, 0, 8, 7, 1, 0, 2017, 0, 0, 2017
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'yuyang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 28, u.id, 'zhangzhaolun', '2026-03-31', 'DEV_WORK_SUMMARY', 6, NULL, 'unified-operation-center', 0, '2026-03-31 17:13:30', '2026-03-31 17:13:30', 0, 0, 11, 719, 1, 2, 0, 0, 0, 0, 0, 0, 0, 719
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangzhaolun'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 30, u.id, 'liuchong', '2026-03-31', 'DEV_WORK_SUMMARY', 6, NULL, 'monitor-alarm-center', 0, '2026-03-31 17:25:21', '2026-03-31 17:25:21', 0, 0, 3, 1222, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1222
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'liuchong'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 31, u.id, 'zhangdi', '2026-03-31', 'DEV_WORK_SUMMARY', 0, NULL, 'portal-web', 0, '2026-03-31 17:31:16', '2026-03-31 17:31:16', 0, 0, 0, 0, 0, 0, 7, 7, 0, 0, 761, 0, 0, 761
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 32, u.id, 'shichao', '2026-03-31', 'DEV_WORK_SUMMARY', 0, NULL, 'zhongshi-iip-basic-framework', 0, '2026-03-31 17:36:44', '2026-03-31 17:36:44', 0, 0, 6, 674, 0, 0, 0, 0, 0, 0, 0, 0, 0, 674
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 33, u.id, 'shichao', '2026-03-31', 'DEV_WORK_SUMMARY', 42, NULL, 'zhongshi-subscription-based-service', 0, '2026-03-31 17:40:48', '2026-03-31 17:40:48', 0, 0, 14, 1563, 1, 6, 0, 0, 0, 0, 0, 0, 0, 1563
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 34, u.id, 'fanqi', '2026-03-31', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation', 0, '2026-03-31 17:47:10', '2026-03-31 17:47:10', 0, 0, 11, 2541, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2541
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanqi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 35, u.id, 'fanhailong01', '2026-03-31', 'DEV_WORK_SUMMARY', 217, NULL, 'unified-operation-fhl-center', 2, '2026-03-31 18:01:05', '2026-03-31 18:01:05', 1, 8, 56, 17312, 7, 6, 0, 0, 0, 0, 0, 0, 0, 17312
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanhailong01'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 36, u.id, 'jianaili', '2026-03-31', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web-yunweipingtai', 1, '2026-03-31 18:11:49', '2026-03-31 18:11:49', 0, 1, 0, 0, 0, 0, 16, 1, 15, 2528, 4139, 0, 0, 6667
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'jianaili'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 37, u.id, 'kunlun zhang', '2026-03-31', 'DEV_WORK_SUMMARY', 7, NULL, 'AI Studio', 9, '2026-03-31 18:13:08', '2026-03-31 18:25:53', 0, 0, 7, 604, 2, 0, 5, 2, 3, 96, 708, 2, 0, 1408
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'kunlun zhang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 38, u.id, 'xuchangpeng', '2026-04-01', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation', 0, '2026-04-01 16:51:51', '2026-04-01 16:51:51', 0, 0, 1, 324, 1, 0, 0, 0, 0, 0, 0, 0, 0, 324
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'xuchangpeng'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 39, u.id, 'yuyang', '2026-04-01', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web', 1, '2026-04-01 17:17:55', '2026-04-01 17:17:55', 0, 0, 0, 0, 0, 0, 8, 6, 2, 0, 4104, 0, 0, 4104
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'yuyang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 40, u.id, 'liuchong', '2026-04-01', 'DEV_WORK_SUMMARY', 6, NULL, 'monitor-alarm-center', 0, '2026-04-01 17:26:20', '2026-04-01 17:26:20', 0, 0, 3, 1271, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1271
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'liuchong'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 41, u.id, 'yuyang', '2026-04-01', 'DEV_WORK_SUMMARY', 0, NULL, 'monitor-alarm-center-web', 0, '2026-04-01 17:32:06', '2026-04-01 17:32:06', 0, 0, 0, 0, 0, 0, 11, 11, 0, 0, 2376, 0, 0, 2376
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'yuyang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 42, u.id, 'zhangzhaolun', '2026-04-01', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-center', 0, '2026-04-01 17:38:42', '2026-04-01 17:38:42', 0, 1, 12, 1066, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1066
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangzhaolun'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 43, u.id, 'chenzhiyuan02', '2026-04-01', 'DEV_WORK_SUMMARY', 56, NULL, 'unified-operations-center-manage-rest', 0, '2026-04-01 17:43:09', '2026-04-01 17:43:09', 0, 0, 14, 1633, 7, 0, 0, 0, 0, 0, 0, 0, 0, 1633
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'chenzhiyuan02'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 44, u.id, 'fanqi', '2026-04-01', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation', 0, '2026-04-01 17:43:23', '2026-04-01 17:43:23', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanqi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 45, u.id, 'fanhailong01', '2026-04-01', 'DEV_WORK_SUMMARY', 261, NULL, 'unified-operation-fhl-center', 0, '2026-04-01 17:44:19', '2026-04-01 17:44:19', 0, 0, 91, 14777, 26, 5, 0, 0, 0, 0, 0, 3, 0, 14777
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanhailong01'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 46, u.id, 'zhangdi', '2026-04-01', 'DEV_WORK_SUMMARY', 0, NULL, 'market-web', 0, '2026-04-01 17:44:49', '2026-04-01 17:44:49', 0, 0, 0, 0, 0, 0, 3, 0, 3, 1821, 1821, 0, 0, 3642
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 47, u.id, 'jianaili', '2026-04-01', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web-yunweipingtai', 0, '2026-04-01 17:45:30', '2026-04-01 17:45:30', 0, 0, 0, 0, 0, 0, 2, 1, 1, 0, 415, 0, 0, 415
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'jianaili'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 48, u.id, 'shichao', '2026-04-01', 'DEV_WORK_SUMMARY', 102, NULL, 'zhongshi-iip-basic-framework', 0, '2026-04-01 17:54:00', '2026-04-01 17:54:00', 0, 0, 42, 3023, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3023
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 49, u.id, 'shichao', '2026-04-01', 'DEV_WORK_SUMMARY', 14, NULL, 'zhongshi-enterprises-and-service-providers', 0, '2026-04-01 17:57:34', '2026-04-01 17:57:34', 0, 0, 8, 873, 2, 2, 0, 0, 0, 0, 0, 0, 0, 873
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 50, u.id, 'chenzhiyuan02', '2026-04-02', 'DEV_WORK_SUMMARY', 1, NULL, 'enterprises-and-service-providers', 0, '2026-04-02 16:15:45', '2026-04-02 16:15:45', 0, 0, 4, 26, 1, 0, 0, 0, 0, 0, 0, 0, 0, 26
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'chenzhiyuan02'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 51, u.id, 'yuyang', '2026-04-02', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web', 0, '2026-04-02 17:20:14', '2026-04-02 17:20:14', 0, 0, 0, 0, 0, 0, 5, 1, 4, 0, 4143, 0, 0, 4143
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'yuyang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 52, u.id, 'xuchangpeng', '2026-04-02', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation', 0, '2026-04-02 17:33:06', '2026-04-02 17:33:06', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'xuchangpeng'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 53, u.id, 'liuchong', '2026-04-02', 'DEV_WORK_SUMMARY', 3, NULL, 'monitor-alarm-center', 0, '2026-04-02 17:34:24', '2026-04-02 17:34:24', 0, 0, 1, 25, 0, 0, 0, 0, 0, 0, 0, 0, 0, 25
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'liuchong'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 54, u.id, 'zhangdi', '2026-04-02', 'DEV_WORK_SUMMARY', 0, NULL, 'market-web', 0, '2026-04-02 17:35:11', '2026-04-02 17:35:11', 0, 0, 0, 0, 0, 0, 5, 5, 0, 298, 1477, 0, 0, 1775
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 55, u.id, 'shichao', '2026-04-02', 'DEV_WORK_SUMMARY', 99, NULL, 'zhongshi-uoc-manage-rest', 0, '2026-04-02 17:46:36', '2026-04-02 17:46:36', 0, 0, 26, 2471, 13, 0, 0, 0, 0, 0, 0, 0, 0, 2471
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 56, u.id, 'kunlun zhang', '2026-04-02', 'DEV_WORK_SUMMARY', 0, NULL, 'AI Studio', 0, '2026-04-02 17:46:47', '2026-04-02 17:46:47', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'kunlun zhang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 57, u.id, 'jianaili', '2026-04-02', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web-yunweipingtai', 0, '2026-04-02 17:49:00', '2026-04-02 17:49:00', 0, 0, 0, 0, 0, 0, 4, 2, 2, 0, 2066, 0, 0, 2066
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'jianaili'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 58, u.id, 'fanqi', '2026-04-02', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation', 0, '2026-04-02 17:50:48', '2026-04-02 17:50:48', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanqi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 59, u.id, 'fanhailong01', '2026-04-02', 'DEV_WORK_SUMMARY', 54, NULL, 'unified-operation-fhl-center', 0, '2026-04-02 17:54:10', '2026-04-02 17:54:10', 0, 0, 45, 5561, 10, 3, 0, 0, 0, 0, 0, 0, 0, 5561
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanhailong01'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 60, u.id, 'xuchangpeng', '2026-04-03', 'DEV_WORK_SUMMARY', 12, NULL, 'unified-operation', 0, '2026-04-03 17:25:20', '2026-04-03 17:25:20', 0, 0, 2, 670, 0, 0, 0, 0, 0, 0, 0, 0, 1, 670
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'xuchangpeng'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 61, u.id, 'yuyang', '2026-04-03', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web', 0, '2026-04-03 17:26:48', '2026-04-03 17:26:48', 0, 0, 0, 0, 0, 0, 10, 4, 6, 0, 5183, 0, 0, 5183
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'yuyang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 62, u.id, 'zhangdi', '2026-04-03', 'DEV_WORK_SUMMARY', 0, NULL, 'portal-web', 0, '2026-04-03 17:35:41', '2026-04-03 17:35:41', 0, 0, 0, 0, 0, 0, 4, 0, 4, 0, 1153, 0, 0, 1153
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 63, u.id, 'shichao', '2026-04-03', 'DEV_WORK_SUMMARY', 16, NULL, 'zhongshi-enterprises-and-service-providers', 0, '2026-04-03 17:45:25', '2026-04-03 17:45:25', 0, 0, 9, 1295, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1295
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 64, u.id, 'fanqi', '2026-04-03', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation', 0, '2026-04-03 17:47:55', '2026-04-03 17:47:55', 0, 0, 1, 1359, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1359
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanqi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 65, u.id, 'jianaili', '2026-04-03', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web-yunweipingtai', 0, '2026-04-03 17:50:49', '2026-04-03 17:50:49', 0, 0, 0, 0, 0, 0, 2, 1, 1, 1326, 1163, 0, 0, 2489
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'jianaili'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 66, u.id, 'chenzhiyuan02', '2026-04-03', 'DEV_WORK_SUMMARY', 1, NULL, 'apsp-enterprises-and-service-providers', 0, '2026-04-03 17:52:20', '2026-04-03 17:52:20', 0, 0, 6, 680, 1, 3, 0, 0, 0, 0, 0, 1, 0, 680
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'chenzhiyuan02'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 67, u.id, 'fanhailong01', '2026-04-03', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-fhl-center', 0, '2026-04-03 18:07:57', '2026-04-03 18:07:57', 0, 0, 8, 4128, 2, 0, 0, 0, 0, 0, 0, 0, 0, 4128
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanhailong01'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 68, u.id, 'fanqi', '2026-04-07', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation', 0, '2026-04-07 17:26:10', '2026-04-07 17:26:10', 0, 0, 1, 1480, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1480
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanqi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 69, u.id, 'yuyang', '2026-04-07', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web', 0, '2026-04-07 17:30:05', '2026-04-07 17:30:05', 0, 0, 0, 0, 0, 0, 12, 5, 7, 517, 5139, 0, 0, 5656
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'yuyang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 70, u.id, 'shichao', '2026-04-07', 'DEV_WORK_SUMMARY', 22, NULL, 'zhongshi-enterprises-and-service-providers', 0, '2026-04-07 17:42:17', '2026-04-07 17:42:17', 0, 0, 12, 1627, 2, 3, 0, 0, 0, 0, 0, 0, 0, 1627
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 71, u.id, 'zhangdi', '2026-04-07', 'DEV_WORK_SUMMARY', 0, NULL, 'portal-web', 0, '2026-04-07 17:43:26', '2026-04-07 17:43:26', 0, 0, 0, 0, 0, 0, 6, 0, 6, 630, 2616, 0, 0, 3246
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 72, u.id, 'zhangzhaolun', '2026-04-07', 'DEV_WORK_SUMMARY', 9, NULL, 'unified-operation-center20260401', 0, '2026-04-07 17:47:41', '2026-04-07 17:47:41', 0, 0, 6, 930, 1, 0, 0, 0, 0, 0, 0, 0, 0, 930
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangzhaolun'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 73, u.id, 'jianaili', '2026-04-07', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web-yunweipingtai', 0, '2026-04-07 17:50:51', '2026-04-07 17:50:51', 0, 0, 0, 0, 0, 0, 3, 1, 2, 1406, 2560, 0, 0, 3966
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'jianaili'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 74, u.id, 'liuchong', '2026-04-07', 'DEV_WORK_SUMMARY', 16, NULL, 'monitor-alarm-center', 0, '2026-04-07 17:51:32', '2026-04-07 17:51:32', 0, 0, 20, 5883, 8, 0, 0, 0, 0, 0, 0, 0, 0, 5883
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'liuchong'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 75, u.id, 'shichao', '2026-04-07', 'DEV_WORK_SUMMARY', 17, NULL, 'zhongshi-uoc-manage-rest', 0, '2026-04-07 17:53:18', '2026-04-07 17:53:18', 0, 0, 4, 629, 2, 0, 0, 0, 0, 0, 0, 0, 0, 629
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 76, u.id, 'chenzhiyuan02', '2026-04-07', 'DEV_WORK_SUMMARY', 3, NULL, 'dictionary', 0, '2026-04-07 17:57:41', '2026-04-07 17:57:41', 0, 0, 3, 291, 1, 0, 0, 0, 0, 0, 0, 0, 0, 291
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'chenzhiyuan02'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 77, u.id, 'chenzhiyuan02', '2026-04-07', 'DEV_WORK_SUMMARY', 21, NULL, 'supply-demand-resources', 0, '2026-04-07 18:07:48', '2026-04-07 18:07:48', 0, 0, 14, 1191, 3, 4, 0, 0, 0, 0, 0, 3, 0, 1191
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'chenzhiyuan02'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 78, u.id, 'zhangdi', '2026-04-08', 'DEV_WORK_SUMMARY', 0, NULL, 'portal-web', 0, '2026-04-08 17:09:11', '2026-04-08 17:09:11', 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 166, 0, 0, 166
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 79, u.id, 'yuyang', '2026-04-08', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web', 0, '2026-04-08 17:16:06', '2026-04-08 17:16:06', 0, 4, 0, 0, 0, 0, 35, 20, 15, 847, 10533, 0, 0, 11380
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'yuyang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 80, u.id, 'zhangdi', '2026-04-08', 'DEV_WORK_SUMMARY', 0, NULL, 'market-web', 0, '2026-04-08 17:32:07', '2026-04-08 17:32:07', 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 109, 0, 0, 109
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 81, u.id, 'shichao', '2026-04-08', 'DEV_WORK_SUMMARY', 18, NULL, 'zhongshi-information-collection-feedback', 0, '2026-04-08 18:34:15', '2026-04-08 18:34:15', 0, 0, 6, 596, 0, 0, 0, 0, 0, 0, 0, 0, 0, 596
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 82, u.id, 'shichao', '2026-04-08', 'DEV_WORK_SUMMARY', 18, NULL, 'zhongshi-iip-basic-framework', 0, '2026-04-08 18:35:21', '2026-04-08 18:35:21', 0, 0, 11, 885, 0, 0, 0, 0, 0, 0, 0, 0, 0, 885
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 83, u.id, 'shichao', '2026-04-08', 'DEV_WORK_SUMMARY', 28, NULL, 'zhongshi-uoc-manage-rest', 0, '2026-04-08 18:36:09', '2026-04-08 18:36:09', 0, 0, 9, 1124, 4, 0, 0, 0, 0, 0, 0, 0, 0, 1124
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 84, u.id, 'jianaili', '2026-04-08', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web-yunweipingtai', 0, '2026-04-08 20:46:33', '2026-04-08 20:46:33', 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 2204, 0, 0, 2204
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'jianaili'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 85, u.id, 'yuyang', '2026-04-09', 'DEV_WORK_SUMMARY', 0, NULL, 'monitor-alarm-center-web', 0, '2026-04-09 17:06:03', '2026-04-09 17:06:03', 0, 0, 0, 0, 0, 0, 3, 3, 0, 0, 546, 0, 0, 546
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'yuyang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 86, u.id, 'zhangdi', '2026-04-09', 'DEV_WORK_SUMMARY', 0, NULL, 'portal-web', 0, '2026-04-09 17:13:34', '2026-04-09 17:13:34', 0, 0, 0, 0, 0, 0, 5, 0, 5, 0, 94, 0, 0, 94
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 87, u.id, 'jianaili', '2026-04-09', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web-yunweipingtai', 0, '2026-04-09 17:15:01', '2026-04-09 17:15:01', 0, 0, 0, 0, 0, 0, 3, 2, 1, 0, 2542, 0, 0, 2542
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'jianaili'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 88, u.id, 'yuyang', '2026-04-09', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web', 0, '2026-04-09 17:18:38', '2026-04-09 17:18:38', 0, 2, 0, 0, 0, 0, 8, 2, 6, 0, 2605, 0, 0, 2605
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'yuyang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 89, u.id, 'zhangdi', '2026-04-09', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operations-center-manage-web', 0, '2026-04-09 17:28:24', '2026-04-09 17:28:24', 0, 0, 0, 0, 0, 0, 6, 6, 0, 551, 1581, 0, 0, 2132
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 90, u.id, 'xuchangpeng', '2026-04-09', 'DEV_WORK_SUMMARY', 0, NULL, 'industrial-third-login-adapter', 0, '2026-04-09 17:29:37', '2026-04-09 17:29:37', 0, 0, 7, 605, 2, 0, 0, 0, 0, 0, 0, 0, 0, 605
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'xuchangpeng'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 91, u.id, 'zhangdi', '2026-04-09', 'DEV_WORK_SUMMARY', 0, NULL, 'market-web', 0, '2026-04-09 17:37:07', '2026-04-09 17:37:07', 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 200, 0, 0, 200
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 92, u.id, 'shichao', '2026-04-09', 'DEV_WORK_SUMMARY', 3, NULL, 'zhongshi-uoc-manage-rest', 0, '2026-04-09 17:40:06', '2026-04-09 17:40:06', 0, 0, 13, 145, 9, 0, 0, 0, 0, 0, 0, 0, 0, 145
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 93, u.id, 'liuchong', '2026-04-09', 'DEV_WORK_SUMMARY', 12, NULL, 'monitor-alarm-center', 0, '2026-04-09 17:46:05', '2026-04-09 17:46:05', 0, 0, 10, 3624, 2, 3, 0, 0, 0, 0, 0, 0, 0, 3624
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'liuchong'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 94, u.id, 'shichao', '2026-04-10', 'DEV_WORK_SUMMARY', 21, NULL, 'zhongshi-subscription-based-service', 0, '2026-04-10 15:20:09', '2026-04-10 15:20:09', 0, 0, 6, 678, 0, 3, 0, 0, 0, 0, 0, 0, 0, 678
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 95, u.id, 'shichao', '2026-04-10', 'DEV_WORK_SUMMARY', 36, NULL, 'zhongshi-iip-basic-framework', 0, '2026-04-10 15:29:51', '2026-04-10 15:29:51', 0, 0, 12, 779, 0, 0, 0, 0, 0, 0, 0, 0, 0, 779
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 96, u.id, 'jianaili', '2026-04-10', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web-yunweipingtai', 0, '2026-04-10 16:14:14', '2026-04-10 16:14:14', 0, 0, 0, 0, 0, 0, 5, 2, 3, 217, 3714, 0, 0, 3931
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'jianaili'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 97, u.id, 'shichao', '2026-04-10', 'DEV_WORK_SUMMARY', 126, NULL, 'zhongshi-uoc-manage-rest', 0, '2026-04-10 16:27:33', '2026-04-10 16:27:33', 0, 0, 18, 1734, 9, 0, 0, 0, 0, 0, 0, 0, 0, 1734
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 98, u.id, 'yuyang', '2026-04-10', 'DEV_WORK_SUMMARY', 73, NULL, 'unified-operation-manage-web', 0, '2026-04-10 17:33:57', '2026-04-10 17:33:57', 0, 0, 0, 0, 0, 0, 14, 8, 6, 677, 6773, 0, 0, 7450
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'yuyang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 99, u.id, 'zhangdi', '2026-04-10', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operations-center-manage-web', 0, '2026-04-10 17:56:01', '2026-04-10 17:56:01', 0, 0, 0, 0, 0, 0, 3, 2, 1, 47, 933, 0, 0, 980
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 100, u.id, 'yuyang', '2026-04-13', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web', 0, '2026-04-13 17:34:11', '2026-04-13 17:34:11', 0, 0, 0, 0, 0, 0, 46, 43, 3, 852, 13028, 0, 0, 13880
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'yuyang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 101, u.id, 'zhangdi', '2026-04-13', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operations-center-manage-web', 0, '2026-04-13 17:36:58', '2026-04-13 17:36:58', 0, 0, 0, 0, 0, 0, 10, 8, 2, 941, 3399, 0, 0, 4340
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 102, u.id, 'jianaili', '2026-04-13', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web-yunweipingtai', 0, '2026-04-13 17:47:44', '2026-04-13 17:47:44', 0, 0, 0, 0, 0, 0, 3, 0, 3, 0, 1906, 0, 0, 1906
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'jianaili'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 103, u.id, 'fanqi', '2026-04-13', 'DEV_WORK_SUMMARY', 44, NULL, 'zsjd-idm', 0, '2026-04-13 17:52:56', '2026-04-13 17:52:56', 0, 0, 1, 440, 0, 0, 0, 0, 0, 0, 0, 0, 0, 440
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanqi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 104, u.id, 'fanhailong01', '2026-04-13', 'DEV_WORK_SUMMARY', 237, NULL, 'unified-operation-fhl-center', 0, '2026-04-13 17:54:24', '2026-04-13 17:54:24', 0, 4, 72, 12084, 7, 6, 0, 0, 0, 0, 0, 0, 0, 12084
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanhailong01'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 105, u.id, 'chenzhiyuan02', '2026-04-13', 'DEV_WORK_SUMMARY', 4, NULL, 'enterprises-and-service-providers', 0, '2026-04-13 18:02:58', '2026-04-13 18:02:58', 0, 0, 4, 658, 1, 0, 0, 0, 0, 0, 0, 0, 0, 658
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'chenzhiyuan02'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 106, u.id, 'zhangzhaolun', '2026-04-13', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-center20260401', 0, '2026-04-13 18:30:56', '2026-04-13 18:30:56', 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangzhaolun'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 107, u.id, 'chenzhiyuan02', '2026-04-13', 'DEV_WORK_SUMMARY', 4, NULL, 'apsp-dictionary', 0, '2026-04-13 18:45:35', '2026-04-13 18:45:35', 0, 0, 4, 420, 1, 0, 0, 0, 0, 0, 0, 0, 0, 420
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'chenzhiyuan02'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 108, u.id, 'yuyang', '2026-04-14', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web', 0, '2026-04-14 17:14:46', '2026-04-14 17:14:46', 0, 0, 0, 0, 0, 0, 8, 4, 4, 492, 1555, 0, 0, 2047
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'yuyang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 109, u.id, 'zhangzhaolun', '2026-04-14', 'DEV_WORK_SUMMARY', 1, NULL, 'unified-operation', 0, '2026-04-14 17:18:29', '2026-04-14 17:18:29', 0, 0, 10, 220, 2, 0, 0, 0, 0, 0, 0, 0, 0, 220
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangzhaolun'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 110, u.id, 'jianaili', '2026-04-14', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web-yunweipingtai', 0, '2026-04-14 17:28:58', '2026-04-14 17:28:58', 0, 0, 0, 0, 0, 0, 4, 4, 0, 263, 2573, 0, 0, 2836
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'jianaili'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 111, u.id, 'zhangdi', '2026-04-14', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operations-center-manage-web', 0, '2026-04-14 17:36:19', '2026-04-14 17:36:19', 0, 0, 0, 0, 0, 0, 5, 2, 3, 0, 1190, 0, 0, 1190
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 112, u.id, 'shichao', '2026-04-14', 'DEV_WORK_SUMMARY', 1, NULL, 'zhongshi-portal-rest', 0, '2026-04-14 17:36:51', '2026-04-14 17:36:51', 0, 0, 22, 1232, 10, 0, 0, 0, 0, 0, 0, 0, 0, 1232
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 113, u.id, 'fanqi', '2026-04-14', 'DEV_WORK_SUMMARY', 0, NULL, 'zsjd-idm', 0, '2026-04-14 17:41:28', '2026-04-14 17:41:28', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanqi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 114, u.id, 'chenzhiyuan02', '2026-04-14', 'DEV_WORK_SUMMARY', 0, NULL, 'enterprises-and-service-providers', 0, '2026-04-14 17:58:47', '2026-04-14 17:58:47', 0, 0, 4, 844, 2, 0, 0, 0, 0, 0, 0, 0, 0, 844
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'chenzhiyuan02'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 115, u.id, 'shichao', '2026-04-15', 'DEV_WORK_SUMMARY', 32, NULL, 'zhongshi-portal-rest', 0, '2026-04-15 17:04:04', '2026-04-15 17:04:04', 0, 0, 29, 2757, 10, 0, 0, 0, 0, 0, 0, 0, 0, 2757
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 116, u.id, 'fanqi', '2026-04-15', 'DEV_WORK_SUMMARY', 0, NULL, 'zsjd-idm', 0, '2026-04-15 17:18:58', '2026-04-15 17:18:58', 0, 0, 2, 124, 0, 0, 0, 0, 0, 0, 0, 0, 0, 124
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanqi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 117, u.id, 'yuyang', '2026-04-15', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web', 0, '2026-04-15 17:22:50', '2026-04-15 17:22:50', 0, 0, 0, 0, 0, 0, 1, 0, 1, 458, 458, 0, 0, 916
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'yuyang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 118, u.id, 'liuchong', '2026-04-15', 'DEV_WORK_SUMMARY', 0, NULL, 'feature-monitor-center', 0, '2026-04-15 17:30:41', '2026-04-15 17:30:41', 0, 0, 1, 150, 0, 0, 0, 0, 0, 0, 0, 0, 0, 150
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'liuchong'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 119, u.id, 'jianaili', '2026-04-15', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web-yunweipingtai', 0, '2026-04-15 17:35:21', '2026-04-15 17:35:21', 0, 0, 0, 0, 0, 0, 4, 4, 0, 338, 2777, 0, 0, 3115
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'jianaili'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 120, u.id, 'zhangdi', '2026-04-15', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operations-center-manage-web', 0, '2026-04-15 17:38:34', '2026-04-15 17:38:34', 0, 0, 0, 0, 0, 0, 2, 2, 0, 499, 782, 0, 0, 1281
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 121, u.id, 'zhangzhaolun', '2026-04-15', 'DEV_WORK_SUMMARY', 8, NULL, 'unified-operation', 0, '2026-04-15 17:45:35', '2026-04-15 17:45:35', 0, 0, 22, 2800, 2, 4, 0, 0, 0, 0, 0, 0, 0, 2800
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangzhaolun'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 122, u.id, 'zhangdi', '2026-04-16', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operations-center-manage-web', 0, '2026-04-16 14:51:41', '2026-04-16 14:51:41', 0, 0, 0, 0, 0, 0, 7, 7, 0, 2, 505, 0, 0, 507
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 123, u.id, 'shichao', '2026-04-16', 'DEV_WORK_SUMMARY', 14, NULL, 'zhongshi-market-portal-rest', 0, '2026-04-16 16:54:40', '2026-04-16 16:54:40', 0, 0, 15, 1259, 7, 0, 0, 0, 0, 0, 0, 0, 0, 1259
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 124, u.id, 'shichao', '2026-04-16', 'DEV_WORK_SUMMARY', 39, NULL, 'zhongshi-portal-rest', 0, '2026-04-16 17:04:39', '2026-04-16 17:04:39', 0, 0, 39, 3549, 16, 7, 0, 0, 0, 0, 0, 0, 0, 3549
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 125, u.id, 'yuyang', '2026-04-16', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web', 0, '2026-04-16 17:26:17', '2026-04-16 17:26:17', 0, 0, 0, 0, 0, 0, 3, 1, 2, 0, 820, 0, 0, 820
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'yuyang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 126, u.id, 'liuchong', '2026-04-16', 'DEV_WORK_SUMMARY', 1, NULL, 'feature-monitor-center', 0, '2026-04-16 17:28:05', '2026-04-16 17:28:05', 0, 0, 3, 1380, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1380
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'liuchong'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 127, u.id, 'zhangdi', '2026-04-16', 'DEV_WORK_SUMMARY', 0, NULL, 'portal-web', 0, '2026-04-16 17:33:49', '2026-04-16 17:33:49', 0, 0, 0, 0, 0, 0, 11, 3, 8, 750, 1966, 0, 0, 2716
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 128, u.id, 'zhangzhaolun', '2026-04-16', 'DEV_WORK_SUMMARY', 58, NULL, 'unified-operation-summary', 0, '2026-04-16 17:46:21', '2026-04-16 17:46:21', 0, 0, 174, 15665, 16, 2, 0, 0, 0, 0, 0, 0, 0, 15665
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangzhaolun'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 129, u.id, 'jianaili', '2026-04-16', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web-yunweipingtai', 0, '2026-04-16 17:59:16', '2026-04-16 17:59:16', 0, 0, 0, 0, 0, 0, 5, 5, 0, 340, 4243, 0, 0, 4583
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'jianaili'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 130, u.id, 'fanqi', '2026-04-16', 'DEV_WORK_SUMMARY', 0, NULL, 'zsjd-idm', 0, '2026-04-16 19:16:40', '2026-04-16 19:16:40', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanqi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 131, u.id, 'zhangdi', '2026-04-17', 'DEV_WORK_SUMMARY', 0, NULL, 'portal-web', 0, '2026-04-17 17:33:09', '2026-04-17 17:33:09', 0, 0, 0, 0, 0, 0, 12, 0, 12, 651, 2514, 0, 0, 3165
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 132, u.id, 'yuyang', '2026-04-17', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web', 0, '2026-04-17 17:37:21', '2026-04-17 17:37:21', 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 703, 0, 0, 703
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'yuyang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 133, u.id, 'fanqi', '2026-04-17', 'DEV_WORK_SUMMARY', 0, NULL, 'zsjd-idm', 0, '2026-04-17 17:43:43', '2026-04-17 17:43:43', 0, 0, 1, 58, 0, 0, 0, 0, 0, 0, 0, 0, 0, 58
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanqi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 134, u.id, 'zhangzhaolun', '2026-04-17', 'DEV_WORK_SUMMARY', 42, NULL, 'unified-operation-dev', 0, '2026-04-17 17:59:54', '2026-04-17 17:59:54', 0, 0, 23, 3737, 2, 4, 0, 0, 0, 0, 0, 0, 0, 3737
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangzhaolun'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 135, u.id, 'liuchong', '2026-04-17', 'DEV_WORK_SUMMARY', 1, NULL, 'feature-monitor-center', 0, '2026-04-17 18:02:41', '2026-04-17 18:02:41', 0, 0, 2, 1225, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1225
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'liuchong'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 136, u.id, 'shichao', '2026-04-17', 'DEV_WORK_SUMMARY', 1, NULL, 'zhongshi-market-portal-rest', 0, '2026-04-17 18:14:01', '2026-04-17 18:14:01', 0, 0, 2, 270, 1, 0, 0, 0, 0, 0, 0, 0, 0, 270
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 137, u.id, 'shichao', '2026-04-17', 'DEV_WORK_SUMMARY', 40, NULL, 'zhongshi-unified-configuration', 0, '2026-04-17 18:14:58', '2026-04-17 18:14:58', 0, 0, 16, 1767, 3, 5, 0, 0, 0, 0, 0, 0, 0, 1767
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 138, u.id, 'shichao', '2026-04-20', 'DEV_WORK_SUMMARY', 17, NULL, 'zhongshi-enterprises-and-service-providers', 0, '2026-04-20 16:05:13', '2026-04-20 16:05:13', 0, 0, 2, 358, 0, 0, 0, 0, 0, 0, 0, 1, 0, 358
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 139, u.id, 'shichao', '2026-04-20', 'DEV_WORK_SUMMARY', 8, NULL, 'zhongshi-unified-configuration', 0, '2026-04-20 16:14:34', '2026-04-20 16:14:34', 0, 0, 8, 60, 0, 0, 0, 0, 0, 0, 0, 0, 0, 60
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 140, u.id, 'zhangzhaolun', '2026-04-20', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-dev', 0, '2026-04-20 17:34:38', '2026-04-20 17:34:38', 0, 0, 4, 436, 1, 1, 0, 0, 0, 0, 0, 0, 0, 436
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangzhaolun'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 141, u.id, 'yuyang', '2026-04-20', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web', 0, '2026-04-20 17:37:36', '2026-04-20 17:37:36', 0, 0, 0, 0, 0, 0, 4, 3, 1, 0, 1246, 0, 0, 1246
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'yuyang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 142, u.id, 'zhangdi', '2026-04-20', 'DEV_WORK_SUMMARY', 0, NULL, 'portal-web', 0, '2026-04-20 17:38:53', '2026-04-20 17:38:53', 0, 0, 0, 0, 0, 0, 14, 1, 13, 48, 2294, 0, 0, 2342
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 143, u.id, 'kunlun zhang', '2026-04-20', 'DEV_WORK_SUMMARY', 7, NULL, 'ai-studio', 1, '2026-04-20 18:00:26', '2026-04-20 18:00:26', 0, 0, 9, 593, 1, 1, 13, 10, 3, 1701, 3565, 1, 0, 5859
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'kunlun zhang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 144, u.id, 'jianaili', '2026-04-20', 'DEV_WORK_SUMMARY', 0, NULL, 'project-web', 0, '2026-04-20 18:20:27', '2026-04-20 18:20:27', 0, 0, 0, 0, 0, 0, 3, 1, 2, 0, 363, 0, 0, 363
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'jianaili'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 145, u.id, 'shichao', '2026-04-20', 'DEV_WORK_SUMMARY', 1, NULL, 'zhongshi-portal-rest', 0, '2026-04-20 18:25:47', '2026-04-20 18:25:47', 0, 0, 2, 329, 1, 0, 0, 0, 0, 0, 0, 0, 0, 329
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 146, u.id, 'fanqi', '2026-04-20', 'DEV_WORK_SUMMARY', 1, NULL, 'zsjd-idm', 0, '2026-04-20 20:22:08', '2026-04-20 20:22:08', 0, 0, 1, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanqi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 147, u.id, 'chenzhiyuan02', '2026-04-20', 'DEV_WORK_SUMMARY', 42, NULL, 'apsp-enterprise-and-provider-rest', 0, '2026-04-21 15:12:55', '2026-04-21 15:12:55', 0, 0, 15, 2861, 8, 0, 0, 0, 0, 0, 0, 0, 0, 2861
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'chenzhiyuan02'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 148, u.id, 'liuchong', '2026-04-21', 'DEV_WORK_SUMMARY', 0, NULL, 'monitor-alarm-center', 0, '2026-04-21 16:33:34', '2026-04-21 16:33:34', 0, 0, 3, 2340, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2340
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'liuchong'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 149, u.id, 'shichao', '2026-04-21', 'DEV_WORK_SUMMARY', 2, NULL, 'zhongshi-portal-rest', 0, '2026-04-21 17:19:54', '2026-04-21 17:19:54', 0, 0, 4, 526, 1, 0, 0, 0, 0, 0, 0, 0, 0, 526
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 150, u.id, 'fanqi', '2026-04-21', 'DEV_WORK_SUMMARY', 0, NULL, 'zsjd-idm', 0, '2026-04-21 17:23:34', '2026-04-21 17:23:34', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanqi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 151, u.id, 'zhangdi', '2026-04-21', 'DEV_WORK_SUMMARY', 0, NULL, 'portal-web', 0, '2026-04-21 17:25:32', '2026-04-21 17:25:32', 0, 0, 0, 0, 0, 0, 18, 5, 13, 139, 3370, 0, 0, 3509
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 152, u.id, 'zhangzhaolun', '2026-04-21', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-dev', 0, '2026-04-21 17:26:49', '2026-04-21 17:26:49', 0, 0, 2, 1321, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1321
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangzhaolun'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 153, u.id, 'jianaili', '2026-04-21', 'DEV_WORK_SUMMARY', 0, NULL, 'project-web', 0, '2026-04-21 17:30:33', '2026-04-21 17:30:33', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'jianaili'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 154, u.id, 'shichao', '2026-04-22', 'DEV_WORK_SUMMARY', 37, NULL, 'zhongshi-portal-rest', 0, '2026-04-22 17:01:26', '2026-04-22 17:01:26', 0, 0, 20, 3085, 9, 0, 0, 0, 0, 0, 0, 0, 0, 3085
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 155, u.id, 'yuyang', '2026-04-22', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-edge-web', 0, '2026-04-22 17:15:15', '2026-04-22 17:15:15', 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 200, 0, 0, 200
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'yuyang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 156, u.id, 'liuchong', '2026-04-22', 'DEV_WORK_SUMMARY', 0, NULL, 'monitor-alarm-center', 0, '2026-04-22 17:23:01', '2026-04-22 17:23:01', 0, 0, 1, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 100
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'liuchong'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 157, u.id, 'jianaili', '2026-04-22', 'DEV_WORK_SUMMARY', 0, NULL, 'workbench', 0, '2026-04-22 17:33:37', '2026-04-22 17:33:37', 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 2702, 0, 0, 2702
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'jianaili'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 158, u.id, 'zhangzhaolun', '2026-04-22', 'DEV_WORK_SUMMARY', 17, NULL, 'unified-operation-dev', 0, '2026-04-22 17:34:00', '2026-04-22 17:34:00', 0, 0, 1, 188, 0, 0, 0, 0, 0, 0, 0, 0, 0, 188
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangzhaolun'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 159, u.id, 'chenzhiyuan02', '2026-04-22', 'DEV_WORK_SUMMARY', 69, NULL, 'apsp-enterprise-and-provider-rest', 0, '2026-04-22 19:07:47', '2026-04-22 19:07:47', 0, 0, 22, 5214, 10, 0, 0, 0, 0, 0, 0, 0, 0, 5214
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'chenzhiyuan02'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 160, u.id, 'chenzhiyuan02', '2026-04-22', 'DEV_WORK_SUMMARY', 1, NULL, 'information-collection-and-feedback', 0, '2026-04-22 19:08:28', '2026-04-22 19:08:28', 0, 0, 7, 559, 5, 0, 0, 0, 0, 0, 0, 0, 0, 559
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'chenzhiyuan02'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 161, u.id, 'shichao', '2026-04-23', 'DEV_WORK_SUMMARY', 9, NULL, 'zhongshi-market-portal-rest', 0, '2026-04-23 16:49:14', '2026-04-23 16:49:14', 0, 0, 5, 751, 2, 0, 0, 0, 0, 0, 0, 0, 0, 751
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 162, u.id, 'shichao', '2026-04-23', 'DEV_WORK_SUMMARY', 21, NULL, 'zhongshi-supply-demand-resources', 0, '2026-04-23 17:28:21', '2026-04-23 17:28:21', 0, 0, 32, 5316, 8, 0, 0, 0, 0, 0, 0, 0, 0, 5316
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 163, u.id, 'fanqi', '2026-04-23', 'DEV_WORK_SUMMARY', 10, NULL, 'zsjd-idm', 0, '2026-04-23 17:39:32', '2026-04-23 17:39:32', 0, 0, 1, 186, 0, 0, 0, 0, 0, 0, 0, 0, 0, 186
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanqi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 164, u.id, 'zhangzhaolun', '2026-04-23', 'DEV_WORK_SUMMARY', 3, NULL, 'unified-operation-dev', 0, '2026-04-23 17:56:32', '2026-04-23 17:56:32', 0, 1, 47, 6233, 5, 5, 0, 0, 0, 0, 0, 0, 0, 6233
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangzhaolun'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 165, u.id, 'zhangdi', '2026-04-23', 'DEV_WORK_SUMMARY', 0, NULL, 'portal-web', 0, '2026-04-23 17:59:51', '2026-04-23 17:59:51', 0, 0, 0, 0, 0, 0, 20, 6, 14, 1626, 3599, 0, 0, 5225
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 166, u.id, 'chenzhiyuan02', '2026-04-23', 'DEV_WORK_SUMMARY', 1, NULL, 'enterprise-and-provider-rest', 0, '2026-04-23 18:49:10', '2026-04-23 18:49:10', 0, 0, 3, 207, 0, 0, 0, 0, 0, 0, 0, 0, 0, 207
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'chenzhiyuan02'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 167, u.id, 'shichao', '2026-04-24', 'DEV_WORK_SUMMARY', 15, NULL, 'zhongshi-market-portal-rest', 0, '2026-04-24 17:19:25', '2026-04-24 17:19:25', 0, 0, 13, 1010, 4, 2, 0, 0, 0, 0, 0, 0, 0, 1010
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 168, u.id, 'shichao', '2026-04-24', 'DEV_WORK_SUMMARY', 24, NULL, 'zhongshi-portal-rest', 0, '2026-04-24 17:27:30', '2026-04-24 17:27:30', 0, 0, 25, 3070, 8, 0, 0, 0, 0, 0, 0, 0, 0, 3070
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 169, u.id, 'yuyang', '2026-04-24', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-edge-web', 0, '2026-04-24 17:50:18', '2026-04-24 17:50:18', 0, 0, 0, 0, 0, 0, 14, 13, 1, 0, 3076, 0, 0, 3076
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'yuyang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 170, u.id, 'jianaili', '2026-04-24', 'DEV_WORK_SUMMARY', 0, NULL, 'project-web', 0, '2026-04-24 17:53:44', '2026-04-24 17:53:44', 0, 0, 0, 0, 0, 0, 9, 4, 5, 0, 5758, 0, 0, 5758
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'jianaili'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 171, u.id, 'fanqi', '2026-04-24', 'DEV_WORK_SUMMARY', 0, NULL, 'zsjd-idm', 0, '2026-04-24 17:54:43', '2026-04-24 17:54:43', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanqi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 172, u.id, 'zhangdi', '2026-04-24', 'DEV_WORK_SUMMARY', 0, NULL, 'portal-web', 0, '2026-04-24 18:04:18', '2026-04-24 18:04:18', 0, 0, 0, 0, 0, 0, 11, 2, 9, 19, 297, 0, 0, 316
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 173, u.id, 'zhangdi', '2026-04-24', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operations-center-manage-web', 0, '2026-04-24 18:12:16', '2026-04-24 18:12:16', 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 82, 0, 0, 82
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 174, u.id, 'yuyang', '2026-04-27', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-edge-web', 0, '2026-04-27 17:22:24', '2026-04-27 17:22:24', 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 250, 0, 0, 250
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'yuyang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 175, u.id, 'zhangdi', '2026-04-27', 'DEV_WORK_SUMMARY', 0, NULL, 'portal-web', 0, '2026-04-27 17:25:49', '2026-04-27 17:25:49', 0, 0, 0, 0, 0, 0, 17, 3, 14, 997, 3366, 0, 0, 4363
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 176, u.id, 'yuyang', '2026-04-27', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web', 0, '2026-04-27 17:26:40', '2026-04-27 17:26:40', 0, 0, 0, 0, 0, 0, 4, 3, 1, 0, 1460, 0, 0, 1460
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'yuyang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 177, u.id, 'jianaili', '2026-04-27', 'DEV_WORK_SUMMARY', 0, NULL, 'project-web', 0, '2026-04-27 17:33:16', '2026-04-27 17:33:16', 0, 0, 0, 0, 0, 0, 8, 4, 4, 76, 5488, 0, 0, 5564
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'jianaili'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 178, u.id, 'kunlun zhang', '2026-04-27', 'DEV_WORK_SUMMARY', 11, NULL, 'ai-studio', 0, '2026-04-27 17:44:29', '2026-04-27 17:44:29', 0, 0, 19, 1080, 2, 3, 0, 0, 0, 0, 0, 3, 0, 1080
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'kunlun zhang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 179, u.id, 'fanhailong01', '2026-04-27', 'DEV_WORK_SUMMARY', 22, NULL, 'unified-operation-all', 0, '2026-04-27 18:24:38', '2026-04-27 18:24:38', 0, 0, 48, 9782, 4, 8, 0, 0, 0, 0, 0, 0, 0, 9782
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanhailong01'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 180, u.id, 'fanqi', '2026-04-27', 'DEV_WORK_SUMMARY', 12, NULL, 'zsjd-idm', 0, '2026-04-27 20:04:52', '2026-04-27 20:04:52', 0, 0, 1, 296, 0, 0, 0, 0, 0, 0, 0, 0, 0, 296
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanqi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 181, u.id, 'fanqi', '2026-04-28', 'DEV_WORK_SUMMARY', 0, NULL, 'zsjd-idm', 0, '2026-04-28 17:10:44', '2026-04-28 17:10:44', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanqi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 182, u.id, 'yuyang', '2026-04-28', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-manage-web', 0, '2026-04-28 17:34:07', '2026-04-28 17:34:07', 0, 0, 0, 0, 0, 0, 4, 1, 3, 274, 958, 0, 0, 1232
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'yuyang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 183, u.id, 'zhangdi', '2026-04-28', 'DEV_WORK_SUMMARY', 0, NULL, 'portal-web', 0, '2026-04-28 17:41:35', '2026-04-28 17:41:35', 0, 0, 0, 0, 0, 0, 17, 2, 15, 3018, 3085, 0, 0, 6103
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 184, u.id, 'jianaili', '2026-04-28', 'DEV_WORK_SUMMARY', 0, NULL, 'workbench', 0, '2026-04-28 18:58:27', '2026-04-28 18:58:27', 0, 0, 0, 0, 0, 0, 37, 29, 8, 0, 12673, 0, 0, 12673
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'jianaili'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 185, u.id, 'shichao', '2026-04-29', 'DEV_WORK_SUMMARY', 8, NULL, 'zhongshi-supply-demand-resources', 0, '2026-04-29 16:06:59', '2026-04-29 16:06:59', 0, 0, 40, 6672, 8, 8, 0, 0, 0, 0, 0, 8, 0, 6672
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 186, u.id, 'shichao', '2026-04-29', 'DEV_WORK_SUMMARY', 24, NULL, 'zhongshi-portal-rest', 0, '2026-04-29 16:10:58', '2026-04-29 16:10:58', 0, 0, 10, 1233, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1233
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 187, u.id, 'shichao', '2026-04-29', 'DEV_WORK_SUMMARY', 6, NULL, 'zhongshi-market-portal-rest', 0, '2026-04-29 16:17:57', '2026-04-29 16:17:57', 0, 0, 3, 515, 1, 0, 0, 0, 0, 0, 0, 0, 0, 515
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'shichao'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 188, u.id, 'yuyang', '2026-04-29', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operation-edge-web', 0, '2026-04-29 17:24:12', '2026-04-29 17:24:12', 0, 0, 0, 0, 0, 0, 5, 5, 0, 0, 1062, 0, 0, 1062
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'yuyang'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 189, u.id, 'fanqi', '2026-04-29', 'DEV_WORK_SUMMARY', 0, NULL, 'zsjd-idm', 0, '2026-04-29 17:36:52', '2026-04-29 17:36:52', 0, 0, 2, 289, 0, 0, 0, 0, 0, 0, 0, 0, 0, 289
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanqi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 190, u.id, 'zhangzhaolun', '2026-04-29', 'DEV_WORK_SUMMARY', 55, NULL, 'unified-operation-dev', 4, '2026-04-29 17:49:06', '2026-04-29 17:49:06', 0, 0, 26, 9616, 3, 3, 0, 0, 0, 0, 0, 0, 0, 9616
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangzhaolun'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 191, u.id, 'zhangdi', '2026-04-29', 'DEV_WORK_SUMMARY', 0, NULL, 'unified-operations-center-manage-web', 0, '2026-04-29 17:58:22', '2026-04-29 17:58:22', 0, 0, 0, 0, 0, 0, 8, 3, 3, 19, 545, 0, 0, 564
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'zhangdi'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 192, u.id, 'jianaili', '2026-04-29', 'DEV_WORK_SUMMARY', 0, NULL, 'workbench', 0, '2026-04-29 19:01:16', '2026-04-29 19:01:16', 0, 0, 0, 0, 0, 0, 6, 5, 1, 0, 2809, 0, 0, 2809
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'jianaili'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);
INSERT INTO `ai-studio-plus`.`member_output`
(`id`, `user_id`, `git_name`, `stat_date`, `output_type`, `api_count`, `remark`, `project_root_name`, `prd_doc_count`, `created_at`, `updated_at`, `data_model_doc_count`, `api_doc_count`, `java_file_count`, `java_code_lines`, `core_biz_service_count`, `entity_count`, `frontend_component_count`, `frontend_page_count`, `frontend_common_component_count`, `ts_code_lines`, `frontend_code_lines`, `sql_script_count`, `test_file_count`, `total_code_lines`)
SELECT 193, u.id, 'fanhailong01', '2026-04-29', 'DEV_WORK_SUMMARY', 63, NULL, 'unified-operation-all', 0, '2026-04-29 21:14:41', '2026-04-29 21:14:41', 0, 0, 32, 12319, 5, 3, 0, 0, 0, 0, 0, 3, 0, 12319
FROM `ai-studio-plus`.`sys_user` u
WHERE u.git_name = 'fanhailong01'
ON DUPLICATE KEY UPDATE
  `user_id` = VALUES(`user_id`),
  `git_name` = VALUES(`git_name`),
  `stat_date` = VALUES(`stat_date`),
  `output_type` = VALUES(`output_type`),
  `api_count` = VALUES(`api_count`),
  `remark` = VALUES(`remark`),
  `project_root_name` = VALUES(`project_root_name`),
  `prd_doc_count` = VALUES(`prd_doc_count`),
  `updated_at` = VALUES(`updated_at`),
  `data_model_doc_count` = VALUES(`data_model_doc_count`),
  `api_doc_count` = VALUES(`api_doc_count`),
  `java_file_count` = VALUES(`java_file_count`),
  `java_code_lines` = VALUES(`java_code_lines`),
  `core_biz_service_count` = VALUES(`core_biz_service_count`),
  `entity_count` = VALUES(`entity_count`),
  `frontend_component_count` = VALUES(`frontend_component_count`),
  `frontend_page_count` = VALUES(`frontend_page_count`),
  `frontend_common_component_count` = VALUES(`frontend_common_component_count`),
  `ts_code_lines` = VALUES(`ts_code_lines`),
  `frontend_code_lines` = VALUES(`frontend_code_lines`),
  `sql_script_count` = VALUES(`sql_script_count`),
  `test_file_count` = VALUES(`test_file_count`),
  `total_code_lines` = VALUES(`total_code_lines`);

