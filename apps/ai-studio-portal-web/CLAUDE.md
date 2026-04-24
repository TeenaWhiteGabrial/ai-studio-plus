# 项目说明

本项目是基于 Nuxt 3 + UnoCSS + Element Plus 的企业级 Web 应用模板，支持动态主题系统、多种登录模式和微前端架构。

## 项目启动

- **Node 版本依赖**: 22.21.0（可通过 `nvm use 22.21.0` 切换）
- **包管理器**: pnpm
- **本地调试**: `pnpm run dev`
- **生产构建**: `pnpm run build:prod`
- **测试环境构建**: `pnpm run build:test`

## 参数配置说明

在生成代码过程中，应当使用 `.env.localdev` 配置文件

### 登录模式参数：`NUXT_LOGIN_TYPE`

项目支持两种登录模式，可在 `.env.*` 文件中通过 `NUXT_LOGIN_TYPE` 参数切换：

| 值 | 模式 | 说明 |
|------|------|------|
| `maxkey` | 统一登录模式 | 使用 MaxKey 单点登录系统 |
| `own` | 自有登录模式 | 使用项目自带登录系统 |

### 其他关键配置参数，只允许人为修改，禁止自动修改

| 参数 | 说明 |
|------|------|
| `NUXT_API_BASE_URL` | API 请求基础路径 |
| `NUXT_LOGIN_URL` | 登录地址 |
| `NUXT_LOGOUT_URL` | 登出地址 |
| `NUXT_REGISTER_URL` | 注册地址 |
| `NUXT_TOKEN_TYPE` | Token 模式（如 `Bearer`） |
| `NUXT_APP_BASE_URL` | 项目基础路由 |

## 项目目录结构

```
├── app/                        # 前端应用主目录
│   ├── components/             # Vue 组件
│   │   ├── Header/             # 头部组件
│   │   ├── Footer/             # 底部组件
│   │   ├── Home/               # 首页组件
│   │   └── Tools/              # 工具组件
│   ├── composables/            # 组合式函数
│   │   ├── useCustomFetch.ts   # 统一请求封装
│   │   └── useSite.ts          # 网站配置管理
│   ├── layouts/                # 布局组件
│   ├── middleware/             # 路由中间件
│   ├── pages/                  # 页面组件
│   ├── plugins/                # 插件
│   ├── stores/                 # Pinia 状态管理
│   ├── types/                  # 类型定义
│   └── utils/                  # 工具函数
├── modules/                    # 自定义 Nuxt 模块
│   └── theme/                  # 动态主题模块
├── server/                     # 服务端代码
│   ├── api/                    # 服务端 API
│   ├── middleware/             # 服务端中间件
│   └── mock/                   # Mock 数据
├── shared/                     # 前后端共享代码
│   └── types/                  # 共享类型定义
└── public/                     # 静态资源
```

## 技术栈

- **框架**: Nuxt 3 (v4.x)
- **UI 组件库**: Element Plus
- **CSS 框架**: UnoCSS (preset-wind3)
- **状态管理**: Pinia + pinia-plugin-persistedstate
- **内容管理**: @nuxt/content
- **图标**: @nuxt/icon (支持 material-symbols 等图标集)
- **图片优化**: @nuxt/image
- **轮播**: nuxt-swiper

## 已实现的公共样式

### 文本省略
- `text-overflow-1`: 单行文本省略
- `text-overflow-2`: 两行文本省略
- `text-overflow-N`: N 行文本省略（N 为任意数字）

### 卡片悬浮效果
- `card-hover-shadow`: 悬浮时显示阴影并放大

### 按钮样式
- `btn-primary`: 主要按钮（主题色背景 + 悬浮效果）
- `btn-primary-light`: 浅色按钮
- `btn-primary-outline`: 轮廓按钮

## 常规开发规范

### 布局规范

#### 顶部和底部组件
`default.vue` 布局中已全局引入 `HeaderApolloNavbar` 和 `FooterApollo` 组件，因此在 `pages` 目录下创建页面时，**无需重复引入**顶部和底部组件，只需编写页面主体内容即可。

```vue
<!-- ✅ 正确示例：pages/about.vue -->
<template>
  <div class="about-page">
    <h1>关于我们</h1>
    <!-- 只需编写页面主体内容 -->
  </div>
</template>
```

