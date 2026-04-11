---
version: 1.0
projectName: AI Coding 统一公共模块
generatedAt: 2026-04-11
changeLog: |
  - v1.0: 初始版本，定义统一登录模块和公共组件
---

# AI Coding 统一公共模块 - 产品需求规格说明书

## 1. 文档说明

### 1.1 目的

本模块为 AI Coding 平台的公共基础设施，为 Portal、Console、Admin 三个前端提供统一的登录认证、权限控制、HTTP 请求管理等基础能力，避免各前端重复实现。

### 1.2 适用范围

| 前端项目 | 使用方式 |
|----------|----------|
| ai-studio-portal-web | 引用本模块实现登录、权限 |
| ai-studio-console-web | 引用本模块实现登录、权限 |
| ai-studio-admin-web | 引用本模块实现登录、权限 |

### 1.3 约束说明

- 三个前端共用同一个后端服务 `ai-studio-service`
- 登录接口：`POST /api/auth/login`
- 认证方式：JWT Token
- 密码加密：RSA + BCrypt（后端双重保障）

---

## 2. 模块结构

```
packages/
└── shared/                    # 共享模块（未来 Monorepo 场景）
    ├── auth/                  # 登录认证模块
    ├── permission/            # 权限控制模块
    ├── http/                  # HTTP 请求模块
    └── utils/                 # 工具函数
```

> 当前阶段：各前端独立实现，登录逻辑参考本规范保持一致。

---

## 3. 模块 C01：统一登录认证

| 配置项 | 值 |
|--------|-----|
| 模块代码 | `common_auth` |
| 优先级 | P0 |
| 共享方式 | 各前端独立实现，逻辑保持一致 |

### 3.1 登录流程

```
用户输入账号密码
       │
       ▼
前端获取 RSA 公钥（GET /api/auth/public-key）
       │
       ▼
前端使用公钥加密密码，发送登录请求
       │
       ▼
后端验证（RSA解密 → BCrypt比对）→ 返回 JWT Token
       │
       ▼
前端存储 Token，跳转首页
```

### 3.2 功能点

| 功能 ID | 功能名称 | 用户故事 | 验收标准 |
|---------|---------|---------|---------|
| C01-F01 | 获取加密公钥 | 作为用户，我希望登录过程安全，以防密码泄露 | 调用 `/api/auth/public-key` 获取 RSA 公钥 |
| C01-F02 | 密码加密传输 | 作为用户，我希望密码加密后传输，以防中间人攻击 | 使用 RSA 公钥加密密码后再发送 |
| C01-F03 | 登录验证 | 作为用户，我希望使用账号密码登录 | 调用 `/api/auth/login` 验证，返回 Token |
| C01-F04 | 记住登录状态 | 作为用户，我希望关闭浏览器后仍保持登录 | Token 存储在 localStorage |
| C01-F05 | 登出 | 作为用户，我希望退出登录 | 清除 Token，跳转登录页 |

### 3.3 登录接口

#### 获取 RSA 公钥

```
GET /api/auth/public-key

Response:
{
  "code": 200,
  "data": "BASE64编码的RSA公钥"
}
```

#### 用户登录

```
POST /api/auth/login

Request:
{
  "username": "string",
  "password": "string"  // RSA 加密后的密文
}

Response:
{
  "code": 200,
  "data": {
    "token": "JWT Token",
    "userId": 1,
    "username": "zhangsan",
    "realName": "张三",
    "roles": ["ROLE_USER"]
  }
}

Error Response:
{
  "code": 401,
  "message": "用户名或密码错误"
}
```

### 3.4 登录页面原型

```
+----------------------------------------------------------+
|                                                          |
|                                                          |
|                    AI Coding                              |
|                                                          |
|              +--------------------------------+           |
|              |                                |           |
|              |  用户名/邮箱: [              ] |           |
|              |                                |           |
|              |  密码:        [              ] |           |
|              |                                |           |
|              |         [      登录      ]    |           |
|              |                                |           |
|              |  忘记密码？  [注册账号]        |           |
|              |                                |           |
|              +--------------------------------+           |
|                                                          |
+----------------------------------------------------------+
```

### 3.5 Token 管理规范

| 项目 | 规范 |
|------|------|
| 存储位置 | localStorage（key: `ai_studio_token`） |
| Token 有效期 | 7 天（后端可配置） |
| 续期机制 | 无需主动续期，过期后重新登录 |
| 敏感操作 | 如重置 Key、删除资源，需二次验证 |

### 3.6 错误处理

| 场景 | 处理方式 |
|------|----------|
| 用户名不存在 | 提示"用户名或密码错误"（不区分具体原因） |
| 密码错误 | 提示"用户名或密码错误" |
| 账号被禁用 | 提示"账号已被禁用，请联系管理员" |
| 网络错误 | 提示"网络异常，请重试" |
| Token 过期 | 跳转登录页，提示"登录已过期，请重新登录" |

---

## 4. 模块 C02：HTTP 请求封装

