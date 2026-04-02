## 1. 数据库变更（无历史数据，直接修改）

- [x] 1.1 ~~检查现有数据中重复情况~~（跳过，无数据）
- [x] 1.2 创建 SQL 迁移脚本：删除旧唯一索引 `(user_id, stat_date)`，添加新唯一索引 `(user_name, stat_date, project_root_name)`
- [x] 1.3 执行数据库迁移脚本

## 2. Service 层修改

- [x] 2.1 修改 `submitOutputByUsername()`：按 `(username, statDate, projectRootName)` 查询和判断
- [x] 2.2 修改 `getTodayOutputByUsername()`：返回 `List<MemberOutput>`
- [x] 2.3 更新 `MemberOutputService` 接口方法签名

## 3. Controller 层修改

- [x] 3.1 修改 `OpenOutputController.getToday()`：返回 `List<MemberOutput>`
- [x] 3.2 更新 Swagger 注解文档

## 4. 测试验证

- [x] 4.1 测试同一天提交不同项目的数据，保存为多条记录
- [x] 4.2 测试同一天同一项目重复提交，覆盖原数据
- [x] 4.3 测试查询今日产出接口，返回多条记录
- [x] 4.4 验证 `project_root_name` 为空的情况
