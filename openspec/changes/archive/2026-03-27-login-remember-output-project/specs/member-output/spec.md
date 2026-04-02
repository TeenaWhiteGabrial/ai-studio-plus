## ADDED Requirements

### Requirement: 开放 API 提交产出（支持项目根目录）
系统 SHALL 允许调用方通过开放 API 提交产出数据时传入 `project_root_name` 参数，用于标识产出所属的项目根目录。

#### Scenario: 提交产出时传入项目根目录名
- **WHEN** 调用方通过开放 API 提交产出数据并传入有效的 `project_root_name`
- **THEN** 系统保存产出数据及项目根目录名，支持后续按项目统计

#### Scenario: 提交产出时未传入项目根目录名
- **WHEN** 调用方通过开放 API 提交产出数据但未传入 `project_root_name`
- **THEN** 系统正常保存产出数据，`project_root_name` 为空，向后兼容

#### Scenario: 项目根目录名格式校验
- **WHEN** 调用方传入的 `project_root_name` 包含非法字符或超过最大长度
- **THEN** 系统返回错误提示"项目根目录名格式不正确"
