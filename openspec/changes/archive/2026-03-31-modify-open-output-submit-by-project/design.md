## Context

当前开放产出数据提交接口 (`/api/open/output/submit`) 的幂等性控制基于 `(username, stat_date)` 组合。即每人每天只能有一条产出记录。

业务需求变化：一个成员可能在同一天参与多个项目，需要分别统计不同项目的产出数据。

**当前数据库表结构：**
```sql
CREATE TABLE member_output (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    user_name VARCHAR(255),
    stat_date DATE,
    project_root_name VARCHAR(255),  -- 已存在，但未参与唯一性约束
    ...
);
```

**当前代码逻辑：**
- `MemberOutputServiceImpl.submitOutputByUsername()`: 使用 `username + statDate` 查询和判断记录是否存在
- `OpenOutputController.getToday()`: 返回单个 `MemberOutput` 对象

## Goals / Non-Goals

**Goals:**
- 支持按 `(username, stat_date, project_root_name)` 维度提交产出数据
- 相同组合重复提交时，后提交的数据覆盖前数据
- 每人每天可以提交多个项目的数据（不同 `projectRootName`）
- 兼容 `projectRootName` 为空的情况（视为一个独立项目）
- 查询接口支持返回多个项目的记录

**Non-Goals:**
- 不涉及内部登录后提交产出的接口修改（`MemberOutputController` 内的接口）
- 不涉及数据库表的新增字段
- 不涉及统计汇总逻辑的重构

## Decisions

### 决策 1: 唯一性约束调整
**选择**: 修改唯一索引为 `(user_name, stat_date, project_root_name)`

**理由**:
- 从业务角度，这是自然的唯一键
- 防止脏数据，数据库层面保证数据一致性

**备选方案**: 仅在代码层面控制，不修改数据库约束
- 拒绝原因：无法防止并发场景下的重复数据

### 决策 2: project_root_name 为空的处理
**选择**: `project_root_name` 为 `NULL` 或空字符串视为同一个项目

**理由**:
- 向后兼容现有数据
- 统一处理逻辑：空值统一按空字符串处理

### 决策 3: 查询今日产出接口返回格式
**选择**: 将 `getToday` 返回类型从 `MemberOutput` 改为 `List<MemberOutput>`

**理由**:
- 每人每天可能有多个项目的记录
- RESTful 设计一致性

**备选方案**: 保持单条返回，默认返回第一个项目
- 拒绝原因：会丢失其他项目数据，不符合需求

### 决策 4: 数据库索引策略
**选择**: 添加联合唯一索引，同时保留普通索引用于查询

**理由**:
- 联合唯一索引保证数据一致性
- 单列索引加速按用户、按日期查询

## Risks / Trade-offs

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 现有数据冲突 | 添加唯一索引时可能失败 | 先清理重复数据（如存在） |
| API 返回格式变更 | 前端可能需要适配 | 通知前端团队；返回列表结构更清晰 |
| project_root_name 为空的数据 | 可能已存在多条记录 | 迁移脚本处理：保留最新一条，或合并数据 |
| 并发提交 | 极端并发可能产生重复 | 数据库唯一索引兜底 |

## Migration Plan

**数据库迁移步骤：**
1. 检查现有数据中 `(user_name, stat_date, project_root_name)` 的重复情况
2. 如有重复，按 `updated_at` 保留最新记录，或合并数据
3. 删除旧索引（如存在 `(user_name, stat_date)` 唯一索引）
4. 添加新唯一索引 `(user_name, stat_date, project_root_name)`

**代码部署步骤：**
1. 先部署代码（兼容新旧逻辑）
2. 执行数据库迁移脚本
3. 验证接口功能

**回滚策略：**
- 代码回滚：直接回退到上一版本
- 数据回滚：如需回退数据库，需先删除唯一索引，再恢复旧索引

## Open Questions

1. 是否需要对 `project_root_name` 做长度限制校验？（当前数据库为 VARCHAR(255)）
2. 历史数据中 `project_root_name` 为空的重复记录如何处理？建议保留最新一条
3. 是否需要为开放接口添加 `project_root_name` 的枚举/白名单校验？
