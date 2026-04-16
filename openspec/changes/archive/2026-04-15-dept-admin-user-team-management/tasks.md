## 1. SecurityUtils 扩展

- [x] 1.1 在 `SecurityUtils.java` 中添加 `isDeptAdmin()` 方法
- [x] 1.2 在 `SecurityUtils.java` 中添加 `isOpAdmin()` 方法
- [x] 1.3 在 `SecurityUtils.java` 中添加 `canManageDept(Long targetDeptId)` 方法
- [x] 1.4 更新 `getCurrentUserRoles()` 支持 ROLE_ 前缀去除（确保 DEPT_ADMIN 角色名正确识别）

## 2. UserService 用户管理改造

- [x] 2.1 修改 `UserService.createUser()` - DEPT_ADMIN 时自动覆盖 deptId 为当前用户部门
- [x] 2.2 修改 `UserService.updateUser()` - 增加 canManageDept 校验
- [x] 2.3 修改 `UserService.deleteUser()` - 增加 canManageDept 校验
- [x] 2.4 修改 `UserService.updateUserStatus()` - 增加 canManageDept 校验（保留"不能禁用自己"的校验）

## 3. AdminUserController 权限注解调整

- [x] 3.1 将 `create()` 的 `@PreAuthorize` 从 `hasRole('SUPER_ADMIN')` 改为 `hasAnyRole('SUPER_ADMIN','DEPT_ADMIN')`
- [x] 3.2 将 `update()` 的 `@PreAuthorize` 从 `hasRole('SUPER_ADMIN')` 改为 `hasAnyRole('SUPER_ADMIN','DEPT_ADMIN')`
- [x] 3.3 将 `delete()` 的 `@PreAuthorize` 从 `hasRole('SUPER_ADMIN')` 改为 `hasAnyRole('SUPER_ADMIN','DEPT_ADMIN')`
- [x] 3.4 确认 `assignRoles()` 和 `batchImport()` 保持 `hasRole('SUPER_ADMIN')`
- [x] 3.5 确认 `list()` 和 `roles()` 保持 `hasAnyRole('SUPER_ADMIN','DEPT_ADMIN')`

## 4. 团队管理 Service 实现

- [x] 4.1 创建 `TeamService.java`（如果不存在）
- [x] 4.2 实现 `createTeam()` - DEPT_ADMIN 时自动覆盖 deptId
- [x] 4.3 实现 `updateTeam()` - 增加 canManageDept 校验
- [x] 4.4 实现 `deleteTeam()` - 增加 canManageDept 校验
- [x] 4.5 实现 `getTeamMembers()` - 增加 canManageDept 校验
- [x] 4.6 实现 `addTeamMembers()` - 校验团队部门 + 成员同部门
- [x] 4.7 实现 `removeTeamMember()` - 增加 canManageDept 校验

## 5. AdminTeamController 改造

- [x] 5.1 将 `create()` 的 `@PreAuthorize` 从 `hasRole('SUPER_ADMIN')` 改为 `hasAnyRole('SUPER_ADMIN','DEPT_ADMIN')`
- [x] 5.2 将 `update()` 的 `@PreAuthorize` 从 `hasRole('SUPER_ADMIN')` 改为 `hasAnyRole('SUPER_ADMIN','DEPT_ADMIN')`
- [x] 5.3 将 `delete()` 的 `@PreAuthorize` 从 `hasRole('SUPER_ADMIN')` 改为 `hasAnyRole('SUPER_ADMIN','DEPT_ADMIN')`
- [x] 5.4 确保 `list()`、`members()`、`addMember()`、`removeMember()` 保持 `hasAnyRole('SUPER_ADMIN','DEPT_ADMIN')`

## 6. 单元测试

- [x] 6.1 编写 SecurityUtils 权限方法测试（isDeptAdmin、canManageDept）
- [x] 6.2 编写 UserService 的 DEPT_ADMIN 权限校验测试
- [x] 6.3 编写 TeamService 的 DEPT_ADMIN 权限校验测试

## 7. 联调验证

- [x] 7.1 用 SUPER_ADMIN 账号登录，验证可操作所有部门用户/团队
- [x] 7.2 用 DEPT_ADMIN 账号登录，验证只能操作自己部门的用户/团队
- [x] 7.3 用 DEPT_ADMIN 账号尝试跨部门操作，验证返回 403 错误
- [x] 7.4 用 OP_ADMIN 账号登录，验证权限与 SUPER_ADMIN 相同
