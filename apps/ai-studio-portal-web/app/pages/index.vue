<template>
  <div class="home-page">
    <!-- 主内容区 -->
    <div class="main-area">
      <!-- 本周精选 -->
      <section class="featured-section">
        <div class="section-header">
          <Icon name="material-symbols:star" class="mr-2 text-yellow-500" />
          <span>本周精选</span>
        </div>
        <div class="featured-grid">
          <div
            v-for="item in featuredItems"
            :key="item.id"
            class="featured-card card-hover-shadow"
            @click="handleFeaturedClick(item)"
          >
            <div class="featured-cover">
              <img v-if="item.coverImage" :src="item.coverImage" :alt="item.title" />
              <div v-else class="default-cover">
                <Icon name="material-symbols:article" class="text-4xl text-primary" />
              </div>
            </div>
            <div class="featured-content">
              <el-tag size="small" :type="item.type === 'article' ? 'primary' : 'success'">
                {{ item.type === 'article' ? '文章' : '问答' }}
              </el-tag>
              <h4 class="featured-title text-overflow-2">{{ item.title }}</h4>
              <div class="featured-meta">
                <span>{{ item.author }}</span>
                <span>{{ item.viewCount }} 阅读</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 最新资源 -->
      <section class="resource-section">
        <div class="section-header">
          <Icon name="material-symbols:new-release" class="mr-2 text-primary" />
          <span>最新发布</span>
          <div class="tab-switch ml-auto">
            <span
              v-for="tab in resourceTabs"
              :key="tab.type"
              class="tab-item"
              :class="{ active: currentResourceTab === tab.type }"
              @click="switchResourceTab(tab.type)"
            >
              {{ tab.name }}
            </span>
          </div>
        </div>
        <div v-if="loadingResources" class="text-center py-8 text-gray-400">
          加载中...
        </div>
        <div v-else class="resource-grid">
          <ResourceCard
            v-for="resource in latestResources"
            :key="resource.id"
            :resource="resource"
            :type="currentResourceTab as 'skill' | 'plugin' | 'tutorial'"
          />
        </div>
        <div v-if="!loadingResources && latestResources.length === 0" class="text-center py-8 text-gray-400">
          暂无数据
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Resource } from '~~/shared/types/resource'

// 定义本周精选数据（后续可改为API获取）
const featuredItems = ref([
  {
    id: '1',
    title: 'AI大模型在企业应用中的最佳实践',
    coverImage: '',
    type: 'article',
    author: '技术专家',
    viewCount: 1256
  },
  {
    id: '2',
    title: '如何快速构建一个智能问答系统？',
    coverImage: '',
    type: 'question',
    author: '开发者小王',
    viewCount: 892
  },
  {
    id: '3',
    title: '使用Vue3和AI加速前端开发',
    coverImage: '',
    type: 'article',
    author: '前端架构师',
    viewCount: 2103
  }
])

const resourceTabs = [
  { name: '技能', type: 'skill' },
  { name: '插件', type: 'plugin' }
]

const currentResourceTab = ref('skill')
const latestResources = ref<Resource[]>([])
const loadingResources = ref(false)

const { getResourceList } = useResource()

async function loadLatestResources() {
  loadingResources.value = true
  try {
    const res = await getResourceList({
      type: currentResourceTab.value as 'skill' | 'plugin',
      sort: 'latest',
      pageSize: 8
    })
    latestResources.value = res.records || []
  }
  catch (err) {
    console.error('加载资源失败:', err)
  }
  finally {
    loadingResources.value = false
  }
}

function switchResourceTab(type: string) {
  currentResourceTab.value = type
  loadLatestResources()
}

function handleFeaturedClick(item: typeof featuredItems.value[0]) {
  if (item.type === 'article') {
    navigateTo(`/community/article/${item.id}`)
  }
  else {
    navigateTo(`/community/question/${item.id}`)
  }
}

onMounted(() => {
  loadLatestResources()
})
</script>

<style scoped>
.home-page {
  @apply flex gap-6;
}

.main-area {
  @apply flex-1 min-w-0;
}

.section-header {
  @apply flex items-center text-lg font-medium text-gray-800 mb-4;
}

.tab-switch {
  @apply flex gap-2;
}

.tab-item {
  @apply px-3 py-1 text-sm rounded-full cursor-pointer transition-all;
  @apply hover:bg-gray-100;
}

.tab-item.active {
  @apply bg-primary text-white;
}

.featured-section {
  @apply mb-8;
}

.featured-grid {
  @apply grid grid-cols-3 gap-4;
}

.featured-card {
  @apply bg-white rounded-lg overflow-hidden cursor-pointer transition-all duration-300;
}

.featured-cover {
  @apply h-32 bg-gray-100 overflow-hidden;
}

.featured-cover img {
  @apply w-full h-full object-cover;
}

.default-cover {
  @apply w-full h-full flex items-center justify-center bg-gradient-to-br from-primary-faint to-primary-light;
}

.featured-content {
  @apply p-3;
}

.featured-title {
  @apply text-sm font-medium text-gray-800 mt-2 mb-2;
}

.featured-meta {
  @apply flex items-center justify-between text-xs text-gray-400;
}

.resource-section {
  @apply mb-8;
}

.resource-grid {
  @apply grid grid-cols-4 gap-4;
}

@media (max-width: 1200px) {
  .resource-grid {
    @apply grid-cols-3;
  }
}

@media (max-width: 900px) {
  .featured-grid {
    @apply grid-cols-2;
  }

  .resource-grid {
    @apply grid-cols-2;
  }
}
</style>
