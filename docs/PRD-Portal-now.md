# Portal 项目代码现状总结

> 生成时间：2026-04-15
> 基于分支：release

---

## 一、项目概述

Portal 是 AI Studio 的**资源门户网站**，面向所有用户（无需登录）提供 Skill、MCP、Plugin、教程、安装包、视频等资源的**浏览、搜索、下载**功能。

- **前端**：`apps/ai-studio-portal-web`（Nuxt 3，SSR）
- **后端**：`ai-studio-service`（Java Spring Boot）

---

## 二、前端代码结构

### 2.1 技术栈

| 项 | 值 |
|----|----|
| 框架 | Nuxt 3.11（SSR 模式） |
| UI 库 | Element Plus 2.7 |
| HTTP 客户端 | axios、fetch |
| 端口 | 5175 |
| API 代理 | `/ai-studio/v1` → `http://localhost:8080` |

### 2.2 目录结构

```
apps/ai-studio-portal-web/
├── app.vue                    # 根组件：NuxtLayout + NuxtPage
├── nuxt.config.ts            # SSR=true，代理配置，runtimeConfig
├── pages/
│   ├── index.vue              # 首页：Banner + 分类导航 + 推荐/最新资源
│   ├── search.vue             # 搜索页：关键词 + 类型筛选 + 排序 + 分页
│   └── resource/
│       └── [type]/
│           ├── index.vue     # 资源列表页（6种类型）
│           └── [id].vue       # 资源详情页
├── layouts/
│   └── default.vue            # 布局：Header导航 + Footer
├── composables/
│   └── useUser.ts             # 用户状态：login/logout/isLoggedIn/initUserInfo
├── server/api/                # Nuxt Server Routes（SSR 数据获取）
│   ├── banner.get.ts          # → /portal/public/banner
│   ├── announcement.get.ts    # → /portal/public/announcement
│   ├── search.get.ts          # → /portal/public/search
│   └── resource/[type].get.ts  # → /portal/public/{type}
│       └── [type]/[id].get.ts # 资源详情（后端接口不存在）
└── public/
```

### 2.3 页面清单

| 页面路由 | 文件 | 功能 | 状态 |
|---------|------|------|------|
| `/` | `pages/index.vue` | Banner轮播、分类导航、推荐/最新资源 | ⚠️ 静态数据，未对接API |
| `/search` | `pages/search.vue` | 关键词搜索、类型筛选、排序、分页 | ⚠️ TODO，API未联调 |
| `/resource/[type]` | `pages/resource/[type]/index.vue` | 资源列表（skill/mcp/plugin/tutorial/installer/video） | ⚠️ TODO，API未联调 |
| `/resource/[type]/[id]` | `pages/resource/[type]/[id].vue` | 资源详情、版本选择、下载、点赞、收藏 | ⚠️ TODO，API未联调 |

### 2.4 布局（layouts/default.vue）

```
Header: Logo + 导航链接 + 登录/用户下拉菜单
Main:   <slot />
Footer: © 2026 AI Studio
```

- 导航链接：首页、Skill、MCP、Plugin、教程、安装包、视频
- 已登录用户显示下拉菜单（控制台入口、退出登录）
- 未登录用户显示"登录"按钮

### 2.5 用户状态（composables/useUser.ts）

```typescript
// 导出一个 useUser composable
const userInfo = useState<any>('userInfo', () => null)  // Nuxt 服务端状态

isLoggedIn()      // !!localStorage.getItem('token')，需 import.meta.client 守卫
login(username, password)   // fetch /ai-studio/v1/open/auth/login
logout()          // 清除 localStorage 和 userInfo
initUserInfo()     // 从 localStorage 恢复用户信息
```

> **注意**：所有 `localStorage` 调用已加 `import.meta.client` 守卫，防止 SSR 报错。

### 2.6 Server API Routes

Nuxt Server Routes 做服务端代理，将请求转发到后端 `/ai-studio/v1`：

