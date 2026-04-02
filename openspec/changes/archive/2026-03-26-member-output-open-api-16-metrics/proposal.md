## Why

当前 `member_output` 表仅有 4 个统计指标，无法全面反映个人产出。需要扩展为 16 个指标，并提供开放 API 接口，让每个人通过调用接口自行提交每日产出数据，无需 token 认证。

## What Changes

- **扩展 `member_output` 表**：从 4 个指标扩展到 16 个指标
- **新增开放 API 接口**：提供无需 token 认证的产出提交接口
- **通过 username 识别用户**：调用接口时传入用户名参数识别身份
- **取消手工录入界面**：仅保留 API 提交方式

## Capabilities

### New Capabilities

- `member-output-open-api`: 开放产出提交 API，通过 username 识别用户，无需 token 认证

### Modified Capabilities

- `member-output`: 扩展产出指标字段从 4 个到 16 个，改为仅支持 API 提交

## Impact

- 数据库：`member_output` 表新增 12 个字段
- 后端：MemberOutput 实体、Mapper、Service 需要更新，新增开放 API 接口
- 前端：移除产出提交表单，保留查询和展示功能
- 调用方：通过调用开放 API 提交数据，传入 username 和 16 个指标
