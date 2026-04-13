## Why

当前 AI Studio 是单体架构（ai-studio-admin-web + ai-studio-service），缺少面向普通用户的 Portal（资源浏览）和面向认证用户的 Console（工作台）。随着产品扩展到 Skill/MCP/Plugin/教程/安装包/视频六类资源，必须拆分为 3 个前端 + 1 个后端，才能支撑各自的业务逻辑和独立迭代。

## What Changes

### 项目组织
- **BREAKING** 将现有项目改造为 **Monorepo**（pnpm workspaces），包含 3 个前端 App、1 个共享包、1 个后端
- 新增 `apps/ai-studio-console-web/`（Vue 3 新建）
- 新增 `apps/ai-studio-portal-web/`（Nuxt 3 SSR 新建）
- 改造 `apps/ai-studio-admin-web/`（现有 admin-web，路由前缀 /admin/）
- 新增 `packages/@ai-studio/shared-auth/`（统一登录模块，三个前端共享）

### 后端改造
- **BREAKING** `context-path` 从 `/ai-hub` 改为 `/ai-studio/v1`，所有 API 路径升级
- **BREAKING** Controller 按 `/admin/`、`/console/`、`/portal/`、`/common/` 四组拆分
- **BREAKING** SecurityConfig 路径匹配规则按新 API 前缀重新配置
- 复用现有 Auth（JWT+RSA+BCrypt）、OSS 文件上传逻辑

### API 路径
| 前端 | API 前缀 | 说明 |
|------|----------|------|
| 统一 | `/ai-studio/v1/auth/*` | 公开：登录、公钥 |
| Admin | `/ai-studio/v1/admin/*` | 需 ADMIN 角色 |
| Console | `/ai-studio/v1/console/*` | 需 USER 角色 |
| Portal | `/ai-studio/v1/portal/*` | 公开浏览；收藏/点赞需认证 |

### 数据库
- 全新 MySQL 数据库，Flyway 版本管理
- 新增 `sys_department`、`sys_team`（组织架构）
- 新增各资源的 **version 表**（skill_version、mcp_server_version、plugin_version、tutorial_version、installer_version）
- 新增 `video`、`installer`、`favorite`、`banner`、`announcement` 等表
- 新增 `project`、`daily_task`、`api_key`（Console 专属）

### 部署
- `/admin/` → ai-studio-admin-web
- `/console/` → ai-studio-console-web
- `/` → ai-studio-portal-web（Nuxt SSR）
- `/ai-studio/v1/*` → ai-studio-service

## Capabilities

### New Capabilities
- `portal-public-browse`: Portal 公开资源浏览（首页、列表、详情、搜索），无需登录
- `portal-user-interaction`: Portal 用户互动（收藏、点赞、评论），需认证用户
- `console-apikey-management`: Console 用户 API Key 申请/查看/重置
- `console-daily-task`: Console 用户每日任务录入和管理
- `console-project-management`: Console 项目创建、编辑、结束
- `console-resource-center`: Console 资源中心（Skill/MCP/Plugin/教程/安装包/视频的上传、版本管理、收藏）
- `console-output-stats`: Console 产出统计展示（只读，数据由其他团队写入）
- `admin-multi-role`: Admin 端三重角色体系（SUPER_ADMIN / OP_ADMIN / DEPT_ADMIN）
- `admin-department-team`: Admin 部门管理和团队管理（替代原有的简单部门字段）
- `admin-resource-audit`: Admin 内容审核（Skill/MCP/Plugin/教程/安装包/视频及其版本的审核）
- `admin-homepage-ops`: Admin 门户运营（Banner、推荐配置、公告、网站配置）
- `admin-realtime-dashboard`: Admin 实时监控大屏
- `shared-auth-module`: 跨前端共享的统一登录模块（Auth 逻辑、Token 管理、路由守卫）

### Modified Capabilities
- `skill-management`: 现有 Skill 功能扩展为支持多版本，审核流程独立到 Admin
- `mcp-management`: 现有 MCP 功能扩展为支持多版本
- `user-management`: 现有用户管理扩展部门/团队归属，角色体系改为 SUPER_ADMIN / OP_ADMIN / DEPT_ADMIN 三种

## Impact

### 影响的代码
- `ai-studio-admin-web/` — 改造：路由前缀、API 路径、视图按 PRD 重构
- `ai-studio-service/` — 改造：context-path、Controller 分组、SecurityConfig、数据库全量重设计

### 影响的 API
- **BREAKING** 所有 `/ai-hub/api/*` 路径变更为 `/ai-studio/v1/*`
- Auth 接口路径不变（统一入口）
- 新增 Portal 公开资源 API
- 新增 Console 任务/项目/产出 API

### 依赖项
- pnpm workspaces（Monorepo 组织）
- Flyway（数据库迁移）
- Nuxt 3（Portal SSR）
- 现有七牛云 OSS 配置（复用）

### 环境变更
- 后端 `application.yml`：`context-path: /ai-studio/v1`
- 前端 `.env`：`VITE_API_BASE_URL=/ai-studio/v1`
- Nginx：配置 `/admin/` `/console/` `/` `/ai-studio/v1/` 四个分发动态