| 文件 | 后端路径 | 说明 |
|------|---------|------|
| `banner.get.ts` | `/portal/public/banner` | ⚠️ 后端返回 null（TODO） |
| `announcement.get.ts` | `/portal/public/announcement` | ⚠️ 后端返回 null（TODO） |
| `search.get.ts` | `/portal/public/search` | ⚠️ 后端该接口不存在 |
| `resource/[type].get.ts` | `/portal/public/{type}` | 资源列表 |
| `resource/[type]/[id].get.ts` | `/portal/public/{type}/{id}` | ⚠️ 后端该接口不存在 |

---

## 三、后端代码结构

### 3.1 技术栈

| 项 | 值 |
|----|----|
| 框架 | Spring Boot 3.x |
| ORM | MyBatis Plus |
| 认证 | JWT + RSA + BCrypt |
| 数据库 | MySQL |
| API 文档 | Swagger（io.swagger.v3.oas） |

### 3.2 关键包结构

```
com.aistudio.service/
├── controller/
│   ├── portal/
│   │   ├── PortalPublicInfoController.java    # /portal/public
│   │   └── PortalResourceBrowseController.java # /portal/resource
│   └── common/
│       └── OpenOutputController.java          # /open/output（无需认证）
├── service/
│   ├── AuthService.java                      # 登录认证
│   └── impl/
│       └── AuthServiceImpl.java
├── entity/
│   ├── Skill.java / SkillVersion.java
│   ├── Plugin.java
│   ├── Tutorial.java
│   ├── McpServer.java
│   └── SysUser.java / SysRole.java / ...
└── dto/
    ├── request/LoginRequest.java
    └── response/LoginResponse.java
```

### 3.3 Portal 相关 Controller

#### PortalPublicInfoController（`/portal/public`）— ⚠️ 均为 TODO

| 接口 | 方法 | 路径 | 状态 |
|------|------|------|------|
| Banner列表 | GET | `/portal/public/banner` | TODO，返回 null |
| 公告列表 | GET | `/portal/public/announcement` | TODO，返回 null |
| 站点信息 | GET | `/portal/public/site-info` | TODO，返回 null |

#### PortalResourceBrowseController（`/portal/resource`）— 基础可用

| 接口 | 方法 | 路径 | 状态 |
|------|------|------|------|
| Skill列表 | GET | `/portal/resource/skill/list` | ✅ |
| Skill详情 | GET | `/portal/resource/skill/{id}` | ✅ |
| Plugin列表 | GET | `/portal/resource/plugin/list` | ✅ |
| Plugin详情 | GET | `/portal/resource/plugin/{id}` | ✅ |
| 教程列表 | GET | `/portal/resource/tutorial/list` | ✅ |
| 教程详情 | GET | `/portal/resource/tutorial/{id}` | ✅ |
| MCP列表 | — | — | ❌ 未实现 |
| MCP详情 | — | — | ❌ 未实现 |
| 安装包 | — | — | ❌ 未实现 |
| 视频 | — | — | ❌ 未实现 |

#### OpenOutputController（`/open/output`）— 成员产出开放接口

| 接口 | 方法 | 路径 | 认证 |
|------|------|------|------|
| 提交产出 | POST | `/open/output/submit` | ❌ 无需认证 |
| 今日产出 | GET | `/open/output/today?username=xxx` | ❌ 无需认证 |
| 历史产出 | GET | `/open/output/history` | ❌ 无需认证 |

#### 认证接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 登录 | POST | `/api/auth/login` | ✅ RSA解密密码 + BCrypt校验 |

### 3.4 数据模型

#### Skill（`skill` 表）

```java
Long id;
String name;           // 名称
String description;    // 描述
String category;       // 分类
String source;         // 来源
Long latestVersionId; // 最新版本ID
String latestVersion;  // 最新版本号
Integer totalVersions; // 版本总数
String author;         // 作者
String tags;           // 标签
Long createdBy;        // 创建人
Long deptId;           // 部门
Integer downloadCount; // 下载次数
LocalDateTime createdAt;
LocalDateTime updatedAt;
```

