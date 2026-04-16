## ADDED Requirements

### Requirement: Portal 登录页

Portal 登录页路由为 `/login`，SHALL 对所有访客开放。页面 SHALL 调用 `/api/auth/login` 后端接口（与 Admin 共用同一套认证）。登录成功后将 token 存储于 localStorage，跳转回 redirect 参数指定的页面（默认跳转首页）。

### Requirement: Portal 登录页 UI

Portal 登录页 UI SHALL 完全复用 Admin 登录页（`apps/ai-studio-admin-web/src/views/login/index.vue`）的组件代码，包括：左侧蓝紫色渐变装饰区（粒子动画/浮动几何/SVG波浪/星光点缀）、右侧白色表单区（欢迎标题/用户名+密码输入框/记住密码/登录按钮）、响应式（平板下隐藏左侧）。实现方式为：将 Admin 的 login/index.vue 组件复制到 Portal 的 `pages/login.vue`，替换其中 `userStore.login()` 调用为 Portal 自己的 `useUser().login()` 方法，替换 `router.push('/dashboard')` 为 `router.push(redirect || '/')`，其余模板/CSS 完全不变。

### Requirement: Portal 登录 API 调用

前端调用 `POST /api/auth/login` 时，请求头 Content-Type 为 application/json，请求体为 { username, password }（均为明文，后端 RSA 解密）。登录成功后后端返回 { token, userId, username, realName, roles[] }，前端将 token 存储于 localStorage 并设置全局用户状态。

### Requirement: 页面级 Auth Guard

Nuxt 路由中间件 `auth.ts` SHALL 实现页面级访问控制：检查 localStorage 中是否存在 token，若无 token 且页面需要登录（/profile/*、/community/write、/community/ask），则重定向到 /login?redirect=当前路径。SSR 期间因 localStorage 不可用，默认允许渲染，客户端 hydration 后重新检查。

### Requirement: Token 刷新机制

JWT token 有效期为 7 天。前端 SHALL 在每次 API 请求时检查 token 有效期，若即将过期（< 24 小时）则提示用户重新登录。Token 存储 key 为 'portal_token'。

### Requirement: 退出登录

用户点击"退出登录"时 SHALL 清除 localStorage 中的 token 和 userInfo，重置全局用户状态，跳转到首页。

### Requirement: 已登录用户访问 /login

若已登录用户访问 /login SHALL 直接跳转到首页（而非显示登录表单）。

### Requirement: 登录错误处理

登录失败（用户名/密码错误、用户已禁用）后端返回 401/403，前端 SHALL 在登录页显示对应错误提示，不跳转页面。

### Requirement: Portal Header 用户状态同步

Layout 的 Header 组件 SHALL 通过 useUser() 获取用户登录状态。登录成功后 Header SHALL 实时更新显示用户头像/昵称（无需刷新页面）。

### Requirement: SSR 下的 Auth Guard

在 Nuxt SSR 模式下，`useUser()` 中的 `isLoggedIn()` 使用 `import.meta.client` 判断当前环境。服务端渲染时始终返回 false，客户端 hydration 后正确检测 localStorage token。这确保了 SSR 不会因 localStorage 访问而报错。
