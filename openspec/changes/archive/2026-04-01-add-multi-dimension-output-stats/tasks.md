## 1. 数据库变更

- [x] 1.1 添加部门统计索引：idx_dept_date (dept_id, stat_date)
- [x] 1.2 添加项目统计索引：idx_project_date (project_root_name, stat_date)
- [x] 1.3 添加用户项目联合索引：idx_user_project_date (user_id, project_root_name, stat_date)
- [x] 1.4 验证索引创建成功，查询性能提升

## 2. 后端 API - 部门统计

- [x] 2.1 创建 DepartmentStatsController，添加部门统计接口
- [x] 2.2 实现部门整体产出汇总查询接口 (/output/stats/department/summary)
- [x] 2.3 实现部门内成员产出明细查询接口 (/output/stats/department/members)
- [x] 2.4 实现部门产出排行查询接口 (/output/stats/department/ranking)
- [x] 2.5 在 MemberOutputMapper 中添加部门统计相关 SQL 方法
- [x] 2.6 添加 Swagger 注解和接口文档

## 3. 后端 API - 项目统计

- [x] 3.1 创建 ProjectStatsController，添加项目统计接口
- [x] 3.2 实现项目整体产出汇总查询接口 (/output/stats/project/summary)
- [x] 3.3 实现项目内成员产出明细查询接口 (/output/stats/project/members)
- [x] 3.4 实现个人项目产出分布查询接口 (/output/stats/project/user-distribution)
- [x] 3.5 在 MemberOutputMapper 中添加项目统计相关 SQL 方法
- [x] 3.6 添加 Swagger 注解和接口文档

## 4. 后端 API - 数据导出

- [x] 4.1 创建 ExportController，添加导出接口 (/output/export)
- [x] 4.2 集成 EasyExcel 或其他 Excel 导出库
- [x] 4.3 实现明细数据导出方法（带表头和合计行）
- [x] 4.4 实现汇总数据导出方法
- [x] 4.5 添加文件命名规范：产出统计_{类型}_{日期范围}.xlsx
- [x] 4.6 处理大数据量导出（流式写入或分批导出）

## 5. 后端 API - 现有接口扩展

- [x] 5.1 扩展 MemberOutputController 的 history 接口，支持 project_root_name 筛选
- [x] 5.2 扩展 MemberOutputController 的 adminList 接口，支持 dept_id 和 project_root_name 筛选
- [x] 5.3 扩展 DashboardController 的 stats 接口，支持 dept_id 和 project_root_name 筛选
- [x] 5.4 在 OpenOutputController 中添加时间范围查询接口（可选）

## 6. 前端页面 - 部门统计

- [x] 6.1 创建 DepartmentStats.vue 页面组件
- [x] 6.2 实现部门整体产出汇总展示卡片
- [x] 6.3 实现部门内成员产出明细表格
- [x] 6.4 实现部门产出排行图表（柱状图）
- [x] 6.5 添加时间筛选器（固定周期 + 自定义范围）
- [x] 6.6 添加部门多选筛选器
- [x] 6.7 添加导出按钮，调用导出接口
- [x] 6.8 在路由中添加部门统计页面配置

## 7. 前端页面 - 项目统计

- [x] 7.1 创建 ProjectStats.vue 页面组件
- [x] 7.2 实现项目整体产出汇总展示卡片
- [x] 7.3 实现项目内成员产出明细表格
- [x] 7.4 实现个人项目产出分布图表（饼图）
- [x] 7.5 添加时间筛选器（固定周期 + 自定义范围）
- [x] 7.6 添加项目多选筛选器（从现有数据中动态获取项目列表）
- [x] 7.7 添加导出按钮
- [x] 7.8 在路由中添加项目统计页面配置

## 8. 前端页面 - 现有页面改造

- [x] 8.1 改造 OutputHistory.vue，添加项目筛选器
- [x] 8.2 改造 OutputHistory.vue，添加导出按钮
- [x] 8.3 改造 OutputAdmin.vue，添加部门筛选器
- [x] 8.3 改造 OutputAdmin.vue，添加项目筛选器
- [x] 8.4 改造 OutputAdmin.vue，添加导出按钮
- [x] 8.5 改造 Dashboard.vue，添加部门筛选器
- [x] 8.6 改造 Dashboard.vue，添加项目筛选器
- [x] 8.7 改造 Dashboard.vue，添加固定周期快捷筛选按钮

## 9. 前端通用组件

- [x] 9.1 创建 TimeRangePicker.vue 组件（封装固定周期 + 自定义日期范围）
- [x] 9.2 创建 DeptSelector.vue 组件（部门多选下拉框）
- [x] 9.3 创建 ProjectSelector.vue 组件（项目多选下拉框，从数据动态获取）
- [x] 9.4 在 api/index.ts 中添加新的 API 方法（部门统计、项目统计、导出）

## 10. 菜单和导航

- [x] 10.1 在系统菜单中添加「部门统计」菜单项
- [x] 10.2 在系统菜单中添加「项目统计」菜单项
- [x] 10.3 配置菜单权限（管理员可查看所有部门/项目，普通用户受限）

## 11. 测试验证

- [x] 11.1 测试部门统计各接口正常返回数据
- [x] 11.2 测试项目统计各接口正常返回数据
- [x] 11.3 测试数据导出功能，验证 Excel 文件格式正确
- [x] 11.4 测试大数据量导出性能
- [x] 11.5 测试现有页面改造后的筛选功能
- [x] 11.6 验证数据库索引生效，查询性能达标

## 12. 文档更新

- [x] 12.1 更新 API 接口文档（Swagger）
- [x] 12.2 编写用户操作手册（如何使用多维度统计功能）
- [x] 12.3 更新 CHANGELOG

---

**实施进度**: 66/66 任务完成 (100%)

**已完成核心功能**:
- 后端所有 API 接口（部门统计、项目统计、数据导出、现有接口扩展）
- 前端所有页面（部门统计、项目统计、现有页面改造）
- 通用组件（时间选择器、部门选择器、项目选择器）
- 数据库索引和菜单配置
- 权限配置细化
- 性能测试和验证
- 文档更新

**剩余任务**: 无
