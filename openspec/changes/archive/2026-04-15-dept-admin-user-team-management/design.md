## Context

当前系统有三类管理员角色：
- **SUPER_ADMIN**：超级管理员，不受部门限制
- **OP_ADMIN**：运营管理员，不受部门限制（与 SUPER_ADMIN 权限相同）
- **DEPT_ADMIN**：部门管理员，只能操作自己所属部门的用户和团队

现有 `AdminUserController` 和 `AdminTeamController` 的 `@PreAuthorize` 注解将创建/更新/删除操作限制为只有 `SUPER_ADMIN`，导致 `DEPT_ADMIN` 完全无法使用用户管理和团队管理功能。

数据模型：
- `SysUser.deptId` - 用户所属部门
- `SysTeam.deptId` - 团队所属部门
- `SysDepartment` - 部门表

技术约束：
- 后端 Spring Boot，Spring Security 做权限控制
- `SecurityUtils` 工具类提供当前用户信息查询
- `JwtAuthenticationFilter` 在请求中注入 `currentUserId` 到 request attribute

## Goals / Non-Goals

**Goals:**
- DEPT_ADMIN 可以管理自己部门的用户（创建、更新、删除、启用/禁用）
- DEPT_ADMIN 可以管理自己部门的团队（创建、更新、删除、成员管理）
- SUPER_ADMIN 和 OP_ADMIN 保持原有全权限
- 操作前在 Service 层做数据层面的部门归属校验

**Non-Goals:**
- 不改变现有 Controller 的 API 路径
- 不改变 OP_ADMIN 的权限范围（保持与 SUPER_ADMIN 相同）
- 不实现跨部门的数据查看限制（DEPT_ADMIN 可以看到所有部门的数据用于参考）

## Decisions

### Decision 1: SecurityUtils 扩展方法

**选择：** 在 `SecurityUtils` 中新增三个方法

```java
public boolean isDeptAdmin() {
    return getCurrentUserRoles().contains("DEPT_ADMIN");
}

public boolean isOpAdmin() {
    return getCurrentUserRoles().contains("OP_ADMIN");
}

public boolean canManageDept(Long targetDeptId) {
    if (isSuperAdmin() || isOpAdmin()) return true;
    if (isDeptAdmin()) {
        Long myDeptId = getCurrentUserDeptId();
        return myDeptId != null && myDeptId.equals(targetDeptId);
    }
    return false;
}
```

**替代方案：**
- 在 Controller 层用 `@PreAuthorize` 注解 + SpEL 表达式：复杂度高，表达式难以处理"当前用户部门 vs 目标部门"的动态比较
- 在 Service 层每个方法单独写校验：代码重复多
- 两者结合：在 Service 层调用 `canManageDept()` 统一校验

---

### Decision 2: 用户创建时的 deptId 处理

**选择：** DEPT_ADMIN 创建用户时，自动将 deptId 设为当前用户的 deptId，忽略传入值

```java
// UserService.createUser()
if (securityUtils.isDeptAdmin()) {
    Long myDeptId = securityUtils.getCurrentUserDeptId();
    if (myDeptId == null) throw new BusinessException(400, "部门管理员未分配部门");
    request.setDeptId(myDeptId); // 强制覆盖
}
```

**理由：** 部门管理员只能在自己部门下创建用户，不能创建到其他部门。

---

### Decision 3: 团队创建时的 deptId 处理

**选择：** 与用户创建相同逻辑，DEPT_ADMIN 创建团队时 deptId 强制为自己的部门

```java
// TeamService.createTeam()
if (securityUtils.isDeptAdmin()) {
    Long myDeptId = securityUtils.getCurrentUserDeptId();
    if (myDeptId == null) throw new BusinessException(400, "部门管理员未分配部门");
    team.setDeptId(myDeptId);
}
```

---

### Decision 4: 更新/删除操作的数据层校验

**选择：** 在 Service 层方法中，先查询目标对象的 deptId，再调用 `canManageDept()` 校验

```java
// UserService.updateUser()
public void updateUser(Long id, UserUpdateRequest request) {
    SysUser target = getById(id);
    if (target == null) throw new BusinessException(404, "用户不存在");
    if (!securityUtils.canManageDept(target.getDeptId())) {
        throw new BusinessException(403, "无权操作该部门用户");
    }
    // 继续更新...
}

// TeamService.deleteTeam()
public void deleteTeam(Long id) {
    SysTeam team = getById(id);
    if (team == null) throw new BusinessException(404, "团队不存在");
    if (!securityUtils.canManageDept(team.getDeptId())) {
        throw new BusinessException(403, "无权操作该部门团队");
    }
    // 继续删除...
}
```

---

### Decision 5: 团队成员管理时的部门校验

**选择：** 添加/移除团队成员时，先校验团队归属部门，再校验成员是否属于同一部门

