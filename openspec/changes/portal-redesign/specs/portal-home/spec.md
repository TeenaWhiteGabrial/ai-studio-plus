## ADDED Requirements

### Requirement: 首页三栏布局

首页 SHALL 采用三栏 CSDN 风格布局：顶部固定导航栏 + 左侧固定边栏（240px）+ 中间自适应主内容区 + 右侧固定边栏（300px）。页面最大宽度 1400px 居中显示。左侧边栏在屏幕宽度 < 768px 时隐藏，右侧边栏在 < 1024px 时隐藏。

### Requirement: 顶部导航栏

顶部导航栏 SHALL 包含：左侧 Logo（跳转首页）、主导航菜单（首页/资源中心/技术社区）、右侧搜索框（跳转 /resources?q=xxx）、用户头像/登录按钮。导航栏固定在页面顶部，高度 60px，z-index 100。登录用户显示头像下拉菜单（个人中心/退出登录）。

### Requirement: 左侧分类导航

首页左侧边栏 SHALL 显示分类导航：资源分类（Skill/MCP/Plugin/教程/安装包/视频，点击跳转对应资源列表）和热门标签（显示 10 个最热标签，点击跳转资源搜索页含标签筛选）。

### Requirement: 右侧热门排行

首页右侧边栏 SHALL 显示：热门文章 TOP 10（标题+阅读数）、热门 Skill TOP 10（名称+下载数）、精选推荐（编辑置顶的 3-5 个资源/文章）。

### Requirement: 首页主内容区

首页主内容区 SHALL 分上下两个 Section：上半区展示"本周精选"（4-6 个资源/文章大卡片，优先推荐置顶内容）；下半区以 Tab 形式展示各资源类型的最新发布（Skill 最新/MCP 最新/Plugin 最新/教程最新/文章最新），每个 Tab 展示 4-8 个资源卡片。内容为空时显示空状态占位图。

### Requirement: 首页访问控制

首页 SHALL 对所有访客开放，无需登录即可访问。未登录用户点击需登录操作（收藏、发布文章、评论等）时 SHALL 重定向到 /login?redirect=当前页。

---

### Requirement: 热门标签数据来源

左侧边栏的热门标签 SHALL 从数据库中统计 article 和 question 表中各标签出现频率，取 TOP 10 展示，实时更新。

### Requirement: 热门排行数据来源

右侧边栏的热门文章 SHALL 按 article.views_count 倒序取 TOP 10；热门 Skill 按 skill.download_count 倒序取 TOP 10。

### Requirement: 精选推荐数据来源

精选推荐内容由管理员在 Admin 后台配置，Portal 通过 `/portal/public/recommended` 接口获取，支持资源（skill/plugin）和文章（article）混合推荐。

### Requirement: 首页响应式

首页 SHALL 在以下断点自适应：桌面（>= 1200px）完整三栏；平板（768px-1199px）隐藏右侧栏；手机（< 768px）隐藏左侧栏，导航收起为汉堡菜单。
