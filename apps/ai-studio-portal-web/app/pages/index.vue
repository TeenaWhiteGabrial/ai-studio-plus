<template>
  <div class="home-page">
    <section class="hero csdn-card">
      <div class="hero-main">
        <h1>AI Studio 技术社区</h1>
        <p>分享实战、沉淀经验、发现高质量 AI 技术资源。</p>
        <div class="hero-actions">
          <el-button type="primary" @click="goAsk">发布问题</el-button>
          <el-button @click="navigateTo('/community')">浏览社区</el-button>
          <el-button @click="navigateTo('/resources')">进入资源中心</el-button>
        </div>
      </div>
      <div class="hero-side">
        <div class="hero-metric">
          <span class="value">{{ articleList.length }}</span>
          <span class="label">推荐文章</span>
        </div>
        <div class="hero-metric">
          <span class="value">{{ questionList.length }}</span>
          <span class="label">活跃问答</span>
        </div>
        <div class="hero-metric">
          <span class="value">{{ latestResources.length }}</span>
          <span class="label">最新资源</span>
        </div>
      </div>
    </section>

    <section class="feed-section">
      <div class="section-head">
        <h2 class="csdn-section-title">推荐文章</h2>
        <NuxtLink to="/community?type=article" class="csdn-link">更多文章</NuxtLink>
      </div>
      <div v-if="loadingArticles" class="csdn-empty">加载中...</div>
      <div v-else class="article-list">
        <ArticleCard v-for="article in articleList" :key="article.id" :article="article" />
      </div>
    </section>

    <section class="feed-section">
      <div class="section-head">
        <h2 class="csdn-section-title">精选问答</h2>
        <NuxtLink to="/community?type=question" class="csdn-link">更多问答</NuxtLink>
      </div>
      <div v-if="loadingQuestions" class="csdn-empty">加载中...</div>
      <div v-else class="question-list">
        <QuestionCard v-for="question in questionList" :key="question.id" :question="question" />
      </div>
    </section>

    <section class="resource-section">
      <div class="section-head">
        <h2 class="csdn-section-title">最新资源</h2>
        <div class="resource-tabs">
          <button
            v-for="tab in resourceTabs"
            :key="tab.value"
            class="tab-btn"
            :class="{ active: currentResourceTab === tab.value }"
            @click="switchResourceTab(tab.value)"
          >
            {{ tab.label }}
          </button>
        </div>
      </div>

      <div v-if="loadingResources" class="csdn-empty">加载中...</div>
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
import type { Question } from '~~/shared/types/question'
import type { Resource } from '~~/shared/types/resource'
import type { ResourceType } from '~/composables/useResource'

const authStore = useAuthStore()
const { getArticleList } = useArticle()
const { getQuestionList } = useQuestion()
const { getResourceList } = useResource()

const articleList = ref<Article[]>([])
const questionList = ref<Question[]>([])
const latestResources = ref<Resource[]>([])

const loadingArticles = ref(false)
const loadingQuestions = ref(false)
const loadingResources = ref(false)

const resourceTabs: Array<{ label: string; value: ResourceType }> = [
  { label: 'Skill', value: 'skill' },
  { label: 'Plugin', value: 'plugin' },
  { label: 'Tutorial', value: 'tutorial' }
]

const currentResourceTab = ref<ResourceType>('skill')

async function loadArticles() {
  loadingArticles.value = true
  try {
    const res = await getArticleList({
      sort: 'hot',
      page: 1,
      pageSize: 6
    })
    articleList.value = res.records || []
  } finally {
    loadingArticles.value = false
  }
}

async function loadQuestions() {
  loadingQuestions.value = true
  try {
    const res = await getQuestionList({
      sort: 'latest',
      page: 1,
      pageSize: 6
    })
    questionList.value = res.records || []
  } finally {
    loadingQuestions.value = false
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

function switchResourceTab(type: ResourceType) {
  currentResourceTab.value = type
  loadResources()
}

function goAsk() {
  if (!authStore.token) {
    goLoginPage()
    return
  }
  navigateTo('/community/ask')
}

onMounted(() => {
  loadArticles()
  loadQuestions()
  loadResources()
})
</script>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.hero {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 280px;
  gap: 20px;
  padding: 20px;
  background: var(--portal-gradient-soft);
}

.hero-main h1 {
  margin: 0 0 10px;
  font-size: 30px;
  line-height: 1.2;
  color: #1f2937;
}

.hero-main p {
  margin: 0;
  color: #4b5563;
  line-height: 1.8;
}

.hero-actions {
  margin-top: 16px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.hero-side {
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
}

.hero-metric {
  border-radius: 10px;
  border: 1px solid #e0e8f8;
  background: #fff;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.hero-metric .value {
  font-size: 22px;
  font-weight: 700;
  color: var(--csdn-primary);
}

.hero-metric .label {
  color: var(--csdn-muted);
  font-size: 13px;
}

.feed-section,
.resource-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.article-list,
.question-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.resource-tabs {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.tab-btn {
  height: 30px;
  border-radius: 999px;
  border: 1px solid var(--csdn-line);
  background: #fff;
  color: var(--csdn-subtext);
  padding: 0 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tab-btn:hover {
  border-color: #bfd5ff;
  color: var(--csdn-primary);
}

.tab-btn.active {
  background: var(--csdn-primary-soft);
  border-color: #a4c2ff;
  color: var(--csdn-primary);
  font-weight: 600;
}

.resource-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

@media (max-width: 1024px) {
  .hero {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .hero-main h1 {
    font-size: 24px;
  }

  .resource-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
