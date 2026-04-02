# /api/open/output/submit 接口测试用例

## 接口基本信息

| 项目 | 内容 |
|------|------|
| 请求方式 | POST |
| 接口路径 | `/api/open/output/submit` |
| 认证要求 | 无需认证（开放接口） |
| Content-Type | `application/json` |

## 请求参数说明

| 字段名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| username | string | 是 | - | 用户名（驼峰格式） |
| stat_date | date | 否 | 当前日期 | 统计日期（格式：yyyy-MM-dd） |
| project_root_name | string | 否 | null | 项目根目录名（用于区分不同项目） |
| prd_doc_count | int | 否 | 0 | PRD文档数量 |
| data_model_doc_count | int | 否 | 0 | 数据模型文档数量 |
| api_doc_count | int | 否 | 0 | API文档数量 |
| java_file_count | int | 否 | 0 | Java文件数量 |
| java_code_lines | int | 否 | 0 | Java代码行数 |
| api_count | int | 否 | 0 | API接口数量 |
| core_biz_service_count | int | 否 | 0 | 核心业务服务数量 |
| entity_count | int | 否 | 0 | 实体类数量 |
| frontend_component_count | int | 否 | 0 | 前端组件数量 |
| frontend_page_count | int | 否 | 0 | 前端页面数量 |
| frontend_common_component_count | int | 否 | 0 | 前端公共组件数量 |
| ts_code_lines | int | 否 | 0 | TypeScript代码行数 |
| frontend_code_lines | int | 否 | 0 | 前端代码总行数 |
| sql_script_count | int | 否 | 0 | SQL脚本数量 |
| test_file_count | int | 否 | 0 | 测试文件数量 |
| total_code_lines | int | 否 | 0 | 总代码行数 |
| remark | string | 否 | null | 备注 |

**注意**：除 `username` 外，所有字段名使用 **snake_case（下划线格式）**

