# admin-user-management

## Overview

用户管理功能，支持 SUPER_ADMIN、OP_ADMIN、DEPT_ADMIN 三种角色分级权限管理。

## User Management Permissions

### Roles

| Role | Code | Description |
|------|------|-------------|
| 超级管理员 | SUPER_ADMIN | 可操作所有部门的用户 |
| 运营管理员 | OP_ADMIN | 可操作所有部门的用户（与 SUPER_ADMIN 相同） |
| 部门管理员 | DEPT_ADMIN | 只能操作自己所属部门的用户 |

### Permission Matrix

| Operation | SUPER_ADMIN | OP_ADMIN | DEPT_ADMIN |
|-----------|-------------|----------|------------|
| list (用户列表) | ✓ 全部部门 | ✓ 全部部门 | ✓ 全部部门 |
| create (创建用户) | ✓ 任意部门 | ✓ 任意部门 | ✓ 仅自己部门 |
| update (更新用户) | ✓ 任意部门 | ✓ 任意部门 | ✓ 仅自己部门 |
| delete (删除用户) | ✓ 任意部门 | ✓ 任意部门 | ✓ 仅自己部门 |
| assignRoles (分配角色) | ✓ 任意部门 | ✗ | ✗ |
| updateStatus (更新状态) | ✓ 任意部门 | ✓ 任意部门 | ✓ 仅自己部门 |
| batchImport (批量导入) | ✓ 任意部门 | ✗ | ✗ |

### Business Rules

1. **部门隔离**：DEPT_ADMIN 只能管理 `deptId` 等于自己所属部门的用户
2. **创建强制部门**：DEPT_ADMIN 创建用户时，系统自动将 `deptId` 设为当前用户的部门，忽略传入值
3. **不能操作自己**：任何管理员不能删除或禁用自己的账号
4. **角色分配限制**：只有 SUPER_ADMIN 可以分配角色

### API Endpoints

| Method | Path | Description | Role |
|--------|------|-------------|------|
| GET | /admin/user/list | 用户列表 | SUPER_ADMIN, OP_ADMIN, DEPT_ADMIN |
| POST | /admin/user | 创建用户 | SUPER_ADMIN, OP_ADMIN, DEPT_ADMIN |
| PUT | /admin/user/{id} | 更新用户 | SUPER_ADMIN, OP_ADMIN, DEPT_ADMIN |
| DELETE | /admin/user/{id} | 删除用户 | SUPER_ADMIN, OP_ADMIN, DEPT_ADMIN |
| PUT | /admin/user/{id}/roles | 分配角色 | SUPER_ADMIN |
| GET | /admin/user/roles | 角色列表 | SUPER_ADMIN, OP_ADMIN, DEPT_ADMIN |
| PUT | /admin/user/{id}/status | 更新状态 | SUPER_ADMIN, OP_ADMIN, DEPT_ADMIN |
| POST | /admin/user/batch | 批量导入 | SUPER_ADMIN |
| GET | /admin/user/template | 下载模板 | PUBLIC |

### Data Model

```java
public class SysUser {
    Long id;
    String username;
    String password;          // BCrypt 加密
    String realName;
    Long deptId;              // 所属部门
    String email;
    Integer status;           // 1=启用, 0=禁用
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
```
