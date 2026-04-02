## Why

当前开放产出数据提交接口的幂等性控制粒度太粗，每人每天只能提交一次数据。实际业务中，一个成员可能在同一天参与多个项目，需要分别统计不同项目的产出数据。需要支持按项目维度细粒度的数据提交和覆盖。

## What Changes

- 修改 `/api/open/output/submit` 接口的幂等判断逻辑
- **BREAKING**: 唯一性约束从 `(username, stat_date)` 改为 `(username, stat_date, project_root_name)`
- 相同人、相同天、相同项目的重复提交，后提交的数据覆盖前数据
- 每人每天可以提交多个项目的数据（不同 `projectRootName` 可有多条记录）
- 如果 `projectRootName` 为空，视为一个默认项目
- 更新查询今日产出的接口逻辑，支持返回多个项目的记录

## Capabilities

### New Capabilities
<!-- 无新增功能 -->

### Modified Capabilities
- `open-output-submit`: 修改幂等性判断逻辑，支持按项目维度提交产出数据

## Impact

- **数据库**: `member_output` 表的唯一索引需要调整
- **API 层**: `OpenOutputController.java` 的 `/api/open/output/today` 查询逻辑
- **Service 层**: `MemberOutputServiceImpl.java` 的 `submitOutputByUsername` 和 `getTodayOutputByUsername` 方法
- **Mapper 层**: `MemberOutputMapper.java` 相关查询方法
- **前端**: 查询今日产出的接口返回结构可能从单条变为列表
