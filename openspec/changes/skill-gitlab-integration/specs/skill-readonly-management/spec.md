## ADDED Requirements

### Requirement: 只读 Skill 列表展示
系统 SHALL 提供只读的 Skill 列表页面，展示从 GitLab 同步的 Skill 元数据。

#### Scenario: 展示 Skill 列表
- **WHEN** 用户访问 Skill 管理页面
- **THEN** 系统展示 Skill 列表，包含名称、分类、描述、作者、下载次数、同步状态

#### Scenario: 列表分页和筛选
- **WHEN** 用户输入关键词、选择分类或状态进行筛选
- **THEN** 系统返回匹配的 Skill 列表（不包含已删除的 Skill）

#### Scenario: 展示同步状态
- **WHEN** 用户查看 Skill 列表
- **THEN** 系统显示最后同步时间和同步状态（成功/失败）

---

### Requirement: Skill 详情查看
系统 SHALL 支持查看 Skill 详细信息，包括元数据和文件内容预览。

#### Scenario: 查看 Skill 详情
- **WHEN** 用户点击某个 Skill 的详情按钮
- **THEN** 系统展示该 Skill 的完整元数据（name, category, description, author, tags 等）

#### Scenario: 查看文件内容预览
- **WHEN** 用户在详情页查看内容
- **THEN** 系统从 GitLab 实时获取文件内容并展示 Markdown 预览

---

### Requirement: 同步控制按钮
系统 SHALL 在 Skill 管理页面提供手动同步按钮，仅管理员可见。

#### Scenario: 管理员看到同步按钮
- **WHEN** 管理员访问 Skill 管理页面
- **THEN** 页面右上角显示"从 GitLab 同步"按钮

#### Scenario: 普通用户不显示同步按钮
- **WHEN** 普通用户访问 Skill 管理页面
- **THEN** 页面不显示同步按钮

---

### Requirement: 同步配置页面
系统 SHALL 提供同步配置页面，供管理员配置 GitLab 连接和同步策略。

#### Scenario: 配置 GitLab 参数
- **WHEN** 管理员进入同步配置页面
- **THEN** 系统展示表单，可配置 GitLab URL、Token、仓库、分支、同步周期

#### Scenario: 验证配置
- **WHEN** 管理员保存配置前点击"验证连接"
- **THEN** 系统调用 GitLab API 验证参数正确性
