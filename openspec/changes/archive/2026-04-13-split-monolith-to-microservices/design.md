## Context

当前 AI Studio 是单体应用：
- `ai-studio-admin-web`（Vue 3 SPA）负责 Admin 管理功能
- `ai-studio-service`（Spring Boot）包含所有业务逻辑和数据库访问

业务定位扩展后，需要：
- **Portal**：面向所有用户，浏览/下载 Skill/MCP/Plugin/教程/安装包/视频，无需登录
- **Console**：面向认证用户，工作台（任务、项目、API Key、产出统计、资源上传/版本管理）
- **Admin**：面向平台运营，审核、用户管理、部门管理、数据报表

技术约束：
- API 路径统一前缀 `/ai-studio/v1`（后端 `context-path` 变更）
- Monorepo 组织（pnpm workspaces）
- 三个前端共享同一套登录逻辑（复用现有 `login/index.vue`）
- 数据库全新搭建，Flyway 管理迁移
- OSS 上传、Auth 逻辑直接复用

## Goals / Non-Goals

**Goals:**
- 三个前端独立部署、独立迭代，互不影响
- 后端 API 按业务域拆分（admin/console/portal），路径清晰
- 统一登录模块跨前端共享，维护一致
- 数据库全新设计，完整支撑 PRD 定义的数据模型
- Portal 支持 SEO（Nux t3 SSR）

**Non-Goals:**
- 不做微服务拆分，后端仍为单体（只是 Controller 路径分组）
- 不迁移现有业务数据
- 不实现单点登录（SSO）或第三方登录（OAuth）
- 不实现 LDAP/AD 集成

## Decisions

### Decision 1: Monorepo 结构（pnpm workspaces）

**选择：** 在 `ai-studio/` 根目录建立 pnpm workspaces

```
ai-studio/                          # pnpm workspaces root
├── package.json                    # { "workspaces": ["apps/*", "packages/*"] }
├── apps/
│   ├── ai-studio-admin-web/       # 改造，端口 5173，路径 /admin/
│   ├── ai-studio-console-web/     # 新建，端口 5174，路径 /console/
│   └── ai-studio-portal-web/      # 新建，端口 5175，路径 /
├── packages/
│   └── @ai-studio/shared-auth/    # 统一登录模块
└── ai-studio-service/             # Spring Boot，后端服务
```

**替代方案：**
- Polyrepo（各项目独立仓库）：共享代码需 npm 包发布流程，增加复杂度
- Nx：学习曲线高，pnpm workspaces 足够简单

---

### Decision 2: 后端 `context-path` 改为 `/ai-studio/v1`

**选择：** `application.yml` 中 `server.servlet.context-path: /ai-studio/v1`

**理由：** 与前端部署路径 `/admin/` `/console/` `/` 形成清晰对应关系，Nginx 只需把 `/ai-studio/v1` 转发到后端，其余按路径分发给各前端。

**API 路径规范：**
```
/ai-studio/v1/auth/login                    # 公开
/ai-studio/v1/auth/public-key               # 公开
/ai-studio/v1/admin/*                       # Admin 端，需 ADMIN 角色
/ai-studio/v1/console/*                     # Console 端，需 USER 角色
/ai-studio/v1/portal/public/*               # Portal 公开浏览，无需认证
/ai-studio/v1/portal/*                      # Portal 需认证操作（收藏/点赞/评论）
/ai-studio/v1/oss/*                         # OSS 上传下载，需认证
```

---

### Decision 3: 统一登录模块 `@ai-studio/shared-auth`

**选择：** 抽取为独立 package，三个前端通过 workspace 依赖共享

`apps/*/package.json`:
```json
"@ai-studio/shared-auth": "workspace:*"
```

统一登录模块内容：
```
packages/@ai-studio/shared-auth/
├── src/
│   ├── views/Login.vue           # 直接复用现有 login/index.vue
│   ├── stores/user.ts           # 登录/登出/Token/角色判断
│   ├── utils/request.ts         # Axios 封装（Bearer Token）
│   ├── utils/auth.ts            # 登录判断守卫
│   └── index.ts                 # 导出所有共享内容
└── package.json
```

