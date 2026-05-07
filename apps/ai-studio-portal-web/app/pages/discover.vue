<template>
  <div class="home-page">
    <section class="top-news csdn-card">
      <div class="top-news-main">
        <span class="top-news-kicker">今日推荐</span>
        <h1 class="top-news-title">AI Studio 技术文章与资源精选</h1>
        <p class="top-news-desc">沉淀实践经验，发现可复用的 Skill 与 Plugin，像逛技术社区一样快速找到有价值的内容。</p>
      </div>
      <div class="top-news-actions">
        <button class="channel-pill active" @click="switchArticleSort('hot')">热门</button>
        <button class="channel-pill" @click="switchArticleSort('latest')">最新</button>
        <button class="channel-pill" @click="switchArticleSort('likes')">点赞</button>
        <button class="channel-pill" @click="switchArticleSort('favorites')">收藏</button>
      </div>
    </section>

    <section class="feed-card csdn-card">
      <div class="feed-head">
        <div>
          <h2 class="feed-title">推荐文章</h2>
          <p class="feed-subtitle">按热度、点赞、收藏综合推荐</p>
        </div>
        <NuxtLink :to="`/community?sort=${articleSort}`" class="csdn-link">查看更多</NuxtLink>
      </div>

      <div v-if="loadingArticles" class="csdn-empty">加载中...</div>
      <div v-else-if="articleList.length === 0" class="csdn-empty">暂无文章</div>
      <div v-else class="article-stream">
        <ArticleCard v-for="article in articleList" :key="article.id" :article="article" />
      </div>
    </section>

    <section class="resource-card csdn-card">
      <div class="feed-head">
        <div>
          <h2 class="feed-title">资源管理</h2>
          <p class="feed-subtitle">Skill 与 Plugin 资源浏览</p>
        </div>
        <NuxtLink to="/resources" class="csdn-link">全部资源</NuxtLink>
      </div>

      <div class="resource-switch">
        <button
          v-for="tab in resourceTabs"
          :key="tab.value"
          class="resource-tab"
          :class="{ active: currentResourceTab === tab.value }"
          @click="switchResourceTab(tab.value)"
        >
          <Icon :name="tab.icon" size="18" />
          <span>{{ tab.label }}</span>
        </button>
      </div>

      <div v-if="loadingResources" class="csdn-empty">加载中...</div>
      <div v-else-if="latestResources.length === 0" class="csdn-empty">暂无资源</div>
      <div v-else class="resource-grid">
        <ResourceCard
          v-for="resource in latestResources"
          :key="resource.id"
          :resource="resource"
          :type="currentResourceTab"
        />
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import type { Article } from '~~/shared/types/article'
import type { Resource } from '~~/shared/types/resource'
import type { ResourceType } from '~/composables/useResource'

const { getArticleList } = useArticle()
const { getResourceList } = useResource()

const articleList = ref<Article[]>([])
const latestResources = ref<Resource[]>([])

const loadingArticles = ref(false)
const loadingResources = ref(false)
type ArticleSort = 'latest' | 'hot' | 'likes' | 'favorites'
const articleSort = ref<ArticleSort>('hot')
const currentResourceTab = ref<ResourceType>('skill')

const resourceTabs: Array<{ label: string; value: ResourceType; icon: string }> = [
  { label: 'Skill', value: 'skill', icon: 'material-symbols:psychology-alt-outline' },
  { label: 'Plugin', value: 'plugin', icon: 'material-symbols:extension-outline' }
]

async function loadArticles() {
  loadingArticles.value = true
  try {
    const res = await getArticleList({
      sort: articleSort.value,
      page: 1,
      pageSize: 10
    })
    articleList.value = res.records || []
  } finally {
    loadingArticles.value = false
  }
}

async function loadResources() {
  loadingResources.value = true
  try {
    const res = await getResourceList({
      type: currentResourceTab.value,
      sort: 'latest',
      page: 1,
      pageSize: 6
    })
    latestResources.value = res.records || []
  } finally {
    loadingResources.value = false
  }
}

function switchArticleSort(sort: ArticleSort) {
  articleSort.value = sort
  loadArticles()
}

function switchResourceTab(type: ResourceType) {
  currentResourceTab.value = type
  loadResources()
}

onMounted(() => {
  loadArticles()
  loadResources()
})
</script>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.top-news {
  padding: 16px 18px;
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 14px;
  align-items: center;
}

.top-news-kicker {
  display: inline-flex;
  width: fit-content;
  margin-bottom: 8px;
  border-radius: 4px;
  padding: 2px 8px;
  background: var(--portal-gradient-soft);
  color: var(--portal-secondary);
  font-size: 12px;
  font-weight: 700;
}

.top-news-title {
  margin: 0;
  color: var(--portal-text);
  font-size: 24px;
  line-height: 1.32;
}

.top-news-desc {
  margin: 8px 0 0;
  color: var(--csdn-subtext);
  line-height: 1.7;
  font-size: 14px;
}

.top-news-actions {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 8px;
}

.channel-pill {
  height: 32px;
  border: 1px solid var(--csdn-line);
  border-radius: 4px;
  background: var(--portal-surface-soft);
  color: var(--csdn-subtext);
  padding: 0 12px;
  font-size: 13px;
  cursor: pointer;
}

.channel-pill:hover,
.channel-pill.active {
  color: var(--portal-secondary);
  border-color: color-mix(in srgb, var(--color-primary) 48%, var(--portal-line));
  background: var(--portal-gradient-soft);
}

.feed-card,
.resource-card {
  padding: 16px;
}

.feed-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--csdn-line);
}

.feed-title {
  margin: 0;
  font-size: 18px;
  line-height: 1.3;
  color: var(--csdn-text);
}

.feed-subtitle {
  margin: 4px 0 0;
  color: var(--csdn-muted);
  font-size: 13px;
}

.article-stream {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.resource-switch {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.resource-tab {
  height: 34px;
  border: 1px solid var(--csdn-line);
  border-radius: 4px;
  background: var(--portal-surface-soft);
  color: var(--csdn-subtext);
  padding: 0 12px;
  display: inline-flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
}

.resource-tab:hover,
.resource-tab.active {
  color: var(--portal-secondary);
  border-color: color-mix(in srgb, var(--color-primary) 48%, var(--portal-line));
  background: var(--portal-gradient-soft);
}

.resource-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

@media (max-width: 900px) {
  .top-news {
    grid-template-columns: 1fr;
  }

  .top-news-actions {
    justify-content: flex-start;
  }

  .resource-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
