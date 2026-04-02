## ADDED Requirements

### Requirement: GitLab 连接配置
系统 SHALL 支持配置 GitLab 连接参数，包括 URL、认证 Token、目标仓库和分支。

#### Scenario: 配置 GitLab 连接
- **WHEN** 管理员在系统配置中填写 GitLab URL、Deploy Token、仓库名（如 ai-studio/skills）和分支（如 main）
- **THEN** 系统保存配置并验证连接可用

#### Scenario: 配置验证失败
- **WHEN** 管理员填写的 GitLab Token 无效或仓库不存在
- **THEN** 系统返回错误提示"GitLab 连接验证失败"

---

### Requirement: Skill 数据同步
系统 SHALL 支持从 GitLab 仓库同步 Skill 数据到本地数据库，解析 YAML frontmatter 元数据。

#### Scenario: 成功同步单个文件
- **WHEN** 系统发现 GitLab 仓库中存在新的 SKILL.md 文件
- **THEN** 系统解析文件内容，提取 YAML frontmatter（name, category, description 等），创建 Skill 记录

#### Scenario: 同步已存在的文件
- **WHEN** 系统发现 GitLab 文件中存在已同步过的 Skill（path 匹配）
- **THEN** 系统更新现有 Skill 记录的元数据和 commit_sha

#### Scenario: 文件在 GitLab 被删除
- **WHEN** 系统同步时发现数据库中存在但 GitLab 中不存在的文件路径
- **THEN** 系统将对应 Skill 标记为已删除（is_deleted = 1）

#### Scenario: YAML 解析失败
- **WHEN** 系统解析某个 SKILL.md 文件时 YAML frontmatter 格式错误
- **THEN** 系统跳过该文件，记录错误信息到 sync_error，继续同步其他文件

---

### Requirement: 手动触发同步
系统 SHALL 提供手动触发同步的接口，供管理员立即执行同步操作。

#### Scenario: 管理员手动同步
- **WHEN** 管理员点击"从 GitLab 同步"按钮
- **THEN** 系统立即执行全量同步，返回同步结果（新增数、更新数、删除数、失败数）

#### Scenario: 同步进行中
- **WHEN** 管理员触发同步时上一次同步尚未完成
- **THEN** 系统返回提示"同步正在进行中，请稍后"

---

### Requirement: 定时自动同步
系统 SHALL 支持配置定时任务，按指定周期自动执行同步。

#### Scenario: 定时同步触发
- **WHEN** 到达配置的同步时间（如每 30 分钟）
- **THEN** 系统自动执行同步操作，记录同步日志

#### Scenario: 定时同步失败
- **WHEN** 定时同步过程中 GitLab 服务不可用
- **THEN** 系统记录失败日志，保留上次成功数据，下次定时任务继续尝试

---

### Requirement: 同步日志记录
系统 SHALL 记录每次同步的详细日志，包括触发方式、执行结果和耗时。

#### Scenario: 记录同步日志
- **WHEN** 同步任务完成（成功或失败）
- **THEN** 系统记录 sync_log，包含 created_count, updated_count, deleted_count, error_count, duration_ms, trigger_type

#### Scenario: 查询同步历史
- **WHEN** 管理员请求查看同步历史
- **THEN** 系统返回最近 N 条同步日志记录
