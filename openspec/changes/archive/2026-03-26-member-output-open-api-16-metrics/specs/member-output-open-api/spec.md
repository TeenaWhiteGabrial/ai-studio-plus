## ADDED Requirements

### Requirement: 开放产出提交接口
系统 SHALL 提供 POST `/api/open/output/submit` 接口，允许用户无需认证提交每日产出数据。

**请求参数**：
```json
{
  "username": "zhangsan",
  "stat_date": "2026-03-26",
  "prd_doc_count": 2,
  "data_model_doc_count": 1,
  "api_doc_count": 3,
  "java_file_count": 5,
  "java_code_lines": 300,
  "api_count": 10,
  "core_biz_service_count": 3,
  "entity_count": 4,
  "frontend_component_count": 8,
  "frontend_page_count": 2,
  "frontend_common_component_count": 3,
  "ts_code_lines": 200,
  "frontend_code_lines": 500,
  "sql_script_count": 2,
  "test_file_count": 3,
  "total_code_lines": 1000,
  "remark": "今日工作完成"
}
```

**响应格式**：
```json
{
  "code": 200,
  "message": "提交成功",
  "data": {
    "id": 123,
    "username": "zhangsan",
    "stat_date": "2026-03-26"
  }
}
```

#### Scenario: 首次提交成功
- **WHEN** 用户传入有效的 username 和 stat_date（今天）且该用户当天无记录
- **THEN** 系统创建新记录，返回成功

#### Scenario: 重复提交更新
- **WHEN** 用户传入有效的 username 和 stat_date（今天）且该用户当天已有记录
- **THEN** 系统更新已有记录，返回成功

#### Scenario: 提交日期非今天
- **WHEN** 用户传入的 stat_date 不是今天
- **THEN** 系统返回错误提示"仅允许提交今天的数据"

#### Scenario: username 为空
- **WHEN** 用户未传入 username 或 username 为空
- **THEN** 系统返回错误提示"username 不能为空"

#### Scenario: username 不存在
- **WHEN** 用户传入的 username 在系统用户表中不存在
- **THEN** 系统仍然接受提交，但记录中标记用户可能不存在
