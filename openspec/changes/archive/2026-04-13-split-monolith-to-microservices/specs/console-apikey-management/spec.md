## ADDED Requirements

### Requirement: API Key 申请
Console 用户在无 Key 时 SHALL 能够申请 API Key，每人只有一个 Key，申请后生成并返回。

#### Scenario: 新用户申请 API Key
- **WHEN** 用户点击"申请 API Key"按钮
- **THEN** 系统生成新 Key 并返回，旧 Key（若存在）立即失效

### Requirement: API Key 查看
拥有 Key 的用户 SHALL 能够查看自己的 API Key（脱敏展示，如 `sk-abc***xyz`）。

### Requirement: API Key 复制
用户 SHALL 能够一键复制 Key 到剪贴板。

### Requirement: API Key 重置
用户 SHALL 能够重置 API Key，重置后旧 Key 立即废弃不可用。

#### Scenario: 重置 API Key
- **WHEN** 用户点击"重置 Key"并二次确认
- **THEN** 系统生成新 Key，旧 Key 立即失效

### Requirement: API Key 使用说明
页面 SHALL 展示 API Key 的配置方法、注意事项和使用说明。
