## 1. 数据库变更

- [x] 1.1 编写数据库迁移脚本 V__alter_member_output_add_columns.sql
- [x] 1.2 新增字段：prd_doc_count、data_model_doc_count、api_doc_count
- [x] 1.3 新增字段：java_file_count、java_code_lines、core_biz_service_count、entity_count
- [x] 1.4 新增字段：frontend_component_count、frontend_page_count、frontend_common_component_count、ts_code_lines
- [x] 1.5 新增字段：sql_script_count、test_file_count、total_code_lines
- [x] 1.6 所有新增字段设置默认值为 0

## 2. 开放 API 接口

- [x] 2.1 创建 OpenOutputController 控制器（无需认证）
- [x] 2.2 实现 POST /api/open/output/submit 接口
- [x] 2.3 接口参数校验（username 不能为空、stat_date 必须为今天）
- [x] 2.4 实现 UPSERT 逻辑，同一天重复提交则更新
- [x] 2.5 添加接口限流保护（同一 username 每天最多提交 N 次）（可选功能，暂不实现）

## 3. 后端实体和服务

- [x] 3.1 修改 MemberOutput 实体类，新增 16 个指标字段
- [x] 3.2 修改 MemberOutputMapper，更新结果集映射
- [x] 3.3 修改 MemberOutputDTO，支持 16 个指标字段
- [x] 3.4 修改 MemberOutputService，支持按 username 提交和查询

## 4. 清理工作

- [x] 4.1 移除前端产出提交表单页面
- [x] 4.2 移除产出提交相关的认证接口
- [x] 4.3 保留产出查询和展示功能

## 5. 测试和验证

- [x] 5.1 验证数据库迁移脚本执行成功
- [x] 5.2 测试开放 API 接口提交功能
- [x] 5.3 测试重复提交更新逻辑
- [x] 5.4 测试日期校验逻辑
- [x] 5.5 测试 username 为空校验
- [x] 5.6 测试产出查询功能（返回 16 个指标）
