## ADDED Requirements

### Requirement: 统一登录模块跨前端共享
统一登录模块（`@ai-studio/shared-auth`）通过 pnpm workspace 共享给 Admin、Console、Portal 三个前端，包含登录页、Token 管理、路由守卫三个核心功能。

### Requirement: 登录流程遵循 PRD-Common 规范
登录流程 SHALL 遵循 `POST /ai-studio/v1/auth/login` 统一接口，前端使用 RSA 公钥加密密码后传输。

#### Scenario: 用户成功登录 Admin
- **WHEN** 用户在 Admin 登录页输入正确账密并提交
- **THEN** Token 存储到 localStorage，跳转至 `/admin/dashboard`

#### Scenario: 用户成功登录 Console
- **WHEN** 用户在 Console 登录页输入正确账密并提交
- **THEN** Token 存储到 localStorage，跳转至 `/console/dashboard`

#### Scenario: 用户成功登录 Portal
- **WHEN** 用户在 Portal 登录页输入正确账密并提交
- **THEN** Token 存储到 localStorage，跳转至 `/`（首页）

#### Scenario: Token 过期后访问需认证页面
- **WHEN** 请求携带过期 Token 访问需认证页面
- **THEN** 后端返回 401，前端清除 localStorage，跳转至各端登录页

#### Scenario: 未登录用户访问需认证操作
- **WHEN** 未登录用户点击收藏/点赞按钮
- **THEN** 前端跳转至登录页，登录成功后回顾前一页面

### Requirement: 各前端登录页视觉统一
三个前端的登录页 SHALL 直接复用 `packages/@ai-studio/shared-auth/src/views/Login.vue`，通过环境变量 `VITE_APP_TYPE` 区分跳转目标。

### Requirement: Axios 请求拦截器统一
请求拦截器 SHALL 自动在请求头携带 `Authorization: Bearer {token}`，401 响应 SHALL 清除 Token 并跳转登录页。

### Requirement: 记住密码功能
登录页 SHALL 支持"记住密码"功能，将用户名和密码（Base64 编码）存储至 localStorage，下次登录时自动填充。