```vue
<!-- ❌ 错误示例：不要重复引入头部和底部 -->
<template>
  <div>
    <HeaderApolloNavbar />  <!-- 不需要 -->
    <div class="content">...</div>
    <FooterApollo />  <!-- 不需要 -->
  </div>
</template>
```

#### 首页组件楼层化配置

- **楼层化**是指首页上每个功能组件应该像楼层那样上下层次分明。
- 每个功能组件应该单独设计，避免多个功能组件之间的样式过于相似
- 首页的功能组件应当存放到app/components/Home目录下

### 鼠标悬浮动效
- 缩放效果：`hover:transform-scale-102`
- 鼠标变小手：`cursor-pointer`
- 阴影效果：`hover:shadow-lg`

### 图标使用
本项目已引入 `@nuxt/icon`，可直接使用图标组件。图标素材参考：[https://icones.js.org/](https://icones.js.org/)

```vue
<template>
  <Icon name="material-symbols:home" />
</template>
```

## 请求调用规范

### 外部接口调用
统一使用 `useSimpleFetch`：

```typescript
const response = await useSimpleFetch<ResponseType>('/api/endpoint', {
  method: 'POST',
  body: { key: 'value' },
  addBaseUrl: true,    // 添加配置中的 baseURL
  withBaseRouter: true, // 添加项目基础路由
  noToken: false,      // 不携带 Token
  throwError: true,    // 请求失败时抛出错误
})
```

### Server 端接口调用
统一使用 `$fetch`：

```typescript
const data = await $fetch('/api/endpoint')
```

## 动态主题系统

项目支持动态主题配置系统，只需配置一个主题色，系统会自动衍生出所有辅助颜色。

### 使用方法
```typescript
const { themeConfig, derivedColors, updateTheme, fetchThemeConfig } = useTheme()

// 获取当前主题色
const currentColor = themeConfig.value.primaryColor

// 更新主题色
updateTheme({ primaryColor: '#10b981' })
```

### 可用的主题色 CSS 类名
- `text-primary` / `text-primary-dark` - 主题色文字
- `bg-primary` / `bg-primary-light` / `bg-primary-faint` - 主题色背景
- `border-primary` - 主题色边框

### CSS 变量
```css
:root {
  --color-primary: #3b82f6;           /* 主题色 */
  --color-primary-dark: #2563eb;      /* 悬浮色 */
  --color-primary-darker: #1d4ed8;    /* 激活色 */
  --color-primary-light: #dbeafe;     /* 浅色版本 */
  --color-primary-lighter: rgb(225, 233, 243);
  --color-primary-faint: rgb(236, 241, 247);
  --color-primary-text: #ffffff;      /* 主题色上的文字颜色 */
}
```

## 状态管理

### useAuthStore
认证相关状态管理：

```typescript
const authStore = useAuthStore()

// 获取用户信息
authStore.userName    // 用户名
authStore.token       // Token
authStore.userId      // 用户 ID
authStore.roles       // 角色权限组

// 方法
authStore.setToken(token)           // 设置 Token
authStore.setLoginInfo(info)        // 设置登录信息
authStore.clearLoginInfo()          // 清除登录信息
```

## 路由跳转工具

### goLoginPage(url?)
跳转登录页，根据 `NUXT_LOGIN_TYPE` 自动选择登录方式

### goLogout()
登出逻辑，根据登录模式执行不同登出流程

### goRegisterPage()
跳转注册页面

### autoNavigate
路由跳转、跳转新窗口、打开链接等

## 类型定义

所有共享类型定义在 `shared/types/` 目录下：

- `response.ts`: API 响应类型
- `site.ts`: 网站配置类型
- `auth.ts`: 认证相关类型
- `component.ts`: 组件相关类型
- `theme.ts`: 主题配置类型

## 注意事项

1. **环境配置文件选择**: 不同环境使用不同的 `.env.*` 配置文件
2. **Token 存储**: Token 存储在 Cookie 中，使用 `pinia-plugin-persistedstate` 持久化
3. **微前端**: 支持微前端架构，服务商工作台可独立部署
4. **SSR 支持**: 服务端渲染支持，注意客户端和服务端代码的区分
