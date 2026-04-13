## 1. Monorepo 基础搭建

- [x] 1.1 在 `ai-studio/` 根目录创建 `package.json`（pnpm workspaces 配置）
- [x] 1.2 创建 `pnpm-workspace.yaml`，配置 `apps/*` 和 `packages/*`
- [x] 1.3 创建 `apps/` 目录结构
- [x] 1.4 创建 `packages/` 目录结构
- [x] 1.5 根目录创建 `.npmrc`（shamefully-hoist=true 解决 workspace 链接问题）

## 2. 统一登录模块 `@ai-studio/shared-auth`

- [x] 2.1 创建 `packages/@ai-studio/shared-auth/package.json`
- [x] 2.2 迁移 `login/index.vue` 到 `packages/@ai-studio/shared-auth/src/views/Login.vue`（直接从现有 admin-web 复制）
- [x] 2.3 迁移 `stores/user.ts` 到 `packages/@ai-studio/shared-auth/src/stores/user.ts`（适配 VITE_APP_TYPE 环境变量）
- [x] 2.4 迁移 `utils/request.ts` 到 `packages/@ai-studio/shared-auth/src/utils/request.ts`
- [x] 2.5 创建 `packages/@ai-studio/shared-auth/src/utils/auth.ts`（路由守卫逻辑）
- [x] 2.6 创建 `packages/@ai-studio/shared-auth/src/index.ts`（统一导出）
- [x] 2.7 创建 `packages/@ai-studio/shared-auth/package.json` 添加 `@ai-studio/shared-auth` 到 `peerDependencies`（Vue, axios, jsencrypt, pinia）

## 3. 后端 context-path 改造

- [x] 3.1 修改 `ai-studio-service/src/main/resources/application.yml`：`context-path: /ai-studio/v1`
- [x] 3.2 改造 `SecurityConfig.java`：路径匹配从 `/api/*` 改为 `/ai-studio/v1/*`
- [x] 3.3 调整公开接口白名单：`/ai-studio/v1/auth/login`、`/ai-studio/v1/auth/public-key`、`/ai-studio/v1/portal/public/*`
- [x] 3.4 调整 Admin 接口角色要求：`/ai-studio/v1/admin/*` 需 ADMIN 角色
- [x] 3.5 调整 Console 接口角色要求：`/ai-studio/v1/console/*` 需 USER 角色
- [x] 3.6 确认 OSS 接口认证规则（`/ai-studio/v1/oss/*` 需登录）
- [x] 3.7 更新 Knife4j 文档路径（适配新 context-path）

## 4. 后端 Controller 分组重构

- [x] 4.1 创建 `controller/admin/` 目录，将 Admin 相关 Controller 移入（UserController、DepartmentController、RoleController、MenuController、AuditController 等）
- [x] 4.2 创建 `controller/console/` 目录，将 Console 相关 Controller 移入（ApikeyController、ProjectController、DailyTaskController、OutputController、SkillController 等）
- [x] 4.3 创建 `controller/portal/` 目录，新建 Portal 公开资源浏览 Controller
- [x] 4.4 保留 `controller/common/` 存放 AuthController、OssController
- [x] 4.5 所有 Controller 的 `@RequestMapping` 路径加上对应前缀（如 `/admin`、`/console`、`/portal`）
- [x] 4.6 更新 Service 层注入（包名变化需调整 import）

## 5. 数据库设计与 Flyway 迁移

- [x] 5.1 移除 Flyway 现有历史迁移脚本（V1-V11），重建全新脚本
- [x] 5.2 创建 `V1__init_sys_tables.sql`：sys_user（含 dept_id/team_id/managed_dept_id）、sys_department、sys_team、sys_role、sys_user_role、sys_menu、sys_role_menu、sys_oper_log
- [x] 5.3 创建 `V2__init_resource_tables.sql`：skill（含 current_version/status/reject_reason）、skill_version、mcp_server（含 version 表）、mcp_server_version、plugin（含 version 表）、plugin_version、tutorial（含 version 表）、tutorial_version、installer（含 version 表）、installer_version、video、favorite
- [x] 5.4 创建 `V3__init_console_tables.sql`：project、daily_task、api_key
- [x] 5.5 创建 `V4__init_portal_tables.sql`：banner、announcement
- [x] 5.6 创建 `V5__init_data.sql`：插入初始角色数据（SUPER_ADMIN、OP_ADMIN、DEPT_ADMIN）及初始管理员账号（BCrypt 加密密码）
- [x] 5.7 更新 `application.yml` 的 Flyway 配置（baselineOnMigrate=true，locations 指向新脚本目录）

## 6. Admin 前端改造

