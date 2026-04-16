# admin-team-management

## Overview

团队管理功能，支持 SUPER_ADMIN、OP_ADMIN、DEPT_ADMIN 三种角色分级权限管理。一个团队只能属于一个部门。

## Team Management Permissions

### Roles

| Role | Code | Description |
|------|------|-------------|
| 超级管理员 | SUPER_ADMIN | 可操作所有部门的团队 |
| 运营管理员 | OP_ADMIN | 可操作所有部门的团队（与 SUPER_ADMIN 相同） |
| 部门管理员 | DEPT_ADMIN | 只能操作自己所属部门的团队 |

### Permission Matrix

| Operation | SUPER_ADMIN | OP_ADMIN | DEPT_ADMIN |
|-----------|-------------|----------|------------|
| list (团队列表) | ✓ 全部部门 | ✓ 全部部门 | ✓ 全部部门 |
| create (创建团队) | ✓ 任意部门 | ✓ 任意部门 | ✓ 仅自己部门 |
| update (更新团队) | ✓ 任意部门 | ✓ 任意部门 | ✓ 仅自己部门 |
| delete (删除团队) | ✓ 任意部门 | ✓ 任意部门 | ✓ 仅自己部门 |
| members (查看成员) | ✓ 任意部门 | ✓ 任意部门 | ✓ 仅自己部门 |
| addMember (添加成员) | ✓ 任意部门 | ✓ 任意部门 | ✓ 仅自己部门 |
| removeMember (移除成员) | ✓ 任意部门 | ✓ 任意部门 | ✓ 仅自己部门 |

### Business Rules

1. **部门隔离**：DEPT_ADMIN 只能管理 `deptId` 等于自己所属部门的团队
2. **创建强制部门**：DEPT_ADMIN 创建团队时，系统自动将 `deptId` 设为当前用户的部门，忽略传入值
3. **同部门成员**：团队成员必须是同一部门的用户
4. **部门管理员视角**：DEPT_ADMIN 看到的团队列表是全部部门（可参考选择），但只能操作自己部门的团队

### API Endpoints

| Method | Path | Description | Role |
|--------|------|-------------|------|
| GET | /admin/team/list | 团队列表 | SUPER_ADMIN, OP_ADMIN, DEPT_ADMIN |
| POST | /admin/team | 创建团队 | SUPER_ADMIN, OP_ADMIN, DEPT_ADMIN |
| PUT | /admin/team/{id} | 更新团队 | SUPER_ADMIN, OP_ADMIN, DEPT_ADMIN |
| DELETE | /admin/team/{id} | 删除团队 | SUPER_ADMIN, OP_ADMIN, DEPT_ADMIN |
| GET | /admin/team/{id}/members | 查看团队成员 | SUPER_ADMIN, OP_ADMIN, DEPT_ADMIN |
| POST | /admin/team/{id}/members | 添加团队成员 | SUPER_ADMIN, OP_ADMIN, DEPT_ADMIN |
| DELETE | /admin/team/{id}/members/{userId} | 移除团队成员 | SUPER_ADMIN, OP_ADMIN, DEPT_ADMIN |

### Data Model

```java
// 团队
public class SysTeam {
    Long id;
    String teamName;
    Long deptId;              // 所属部门
    String description;
    Integer status;           // 1=启用, 0=禁用
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}

// 团队成员
public class SysTeamMember {
    Long id;
    Long teamId;
    Long userId;
    String role;              // MEMBER / LEADER
    LocalDateTime joinedAt;
}
```

### Relationships

```
SysDepartment (1) ──< SysTeam (N)
SysTeam (1) ──< SysTeamMember (N) >── SysUser (1)
SysDepartment (1) ──< SysUser (N)
```
