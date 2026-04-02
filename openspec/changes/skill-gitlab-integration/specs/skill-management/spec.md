## MODIFIED Requirements

### Requirement: Skill 列表分页查询
系统 SHALL 支持分页查询 Skill 列表，支持按名称关键词搜索、按分类和状态筛选，返回总条数。查询结果仅包含从 GitLab 同步且未删除的 Skill。

#### Scenario: 默认分页查询
- **WHEN** 用户请求 Skill 列表（不带任何筛选参数）
- **THEN** 系统返回第 1 页数据（默认每页 10 条）及总条数，仅包含 is_deleted = 0 的记录

---

### Requirement: Skill 文件下载
系统 SHALL 在用户下载 Skill 文件时自动累加下载次数。下载时实时从 GitLab 获取内容，不再使用 OSS 链接。

#### Scenario: 成功下载
- **WHEN** 已登录用户请求下载指定 Skill
- **THEN** 系统将 `download_count` 加 1，调用 GitLab API 获取文件 Raw 内容并返回

---

## REMOVED Requirements

### Requirement: 创建 Skill
**Reason**: Skill 统一在 GitLab 仓库中管理，不再通过 AI Studio 界面创建
**Migration**: 在 GitLab 仓库对应分类目录下新建 SKILL.md 文件，执行同步后即可在 AI Studio 展示

### Requirement: 编辑 Skill
**Reason**: Skill 编辑在 GitLab 中完成，AI Studio 仅作为展示平台
**Migration**: 在 GitLab 中编辑 SKILL.md 文件，提交后执行同步更新 AI Studio 数据

### Requirement: 删除 Skill
**Reason**: Skill 删除在 GitLab 中完成（删除文件），AI Studio 仅同步状态
**Migration**: 在 GitLab 中删除 SKILL.md 文件，下次同步时自动标记为已删除

### Requirement: 批量 ZIP 导入 Skill
**Reason**: Skill 统一在 GitLab 中管理，不再支持 ZIP 批量导入功能
**Migration**: 将 SKILL.md 文件提交到 GitLab 仓库，执行同步即可

### Requirement: Skill 详情查询
**Status**: 保留，行为不变。详情中不再返回 content_oss_key 和 content_url 字段，改为返回 gitlab_repo 和 gitlab_path。
