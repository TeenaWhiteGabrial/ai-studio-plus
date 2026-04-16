## Why

当前系统有三类管理员角色：SUPER_ADMIN（超级管理员）、OP_ADMIN（运营管理员）、DEPT_ADMIN（部门管理员）。用户管理和团队管理功能目前只有 SUPER_ADMIN 可以操作，DEPT_ADMIN 无法使用。这不符合组织架构管理的实际需求：
- DEPT_ADMIN 应该能够管理自己部门的用户（创建、更新、删除）
- DEPT_ADMIN 应该能够管理自己部门的团队
- 但 DEPT_ADMIN 不应该能操作其他部门的用户和团队

## What Changes

### 用户管理功能改造

| 操作 | SUPER_ADMIN | OP_ADMIN | DEPT_ADMIN |
|------|-------------|----------|------------|
| 查看用户列表 | 全部部门 | 全部部门 | 全部部门（可见） |
| 创建用户 | 任意部门 | 任意部门 | 仅自己部门（deptId自动设为所属部门） |
| 更新用户 | 任意部门 | 任意部门 | 仅自己部门 |
| 删除用户 | 任意部门 | 任意部门 | 仅自己部门 |
| 分配角色 | 任意部门 | 不支持 | 不支持 |
| 批量导入 | 任意部门 | 不支持 | 不支持 |
| 更新状态 | 任意部门 | 任意部门 | 仅自己部门 |

### 团队管理功能改造

| 操作 | SUPER_ADMIN | OP_ADMIN | DEPT_ADMIN |
|------|-------------|----------|------------|
| 查看团队列表 | 全部部门 | 全部部门 | 全部部门（可见） |
| 创建团队 | 任意部门 | 任意部门 | 仅自己部门（deptId自动设为所属部门） |
| 更新团队 | 任意部门 | 任意部门 | 仅自己部门 |
| 删除团队 | 任意部门 | 任意部门 | 仅自己部门 |
| 查看团队成员 | 任意部门 | 任意部门 | 仅自己部门的团队 |
| 添加团队成员 | 任意部门 | 任意部门 | 仅自己部门的团队 |
| 移出团队成员 | 任意部门 | 任意部门 | 仅自己部门的团队 |

### 核心实现机制

在 `SecurityUtils` 中新增 `canManageDept(Long targetDeptId)` 方法：
- SUPER_ADMIN / OP_ADMIN：返回 true（不受限制）
- DEPT_ADMIN：比较 `targetDeptId` 与当前用户的 `deptId`，相等才返回 true

创建用户/团队时，如果是 DEPT_ADMIN，自动将 deptId 设置为当前用户的 deptId，忽略传入值。

## Capabilities

### Modified Capabilities
- `admin-user-management`: 用户管理功能扩展 DEPT_ADMIN 权限支持
- `admin-team-management`: 团队管理功能扩展 DEPT_ADMIN 权限支持

## Impact

### 影响的代码
- `SecurityUtils.java` - 新增 `canManageDept()`、`isDeptAdmin()` 方法
- `UserService.java` - 创建/更新/删除时增加部门归属校验
- `AdminUserController.java` - 更新权限注解
- `DepartmentService.java` 或新建 TeamService - 团队管理实现和部门归属校验
- `AdminTeamController.java` - 更新权限注解和部门归属校验

### 权限判断逻辑
```java
public boolean canManageDept(Long targetDeptId) {
    if (isSuperAdmin() || isOpAdmin()) return true;
    if (isDeptAdmin()) {
        Long myDeptId = getCurrentUserDeptId();
        return myDeptId != null && myDeptId.equals(targetDeptId);
    }
    return false;
}
```

### 环境变更
- 仅涉及后端权限逻辑和数据层校验，无 API 路径变更