| 配置项 | 值 |
|--------|-----|
| 模块代码 | `common_http` |
| 优先级 | P0 |

### 4.1 请求拦截器

| 类型 | 处理逻辑 |
|------|----------|
| 请求拦截 | 携带 `Authorization: Bearer {token}` 请求头 |
| 响应拦截 | 401 时清除 Token 并跳转登录页 |

### 4.2 请求示例

```typescript
// 各前端统一的请求配置
const request = axios.create({
  baseURL: '/api',
  timeout: 30000,
})

// 请求拦截 - 自动携带 Token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('ai_studio_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截 - Token 过期处理
request.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('ai_studio_token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)
```

---

## 5. 模块 C03：路由权限控制

| 配置项 | 值 |
|--------|-----|
| 模块代码 | `common_permission` |
| 优先级 | P0 |

### 5.1 路由守卫

| 场景 | 处理方式 |
|------|----------|
| 已登录，访问/login | 跳转首页 |
| 未登录，访问需授权页面 | 跳转登录页 |
| 已登录，Token 过期 | 跳转登录页 |
| 有 Token，但无菜单权限 | 跳转 403 页面或首页 |

### 5.2 权限验证

```typescript
// 路由配置示例
const routes = [
  {
    path: '/dashboard',
    name: 'Dashboard',
    meta: { requiresAuth: true },
  },
  {
    path: '/admin',
    name: 'Admin',
    meta: { requiresAuth: true, roles: ['ROLE_ADMIN'] },
  },
]

// 路由守卫伪代码
router.beforeEach((to, from) => {
  const token = localStorage.getItem('ai_studio_token')

  if (to.meta.requiresAuth && !token) {
    return { path: '/login' }
  }

  if (to.meta.roles) {
    const userRoles = store.state.user.roles
    const hasPermission = to.meta.roles.some(role => userRoles.includes(role))
    if (!hasPermission) {
      return { path: '/403' }
    }
  }
})
```

---

## 6. 模块 C04：用户信息管理

| 配置项 | 值 |
|--------|-----|
| 模块代码 | `common_user` |
| 优先级 | P0 |

### 6.1 用户信息存储

| 信息 | 存储位置 | 说明 |
|------|----------|------|
| Token | localStorage | 认证凭证 |
| userId | Pinia store | 用户ID |
| username | Pinia store | 用户名 |
| realName | Pinia store | 真实姓名 |
| roles | Pinia store | 角色列表 |

### 6.2 登录响应数据

```typescript
interface LoginResponse {
  token: string       // JWT Token
  userId: number      // 用户ID
  username: string    // 用户名
  realName: string    // 真实姓名
  roles: string[]     // 角色列表，如 ['ROLE_USER', 'ROLE_ADMIN']
}
```

---

## 7. 模块 C05：公共 UI 组件

| 配置项 | 值 |
|--------|-----|
| 模块代码 | `common_components` |
| 优先级 | P1 |

### 7.1 组件列表

| 组件名 | 说明 | 使用位置 |
|--------|------|----------|
| `UserAvatar` | 用户头像组件 | Header、评论區 |
| `LoadingMask` | 全局加载遮罩 | 页面切换 |
| `MessageTip` | 消息提示封装 | 表单提交反馈 |
| `ConfirmDialog` | 确认对话框 | 删除等危险操作 |
| `EmptyState` | 空状态展示 | 列表为空时 |
| `PageHeader` | 页面标题栏 | 各页面顶部 |

---

## 8. 安全规范

| 项 | 规范 |
|-----|------|
| 密码传输 | 必须 RSA 加密后传输 |
| Token 存储 | localStorage，避免 XSS |
| CSRF | 后端接口已禁用 CSRF（.csrf().disable()） |
| 敏感操作 | 删除/重置等操作需前端二次确认 |
| 密码强度 | 后端校验：至少8位，包含字母和数字 |

---

## 9. 各前端引用说明

### 9.1 Portal 前端

Portal 的登录模块（M04-用户相关）引用本规范的 C01、C02、C03、C04 模块。

### 9.2 Console 前端

Console 的登录和权限引用本规范的 C01、C02、C03、C04 模块。

### 9.3 Admin 前端

Admin 的登录和权限引用本规范的 C01、C02、C03、C04 模块。

---

## 10. 附录

### 10.1 相关文档

| 文档 | 路径 |
|------|------|
| 后端接口定义 | ai-studio-service/src/main/java/com/aistudio/service/controller/AuthController.java |
| JWT 工具类 | ai-studio-service/src/main/java/com/aistudio/service/config/JwtTokenProvider.java |
| 安全配置 | ai-studio-service/src/main/java/com/aistudio/service/config/SecurityConfig.java |

### 10.2 后续扩展

- [ ] 抽取为独立 NPM 包 `ai-studio-auth`
- [ ] 支持单点登录（SSO）
- [ ] 支持第三方登录（OAuth2）
- [ ] 支持 LDAP/AD 集成