#### SkillVersion（`skill_version` 表）

```java
Long id;
Long skillId;
String version;         // 版本号 1.2.3
Integer major/minor/patch;
Integer versionNumber;   // 用于排序
String ossKey;          // OSS存储key
String ossUrl;          // OSS访问URL
Long fileSize;          // 文件大小
String changelog;       // 变更说明
Long createdBy;
LocalDateTime createdAt;
```

#### Plugin（`plugin` 表）

```java
Long id;
String name;
String description;
String type;            // 类型
String version;
String fileOssKey;
String fileUrl;
Long fileSize;
Integer status;
Integer downloadCount;
Long createdBy;
LocalDateTime createdAt;
LocalDateTime updatedAt;
```

#### Tutorial（`tutorial` 表）

```java
Long id;
String title;
String category;
String tags;
String contentOssKey;   // 内容OSS key
String contentUrl;
Integer status;
Integer viewCount;
Long createdBy;
LocalDateTime createdAt;
LocalDateTime updatedAt;
```

#### McpServer（`mcp_server` 表）

```java
Long id;
String name;
String description;
String apiEndpoint;
String authType;
String configJson;
Integer status;
Long createdBy;
LocalDateTime createdAt;
LocalDateTime updatedAt;
```

### 3.5 登录认证流程（AuthServiceImpl）

```
1. 前端用 RSA 公钥加密密码 → POST /api/auth/login
2. 后端用 RSA 私钥解密得到明文密码
3. 查询 sys_user 表，比对 BCrypt 密码
4. 校验用户状态（status != 0）
5. 查询用户角色
6. JWT TokenProvider 生成 token
7. 返回 LoginResponse { token, userId, username, realName, roles[] }
```

---

## 四、现状问题与待办

### 4.1 前端待办

| # | 问题 | 说明 |
|---|------|------|
| 1 | 首页静态数据 | `banners`、`categories`、`featuredResources`、`latestResources` 均为静态数据，需对接 API |
| 2 | 资源列表未联调 | `pages/resource/[type]/index.vue` 的 `fetchResources()` 为空，需调用 server API |
| 3 | 资源详情未联调 | `pages/resource/[type]/[id].vue` 的 `onMounted` 为空，需获取资源详情 |
| 4 | 搜索页未联调 | `pages/search.vue` 的 `fetchResults()` 为空 |
| 5 | 登录页不存在 | layout 中引用了 `/login` 路由，但该页面不存在 |
| 6 | Banner/公告接口 | server/api 中有代理但后端返回 null |

### 4.2 后端待办

| # | 问题 | 说明 |
|---|------|------|
| 1 | `/portal/public/banner` | TODO，返回 null |
| 2 | `/portal/public/announcement` | TODO，返回 null |
| 3 | `/portal/public/site-info` | TODO，返回 null |
| 4 | `/portal/public/search` | 接口不存在，需新增统一搜索接口 |
| 5 | `/portal/resource/mcp/list` | MCP 资源浏览接口不存在 |
| 6 | `/portal/resource/mcp/{id}` | MCP 资源详情接口不存在 |
| 7 | `/portal/resource/installer` | 安装包类型接口不存在 |
| 8 | `/portal/resource/video` | 视频类型接口不存在 |
| 9 | `/open/auth/login` | portal 前端 useUser.ts 调用了此接口但不存在，实际用 `/api/auth/login` |
| 10 | 点赞/收藏/评论 | Portal PRD 中规划的功能，后端无对应接口 |

### 4.3 安全问题

