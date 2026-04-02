## ADDED Requirements

### Requirement: Skill 文件下载
系统 SHALL 支持下载 Skill 文件，实时从 GitLab 获取内容并返回给客户端。

#### Scenario: 成功下载 Skill 文件
- **WHEN** 已登录用户点击下载按钮
- **THEN** 系统调用 GitLab API 获取文件 Raw 内容，download_count + 1，返回文件内容

#### Scenario: 未登录用户下载
- **WHEN** 未携带有效 Token 的请求访问下载接口
- **THEN** 系统返回 401 Unauthorized

#### Scenario: Skill 不存在
- **WHEN** 用户请求下载已删除或不存在的 Skill
- **THEN** 系统返回 404 Not Found

#### Scenario: GitLab 服务不可用
- **WHEN** 用户下载时 GitLab 服务不可用
- **THEN** 系统返回 503 Service Unavailable，提示"GitLab 服务暂不可用"

---

### Requirement: Skill Raw 内容接口
系统 SHALL 提供 Raw 内容接口，供 Claude Code 等外部工具直接获取 Skill 内容。

#### Scenario: 获取 Raw 内容
- **WHEN** 外部工具请求 `/api/skill/{id}/raw`
- **THEN** 系统从 GitLab 获取文件内容，直接返回 Markdown 文本（不包装 Result）

#### Scenario: Raw 接口统计下载
- **WHEN** 通过 Raw 接口获取内容成功
- **THEN** 系统累加该 Skill 的 download_count

---

### Requirement: Claude Code 命令生成
系统 SHALL 在 Skill 卡片上提供"复制命令"功能，生成 Claude Code 可用的添加 Skill 命令。

#### Scenario: 复制 Claude 命令
- **WHEN** 用户点击"复制命令"按钮
- **THEN** 系统生成命令 `claude config add skill {baseUrl}/api/skill/{id}/raw` 并复制到剪贴板

#### Scenario: 命令复制成功提示
- **WHEN** 命令成功复制到剪贴板
- **THEN** 系统显示提示"命令已复制，粘贴到终端即可使用"

#### Scenario: 展示命令预览
- **WHEN** 用户展开"复制命令"下拉菜单
- **THEN** 系统展示完整的命令文本，包含实际的 URL