## 响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "username": "zhangsan",
    "stat_date": "2026-03-31"
  }
}
```

---

## 测试用例列表

### TC-01: 首次提交成功（基础场景）

**描述**: 新用户使用有效参数首次提交今日产出数据

**请求参数**:
```json
{
  "username": "zhangsan",
  "stat_date": "2026-03-31",
  "prd_doc_count": 2,
  "data_model_doc_count": 1,
  "api_doc_count": 3,
  "java_file_count": 5,
  "java_code_lines": 300,
  "api_count": 10,
  "core_biz_service_count": 3,
  "entity_count": 4,
  "frontend_component_count": 8,
  "frontend_page_count": 2,
  "frontend_common_component_count": 3,
  "ts_code_lines": 200,
  "frontend_code_lines": 500,
  "sql_script_count": 2,
  "test_file_count": 3,
  "total_code_lines": 1000,
  "remark": "今日工作完成"
}
```

**预期结果**:
- HTTP状态码: 200
- 响应: `{"code": 200, "message": "success", "data": {"username": "zhangsan", "stat_date": "2026-03-31"}}`
- 数据库: 新增一条记录，user_id 关联到 zhangsan 的用户ID

---

### TC-02: 重复提交更新（同一用户、同一天、同一项目）

**描述**: 同一用户再次提交今天同一项目的产出数据，应更新而非新建

**前置条件**: TC-01 已执行

**请求参数**:
```json
{
  "username": "zhangsan",
  "stat_date": "2026-03-31",
  "java_code_lines": 500,
  "total_code_lines": 1200,
  "remark": "补充了一些代码"
}
```

**预期结果**:
- HTTP状态码: 200
- 响应: 成功
- 数据库: 原记录被更新（java_code_lines 变为 500，total_code_lines 变为 1200，其余字段保持原值或更新），记录数不变

---

### TC-03: 不同项目的独立提交

**描述**: 同一用户同一天提交不同项目的产出数据，应创建多条记录

**前置条件**: TC-01 已执行（项目A）

**请求参数**:
```json
{
  "username": "zhangsan",
  "stat_date": "2026-03-31",
  "project_root_name": "project-b",
  "java_code_lines": 200,
  "total_code_lines": 400,
  "remark": "项目B的产出"
}
```

**预期结果**:
- HTTP状态码: 200
- 数据库: 新增一条 project_root_name = "project-b" 的记录，该用户当天共有2条记录

---

### TC-04: 省略 stat_date（使用默认值）

**描述**: 请求中不传递 stat_date，系统自动使用当前日期

**请求参数**:
```json
{
  "username": "zhangsan",
  "java_code_lines": 100,
  "total_code_lines": 200
}
```

**预期结果**:
- HTTP状态码: 200
- 数据库: 记录 stat_date 为当前日期（2026-03-31）

---

### TC-05: username 为空字符串

**描述**: 请求中 username 为空字符串

**请求参数**:
```json
{
  "username": "",
  "java_code_lines": 100
}
```

**预期结果**:
- HTTP状态码: 400 或 500
- 响应: `{"code": 500, "message": "username 不能为空"}`

---

### TC-06: username 为 null（字段缺失）

**描述**: 请求中不包含 username 字段

**请求参数**:
```json
{
  "java_code_lines": 100,
  "total_code_lines": 200
}
```

**预期结果**:
- HTTP状态码: 400
- 响应: 校验错误提示 "username 不能为空"

---

### TC-07: 不存在的 username

**描述**: username 在系统中不存在

**请求参数**:
```json
{
  "username": "notexistuser",
  "stat_date": "2026-03-31",
  "java_code_lines": 100
}
```

**预期结果**:
- HTTP状态码: 500
- 响应: `{"code": 500, "message": "用户不存在：notexistuser"}`

---

### TC-08: project_root_name 为空字符串（规范化处理）

**描述**: project_root_name 传递空字符串，应被处理为 null

**请求参数**:
```json
{
  "username": "zhangsan",
  "stat_date": "2026-03-31",
  "project_root_name": "",
  "java_code_lines": 100
}
```

**预期结果**:
- HTTP状态码: 200
- 数据库: project_root_name 字段为 null（与未传项目名的记录是同一维度，会更新而非新建）

---

### TC-09: 仅传递部分指标字段

**描述**: 只传递部分产出指标，其余字段使用默认值 0

**请求参数**:
```json
{
  "username": "zhangsan",
  "stat_date": "2026-03-31",
  "prd_doc_count": 1,
  "remark": "只写了文档"
}
```

**预期结果**:
- HTTP状态码: 200
- 数据库: prd_doc_count = 1, 其余数值字段 = 0, remark = "只写了文档"

---

### TC-10: 所有指标字段为 0

**描述**: 所有数值指标都显式传递为 0

**请求参数**:
```json
{
  "username": "zhangsan",
  "stat_date": "2026-03-31",
  "prd_doc_count": 0,
  "data_model_doc_count": 0,
  "java_code_lines": 0,
  "total_code_lines": 0
}
```

**预期结果**:
- HTTP状态码: 200
- 数据库: 所有数值字段为 0，记录正常创建/更新

---

### TC-11: 特殊字符 username（安全测试）

**描述**: username 包含特殊字符（XSS/注入测试）

**请求参数**:
```json
{
  "username": "zhangsan<script>alert(1)</script>",
  "java_code_lines": 100
}
```

**预期结果**:
- HTTP状态码: 500（用户不存在）
- 数据库: 无新记录（安全处理，无注入风险）

---

### TC-12: 超长 remark

**描述**: remark 字段传递超长字符串

**请求参数**:
```json
{
  "username": "zhangsan",
  "stat_date": "2026-03-31",
  "remark": "今日工作...（超过1000字符的字符串）"
}
```

**预期结果**:
- HTTP状态码: 取决于数据库字段长度限制（可能成功或截断）

---

### TC-13: 负数指标值

**描述**: 传递负数作为产出指标

**请求参数**:
```json
{
  "username": "zhangsan",
  "stat_date": "2026-03-31",
  "java_code_lines": -100
}
```

**预期结果**:
- HTTP状态码: 200（代码层未校验负数）
- 数据库: 存储 -100（如果业务需要限制非负数，应补充校验）

---

### TC-14: 并发提交测试

**描述**: 同一用户同时发起多个提交请求

**并发请求**:
```bash
# 同时发送3个请求
curl -X POST /api/open/output/submit -d '{"username":"zhangsan","java_code_lines":100}'
curl -X POST /api/open/output/submit -d '{"username":"zhangsan","java_code_lines":200}'
curl -X POST /api/open/output/submit -d '{"username":"zhangsan","java_code_lines":300}'
```

**预期结果**:
- 无异常报错
- 最终数据为最后一次成功的更新结果

---

### TC-15: 不同日期独立记录

**描述**: 同一用户提交不同日期的产出

**请求参数**:
```json
{
  "username": "zhangsan",
  "stat_date": "2026-03-30",
  "java_code_lines": 500,
  "total_code_lines": 800
}
```

**预期结果**:
- HTTP状态码: 200
- 数据库: 新增一条 stat_date = 2026-03-30 的记录，与 03-31 的记录独立

---

### TC-16: 使用驼峰格式参数名（兼容性测试）

**描述**: 测试使用驼峰格式参数名是否能被正确识别

**请求参数**:
```json
{
  "username": "zhangsan",
  "statDate": "2026-03-31",
  "javaCodeLines": 100,
  "totalCodeLines": 200
}
```

**预期结果**:
- HTTP状态码: 200 或 400（取决于 Jackson 配置是否允许双向转换）
- 注：后端配置为 SNAKE_CASE，驼峰格式参数可能无法被正确映射，建议使用下划线格式

---

## 测试数据建议

| 字段 | 有效值示例 | 无效值示例 |
|------|-----------|-----------|
| username | zhangsan, lisi, wangwu | "", null, "不存在的用户" |
| stat_date | 2026-03-31, null | "2026-13-01", "invalid-date" |
| project_root_name | project-a, project-b | null, "" |
| 数值指标 | 0, 100, 999 | -1, null |

**命名格式注意**：
- ✅ 正确：`stat_date`, `java_code_lines`, `project_root_name`
- ❌ 错误：`statDate`, `javaCodeLines`, `projectRootName`

---

## 自动化测试脚本示例（curl）

```bash
#!/bin/bash

