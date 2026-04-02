## ADDED Requirements

### Requirement: 统计结果导出为 Excel
系统 SHALL 支持将统计查询结果导出为 Excel 文件下载。

#### Scenario: 导出当前查询结果
- **WHEN** 用户点击"导出"按钮
- **THEN** 系统生成 Excel 文件并提供下载，文件包含当前查询条件对应的全部数据

#### Scenario: 导出大数据量结果
- **WHEN** 查询结果数据量超过阈值（如10000条）
- **THEN** 系统提示"数据量较大，导出可能需要较长时间"并继续导出

#### Scenario: 导出空结果
- **WHEN** 用户导出查询条件无匹配数据的结果
- **THEN** 系统生成仅包含表头的空 Excel 文件

### Requirement: Excel 文件格式规范
系统 SHALL 按照规范格式生成 Excel 文件，包含表头、数据区和合计行。

#### Scenario: 导出明细数据
- **WHEN** 用户导出明细列表
- **THEN** Excel 包含：表头行（中文字段名）、数据行、底部合计行（数值字段求和）

#### Scenario: 导出汇总数据
- **WHEN** 用户导出汇总统计
- **THEN** Excel 包含：分组字段列、各指标列，按分组展示汇总值

### Requirement: 导出文件命名规范
系统 SHALL 按规范自动命名导出的 Excel 文件。

#### Scenario: 自动命名导出文件
- **WHEN** 用户导出数据
- **THEN** 文件名为"产出统计_{统计类型}_{日期范围}.xlsx"格式
