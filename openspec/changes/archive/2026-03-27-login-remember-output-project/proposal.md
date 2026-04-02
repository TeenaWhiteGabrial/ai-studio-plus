## Why

1. **记住密码功能**：当前用户每次访问系统都需要重新输入用户名和密码，使用体验不佳。添加记住密码功能可以让用户选择是否自动填充登录信息，提升日常使用便利性。

2. **项目根目录名参数**：当前提交产出数据的 API 缺少项目维度标识，无法区分同一产出数据属于哪个项目。添加 `project_root_name` 参数后，可以按项目统计产出数据，支持多项目管理场景。

## What Changes

- **登录页**：
  - 新增"记住密码"复选框
  - 使用 localStorage 加密存储用户名和密码
  - 用户勾选后，下次打开登录页自动填充凭据
  - 默认不勾选，用户主动选择是否记住

- **产出提交 API**：
  - 新增 `project_root_name` 字段（可选参数）
  - 开放 API 调用方可以传入项目根目录名
  - 后端存储项目信息，支持按项目查询统计

## Capabilities

### New Capabilities

- `login-remember-me`: 登录记住密码功能，包括凭据加密存储、自动填充、清除逻辑

### Modified Capabilities

- `member-output`: 新增 `project_root_name` 字段，支持按项目统计产出数据

## Impact

- **前端**：
  - 登录页面 UI 和逻辑变更
  - user store 增加记住密码相关方法
  - 产出提交表单或 API 调用需要传入 `project_root_name`

- **后端**：
  - `OpenOutputSubmitRequest` 新增 `project_root_name` 字段
  - 数据库表 `member_output` 可能需要新增字段（如需要持久化项目信息）
  - 开放 API 控制器和服务实现变更

- **数据库**：
  - 如果 `member_output` 表需要存储项目信息，需要新增 `project_root_name` 字段
