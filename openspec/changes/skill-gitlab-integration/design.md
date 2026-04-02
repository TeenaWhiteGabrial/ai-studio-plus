## Context

当前 AI Studio 的 Skill 管理模块采用 OSS 作为文件存储后端，用户通过前端上传文件到七牛云 OSS，数据库存储 OSS Key 和访问 URL。这种方式存在以下问题：

1. **版本控制缺失**：Skill 文件更新后无法追踪历史版本
2. **协作流程不完善**：多人编辑 Skill 时容易产生冲突
3. **审查机制缺失**：Skill 内容变更缺乏审核流程

公司已在内部部署 GitLab，团队习惯于在 GitLab 上协作管理文档。将 Skill 仓库迁移到 GitLab 可以利用现有的版本控制、Merge Request 审查流程。

**当前架构**：
```
用户上传 → 前端直传 OSS → 数据库存储 key + url → 下载直接跳转
```

**目标架构**：
```
用户在 GitLab 编辑 → GitLab 存储 → AI Studio 定时/手动同步 → 展示 + 下载
```

## Goals / Non-Goals

**Goals:**
- 实现 AI Studio 与 GitLab 私有部署的集成
- Skill 数据从 GitLab 仓库自动同步到 AI Studio 数据库
- 支持定时同步、手动同步两种触发方式
- 用户可从 AI Studio 下载 Skill 文件（实时从 GitLab 拉取）
- 提供 Claude Code 命令生成功能（`claude config add skill <url>`）
- 前端展示页面改为只读模式，移除编辑功能

**Non-Goals:**
- 不在 AI Studio 实现 Skill 在线编辑功能
- 不实现 Webhook 实时同步（Phase 1，可后续扩展）
- 不实现版本历史对比和回滚功能（Phase 2）
- 不迁移历史 OSS 文件（仅同步机制，数据可重新导入）

## Decisions

### 1. 同步策略：定时 + 手动，暂不使用 Webhook

**决策**：采用定时任务（每30分钟）+ 手动按钮触发，第一阶段不实现 Webhook。

**理由**：
- Webhook 需要 GitLab 配置和网络连通性，部署复杂
- Skill 更新频率虽高，但30分钟延迟在业务上可接受
- 手动按钮可让管理员在需要时立即同步

**替代方案**：Webhook 实时推送 - 复杂度更高，后续迭代可考虑

### 2. 数据模型：保留元数据缓存，移除 OSS 字段

**决策**：数据库保留 Skill 元数据（name, description, category 等），删除时软删除标记

**理由**：
- 列表查询性能：本地数据库查询远快于调用 GitLab API
- 搜索筛选：支持关键词、分类筛选需要本地索引
- 统计功能：download_count 需要在本地维护

**替代方案**：纯代理模式（每次查询 GitLab）- 性能差，不适合列表页

### 3. 仓库结构：单仓库 + 目录分类

**决策**：使用单仓库 `ai-studio/skills`，按分类建立子目录

```
ai-studio/skills.git
├── skills/
│   ├── frontend/
│   │   └── react-hooks.md
│   ├── backend/
│   │   └── spring-guide.md
│   └── data-analysis/
│       └── pandas-tutorial.md
```

**理由**：
- 简化权限管理：一个仓库统一配置
- 符合 YAML frontmatter 元数据方案
- 便于批量操作和遍历

**替代方案**：多仓库（每 Skill 一个仓库）- 权限管理复杂，仓库数量爆炸

### 4. GitLab 认证：Deploy Token

**决策**：使用 GitLab Deploy Token 进行 API 认证

**理由**：
- 权限粒度适中：可限制为只读或读写特定仓库
- 管理简单：一个 token 服务整个应用
- 安全性：可在 GitLab 端随时撤销

**替代方案**：OAuth 用户令牌 - 复杂度高，需要用户绑定

### 5. 下载方式：后端代理拉取

**决策**：下载时后端调用 GitLab API 获取 Raw 内容，再返回给前端

**理由**：
- 统一入口：便于统计下载次数
- 隐藏 GitLab 细节：前端不直接暴露 GitLab URL
- 权限控制：可在后端增加访问控制逻辑

**替代方案**：直接返回 GitLab Raw URL - 无法统计下载次数，暴露内部地址

### 6. 元数据解析：YAML Frontmatter

**决策**：SKILL.md 文件顶部使用 YAML frontmatter 定义元数据

```markdown
---
name: "React Hooks 最佳实践"
category: "frontend"
description: "React Hooks 使用规范"
---

正文内容...
```

**理由**：
- 标准化：GitLab 和其他工具原生支持
- 扩展性：可随时添加新字段
- 解析简单：使用现有 YAML 库即可

**替代方案**：文件名约定 - 表达能力有限，难以描述复杂信息

## Risks / Trade-offs

**Risk**: GitLab 服务不可用时，Skill 下载功能受影响
→ **Mitigation**: 后端实现短期缓存（可选），列表查询不受影响（走本地数据库）

**Risk**: 同步延迟导致数据不一致（GitLab 已更新，AI Studio 未同步）
→ **Mitigation**: 页面上显示最后同步时间，提供手动同步按钮

**Risk**: YAML frontmatter 格式不统一导致解析失败
→ **Mitigation**: 同步时校验格式，错误记录到 sync_error 字段，不阻断其他文件同步

**Risk**: Skill 文件在 GitLab 被重命名或移动路径
→ **Mitigation**: 同步时按路径匹配，找不到原路径时标记为删除，新路径视为新 Skill

**Trade-off**: 移除了 OSS 上传功能，用户无法通过 AI Studio 上传 Skill
→ **Acceptance**: 业务上接受，Skill 统一在 GitLab 管理

## Migration Plan

### Phase 1: 数据库变更（零停机）
1. 新增 `skill` 表字段（gitlab_repo, gitlab_path 等）
2. 新增 `skill_sync_log` 表
3. 旧字段（content_oss_key, content_url）保留但不再使用

### Phase 2: 后端开发
1. 实现 GitLab API Client
2. 实现 Sync Service
3. 改造 Skill Controller（移除创建/编辑，新增下载/raw 接口）
4. 配置定时任务

### Phase 3: 前端开发
1. 改造 Skill 列表页（移除表单，新增同步按钮）
2. 新增同步配置页面
3. 新增 Claude 命令复制功能

### Phase 4: 数据迁移
1. 准备 GitLab 仓库和初始 Skill 文件
2. 执行手动同步导入初始数据
3. 验证功能正常

### Phase 5: 清理（后续迭代）
1. 移除旧 OSS 字段（确认不再需要后）

## Open Questions

1. **GitLab 版本**：需确认公司 GitLab 版本支持哪些 API（至少需支持 Repository Files API）
2. **网络连通性**：AI Studio 服务能否访问 GitLab 私有部署地址
3. **初始数据**：现有 OSS 中的 Skill 是否需要迁移到 GitLab，还是重新创建
4. **权限映射**：GitLab 仓库的访问权限如何与 AI Studio 用户权限协调
