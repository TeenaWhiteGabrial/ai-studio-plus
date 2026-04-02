## Context

当前部门以字符串存储，无法统一管理。需要建立独立的部门表，并与用户建立外键关联。

## Goals / Non-Goals

**Goals:**
- 部门独立管理（增删改查）
- 用户关联部门外键
- 按部门统计产出
- 部门软删除（status 字段）

**Non-Goals:**
- 不支持层级部门（扁平结构）
- 不支持部门负责人设置

## Decisions

### 决策 1: 表结构
```sql
CREATE TABLE sys_department (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    dept_name VARCHAR(50) NOT NULL,
    sort INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_at DATETIME,
    updated_at DATETIME
);
```

### 决策 2: 数据迁移策略
**步骤**:
1. 创建新表
2. 提取现有 department 值到新表
3. 添加 dept_id 字段
4. 更新外键关联
5. 删除原字段

### 决策 3: 部门禁用处理
**选择**: 禁用后，已关联用户保留 dept_id，新用户无法选择

**理由**: 保留历史数据完整性

### 决策 4: 统计方式
**选择**: JOIN 查询，按部门分组聚合

```sql
SELECT d.dept_name, SUM(mo.java_code_lines) as total
FROM member_output mo
JOIN sys_user u ON mo.user_id = u.id
JOIN sys_department d ON u.dept_id = d.id
GROUP BY d.id
```

## Risks / Trade-offs

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 数据迁移失败 | 部门数据丢失 | 备份后执行，测试环境先验证 |
| 关联查询性能 | 中 | 添加索引，数据量小暂无需优化 |

## Migration Plan

**阶段 1**: 应用启动前执行 SQL 迁移
**阶段 2**: 部署后端代码
**阶段 3**: 部署前端代码

## Open Questions

1. 部门名称是否唯一？（建议唯一）
2. 是否需要在用户列表显示部门筛选？（建议添加）
