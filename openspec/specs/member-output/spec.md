## ADDED Requirements

### Requirement: 查询今日产出
系统 SHALL 允许已登录用户查询自己当天的产出记录，未录入则返回空数据。

#### Scenario: 已录入今日产出
- **WHEN** 用户查询今日产出且当天已有记录
- **THEN** 系统返回当天的产出数据（prd_count、api_count、java_lines、frontend_lines、remark）

#### Scenario: 未录入今日产出
- **WHEN** 用户查询今日产出且当天无记录
- **THEN** 系统返回空数据（data 为 null 或空对象）

---

### Requirement: 提交今日产出
系统 SHALL 允许用户录入当天的产出数据，`stat_date` 必须为今天；重复提交则更新记录。

#### Scenario: 首次提交今日产出
- **WHEN** 用户提交今日产出数据（stat_date 为今天）且当天无记录
- **THEN** 系统创建新产出记录，返回成功

#### Scenario: 重复提交今日产出
- **WHEN** 用户再次提交今日产出数据且当天已有记录
- **THEN** 系统更新已有记录（UPSERT），返回成功，不报错

#### Scenario: 提交非今天日期
- **WHEN** 用户提交的 `stat_date` 不是当天（无论过去或未来）
- **THEN** 系统返回错误提示"仅允许录入当天数据"

---

### Requirement: 查询个人产出历史
系统 SHALL 允许用户按日期范围查询自己的历史产出记录。

#### Scenario: 按日期范围查询历史
- **WHEN** 用户传入 `startDate` 和 `endDate` 参数
- **THEN** 系统返回该日期范围内当前用户的所有产出记录，按日期降序排列

#### Scenario: 无历史记录
- **WHEN** 用户查询的日期范围内无产出记录
- **THEN** 系统返回空列表

---

### Requirement: 管理员查询全员产出列表
系统 SHALL 允许管理员按指定日期查看所有成员的产出记录，普通用户无权访问。

#### Scenario: 管理员查询指定日期全员产出
- **WHEN** ADMIN 或 SUPER_ADMIN 传入 `date` 参数
- **THEN** 系统返回该日期所有成员的产出记录列表（含用户名、部门）

#### Scenario: 普通用户访问全员产出
- **WHEN** USER 访问管理员产出列表接口
- **THEN** 系统返回 403 Forbidden

---

### Requirement: 产出汇总统计
系统 SHALL 支持按日期范围返回产出汇总统计数据（总计、平均值）。

#### Scenario: 查询产出汇总统计
- **WHEN** 用户传入 `startDate` 和 `endDate` 参数请求统计数据
- **THEN** 系统返回该范围内 prd_count、api_count、java_lines、frontend_lines 的总计和日均值

#### Scenario: 普通用户仅能看自己的统计
- **WHEN** USER 请求产出统计
- **THEN** 系统仅统计当前用户的数据

---

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
