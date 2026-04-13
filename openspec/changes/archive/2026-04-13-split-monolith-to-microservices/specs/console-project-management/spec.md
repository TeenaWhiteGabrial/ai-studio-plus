## ADDED Requirements

### Requirement: 项目列表查看
所有认证用户 SHALL 能够查看所有项目列表，列表 SHALL 展示项目名称、创建人、创建时间，支持 Tab 切换"进行中/已结束"。

#### Scenario: 查看项目列表
- **WHEN** 用户访问项目管理页面
- **THEN** 系统展示所有项目列表，默认显示"进行中"Tab

### Requirement: 创建项目
任何认证用户 SHALL 能够创建新项目，必填字段为项目名称，选填字段为项目描述。

#### Scenario: 创建新项目
- **WHEN** 用户填写项目名称和描述后点击创建
- **THEN** 项目数据插入 `project` 表，状态为"进行中"，创建人为当前用户

### Requirement: 编辑项目
项目创建人或管理员 SHALL 能够编辑项目名称和描述。

### Requirement: 结束项目
项目创建人 SHALL 能够将进行中的项目标记为"已结束"，结束后不可新增任务，历史任务保留。

#### Scenario: 结束项目
- **WHEN** 用户点击"结束项目"并确认
- **THEN** 项目 `status` 更新为"已结束"，不可再新增任务
