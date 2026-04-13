## ADDED Requirements

### Requirement: 资源审核列表
管理员 SHALL 能够查看所有待审核资源（Skill/MCP/Plugin/教程/安装包/视频），展示 AI 预检结果和原始提交信息。

#### Scenario: 查看待审核资源列表
- **WHEN** 管理员访问内容审核页面
- **THEN** 系统展示所有 `status=待审核` 的资源，支持按类型筛选

### Requirement: AI 预检结果展示
提交审核的资源 SHALL 展示 AI 预检结果，包括检测出的问题和风险等级，供管理员参考。

### Requirement: 审核通过操作
管理员 SHALL 能够通过审核，资源状态从"待审核"变为"已通过"，资源在 Portal 上线展示。

### Requirement: 审核拒绝操作
管理员 SHALL 能够拒绝资源审核，必须填写拒绝原因，系统 SHALL 通知上传者。

#### Scenario: 拒绝资源并填写原因
- **WHEN** 管理员点击拒绝并填写原因后提交
- **THEN** 资源状态变为"已拒绝"，拒绝原因存储到 `reject_reason` 字段，通知上传者

### Requirement: 版本更新审核
资源发布新版本时 SHALL 重新进入待审核状态，管理员审核通过后版本更新。

#### Scenario: Skill 发布新版本后需重新审核
- **WHEN** 作者发布 Skill v1.2.0 版本并提交审核
- **THEN** 该版本 `status=待审核`，审核通过后 Skill 的 `current_version` 更新为 v1.2.0

### Requirement: 审核历史记录
管理员 SHALL 能够查看所有资源的审核历史，包含审核人、审核时间、结果和原因。

### Requirement: AI 预检配置
管理员 SHALL 能够配置 AI 预检规则，设置各检测项和阈值。
