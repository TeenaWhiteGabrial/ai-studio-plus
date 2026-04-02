## Why

当前用户表虽有 status 字段，但缺乏管理功能。管理员无法在界面启用/禁用用户账号，需要补充完整的用户状态管理功能。

## What Changes

- 后端登录接口增加用户状态校验，禁用用户无法登录
- 后端提供切换用户状态接口
- 前端用户列表增加启用/禁用状态显示和切换开关
- 查询用户列表时支持按状态筛选

## Capabilities

### New Capabilities
- `user-status-toggle`: 管理员可通过开关快速启用/禁用用户

### Modified Capabilities
- `user-permission`: 登录时需校验用户状态，禁用用户拒绝登录

## Impact

- **后端**: `AuthService.login()` 增加状态校验，`UserController` 新增状态切换接口
- **前端**: `ai-studio-web/src/views/user/index.vue` 增加状态列和操作开关
- **数据库**: 无变更，使用现有的 `status` 字段
