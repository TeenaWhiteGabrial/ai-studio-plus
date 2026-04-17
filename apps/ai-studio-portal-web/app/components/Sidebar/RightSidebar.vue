<template>
  <div class="right-sidebar-content">
    <!-- 热门文章 -->
    <div class="sidebar-card">
      <div class="sidebar-title">
        <Icon name="material-symbols:article" class="mr-2" />
        热门文章
      </div>
      <div class="hot-list">
        <div
          v-for="(article, index) in hotArticles"
          :key="article.id"
          class="hot-item"
          @click="handleArticleClick(article)"
        >
          <span class="rank" :class="{ top: index < 3 }">{{ index + 1 }}</span>
          <span class="title text-overflow-2">{{ article.title }}</span>
        </div>
        <div v-if="loadingArticles" class="text-center text-gray-400 py-4">
          加载中...
        </div>
        <div v-else-if="hotArticles.length === 0" class="text-center text-gray-400 py-4">
          暂无数据
        </div>
      </div>
    </div>

    <!-- 热门技能 -->
    <div class="sidebar-card mt-4">
      <div class="sidebar-title">
        <Icon name="material-symbols:smart-toy" class="mr-2" />
        热门技能
      </div>
      <div class="hot-list">
        <div
          v-for="(resource, index) in hotSkills"
          :key="resource.id"
          class="hot-item"
          @click="handleResourceClick(resource, 'skill')"
        >
          <span class="rank" :class="{ top: index < 3 }">{{ index + 1 }}</span>
          <span class="title text-overflow-2">{{ resource.name }}</span>
        </div>
        <div v-if="loadingSkills" class="text-center text-gray-400 py-4">
          加载中...
        </div>
        <div v-else-if="hotSkills.length === 0" class="text-center text-gray-400 py-4">
          暂无数据
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Article } from '~~/shared/types/article'
import type { Resource } from '~~/shared/types/resource'

const { getArticleList } = useArticle()
const { getHotResources } = useResource()

const hotArticles = ref<Article[]>([])
const hotSkills = ref<Resource[]>([])
const loadingArticles = ref(false)
const loadingSkills = ref(false)

// 加载热门文章
async function loadHotArticles() {
  loadingArticles.value = true
  try {
    const res = await getArticleList({ sort: 'hot', pageSize: 10 })
    hotArticles.value = res.records || []
  }
  catch (err) {
    console.error('加载热门文章失败:', err)
  }
  finally {
    loadingArticles.value = false
  }
}

// 加载热门技能
async function loadHotSkills() {
  loadingSkills.value = true
  try {
    const res = await getHotResources('skill', 10)
    hotSkills.value = res || []
  }
  catch (err) {
    console.error('加载热门技能失败:', err)
  }
  finally {
    loadingSkills.value = false
  }
}

function handleArticleClick(article: Article) {
  navigateTo(`/community/article/${article.id}`)
}

function handleResourceClick(resource: Resource, type: string) {
  navigateTo(`/resources/${type}/${resource.id}`)
}

onMounted(() => {
  loadHotArticles()
  loadHotSkills()
})
</script>

<style scoped>
.sidebar-card {
  @apply bg-white rounded-lg p-4 shadow-sm;
}

.sidebar-title {
  @apply text-base font-medium text-gray-800 mb-4 flex items-center;
}

.hot-list {
  @apply space-y-3;
}

.hot-item {
  @apply flex items-start gap-3 cursor-pointer group;
}

.hot-item .rank {
  @apply flex-shrink-0 w-5 h-5 rounded flex items-center justify-center text-xs font-medium bg-gray-100 text-gray-500;
}

.hot-item .rank.top {
  @apply bg-red-50 text-red-500;
}

.hot-item .title {
  @apply flex-1 text-sm text-gray-600 group-hover:text-primary transition-colors;
}
</style>
