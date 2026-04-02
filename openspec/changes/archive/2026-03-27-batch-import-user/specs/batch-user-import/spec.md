## ADDED Requirements

### Requirement: 批量导入用户
系统 SHALL 允许 SUPER_ADMIN 通过上传 Excel 或 CSV 文件批量导入用户数据，支持模板下载、数据预览、结果统计。

#### Scenario: 下载导入模板
- **WHEN** 用户点击"下载模板"按钮
- **THEN** 系统下载标准 Excel 模板文件，包含表头：账号、密码、姓名、部门、邮箱

#### Scenario: 上传并解析 Excel 文件成功
- **WHEN** 用户上传合法的 .xlsx 或 .xls 文件
- **THEN** 系统解析文件内容并展示数据预览

#### Scenario: 上传并解析 CSV 文件成功
- **WHEN** 用户上传合法的 .csv 文件
- **THEN** 系统解析文件内容并展示数据预览

#### Scenario: 文件格式不支持
- **WHEN** 用户上传非 Excel/CSV 格式的文件
- **THEN** 系统提示"仅支持 Excel (.xlsx, .xls) 和 CSV 格式文件"

#### Scenario: 前端数据校验失败
- **WHEN** 解析后的数据缺少必填字段或邮箱格式不正确
- **THEN** 系统在校验阶段提示错误，阻止提交

#### Scenario: 批量导入全部成功
- **WHEN** SUPER_ADMIN 提交合法的批量用户数据
- **THEN** 系统创建所有用户并返回成功数量

#### Scenario: 批量导入部分失败
- **WHEN** 提交的数据中包含用户名重复或格式错误的记录
- **THEN** 系统返回每条数据的处理结果（成功/失败及原因）

#### Scenario: 非管理员访问批量导入接口
- **WHEN** 非 SUPER_ADMIN 角色用户请求批量导入接口
- **THEN** 系统返回 403 Forbidden
