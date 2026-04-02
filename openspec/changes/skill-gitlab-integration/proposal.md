## Why

当前 Skill 管理功能使用 OSS 存储 SKILL.md 文件，存在版本控制缺失、协作流程不完善的问题。团队需要在 GitLab 上统一维护 Skill 文件，利用 GitLab 的版本控制、代码审查和权限管理能力。AI Studio 作为 Skill 的展示和分发平台，需要从 GitLab 同步数据并提供给 Claude Code 使用。

## What Changes

- **BREAKING**: 移除 OSS 文件存储相关功能（上传、直传、OSS Token 获取）
- 新增 GitLab API 集成模块，支持读取私有部署 GitLab 仓库内容
- 重构 Skill 数据模型，移除 OSS 相关字段，增加 GitLab 元数据字段
- 新增 Skill 同步机制：定时同步、手动同步按钮、Webhook 增量同步（可选）
- 新增 Skill 下载功能（实时从 GitLab 拉取 Raw 内容）
- 新增 Claude Code 命令生成功能（`claude config add skill <url>`）
- 前端移除 Skill 新建/编辑表单，改为只读展示页面
- 新增同步状态展示和同步配置页面

## Capabilities

### New Capabilities
- `gitlab-sync`: GitLab 仓库集成与 Skill 数据同步
- `skill-readonly-management`: 只读模式的 Skill 管理页面
- `skill-download`: Skill 文件下载与 Claude Code 命令生成

### Modified Capabilities
- `skill-management`: 从完整的 CRUD 改为只读展示，数据源从 OSS 改为 GitLab 同步

## Impact

- **后端**:
  - 移除 `OssService` 在 Skill 模块的依赖
  - 新增 `GitLabService` 用于 API 调用
  - 新增 `SkillSyncService` 处理同步逻辑
  - `Skill` 实体类字段变更
  - `SkillController` API 调整（移除创建/编辑接口）

- **前端**:
  - `skill/index.vue` 页面重构（移除表单，新增同步按钮和命令复制）
  - 新增同步配置页面
  - 移除 `oss.ts` 工具类在 Skill 模块的使用

- **数据库**:
  - `skill` 表结构调整（字段增删）
  - 新增 `skill_sync_log` 同步日志表

- **配置**:
  - 新增 GitLab 连接配置（URL、Token、仓库名）
  - 新增同步策略配置（定时规则、Webhook Secret）
