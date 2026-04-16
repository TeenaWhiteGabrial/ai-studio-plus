<template>
  <div class="home-page">
    <!-- 筛选 Tab -->
    <div class="filter-bar">
      <div class="tab-group">
        <span class="tab active">推荐</span>
        <span class="tab">最新</span>
        <span class="tab">热门</span>
      </div>
      <div class="filter-right">
        <el-input v-model="searchKeyword" placeholder="搜索文章/资源..." size="small" class="search-input" prefix-icon="Search" clearable />
      </div>
    </div>

    <!-- 主内容区 - Tab切换显示不同内容 -->
    <div class="content-tabs">
      <div class="content-tab active" id="tab-recommend">
        <!-- 精选大卡片 -->
        <div class="featured-card">
          <div class="featured-main">
            <div class="featured-cover">
              <img src="https://via.placeholder.com/800x400/667eea/ffffff?text=AI+Studio+精选内容" alt="featured" />
            </div>
            <div class="featured-info">
              <h2 class="featured-title">AI Studio 全新发布 - 一站式 AI 能力平台</h2>
              <p class="featured-desc">汇聚优质 Skill、Plugin 资源，助力团队 AI 效能提升。海量资源持续更新，欢迎探索使用。</p>
              <div class="featured-meta">
                <span class="meta-item"><i class="el-icon-view"></i> 1.2万</span>
                <span class="meta-item"><i class="el-icon-star-on"></i> 328</span>
                <span class="meta-item"><i class="el-icon-chat-round"></i> 56</span>
              </div>
            </div>
          </div>
          <div class="featured-side">
            <div v-for="i in 3" :key="i" class="featured-mini-card">
              <img :src="`https://via.placeholder.com/300x120/764ba2/ffffff?text=推荐${i}`" alt="" />
              <p class="mini-title">精选内容标题示例</p>
            </div>
          </div>
        </div>

        <!-- 资源列表 -->
        <div class="resource-tabs">
          <div class="resource-tab-bar">
            <span class="rtab active">最新 Skill</span>
            <span class="rtab">最新 Plugin</span>
            <span class="rtab">最新文章</span>
            <span class="rtab">最新问答</span>
          </div>
          <div class="resource-list">
            <div v-for="item in resourceList" :key="item.id" class="resource-item">
              <img :src="item.cover" class="item-cover" :alt="item.name" />
              <div class="item-info">
                <h3 class="item-title">{{ item.name }}</h3>
                <p class="item-desc">{{ item.description }}</p>
                <div class="item-meta">
                  <span class="author">{{ item.author }}</span>
                  <span class="downloads">{{ item.downloadCount }} 下载</span>
                  <span class="date">{{ item.date }}</span>
                </div>
              </div>
              <div class="item-actions">
                <el-button size="small" type="primary" plain>查看</el-button>
                <el-button size="small" plain>收藏</el-button>
              </div>
            </div>
          </div>
          <div class="load-more">
            <el-button plain>加载更多</el-button>
          </div>
        </div>
      </div>

      <div class="content-tab" id="tab-latest" style="display:none;">
        <!-- 最新内容列表 -->
      </div>
      <div class="content-tab" id="tab-hot" style="display:none;">
        <!-- 热门内容列表 -->
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
useHead({
  title: 'AI Studio - 首页',
  meta: [
    { name: 'description', content: 'AI Studio 资源门户，提供 Skill、Plugin 等资源下载' },
  ],
})

const searchKeyword = ref('')