BASE_URL="http://localhost:8080/ai-hub/api/open/output/submit"

echo "=== TC-01: 首次提交 ==="
curl -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "stat_date": "2026-03-31",
    "prd_doc_count": 2,
    "data_model_doc_count": 1,
    "java_code_lines": 300,
    "total_code_lines": 500,
    "remark": "测试产出"
  }'

echo -e "\n\n=== TC-05: username为空 ==="
curl -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "",
    "java_code_lines": 100
  }'

echo -e "\n\n=== TC-07: 用户不存在 ==="
curl -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "notexist",
    "java_code_lines": 100
  }'

echo -e "\n\n=== TC-03: 带项目名提交 ==="
curl -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "stat_date": "2026-03-31",
    "project_root_name": "my-project",
    "java_code_lines": 200
  }'

echo -e "\n\n=== TC-04: 省略日期（使用默认） ==="
curl -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "java_code_lines": 150
  }'

echo -e "\n\n测试完成"
```

---

## 业务规则验证清单

- [x] 首次提交创建新记录
- [x] 重复提交更新已有记录（同用户、同日期、同项目）
- [x] 不同项目创建独立记录
- [x] 不同日期创建独立记录
- [x] stat_date 为空时使用当前日期
- [x] project_root_name 为空字符串时按 null 处理
- [x] username 为空时返回错误
- [x] username 不存在时返回错误
- [x] 数值字段默认值为 0
- [x] 参数名使用 snake_case（下划线）格式

---

## 配置说明

后端 `application.yml` 配置了 Jackson 的命名策略：

```yaml
spring:
  jackson:
    property-naming-strategy: SNAKE_CASE
```

这会将 Java 对象的驼峰属性名（如 `javaCodeLines`）自动转换为下划线格式（`java_code_lines`）进行 JSON 序列化/反序列化。

---

*文档生成时间: 2026-03-31*