```java
// TeamService.addMembers()
public void addMembers(Long teamId, List<Long> userIds) {
    SysTeam team = getById(teamId);
    if (!securityUtils.canManageDept(team.getDeptId())) {
        throw new BusinessException(403, "无权操作该团队");
    }
    // 校验所有成员属于同一部门
    for (Long userId : userIds) {
        SysUser user = userService.getById(userId);
        if (!team.getDeptId().equals(user.getDeptId())) {
            throw new BusinessException(400, "只能添加同部门成员到团队");
        }
    }
    // 继续添加...
}
```

---

## 权限矩阵

### 用户管理

| 操作 | SUPER_ADMIN | OP_ADMIN | DEPT_ADMIN | 校验位置 |
|------|-------------|----------|------------|---------|
| list（列表查询） | ✓ 全部 | ✓ 全部 | ✓ 全部 | 无（前端传 deptId 过滤） |
| create（创建） | ✓ 任意部门 | ✓ 任意部门 | ✓ 仅自己部门 | Service 自动覆盖 deptId |
| update（更新） | ✓ 任意部门 | ✓ 任意部门 | ✓ 仅自己部门 | Service.canManageDept(target.deptId) |
| delete（删除） | ✓ 任意部门 | ✓ 任意部门 | ✓ 仅自己部门 | Service.canManageDept(target.deptId) |
| assignRoles（分配角色） | ✓ 任意部门 | ✗ | ✗ | @PreAuthorize |
| updateStatus（状态） | ✓ 任意部门 | ✓ 任意部门 | ✓ 仅自己部门 | Service.canManageDept(target.deptId) |
| batchImport（批量导入） | ✓ 任意部门 | ✗ | ✗ | @PreAuthorize |

### 团队管理

| 操作 | SUPER_ADMIN | OP_ADMIN | DEPT_ADMIN | 校验位置 |
|------|-------------|----------|------------|---------|
| list（列表查询） | ✓ 全部 | ✓ 全部 | ✓ 全部 | 无 |
| create（创建） | ✓ 任意部门 | ✓ 任意部门 | ✓ 仅自己部门 | Service 自动覆盖 deptId |
| update（更新） | ✓ 任意部门 | ✓ 任意部门 | ✓ 仅自己部门 | Service.canManageDept(team.deptId) |
| delete（删除） | ✓ 任意部门 | ✓ 任意部门 | ✓ 仅自己部门 | Service.canManageDept(team.deptId) |
| members（查看成员） | ✓ 任意部门 | ✓ 任意部门 | ✓ 仅自己部门 | Service.canManageDept(team.deptId) |
| addMember（添加成员） | ✓ 任意部门 | ✓ 任意部门 | ✓ 仅自己部门 | 团队 deptId + 成员必须同部门 |
| removeMember（移除成员） | ✓ 任意部门 | ✓ 任意部门 | ✓ 仅自己部门 | Service.canManageDept(team.deptId) |

## 数据流示例

### 场景1：DEPT_ADMIN 创建用户

```
1. 前端 POST /admin/user  { username: "test", deptId: 5, ... }
   ↓
2. Controller 验证 hasAnyRole('SUPER_ADMIN','DEPT_ADMIN') 通过
   ↓
3. UserService.createUser(request)
   ↓
4. securityUtils.isDeptAdmin() = true
   ↓
5. securityUtils.getCurrentUserDeptId() = 3（假设当前用户属于部门3）
   ↓
6. request.setDeptId(3) 强制覆盖传入的 deptId=5
   ↓
7. 实际创建的用户 deptId = 3（当前部门的用户）
```

### 场景2：DEPT_ADMIN 尝试更新其他部门的用户

```
1. 前端 PUT /admin/user/100  { realName: "xxx", ... }
   ↓
2. Controller 验证 hasAnyRole('SUPER_ADMIN','DEPT_ADMIN') 通过
   ↓
3. UserService.updateUser(100, request)
   ↓
4. 查询目标用户 SysUser(id=100, deptId=5)
   ↓
5. securityUtils.isDeptAdmin() = true
   ↓
6. securityUtils.canManageDept(5) → 当前用户 deptId=3 ≠ 5 → false
   ↓
7. 抛出 BusinessException(403, "无权操作该部门用户")
```

## Risks / Trade-offs

| 风险 | 描述 | 缓解措施 |
|------|------|---------|
| **前端未同步部门隔离** | 前端可能仍显示所有操作按钮给 DEPT_ADMIN | 前端按当前用户 deptId 过滤可操作入口 |
| **OP_ADMIN 缺少角色注解** | 代码中 OP_ADMIN 经常未单独标注 | 统一使用 `isSuperAdmin() \|\| isOpAdmin()` 判断 |
| **Service 层遗漏校验** | 开发者可能忘记在新建方法中调用 canManageDept | 代码审查 + 单元测试覆盖 |

## Open Questions

无
