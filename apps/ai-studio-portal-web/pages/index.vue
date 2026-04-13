<template>
  <div class="home-page">
    <!-- Banner 轮播 -->
    <section class="banner-section">
      <el-carousel height="400px" indicator-position="outside">
        <el-carousel-item v-for="(banner, index) in banners" :key="index">
          <div class="banner-item" :style="{ backgroundImage: `url(${banner.imageUrl})` }">
            <div class="banner-content">
              <h2>{{ banner.title }}</h2>
              <p>{{ banner.description }}</p>
              <el-button type="primary" size="large">立即查看</el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- 分类导航 -->
    <section class="category-section">
      <div class="container">
        <h2 class="section-title">资源分类</h2>
        <el-row :gutter="20">
          <el-col :span="4" v-for="(cat, index) in categories" :key="index">
            <NuxtLink :to="`/resource/${cat.type}`">
              <div class="category-card">
                <div class="category-icon">{{ cat.icon }}</div>
                <div class="category-name">{{ cat.name }}</div>
                <div class="category-count">{{ cat.count }} 个资源</div>
              </div>
            </NuxtLink>
          </el-col>
        </el-row>
      </div>
    </section>

    <!-- 推荐资源 -->
    <section class="resource-section">
      <div class="container">
        <h2 class="section-title">热门推荐</h2>
        <el-row :gutter="20">
          <el-col :span="6" v-for="(item, index) in featuredResources" :key="index">
            <NuxtLink :to="`/resource/${item.type}/${item.id}`">
              <el-card class="resource-card" :body-style="{ padding: '0px' }">
                <div class="resource-cover">
                  <img :src="item.cover" :alt="item.name" />
                </div>
                <div class="resource-info">
                  <h3 class="resource-name">{{ item.name }}</h3>
                  <p class="resource-desc">{{ item.description }}</p>
                  <div class="resource-meta">
                    <span class="author">{{ item.author }}</span>
                    <span class="downloads">{{ item.downloadCount }} 下载</span>
                  </div>
                </div>
              </el-card>
            </NuxtLink>
          </el-col>
        </el-row>
      </div>
    </section>

    <!-- 最新上传 -->
    <section class="resource-section">
      <div class="container">
        <h2 class="section-title">最新上传</h2>
        <el-row :gutter="20">
          <el-col :span="6" v-for="(item, index) in latestResources" :key="index">
            <NuxtLink :to="`/resource/${item.type}/${item.id}`">
              <el-card class="resource-card" :body-style="{ padding: '0px' }">
                <div class="resource-cover">
                  <img :src="item.cover" :alt="item.name" />
                </div>
                <div class="resource-info">
                  <h3 class="resource-name">{{ item.name }}</h3>
                  <p class="resource-desc">{{ item.description }}</p>
                  <div class="resource-meta">
                    <span class="author">{{ item.author }}</span>
                    <span class="downloads">{{ item.downloadCount }} 下载</span>
                  </div>
                </div>
              </el-card>
            </NuxtLink>
          </el-col>
        </el-row>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
useHead({
  title: 'AI Studio - 资源门户',
  meta: [
    { name: 'description', content: 'AI Studio 资源门户，提供 Skill、MCP、Plugin、教程、安装包、视频等资源下载' },
    { property: 'og:title', content: 'AI Studio - 资源门户' },
    { property: 'og:description', content: 'AI Studio 资源门户，提供各类 AI 资源下载' },
  ],
})

const banners = ref([
  { title: 'AI Studio 全新发布', description: '一站式 AI 能力平台', imageUrl: 'https://via.placeholder.com/1200x400/667eea/ffffff?text=Banner+1' },
  { title: '海量 Skill 资源', description: '发现和使用优秀的 AI 技能', imageUrl: 'https://via.placeholder.com/1200x400/764ba2/ffffff?text=Banner+2' },
])

const categories = ref([
  { type: 'skill', name: 'Skill', icon: '📦', count: 0 },
  { type: 'mcp', name: 'MCP', icon: '🔌', count: 0 },
  { type: 'plugin', name: 'Plugin', icon: '🧩', count: 0 },
  { type: 'tutorial', name: '教程', icon: '📚', count: 0 },
  { type: 'installer', name: '安装包', icon: '⬇️', count: 0 },
  { type: 'video', name: '视频', icon: '🎬', count: 0 },
])

const featuredResources = ref<Array<{type: string; id: number; cover: string; name: string; description: string; author: string; downloadCount: number}>>([])
const latestResources = ref<Array<{type: string; id: number; cover: string; name: string; description: string; author: string; downloadCount: number}>>([])
</script>

<style scoped>
.banner-section {
  background: #fff;
}

.banner-item {
  height: 100%;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.banner-content {
  text-align: center;
  color: #fff;
}

.banner-content h2 {
  font-size: 48px;
  margin-bottom: 16px;
}

.banner-content p {
  font-size: 20px;
  margin-bottom: 24px;
}

.category-section {
  padding: 60px 0;
  background: #fff;
}

.section-title {
  font-size: 28px;
  text-align: center;
  margin-bottom: 40px;
  color: #303133;
}

.category-card {
  background: #f5f7fa;
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  transition: all 0.3s;
  text-decoration: none;
  color: inherit;
}

.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.category-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.category-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.category-count {
  font-size: 12px;
  color: #909399;
}

.resource-section {
  padding: 60px 0;
}

.resource-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
  text-decoration: none;
  color: inherit;
}

.resource-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.resource-cover {
  height: 160px;
  overflow: hidden;
}

.resource-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.resource-info {
  padding: 16px;
}

.resource-name {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.resource-desc {
  font-size: 12px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 12px;
}

.resource-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #c0c4cc;
}
</style>