**各前端差异化配置（通过环境变量或参数注入）：**
| 前端 | 登录后跳转 | 路由前缀 |
|------|-----------|---------|
| Admin | `/admin/dashboard` | `/admin/` |
| Console | `/console/dashboard` | `/console/` |
| Portal | `/` | `/` |

**实现方式：** Login.vue 读取环境变量 `VITE_APP_TYPE`（admin/console/portal）决定跳转目标；router 守卫同理。

---

### Decision 4: 数据库全新设计（Flyway）

**选择：** 清空现有数据，用 Flyway 管理全新数据库 schema

**迁移脚本顺序：**
```
V1__init_sys_tables.sql          # 用户/角色/部门/团队/菜单
V2__init_resource_tables.sql     # Skill/MCP/Plugin/Tutorial + 各 version 表
V3__init_console_tables.sql      # Project/DailyTask/ApiKey
V4__init_portal_tables.sql       # Banner/Announcement/Video/Installer/Favorite
V5__init_data.sql                # 初始角色数据（SUPER_ADMIN/OP_ADMIN/DEPT_ADMIN）
```

**资源独立建表（按 PRD）：**
- `skill` + `skill_version`
- `mcp_server` + `mcp_server_version`
- `plugin` + `plugin_version`
- `tutorial` + `tutorial_version`
- `installer` + `installer_version`
- `video`（无版本）

---

### Decision 5: SecurityConfig 路径匹配改造

**选择：** 基于 Ant风格的路径模式匹配

```java
// 公开接口
.requestMatchers("/ai-studio/v1/auth/**").permitAll()
.requestMatchers("/ai-studio/v1/portal/public/**").permitAll()

// Admin 端
.requestMatchers("/ai-studio/v1/admin/**").hasAnyRole("SUPER_ADMIN", "OP_ADMIN", "DEPT_ADMIN")

// Console 端
.requestMatchers("/ai-studio/v1/console/**").hasRole("USER")

// Portal 认证操作（需登录）
.requestMatchers("/ai-studio/v1/portal/favorite/**",
                 "/ai-studio/v1/portal/like/**",
                 "/ai-studio/v1/portal/comment/**").authenticated()

// OSS（各端共用，需登录）
.requestMatchers("/ai-studio/v1/oss/**").authenticated()
```

---

### Decision 6: 前端 Vite proxy 配置

**各前端 `vite.config.ts` 中的 server.proxy：**
```js
server: {
  proxy: {
    '/ai-studio/v1': {
      target: 'http://localhost:8080',
      changeOrigin: true,
    },
  }
}
```

**各前端 `.env.development`：**
```
# Admin
VITE_API_BASE_URL=/ai-studio/v1/admin
VITE_BASE_ROUTER=/admin/
VITE_APP_TYPE=admin

# Console
VITE_API_BASE_URL=/ai-studio/v1/console
VITE_BASE_ROUTER=/console/
VITE_APP_TYPE=console

# Portal
VITE_API_BASE_URL=/ai-studio/v1
VITE_BASE_ROUTER=/
VITE_APP_TYPE=portal
```

---

### Decision 7: Portal Nuxt 3 SSR 方案

**选择：** Nuxt 3 SSR 模式，公开页面服务端渲染，认证操作走客户端

```
ai-studio-portal-web/
├── nuxt.config.ts
├── app.vue
├── pages/
│   ├── index.vue              # 首页（Banner、分类、推荐、最新）
│   ├── resource/
│   │   ├── [type]/index.vue  # 资源列表（skill/mcp/plugin/tutorial/installer/video）
│   │   └── [type]/[id].vue   # 资源详情
│   └── search.vue             # 搜索结果页
├── server/api/               # SSR 端直调后端 API
│   └── resource/
└── composables/
    └── useAuth.ts            # 客户端认证状态
```