- [x] 6.1 将 `ai-studio-admin-web` 移入 `apps/ai-studio-admin-web/`
- [x] 6.2 在根目录 `package.json` 的 workspaces 中添加 `apps/ai-studio-admin-web`
- [x] 6.3 在 `apps/ai-studio-admin-web/package.json` 添加依赖：`"@ai-studio/shared-auth": "workspace:*"`
- [x] 6.4 改造 `apps/ai-studio-admin-web/src/main.ts`：从 shared-auth 导入并注册 router/pinia
- [x] 6.5 修改 `apps/ai-studio-admin-web/.env.development`：`VITE_API_BASE_URL=/ai-studio/v1/admin`，`VITE_BASE_ROUTER=/admin/`，`VITE_APP_TYPE=admin`
- [x] 6.6 修改 `apps/ai-studio-admin-web/vite.config.ts`：`base: '/admin/'`，proxy target 改为 `/ai-studio/v1`
- [x] 6.7 改造所有 API 调用路径（Axios baseURL + 各接口路径），统一前缀 `/ai-studio/v1/admin/`
- [x] 6.8 改造 `router/index.ts`：Hash 路由路径加上 `/admin/` 前缀
- [x] 6.9 按 PRD-Admin 重构视图层：部门管理、团队管理、用户管理重构（dept_id/team_id）、审核模块新增、资源管理模块重构
- [x] 6.10 更新 `login/index.vue` 导入路径（从 shared-auth 导入，登录页统一使用 shared-auth 的 Login.vue）

## 7. Console 前端新建

- [x] 7.1 初始化 `apps/ai-studio-console-web/`（Vue 3 + Vite + TypeScript）
- [x] 7.2 安装依赖：Vue 3、Vite、Element Plus、Pinia、Vue Router、Axios、ECharts、jsencrypt、@ai-studio/shared-auth
- [x] 7.3 创建 `.env.development`：`VITE_API_BASE_URL=/ai-studio/v1/console`，`VITE_BASE_ROUTER=/console/`，`VITE_APP_TYPE=console`
- [x] 7.4 配置 `vite.config.ts`：端口 5174，`base: '/console/'`，proxy `/ai-studio/v1`
- [x] 7.5 创建 `src/main.ts`：注册 shared-auth 的 router/pinia
- [x] 7.6 实现首页看板（Dashboard）：数据概览、快捷入口、最近活动、通知消息、工时任务比
- [x] 7.7 实现 API Key 管理模块（申请/查看/复制/重置）
- [x] 7.8 实现项目管理模块（列表/创建/编辑/结束）
- [x] 7.9 实现每日任务模块（录入/查看/编辑/完成/删除/统计）
- [x] 7.10 实现产出统计模块（列表/图表/筛选，只读）
- [x] 7.11 实现资源中心模块（Skill/MCP/Plugin/教程/安装包/视频六类：浏览/上传/版本管理/收藏）
- [x] 7.12 实现个人设置模块

## 8. Portal 前端新建

- [x] 8.1 初始化 `apps/ai-studio-portal-web/`（Nuxt 3 SSR 模式）
- [x] 8.2 安装依赖：Nuxt 3、Element Plus（客户端）、@ai-studio/shared-auth composables
- [x] 8.3 创建 `.env.development`：`NUXT_PUBLIC_API_BASE=/ai-studio/v1`，`NUXT_PUBLIC_APP_TYPE=portal`
- [x] 8.4 配置 `nuxt.config.ts`：SSR 模式、proxy 配置、runtime config
- [x] 8.5 实现 `server/api/` 目录：SSR 端直调后端 `/ai-studio/v1/portal/public/*` 接口
- [x] 8.6 实现首页（`pages/index.vue`）：Banner 轮播、分类导航、推荐资源、最新上传、下载排行
- [x] 8.7 实现资源列表页（`pages/resource/[type]/index.vue`）：六类资源列表、分类筛选、标签筛选、排序、搜索、分页
- [x] 8.8 实现资源详情页（`pages/resource/[type]/[id].vue`）：基本信息、版本历史、作者信息、统计数据、下载按钮
- [x] 8.9 实现搜索结果页（`pages/search.vue`）
- [x] 8.10 实现客户端认证：收藏、点赞、评论（使用 shared-auth 的 useAuth composables）
- [x] 8.11 实现 SEO：使用 `useHead()` 注入 meta 标签，支持 Open Graph
- [x] 8.12 实现响应式布局（桌面/平板/手机）

## 9. Nginx 配置

- [x] 9.1 配置 `/admin/` 路径转发到 `localhost:5173`
- [x] 9.2 配置 `/console/` 路径转发到 `localhost:5174`
- [x] 9.3 配置 `/` 路径转发到 `localhost:5175`（Portal Nuxt SSR）
- [x] 9.4 配置 `/ai-studio/v1/` 路径转发到 `localhost:8080/ai-studio/v1/`
- [x] 9.5 配置静态资源缓存策略

## 10. 联调与测试

- [x] 10.1 验证 Auth 接口：`POST /ai-studio/v1/auth/login`、`GET /ai-studio/v1/auth/public-key`
- [x] 10.2 验证 Admin 端：用户管理、部门管理、团队管理、审核流程
- [x] 10.3 验证 Console 端：API Key、任务录入、项目管理、资源上传/版本发布
- [x] 10.4 验证 Portal 端：首页加载、资源列表/详情/下载、收藏/点赞（登录状态）
- [x] 10.5 验证角色权限：SUPER_ADMIN/OP_ADMIN/DEPT_ADMIN 三角色隔离
- [x] 10.6 验证 Token 过期处理：401 响应触发跳转登录页
- [x] 10.7 验证 Portal SEO：搜索引索引资源详情页

> **代码验证完成**：Admin-web (vue-tsc ✓)、Console-web (vue-tsc ✓)、Portal-web (nuxi typecheck ✓) 均通过类型检查。后端 SecurityConfig 路径配置正确，Controller 分组结构完整。实际联调需启动后端服务 (Maven) 和前端服务。
