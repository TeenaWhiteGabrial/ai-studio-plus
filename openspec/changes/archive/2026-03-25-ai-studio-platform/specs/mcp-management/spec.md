## ADDED Requirements

### Requirement: MCP 服务器列表分页查询
系统 SHALL 支持分页查询 MCP 服务器列表，支持按名称搜索、按状态筛选，返回总条数。

#### Scenario: 默认分页查询
- **WHEN** 用户请求 MCP 列表（不带筛选参数）
- **THEN** 系统返回第 1 页数据及总条数

#### Scenario: 按名称搜索
- **WHEN** 用户传入 `keyword` 参数
- **THEN** 系统返回名称包含该关键词的 MCP 服务器列表

#### Scenario: 按状态筛选
- **WHEN** 用户传入 `status` 参数
- **THEN** 系统仅返回对应状态的 MCP 服务器

---

### Requirement: 创建 MCP 服务器
系统 SHALL 允许已登录用户录入 MCP 服务器配置，创建人记录为当前用户。

#### Scenario: 成功创建
- **WHEN** 用户提交合法的 MCP 表单（名称、描述、api_endpoint、auth_type、config_json）
- **THEN** 系统创建记录，`created_by` 设为当前用户 ID，返回新建 MCP 的 ID

#### Scenario: 必填字段缺失
- **WHEN** 用户提交时缺少 `api_endpoint` 或 `auth_type`
- **THEN** 系统返回 400 Bad Request 并说明缺失字段

---

### Requirement: 更新 MCP 服务器
系统 SHALL 允许管理员修改任意 MCP，普通用户只能修改自己创建的 MCP。

#### Scenario: 管理员更新任意 MCP
- **WHEN** ADMIN 或 SUPER_ADMIN 提交更新请求
- **THEN** 系统更新 MCP 信息并返回成功

#### Scenario: 普通用户更新自己的 MCP
- **WHEN** USER 提交更新请求且 `created_by == currentUserId`
- **THEN** 系统更新 MCP 信息并返回成功

#### Scenario: 普通用户更新他人 MCP
- **WHEN** USER 提交更新请求且 `created_by != currentUserId`
- **THEN** 系统返回 403 Forbidden

---

### Requirement: 删除 MCP 服务器
系统 SHALL 允许管理员删除任意 MCP，普通用户只能删除自己创建的 MCP。

#### Scenario: 管理员删除任意 MCP
- **WHEN** ADMIN 或 SUPER_ADMIN 发起删除请求
- **THEN** 系统删除对应 MCP 记录并返回成功

#### Scenario: 普通用户删除他人 MCP
- **WHEN** USER 发起删除请求且 `created_by != currentUserId`
- **THEN** 系统返回 403 Forbidden

---

### Requirement: MCP 连接测试
系统 SHALL 允许管理员测试 MCP 服务器的连通性，返回连接状态和响应时间。

#### Scenario: 连接成功
- **WHEN** ADMIN 或 SUPER_ADMIN 发起连接测试，目标服务器可达
- **THEN** 系统返回 `{status: "success", responseTime: <ms>}`

#### Scenario: 连接失败或超时
- **WHEN** 目标服务器不可达或超时（默认 5s）
- **THEN** 系统返回 `{status: "failed", error: "<错误信息>"}`

#### Scenario: 普通用户发起连接测试
- **WHEN** USER 角色调用连接测试接口
- **THEN** 系统返回 403 Forbidden
