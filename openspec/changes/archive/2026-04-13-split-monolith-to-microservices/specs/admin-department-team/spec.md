## ADDED Requirements

### Requirement: 部门管理（SUPER_ADMIN 专属）
SUPER_ADMIN SHALL 能够新增、编辑、禁用/启用部门，部门 SHALL 包含 `dept_name`（名称）、`sort`（排序）、`status`（状态）字段。

#### Scenario: 新增部门
- **WHEN** SUPER_ADMIN 填写部门名称和排序后点击保存
- **THEN** 部门数据插入 `sys_department` 表，状态默认为启用

#### Scenario: 禁用部门
- **WHEN** SUPER_ADMIN 禁用某部门
- **THEN** 该部门下用户和团队不受影响，仍可正常使用

### Requirement: 团队归属部门
每个团队 SHALL 必须归属一个部门（`dept_id` 必填），一个部门可包含多个团队。

### Requirement: 用户归属组织
用户 SHALL 必须先有 `dept_id`，才能有 `team_id`（选填）。`dept_id` 为必填，`team_id` 为选填。一个用户只能属于一个团队。

#### Scenario: 将用户添加到部门（DEPT_ADMIN）
- **WHEN** DEPT_ADMIN 将 `dept_id=null` 的用户添加到自己部门
- **THEN** 该用户的 `dept_id` 被设置为 DEPT_ADMIN 的 `managed_dept_id`

#### Scenario: 将用户分配到团队
- **WHEN** 管理员将本部门且 `team_id=null` 的用户分配到某团队
- **THEN** 该用户的 `team_id` 被设置为目标团队 ID

#### Scenario: 从团队移出成员
- **WHEN** 管理员将某成员从团队中移出
- **THEN** 该成员的 `team_id` 置为空，`dept_id` 保留

### Requirement: 团队数据统计
管理员 SHALL 能够查看团队的资源数、成员数、下载量等统计数据。
