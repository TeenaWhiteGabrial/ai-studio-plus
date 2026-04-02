## ADDED Requirements

### Requirement: 资源概览数据查询
系统 SHALL 返回平台各类资源的总数量和总下载量概览。

#### Scenario: 查询资源概览
- **WHEN** 已登录用户请求概览数据
- **THEN** 系统返回 Skills 总数、MCPs 总数、Plugins 总数、Tutorials 总数，以及 Skills 总下载量、Plugins 总下载量、Tutorials 总浏览量

---

### Requirement: 产出趋势查询
系统 SHALL 支持按天/周/月粒度查询团队产出趋势数据。

#### Scenario: 按天粒度查询趋势
- **WHEN** 用户传入 `granularity=day`、`startDate`、`endDate`
- **THEN** 系统返回该日期范围内每天的产出汇总数据（prd_count、api_count、java_lines、frontend_lines）

#### Scenario: 按周粒度查询趋势
- **WHEN** 用户传入 `granularity=week`、`startDate`、`endDate`
- **THEN** 系统返回按自然周聚合的产出汇总数据

#### Scenario: 按月粒度查询趋势
- **WHEN** 用户传入 `granularity=month`、`startDate`、`endDate`
- **THEN** 系统返回按月聚合的产出汇总数据

---

### Requirement: 成员产出排行榜
系统 SHALL 支持按本周或本月周期返回产出最高的前 N 名成员排行榜。

#### Scenario: 查询本周排行榜
- **WHEN** 用户传入 `period=week`、`topN=10`
- **THEN** 系统返回本周产出总量（java_lines + frontend_lines + api_count * 10 + prd_count * 100）最高的前 10 名成员

#### Scenario: 查询本月排行榜
- **WHEN** 用户传入 `period=month`、`topN=10`
- **THEN** 系统返回本月产出最高的前 10 名成员

---

### Requirement: 详细统计查询（管理员）
系统 SHALL 允许管理员按部门或个人维度查询产出明细统计，普通用户无权访问。

#### Scenario: 按部门分组统计
- **WHEN** ADMIN 或 SUPER_ADMIN 传入 `groupBy=department`、`startDate`、`endDate`
- **THEN** 系统返回各部门在该日期范围内的产出汇总数据

#### Scenario: 按个人分组统计
- **WHEN** ADMIN 或 SUPER_ADMIN 传入 `groupBy=user`、`startDate`、`endDate`
- **THEN** 系统返回每个用户在该日期范围内的产出汇总数据（含用户名、部门）

#### Scenario: 普通用户访问详细统计
- **WHEN** USER 访问详细统计接口
- **THEN** 系统返回 403 Forbidden