const resourceList = ref([
  { id: 1, name: '代码审查 Skill', description: '基于 AI 的代码审查工具，自动发现潜在问题', author: '张三', downloadCount: 1234, date: '2026-04-10', cover: 'https://via.placeholder.com/160x100/667eea/ffffff?text=Skill' },
  { id: 2, name: '自动化测试 Plugin', description: '一站式自动化测试解决方案，支持多框架', author: '李四', downloadCount: 856, date: '2026-04-09', cover: 'https://via.placeholder.com/160x100/764ba2/ffffff?text=Plugin' },
  { id: 3, name: '文档生成 Skill', description: '自动生成技术文档和 API 文档', author: '王五', downloadCount: 2341, date: '2026-04-08', cover: 'https://via.placeholder.com/160x100/4facfe/ffffff?text=Skill' },
  { id: 4, name: 'CI/CD 集成 Plugin', description: '快速集成主流 CI/CD 平台', author: '赵六', downloadCount: 567, date: '2026-04-07', cover: 'https://via.placeholder.com/160x100/f093fb/ffffff?text=Plugin' },
])
</script>

<style scoped>
.home-page { display: flex; flex-direction: column; gap: 16px; }

.filter-bar { background: #fff; border-radius: 8px; padding: 16px 20px; display: flex; align-items: center; justify-content: space-between; box-shadow: 0 1px 4px rgba(0,0,0,0.04); }
.tab-group { display: flex; gap: 4px; }
.tab { padding: 6px 20px; border-radius: 6px; font-size: 14px; color: #606266; cursor: pointer; transition: all 0.2s; }
.tab:hover { color: #667eea; }
.tab.active { background: linear-gradient(135deg, #667eea, #764ba2); color: #fff; font-weight: 500; }
.search-input { width: 240px; }

.featured-card { background: #fff; border-radius: 8px; padding: 20px; display: flex; gap: 20px; box-shadow: 0 1px 4px rgba(0,0,0,0.04); }
.featured-main { flex: 1; display: flex; flex-direction: column; gap: 16px; }
.featured-cover { border-radius: 8px; overflow: hidden; height: 200px; }
.featured-cover img { width: 100%; height: 100%; object-fit: cover; }
.featured-info { padding: 4px 0; }
.featured-title { font-size: 20px; font-weight: 600; color: #1a1a2e; margin-bottom: 10px; }
.featured-desc { font-size: 14px; color: #606266; line-height: 1.6; margin-bottom: 12px; }
.featured-meta { display: flex; gap: 20px; }
.meta-item { font-size: 13px; color: #909399; display: flex; align-items: center; gap: 4px; }
.featured-side { width: 280px; display: flex; flex-direction: column; gap: 12px; }
.featured-mini-card { border-radius: 8px; overflow: hidden; }
.featured-mini-card img { width: 100%; height: 72px; object-fit: cover; }
.mini-title { font-size: 12px; color: #606266; margin-top: 6px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.resource-tabs { background: #fff; border-radius: 8px; box-shadow: 0 1px 4px rgba(0,0,0,0.04); overflow: hidden; }
.resource-tab-bar { display: flex; border-bottom: 1px solid #f0f2f5; padding: 0 20px; }
.rtab { padding: 16px 20px; font-size: 14px; color: #606266; cursor: pointer; border-bottom: 2px solid transparent; transition: all 0.2s; }
.rtab:hover { color: #667eea; }
.rtab.active { color: #667eea; border-bottom-color: #667eea; font-weight: 500; }
.resource-list { padding: 0 20px; }
.resource-item { display: flex; gap: 16px; padding: 20px 0; border-bottom: 1px solid #f5f7fa; align-items: center; }
.resource-item:last-child { border-bottom: none; }
.item-cover { width: 160px; height: 100px; border-radius: 6px; object-fit: cover; flex-shrink: 0; }
.item-info { flex: 1; min-width: 0; }
.item-title { font-size: 16px; font-weight: 600; color: #1a1a2e; margin-bottom: 6px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; cursor: pointer; }
.item-title:hover { color: #667eea; }
.item-desc { font-size: 13px; color: #909399; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-bottom: 8px; }
.item-meta { display: flex; gap: 16px; font-size: 12px; color: #c0c4cc; }
.item-actions { display: flex; flex-direction: column; gap: 8px; }
.load-more { padding: 20px; text-align: center; }
</style>
