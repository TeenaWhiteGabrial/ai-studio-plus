## MODIFIED Requirements

### Requirement: User Creation Requires Role Assignment

**原文**：
用户创建时，只需提供用户名、密码、姓名、部门、邮箱等基本信息。角色需要通过单独的 `/admin/user/{id}/roles` 接口分配。

**修改为**：
用户创建时必须指定角色，通过 `roleIds` 字段传递。若未指定 `roleIds` 或为空数组，系统自动分配"普通用户"角色（role_id = 4）。

#### Scenario: Create user with explicit role
- **WHEN** admin calls `POST /admin/user` with `roleIds: [2]`
- **THEN** user is created with role_id = 2 assigned

#### Scenario: Create user without roleIds (default to regular user)
- **WHEN** admin calls `POST /admin/user` without `roleIds` field
- **THEN** system creates user and automatically assigns role_id = 4 (普通用户)

#### Scenario: Create user with empty roleIds array
- **WHEN** admin calls `POST /admin/user` with `roleIds: []`
- **THEN** system creates user and automatically assigns role_id = 4 (普通用户)

#### Scenario: Create user with multiple roles
- **WHEN** admin calls `POST /admin/user` with `roleIds: [2, 4]`
- **THEN** user is created with both role_id = 2 and role_id = 4 assigned

### Requirement: User Creation API

**原文**：
```
POST /admin/user
Request Body:
{
  "username": "zhangsan",
  "password": "Admin@123",
  "realName": "张三",
  "deptId": 1,
  "email": "zhangsan@example.com",
  "status": 1
}
```

**修改为**：
```
POST /admin/user
Request Body:
{
  "username": "zhangsan",
  "password": "Admin@123",
  "realName": "张三",
  "deptId": 1,
  "email": "zhangsan@example.com",
  "status": 1,
  "roleIds": [4]  // 可选，默认为 [4]（普通用户）
}
```
