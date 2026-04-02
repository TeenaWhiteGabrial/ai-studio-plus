## ADDED Requirements

### Requirement: Skill 列表分页查询
系统 SHALL 支持分页查询 Skill 列表，支持按名称关键词搜索、按分类和状态筛选，返回总条数。

#### Scenario: 默认分页查询
- **WHEN** 用户请求 Skill 列表（不带任何筛选参数）
- **THEN** 系统返回第 1 页数据（默认每页 10 条）及总条数

#### Scenario: 按名称关键词搜索
- **WHEN** 用户传入 `keyword` 参数
- **THEN** 系统返回名称包含该关键词的 Skill 列表

#### Scenario: 按分类筛选
- **WHEN** 用户传入 `category` 参数
- **THEN** 系统仅返回该分类下的 Skill

#### Scenario: 按状态筛选
- **WHEN** 用户传入 `status` 参数（1-正常 / 0-禁用）
- **THEN** 系统仅返回对应状态的 Skill

---

### Requirement: 创建 Skill
系统 SHALL 允许已登录用户创建新 Skill，名称全局唯一，创建人记录为当前用户。

#### Scenario: 成功创建
- **WHEN** 用户提交合法的 Skill 表单（名称、描述、分类、来源、content_oss_key、content_url）
- **THEN** 系统创建记录，`created_by` 设为当前用户 ID，返回新建 Skill 的 ID

#### Scenario: 名称重复
- **WHEN** 用户提交的名称与已有 Skill 名称相同
- **THEN** 系统返回错误提示"Skill 名称已存在"

---

### Requirement: 编辑 Skill
系统 SHALL 允许管理员修改任意 Skill，普通用户只能修改自己创建的 Skill。

#### Scenario: 管理员编辑任意 Skill
- **WHEN** ADMIN 或 SUPER_ADMIN 提交编辑请求
- **THEN** 系统更新 Skill 信息并返回成功

#### Scenario: 普通用户编辑自己的 Skill
- **WHEN** USER 提交编辑请求且 `created_by == currentUserId`
- **THEN** 系统更新 Skill 信息并返回成功

#### Scenario: 普通用户编辑他人 Skill
- **WHEN** USER 提交编辑请求且 `created_by != currentUserId`
- **THEN** 系统返回 403 Forbidden

---

### Requirement: 删除 Skill
系统 SHALL 支持单个和批量删除 Skill，权限规则与编辑相同。

#### Scenario: 管理员删除任意 Skill
- **WHEN** ADMIN 或 SUPER_ADMIN 发起删除请求
- **THEN** 系统删除对应 Skill 记录并返回成功

#### Scenario: 普通用户删除自己的 Skill
- **WHEN** USER 发起删除请求且 `created_by == currentUserId`
- **THEN** 系统删除记录并返回成功

#### Scenario: 普通用户删除他人 Skill
- **WHEN** USER 发起删除请求且 `created_by != currentUserId`
- **THEN** 系统返回 403 Forbidden

#### Scenario: 批量删除
- **WHEN** 用户传入多个 Skill ID 发起批量删除
- **THEN** 系统对每个 ID 执行权限校验，有权限的删除，无权限的跳过并在响应中说明

---

### Requirement: Skill 详情查询
系统 SHALL 返回指定 Skill 的完整信息。

#### Scenario: 查询存在的 Skill
- **WHEN** 用户请求指定 ID 的 Skill 详情
- **THEN** 系统返回该 Skill 的全部字段

#### Scenario: 查询不存在的 Skill
- **WHEN** 用户请求的 ID 不存在
- **THEN** 系统返回 404 Not Found

---

### Requirement: Skill 文件下载
系统 SHALL 在用户下载 Skill 文件时自动累加下载次数。

#### Scenario: 成功下载
- **WHEN** 已登录用户请求下载指定 Skill
- **THEN** 系统将 `download_count` 加 1，返回文件内容或 OSS 下载链接

#### Scenario: 未登录下载
- **WHEN** 未携带有效 Token 的请求访问下载接口
- **THEN** 系统返回 401 Unauthorized

---

### Requirement: 批量 ZIP 导入 Skill
系统 SHALL 支持上传 ZIP 文件批量导入多个 SKILL.md，返回成功数量和失败数量。

#### Scenario: 成功批量导入
- **WHEN** 用户上传包含多个 SKILL.md 文件的 ZIP 包
- **THEN** 系统解析每个文件，创建对应 Skill 记录，返回 `{success: N, failed: M}`

#### Scenario: 部分文件名重复
- **WHEN** ZIP 中某些文件对应的 Skill 名称已存在
- **THEN** 系统跳过重复项，其余正常导入，在失败列表中说明原因

#### Scenario: ZIP 超过大小限制
- **WHEN** 上传的 ZIP 文件超过 100MB
- **THEN** 系统返回错误提示"文件大小超过限制"
