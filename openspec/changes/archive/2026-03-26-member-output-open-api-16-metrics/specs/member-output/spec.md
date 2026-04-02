## MODIFIED Requirements

### Requirement: 查询今日产出
系统 SHALL 允许已登录用户查询自己当天的产出记录。产出记录包含以下 16 个指标字段：

**文档类指标**：
- `prd_doc_count`: PRD 文档数量
- `data_model_doc_count`: 数据模型文档数量
- `api_doc_count`: API 接口文档数量

**Java 后端指标**：
- `java_file_count`: Java 文件数量
- `java_code_lines`: Java 代码行数
- `api_count`: API 接口数量
- `core_biz_service_count`: 核心业务服务数量
- `entity_count`: 数据库实体数量

**前端指标**：
- `frontend_component_count`: 前端组件数量
- `frontend_page_count`: 前端页面数量
- `frontend_common_component_count`: 前端公共组件数量
- `ts_code_lines`: TypeScript 代码行数
- `frontend_code_lines`: 前端代码行数

**其他指标**：
- `sql_script_count`: SQL 脚本数量
- `test_file_count`: 测试文件数量
- `total_code_lines`: 总代码行数

**公共字段**：
- `stat_date`: 统计日期
- `remark`: 备注

#### Scenario: 已录入今日产出
- **WHEN** 用户查询今日产出且当天已有记录
- **THEN** 系统返回当天的产出数据（包含全部 16 个指标字段和 remark）

#### Scenario: 未录入今日产出
- **WHEN** 用户查询今日产出且当天无记录
- **THEN** 系统返回空数据（data 为 null 或空对象）

---

### Requirement: 提交今日产出
**REMOVED**: 改为通过开放 API 接口提交，不再提供手工录入功能。

---

### Requirement: 查询个人产出历史
系统 SHALL 允许用户按日期范围查询自己的历史产出记录。

#### Scenario: 按日期范围查询历史
- **WHEN** 用户传入 `startDate` 和 `endDate` 参数
- **THEN** 系统返回该日期范围内当前用户的所有产出记录，每条记录包含 16 个指标字段，按日期降序排列

#### Scenario: 无历史记录
- **WHEN** 用户查询的日期范围内无产出记录
- **THEN** 系统返回空列表

---

### Requirement: 管理员查询全员产出列表
系统 SHALL 允许管理员按指定日期查看所有成员的产出记录，普通用户无权访问。

#### Scenario: 管理员查询指定日期全员产出
- **WHEN** ADMIN 或 SUPER_ADMIN 传入 `date` 参数
- **THEN** 系统返回该日期所有成员的产出记录列表（含用户名、部门、16 个指标数据）

#### Scenario: 普通用户访问全员产出
- **WHEN** USER 访问管理员产出列表接口
- **THEN** 系统返回 403 Forbidden

---

### Requirement: 产出汇总统计
系统 SHALL 支持按日期范围返回产出汇总统计数据（总计、平均值）。

#### Scenario: 查询产出汇总统计
- **WHEN** 用户传入 `startDate` 和 `endDate` 参数请求统计数据
- **THEN** 系统返回该范围内 16 个指标的总计和日均值

#### Scenario: 普通用户仅能看自己的统计
- **WHEN** USER 请求产出统计
- **THEN** 系统仅统计当前用户的数据
