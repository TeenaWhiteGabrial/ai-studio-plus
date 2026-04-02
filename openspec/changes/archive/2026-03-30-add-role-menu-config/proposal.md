## Why

当前角色菜单通过 SQL 脚本初始化，无法在系统运行期间动态配置。超级管理员需要能够灵活配置各角色的菜单权限。

## What Changes

- 角色管理页面增加"配置菜单"功能
- 超级管理员可以为 SUPER_ADMIN、ADMIN、USER 三类角色配置菜单
- **限制**: 不允许编辑 SUPER_ADMIN 自身的菜单（防止锁死）
- 使用菜单树形选择组件，支持勾选/取消
- 不缓存菜单配置，实时查询数据库

## Capabilities

### New Capabilities
- `role-menu-config`: 超级管理员可配置角色的菜单权限

### Modified Capabilities
- `role-management`: 角色列表增加配置菜单入口

## Impact

- **后端**: 新增 `RoleController` 菜单配置接口（获取角色菜单、更新角色菜单）
- **前端**: `ai-studio-web/src/views/system/role/index.vue` 增加菜单配置抽屉
- **数据库**: 无变更，使用现有的 `sys_role_menu` 表
