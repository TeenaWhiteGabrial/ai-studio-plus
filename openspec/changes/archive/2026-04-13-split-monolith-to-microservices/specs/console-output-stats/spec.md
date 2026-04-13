## ADDED Requirements

### Requirement: 产出数据只读展示
Console 产出管理 SHALL 仅做数据展示，不提供写入功能。产出数据由其他团队写入，本模块只读取 `member_output` 表。

### Requirement: 产出列表查看
用户 SHALL 能够按日期/项目查看产出数据列表，显示日期、项目、代码行数、文档数。

### Requirement: 产出统计图表
用户 SHALL 能够查看产出统计图表，包括代码行数趋势（折线图/柱状图）、文档数趋势。

### Requirement: 日期范围筛选
用户 SHALL 能够按日期范围筛选产出数据，支持"近7天/近30天/自定义范围"。

### Requirement: 项目筛选
用户 SHALL 能够按项目名称筛选产出数据。

### Requirement: 产出指标展示
展示指标 SHALL 包括：
- 代码行数：`java_code_lines + ts_code_lines + frontend_code_lines`（参考 `total_code_lines`）
- 文档数：`prd_doc_count + data_model_doc_count + api_doc_count`
