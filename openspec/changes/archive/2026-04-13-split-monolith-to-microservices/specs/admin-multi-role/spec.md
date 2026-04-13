## ADDED Requirements

### Requirement: Admin 端三重角色体系
Admin 端 SHALL 支持 SUPER_ADMIN（超级管理员）、OP_ADMIN（运营管理员）、DEPT_ADMIN（部门管理员）三种角色，各角色权限范围严格按 PRD-Admin 定义执行。

### Requirement: SUPER_ADMIN 全权限
SUPER_ADMIN SHALL 有权访问所有 Admin 功能，包括用户管理（全局）、部门管理、团队管理、资源审核、数据报表、系统管理。

### Requirement: OP_ADMIN 运营权限
OP_ADMIN SHALL 只能访问网站配置、首页运营、内容审核、全部统计报表，无权访问用户列表、部门管理。

#### Scenario: OP_ADMIN 访问用户管理页面
- **WHEN** OP_ADMIN 用户访问 `/admin/system/user`
- **THEN** 后端返回 403，前端提示"无权限访问"

### Requirement: DEPT_ADMIN 本部门权限
DEPT_ADMIN SHALL 只能管理自己所属部门（`managed_dept_id`）下的用户和团队，可查看本部门统计报表，无权访问其他模块。

#### Scenario: DEPT_ADMIN 查看本部门用户
- **WHEN** DEPT_ADMIN 用户访问用户列表
- **THEN** 后端只返回 `dept_id = managed_dept_id` 的用户数据

#### Scenario: DEPT_ADMIN 尝试管理其他部门用户
- **WHEN** DEPT_ADMIN 用户尝试修改其他部门用户信息
- **THEN** 后端返回 403，前端提示"无权限访问"

### Requirement: 角色分配管理
SUPER_ADMIN SHALL 能够为用户分配角色，其中分配 DEPT_ADMIN 时必须同时指定 `managed_dept_id`。

### Requirement: 路径权限拦截
未携带有效 Token 的请求 SHALL 返回 401；携带 Token 但不具备对应角色权限的请求 SHALL 返回 403。