| # | 问题 | 说明 |
|---|------|------|
| 1 | 登录接口路径 | 前端调用 `/ai-studio/v1/open/auth/login`，但后端只有 `/api/auth/login`，路径不匹配 |
| 2 | `useUser.ts` 中 login 未区分 admin/portal | portal 应调用公开登录接口，当前直接调用 admin 接口 |

---

## 五、前后端接口映射表

### 5.1 已实现

| 前端调用 | Server Route | 后端 Controller | 路径 | 状态 |
|---------|-------------|----------------|------|------|
| — | — | AuthService | POST /api/auth/login | ✅ |
| — | — | SkillController | GET /api/skill/list | ✅ 管理端 |
| — | — | SkillController | GET /api/skill/{id} | ✅ 管理端 |
| — | — | PluginController | GET /api/plugin/list | ✅ 管理端 |
| — | — | PluginController | GET /api/plugin/{id} | ✅ 管理端 |
| — | — | TutorialController | GET /api/tutorial/list | ✅ 管理端 |
| — | — | TutorialController | GET /api/tutorial/{id} | ✅ 管理端 |

### 5.2 Portal 公开接口（后端已有）

| 接口 | 方法 | 路径 | 对应前端 |
|------|------|------|---------|
| Skill列表 | GET | `/portal/resource/skill/list` | ⚠️ 未联调 |
| Skill详情 | GET | `/portal/resource/skill/{id}` | ⚠️ 未联调 |
| Plugin列表 | GET | `/portal/resource/plugin/list` | ⚠️ 未联调 |
| Plugin详情 | GET | `/portal/resource/plugin/{id}` | ⚠️ 未联调 |
| 教程列表 | GET | `/portal/resource/tutorial/list` | ⚠️ 未联调 |
| 教程详情 | GET | `/portal/resource/tutorial/{id}` | ⚠️ 未联调 |

### 5.3 Portal 缺失接口

| 资源类型 | 列表接口 | 详情接口 |
|---------|---------|---------|
| MCP | `/portal/resource/mcp/list` ❌ | `/portal/resource/mcp/{id}` ❌ |
| 安装包 | `/portal/resource/installer/list` ❌ | `/portal/resource/installer/{id}` ❌ |
| 视频 | `/portal/resource/video/list` ❌ | `/portal/resource/video/{id}` ❌ |
| 统一搜索 | — | `/portal/public/search` ❌ |
| Banner | — | `/portal/public/banner` ⚠️ TODO |
| 公告 | — | `/portal/public/announcement` ⚠️ TODO |
| 站点信息 | — | `/portal/public/site-info` ⚠️ TODO |

---

## 六、关键文件路径索引

### 前端

| 文件 | 说明 |
|------|------|
| `apps/ai-studio-portal-web/nuxt.config.ts` | Nuxt 配置、代理、SSR |
| `apps/ai-studio-portal-web/pages/index.vue` | 首页 |
| `apps/ai-studio-portal-web/pages/search.vue` | 搜索页 |
| `apps/ai-studio-portal-web/pages/resource/[type]/index.vue` | 资源列表 |
| `apps/ai-studio-portal-web/pages/resource/[type]/[id].vue` | 资源详情 |
| `apps/ai-studio-portal-web/layouts/default.vue` | 布局（含Header/Footer） |
| `apps/ai-studio-portal-web/composables/useUser.ts` | 用户状态管理 |

### 后端

| 文件 | 说明 |
|------|------|
| `controller/portal/PortalPublicInfoController.java` | 公开信息（Banner/公告） |
| `controller/portal/PortalResourceBrowseController.java` | 资源浏览 |
| `controller/common/OpenOutputController.java` | 产出开放接口 |
| `service/impl/AuthServiceImpl.java` | 登录认证逻辑 |
| `entity/Skill.java` | Skill 实体 |
| `entity/SkillVersion.java` | Skill 版本实体 |
| `entity/Plugin.java` | Plugin 实体 |
| `entity/Tutorial.java` | 教程实体 |
| `entity/McpServer.java` | MCP 实体 |
