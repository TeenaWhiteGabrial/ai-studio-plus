## Why

当前部门信息以字符串形式存储在用户表，无法维护部门列表、无法按部门维度统计产出。需要建立独立的部门管理体系。

## What Changes

- **BREAKING**: 新增 `sys_department` 部门表，包含部门名称、排序、状态字段
- **BREAKING**: 修改 `sys_user` 表，将 `department` 字符串字段改为 `dept_id` 外键
- 提供部门 CRUD 管理功能（系统管理 > 部门管理）
- 用户管理中的部门字段改为下拉选择
- 支持按部门统计产出数据
- 部门软删除：使用 status 字段，禁用后新用户无法选择，已关联用户保留关系

## Capabilities

### New Capabilities
- `department-management`: 部门的增删改查管理
- `department-stats`: 按部门维度统计产出数据

### Modified Capabilities
- `user-management`: 用户关联部门改为外键关联

## Impact

- **数据库**: 新增 `sys_department` 表，修改 `sys_user` 表结构
- **后端**: 新增 DepartmentController/Service/Mapper，修改 UserService
- **前端**: 新增部门管理页面，修改用户管理页面
- **数据迁移**: 需要迁移现有部门数据
