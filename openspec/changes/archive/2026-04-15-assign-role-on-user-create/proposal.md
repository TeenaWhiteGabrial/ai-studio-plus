## Why

当前新建用户流程与角色分配是分离的：先创建用户，再单独调用分配角色接口。这导致：
1. 创建用户后忘记分配角色，用户无法正常使用
2. 操作步骤繁琐，不符合"创建即可用"的产品原则
3. DEPT_ADMIN 没有权限调用分配角色接口（只有 SUPER_ADMIN 可以），导致部门管理员创建的用户默认没有角色

## What Changes

### 新建用户时强制分配角色

| 变更点 | 说明 |
|--------|------|
| 创建用户接口扩展 | `POST /admin/user` 在创建时即接受 `roleIds` 参数 |
| 角色必填 | 创建用户时 `roleIds` 为必填项，不能为空 |
| 默认角色 | 若未指定，默认分配"普通用户"角色（role_code = `USER`） |
| 前端改造 | 新建用户弹窗增加角色选择器，默认选中"普通用户" |

### 影响范围

| 操作 | 变更前 | 变更后 |
|------|--------|--------|
| POST /admin/user | 仅创建用户，角色为null | 创建用户并自动分配 roleIds |
| 前端新建用户弹窗 | 无角色选择 | 增加角色选择，默认"普通用户" |

## Capabilities

### Modified Capabilities
- `admin-user-management`: 用户创建流程改造，创建时即分配角色

## Impact

### 影响的代码
- `UserCreateRequest.java` - 增加 `roleIds` 字段
- `UserServiceImpl.java` - 创建用户时同时插入用户角色关联
- `AdminUserController.java` - 接口参数说明更新
- `AdminUserController.java` - 创建用户时调用角色分配逻辑
- 前端 `user/index.vue` - 新建弹窗增加角色选择组件

### API 变更
```
POST /admin/user
Request Body:
{
  "username": "zhangsan",
  "password": "...",
  "realName": "张三",
  "deptId": 1,
  "email": "zhangsan@example.com",
  "status": 1,
  "roleIds": [4]  // 新增：可选，默认为 [4]（普通用户）
}
```

### 数据库变更
无新增表或字段，`sys_user_role` 表已存在，用于存储用户角色关联关系。
