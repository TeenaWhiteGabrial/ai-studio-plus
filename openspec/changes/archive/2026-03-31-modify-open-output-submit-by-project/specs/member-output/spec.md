## MODIFIED Requirements

### Requirement: 开放 API 提交产出（支持项目根目录）
系统 SHALL 允许调用方通过开放 API 提交产出数据时传入 `project_root_name` 参数。幂等性控制维度为 `(username, stat_date, project_root_name)`，即每人每天每项目只能有一条记录，重复提交则覆盖。

#### Scenario: 提交产出时传入项目根目录名
- **WHEN** 调用方通过开放 API 提交产出数据并传入有效的 `project_root_name`
- **THEN** 系统保存产出数据及项目根目录名
- **AND** 若该用户当天该项目已有记录，则更新原记录
- **AND** 若该用户当天该项目无记录，则创建新记录

#### Scenario: 提交产出时未传入项目根目录名
- **WHEN** 调用方通过开放 API 提交产出数据但未传入 `project_root_name`
- **THEN** 系统正常保存产出数据，`project_root_name` 为空
- **AND** 空 `project_root_name` 视为独立项目，与其他项目数据不冲突

#### Scenario: 同一天提交多个项目的产出
- **WHEN** 调用方使用相同 `username` 和 `stat_date`，但不同 `project_root_name` 提交多次
- **THEN** 系统分别保存为多条独立记录
- **AND** 每条记录独立计算，互不覆盖

#### Scenario: 相同项目重复提交产出
- **WHEN** 调用方使用相同 `username`、`stat_date` 和 `project_root_name` 再次提交
- **THEN** 系统更新该组合的原有记录（UPSERT）
- **AND** 保留最新的数据内容

## ADDED Requirements

### Requirement: 开放 API 查询今日产出（支持多项目）
系统 SHALL 允许调用方通过开放 API 查询用户当天的所有项目产出记录。

#### Scenario: 查询当日有多个项目的产出
- **WHEN** 调用方传入 `username` 查询今日产出
- **AND** 该用户当天有多个项目的记录
- **THEN** 系统返回所有项目的产出记录列表

#### Scenario: 查询当日无产出
- **WHEN** 调用方传入 `username` 查询今日产出
- **AND** 该用户当天无任何项目记录
- **THEN** 系统返回空列表

#### Scenario: 查询当日单个项目的产出
- **WHEN** 调用方传入 `username` 查询今日产出
- **AND** 该用户当天只有一个项目的记录
- **THEN** 系统返回包含单条记录的列表
