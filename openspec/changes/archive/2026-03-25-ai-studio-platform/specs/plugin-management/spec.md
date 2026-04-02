## ADDED Requirements

### Requirement: Plugin 列表分页查询
系统 SHALL 支持分页查询 Plugin 列表，支持按名称搜索、按类型和状态筛选，返回总条数。

#### Scenario: 默认分页查询
- **WHEN** 用户请求 Plugin 列表（不带筛选参数）
- **THEN** 系统返回第 1 页数据及总条数

#### Scenario: 按名称搜索
- **WHEN** 用户传入 `keyword` 参数
- **THEN** 系统返回名称包含该关键词的 Plugin 列表

#### Scenario: 按类型筛选
- **WHEN** 用户传入 `type` 参数（editor/build/lint 等）
- **THEN** 系统仅返回该类型的 Plugin

---

### Requirement: 上传 Plugin
系统 SHALL 允许已登录用户上传 Plugin 文件（通过七牛云 OSS），创建对应记录，文件大小不超过 100MB。

#### Scenario: 成功上传
- **WHEN** 用户提交 Plugin 表单（名称、描述、类型、版本号、file_oss_key、file_url、file_size）
- **THEN** 系统创建 Plugin 记录，`created_by` 设为当前用户 ID，返回新建 Plugin 的 ID

#### Scenario: 文件超过大小限制
- **WHEN** 用户提交的 `file_size` 超过 100MB（104,857,600 字节）
- **THEN** 系统返回错误提示"文件大小超过 100MB 限制"

---

### Requirement: 更新 Plugin
系统 SHALL 允许管理员更新任意 Plugin，普通用户只能更新自己创建的 Plugin。

#### Scenario: 管理员更新任意 Plugin
- **WHEN** ADMIN 或 SUPER_ADMIN 提交更新请求
- **THEN** 系统更新 Plugin 信息并返回成功

#### Scenario: 普通用户更新自己的 Plugin
- **WHEN** USER 提交更新请求且 `created_by == currentUserId`
- **THEN** 系统更新 Plugin 信息并返回成功

#### Scenario: 普通用户更新他人 Plugin
- **WHEN** USER 提交更新请求且 `created_by != currentUserId`
- **THEN** 系统返回 403 Forbidden

---

### Requirement: 删除 Plugin
系统 SHALL 允许管理员删除任意 Plugin，普通用户只能删除自己创建的 Plugin。

#### Scenario: 管理员删除任意 Plugin
- **WHEN** ADMIN 或 SUPER_ADMIN 发起删除请求
- **THEN** 系统删除对应 Plugin 记录并返回成功

#### Scenario: 普通用户删除他人 Plugin
- **WHEN** USER 发起删除请求且 `created_by != currentUserId`
- **THEN** 系统返回 403 Forbidden

---

### Requirement: Plugin 文件下载
系统 SHALL 在用户下载 Plugin 文件时自动累加下载次数，返回 OSS 下载链接。

#### Scenario: 成功下载
- **WHEN** 已登录用户请求下载指定 Plugin
- **THEN** 系统将 `download_count` 加 1，返回 OSS 文件下载链接

#### Scenario: OSS 文件不存在
- **WHEN** OSS 中对应文件已被删除
- **THEN** 系统返回 404 并提示"文件不存在"

#### Scenario: 未登录下载
- **WHEN** 未携带有效 Token 的请求访问下载接口
- **THEN** 系统返回 401 Unauthorized
