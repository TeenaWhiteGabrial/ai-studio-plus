<template>
  <div class="community-page">
    <!-- 顶部 Tab -->
    <div class="filter-bar">
      <div class="tab-group">
        <span class="tab active">文章</span>
        <span class="tab">问答</span>
      </div>
      <div class="filter-right">
        <el-button type="primary" size="small" @click="$router.push('/community/write')">发布文章</el-button>
        <el-button size="small" @click="$router.push('/community/ask')">提问</el-button>
      </div>
    </div>

    <!-- 文章列表 -->
    <div class="article-list">
      <div v-for="item in articleList" :key="item.id" class="article-item" @click="$router.push(`/community/article/${item.id}`)">
        <div class="article-left">
          <h3 class="article-title">{{ item.title }}</h3>
          <p class="article-summary">{{ item.summary }}</p>
          <div class="article-meta">
            <span class="author">{{ item.authorName }}</span>
            <span class="date">{{ item.date }}</span>
            <span class="views"><i class="el-icon-view"></i> {{ item.viewsCount }}</span>
            <span class="likes"><i class="el-icon-star-on"></i> {{ item.likesCount }}</span>
            <span class="comments"><i class="el-icon-chat-round"></i> {{ item.commentsCount }}</span>
          </div>
        </div>
        <div class="article-right">
          <img v-if="item.coverImage" :src="item.coverImage" class="article-cover" :alt="item.title" />
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrap">
      <el-pagination background layout="prev, pager, next" :total="100" :page-size="10" />
    </div>
  </div>
</template>

<script setup lang="ts">
useHead({ title: '技术社区 - AI Studio' })

const articleList = ref([
  { id: 1, title: '如何在 AI Studio 中创建自定义 Skill', summary: '本文详细介绍如何在 AI Studio 平台创建自定义 Skill，包括配置、调试和发布全流程。', authorName: '张三', date: '2026-04-10', viewsCount: 1234, likesCount: 89, commentsCount: 23, coverImage: 'https://via.placeholder.com/200x120/667eea/ffffff?text=Skill' },
  { id: 2, title: 'AI 代码审查实战经验分享', summary: '分享使用 AI Studio 进行代码审查的实战经验，包括常见问题与解决方案。', authorName: '李四', date: '2026-04-09', viewsCount: 856, likesCount: 67, commentsCount: 15, coverImage: 'https://via.placeholder.com/200x120/764ba2/ffffff?text=AI' },
  { id: 3, title: 'Plugin 开发入门指南', summary: '从零开始学习 Plugin 开发，快速掌握核心概念和开发流程。', authorName: '王五', date: '2026-04-08', viewsCount: 2341, likesCount: 156, commentsCount: 42, coverImage: 'https://via.placeholder.com/200x120/4facfe/ffffff?text=Plugin' },
  { id: 4, title: 'MCP 服务集成最佳实践', summary: '探讨 MCP 服务集成的最佳实践，提升 AI 能力扩展性。', authorName: '赵六', date: '2026-04-07', viewsCount: 567, likesCount: 45, commentsCount: 8, coverImage: '' },
])
</script>

<style scoped>
.community-page { display: flex; flex-direction: column; gap: 16px; }
.filter-bar { background: #fff; border-radius: 8px; padding: 16px 20px; display: flex; align-items: center; justify-content: space-between; box-shadow: 0 1px 4px rgba(0,0,0,0.04); }
.tab-group { display: flex; gap: 4px; }
.tab { padding: 6px 20px; border-radius: 6px; font-size: 14px; color: #606266; cursor: pointer; transition: all 0.2s; }
.tab:hover { color: #667eea; }
.tab.active { background: linear-gradient(135deg, #667eea, #764ba2); color: #fff; font-weight: 500; }
.article-list { background: #fff; border-radius: 8px; box-shadow: 0 1px 4px rgba(0,0,0,0.04); overflow: hidden; }
.article-item { display: flex; gap: 20px; padding: 20px; border-bottom: 1px solid #f5f7fa; cursor: pointer; transition: background 0.2s; }
.article-item:hover { background: #fafafa; }
.article-item:last-child { border-bottom: none; }
.article-left { flex: 1; min-width: 0; }
.article-title { font-size: 17px; font-weight: 600; color: #1a1a2e; margin-bottom: 8px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.article-title:hover { color: #667eea; }
.article-summary { font-size: 13px; color: #909399; margin-bottom: 12px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.article-meta { display: flex; gap: 20px; font-size: 12px; color: #c0c4cc; }
.article-meta span { display: flex; align-items: center; gap: 4px; }
.article-right { flex-shrink: 0; }
.article-cover { width: 160px; height: 100px; border-radius: 6px; object-fit: cover; }
.pagination-wrap { text-align: center; padding: 20px; }
</style>
