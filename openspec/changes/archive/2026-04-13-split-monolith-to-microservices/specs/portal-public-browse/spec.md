## ADDED Requirements

### Requirement: Portal 首页展示
Portal SHALL 在首页展示 Banner 轮播、分类导航（Skill/MCP/Plugin/教程/安装包/视频）、推荐资源、最新上传、下载排行。

#### Scenario: 首页加载
- **WHEN** 用户访问 Portal 首页
- **THEN** 系统展示 Banner（按排序）、六大分类入口、推荐资源（管理员配置）、最新资源（按时间倒序）、下载排行 TOP10

### Requirement: 资源分类列表
用户 SHALL 能够按类型浏览资源列表（Skill/MCP/Plugin/教程/安装包/视频），支持分类筛选、标签筛选、排序（最新/最热/下载最多）、关键词搜索。

#### Scenario: 浏览 Skill 列表
- **WHEN** 用户选择 Skill 分类
- **THEN** 系统展示所有 `status=已通过` 的 Skill，支持标签筛选、排序、搜索

### Requirement: 资源详情查看
用户 SHALL 能够查看资源详细信息，包括名称、描述、分类、标签、版本、作者、统计数据（下载数/点赞数/收藏数）。

### Requirement: 版本历史查看
用户 SHALL SHALL 能够在资源详情页查看版本历史，显示各版本号、发布时间、更新说明，可切换版本。

### Requirement: 资源下载
用户 SHALL SHALL 能够下载资源文件（无需登录），下载次数 +1。

#### Scenario: 下载 Skill
- **WHEN** 用户点击下载按钮
- **THEN** 系统重定向到 OSS 下载地址，`skill.download_count + 1`

### Requirement: 搜索功能
用户 SHALL SHALL 能够通过关键词搜索资源，支持标题、描述、标签搜索，搜索结果按相关度排序。

### Requirement: 分页/滚动加载
资源列表 SHALL 支持分页或滚动加载，每页加载数量合理（20-50条）。

### Requirement: 响应式适配
Portal SHALL 适配桌面、平板、手机三种设备尺寸。
