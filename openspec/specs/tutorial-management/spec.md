## ADDED Requirements

### Requirement: 教程列表分页查询
系统 SHALL 支持分页查询教程列表，支持按标题搜索、按分类和标签筛选，返回总条数。

#### Scenario: 默认分页查询
- **WHEN** 用户请求教程列表（不带筛选参数）
- **THEN** 系统返回第 1 页数据及总条数

#### Scenario: 按标题搜索
- **WHEN** 用户传入 `keyword` 参数
- **THEN** 系统返回标题包含该关键词的教程列表

#### Scenario: 按分类筛选
- **WHEN** 用户传入 `category` 参数
- **THEN** 系统仅返回该分类下的教程

#### Scenario: 按标签筛选
- **WHEN** 用户传入 `tag` 参数
- **THEN** 系统仅返回 `tags` 字段包含该标签值的教程

---

### Requirement: 创建教程
系统 SHALL 允许已登录用户创建教程，Markdown 内容存储于七牛云 OSS，后端存储 key 和访问 URL。

#### Scenario: 成功创建
- **WHEN** 用户提交合法的教程表单（标题、分类、标签、content_oss_key、content_url）
- **THEN** 系统创建教程记录，`created_by` 设为当前用户 ID，返回新建教程的 ID

#### Scenario: 标题为空
- **WHEN** 用户提交时标题为空
- **THEN** 系统返回 400 Bad Request 并说明标题必填

---

### Requirement: 更新教程
系统 SHALL 允许管理员更新任意教程，普通用户只能更新自己创建的教程。

#### Scenario: 管理员更新任意教程
- **WHEN** ADMIN 或 SUPER_ADMIN 提交更新请求
- **THEN** 系统更新教程信息并返回成功

#### Scenario: 普通用户更新自己的教程
- **WHEN** USER 提交更新请求且 `created_by == currentUserId`
- **THEN** 系统更新教程信息并返回成功

#### Scenario: 普通用户更新他人教程
- **WHEN** USER 提交更新请求且 `created_by != currentUserId`
- **THEN** 系统返回 403 Forbidden

---

### Requirement: 删除教程
系统 SHALL 允许管理员删除任意教程，普通用户只能删除自己创建的教程。

#### Scenario: 管理员删除任意教程
- **WHEN** ADMIN 或 SUPER_ADMIN 发起删除请求
- **THEN** 系统删除对应教程记录并返回成功

#### Scenario: 普通用户删除他人教程
- **WHEN** USER 发起删除请求且 `created_by != currentUserId`
- **THEN** 系统返回 403 Forbidden

---

### Requirement: 教程详情查询（浏览计数）
系统 SHALL 在返回教程详情时自动累加浏览次数。

#### Scenario: 成功查询教程详情
- **WHEN** 已登录用户请求指定 ID 的教程详情
- **THEN** 系统将 `view_count` 加 1，返回教程完整信息（含 content_url）

#### Scenario: 查询不存在的教程
- **WHEN** 用户请求的 ID 不存在
- **THEN** 系统返回 404 Not Found