**SEO 策略：** 资源详情页使用 `useHead()` 注入 meta 标签，支持 Open Graph。

---

### Decision 8: Nginx 分发配置

```nginx
server {
    listen 80;
    server_name ai-coding.com;

    # 前端应用
    location /admin/ {
        proxy_pass http://localhost:5173/;
        try_files $uri $uri/ /admin/index.html;
    }

    location /console/ {
        proxy_pass http://localhost:5174/;
        try_files $uri $uri/ /console/index.html;
    }

    location / {
        proxy_pass http://localhost:5175/;
        # Nuxt SSR
    }

    # 后端 API
    location /ai-studio/v1/ {
        proxy_pass http://localhost:8080/ai-studio/v1/;
        proxy_set_header Host $host;
    }
}
```

---

## Risks / Trade-offs

| 风险 | 描述 | 缓解措施 |
|------|------|---------|
| **前端项目重建工作量** | Console 和 Portal 是全新项目，Admin 也需大规模改造 | 按 tasks.md 分阶段实施，先建基础设施再填业务 |
| **三个前端共享登录的维护** | Login.vue 修改时需同步到所有前端 | 统一抽取到 `@ai-studio/shared-auth`，通过 workspace 确保版本一致 |
| **Portal SSR SEO 复杂度** | Nuxt 3 SSR 相比 SPA 有更高部署复杂度 | 开发阶段用 `nuxt dev`，生产用 Node 进程管理 |
| **数据库全量重建** | 现有数据无法迁移，需重新录入 | 全新数据库只是 schema 新建，不影响新功能开发 |
| **API 路径变更** | 所有前端需同步修改 API 调用前缀 | 后端先改 context-path，前端按模块逐步适配 |
| **Monorepo CI/CD** | pnpm workspaces 在部分 CI 平台需特殊配置 | 使用官方 pnpm/setup-action，缓存 workspace 依赖 |

---

## Migration Plan

### Phase 1: 基础设施搭建（1-2 周）
1. 创建 Monorepo 结构（package.json、pnpm-workspace.yaml）
2. 创建 `@ai-studio/shared-auth` 包，迁移登录逻辑
3. 后端改动 context-path = `/ai-studio/v1`，调整 SecurityConfig
4. Flyway 数据库初始化脚本（V1-V5）
5. 各前端项目基础架子（Vite/Nuxt 配置、proxy、.env）
6. Nginx 配置更新

### Phase 2: 后端 API 改造（1 周）
1. Controller 按 admin/console/portal/common 分组重命名
2. 逐模块适配 API 路径变更
3. 数据库 Entity/Mapper 按新表结构改造

### Phase 3: Admin 前端改造（1 周）
1. 路由前缀改为 `/admin/`
2. API 路径前缀改为 `/ai-studio/v1/admin/`
3. 按 PRD-Admin 重构视图层
4. 接入 shared-auth 统一登录

### Phase 4: Console 前端新建（2 周）
1. 项目初始化（Vue 3 + Vite + Element Plus）
2. 接入 shared-auth
3. 按 PRD-Console 实现各模块

### Phase 5: Portal 前端新建（2 周）
1. 项目初始化（Nuxt 3 SSR）
2. 实现首页、列表、详情、搜索等公开页面
3. 客户端实现收藏/点赞/评论（需认证）
4. SEO 优化

### Rollback
- Phase 1-2 完成后可随时切换回单体，只需 Nginx 改回原路径
- Phase 3+ 完成后需保留旧版 Admin web 作为备份

---

## Open Questions

1. **Portal 的视频资源如何播放？** 是使用自带播放器还是集成第三方（如 Video.js）？
2. **Installer 资源下载是否走 OSS 直链？** 还是通过后端代理？
3. **Banner/公告等运营数据是否有初始数据需求？** 还是完全由 Admin 录入？
4. **Console 的 API Key 生成逻辑是否复用现有？** 现有是前端模拟生成，是否需要后端真正生成？
5. **Portal 的第三方统计（百度统计/GA）是否需要埋点？**
