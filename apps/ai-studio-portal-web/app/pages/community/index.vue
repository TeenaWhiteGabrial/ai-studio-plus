<template>
  <div class="community-page">
    <!-- 主内容区 -->
    <div class="main-area">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">技术社区</h1>
        <el-button v-if="authStore.token" type="primary" @click="goToWrite">
          <Icon name="material-symbols:add" class="mr-1" />
          发布内容
        </el-button>
      </div>

      <!-- Tab 切换 -->
      <div class="tab-container">
        <div class="tab-list">
          <span
            v-for="tab in tabs"
            :key="tab.type"
            class="tab-item"
            :class="{ active: currentTab === tab.type }"
            @click="switchTab(tab.type)"
          >
            <Icon :name="tab.icon" class="mr-1" />
            {{ tab.name }}
          </span>
        </div>
        <!-- 排序 -->
        <div class="sort-select">
          <el-select v-model="currentSort" @change="handleSortChange">
            <el-option label="最新发布" value="latest" />
            <el-option label="最热门的" value="hot" />
            <el-option label="推荐阅读" value="recommend" />
          </el-select>
        </div>
      </div>

      <!-- 内容列表 -->
      <div v-if="loading" class="text-center py-12 text-gray-400">
        加载中...
      </div>
      <div v-else-if="currentTab === 'article'">
        <div v-if="articleList.length > 0" class="article-list">
          <ArticleCard
            v-for="article in articleList"
            :key="article.id"
            :article="article"
          />
        </div>
        <div v-else class="empty-state">
          <Icon name="material-symbols:article-outline" class="text-6xl text-gray-300 mb-4" />
          <p>暂无文章</p>
        </div>
      </div>
      <div v-else>
        <div v-if="questionList.length > 0" class="question-list">
          <QuestionCard
            v-for="question in questionList"
            :key="question.id"
            :question="question"
          />
        </div>
        <div v-else class="empty-state">
          <Icon name="material-symbols:help-outline" class="text-6xl text-gray-300 mb-4" />
          <p>暂无问答</p>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="totalCount > pageSize" class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="totalCount"
          layout="prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Article } from '~~/shared/types/article'
import type { Question } from '~~/shared/types/question'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const { getArticleList } = useArticle()
const { getQuestionList } = useQuestion()

const tabs = [
  { name: '文章', type: 'article', icon: 'material-symbols:article' },
  { name: '问答', type: 'question', icon: 'material-symbols:help-outline' }
]

const currentTab = ref((route.query.type as string) || 'article')
const currentSort = ref((route.query.sort as string) || 'latest')
const currentPage = ref(1)
const pageSize = 10
const totalCount = ref(0)
const loading = ref(false)
const articleList = ref<Article[]>([])
const questionList = ref<Question[]>([])

async function loadArticleList() {
  loading.value = true
  try {
    const res = await getArticleList({
      page: currentPage.value,
      pageSize,
      sort: currentSort.value as 'latest' | 'hot' | 'recommend',
      tagId: route.query.tagId as string
    })
    articleList.value = res.records || []
    totalCount.value = res.total || 0
  }
  catch (err) {
    console.error('加载文章列表失败:', err)
  }
  finally {
    loading.value = false
  }
}

async function loadQuestionList() {
  loading.value = true
  try {
    const res = await getQuestionList({
      page: currentPage.value,
      pageSize,
      sort: currentSort.value as 'latest' | 'hot' | 'unanswered',
      tagId: route.query.tagId as string
    })
    questionList.value = res.records || []
    totalCount.value = res.total || 0
  }
  catch (err) {
    console.error('加载问答列表失败:', err)
  }
  finally {
    loading.value = false
  }
}

function switchTab(type: string) {
  currentTab.value = type
  currentPage.value = 1
  updateQuery()
  if (type === 'article') {
    loadArticleList()
  }
  else {
    loadQuestionList()
  }
}

function handleSortChange() {
  currentPage.value = 1
  updateQuery()
  if (currentTab.value === 'article') {
    loadArticleList()
  }
  else {
    loadQuestionList()
  }
}

function handlePageChange(page: number) {
  currentPage.value = page
  if (currentTab.value === 'article') {
    loadArticleList()
  }
  else {
    loadQuestionList()
  }
}

function updateQuery() {
  router.replace({
    query: {
      ...route.query,
      type: currentTab.value,
      sort: currentSort.value
    }
  })
}

function goToWrite() {
  if (currentTab.value === 'article') {
    navigateTo('/community/write')
  }
  else {
    navigateTo('/community/ask')
  }
}

onMounted(() => {
  if (currentTab.value === 'article') {
    loadArticleList()
  }
  else {
    loadQuestionList()
  }
})
</script>

<style scoped>
.community-page {
  @apply flex gap-6;
}

.main-area {
  @apply flex-1 min-w-0;
}

.page-header {
  @apply flex items-center justify-between mb-6;
}

.page-title {
  @apply text-2xl font-bold text-gray-800;
}

.tab-container {
  @apply flex items-center justify-between mb-6 bg-white p-2 rounded-lg;
}

.tab-list {
  @apply flex gap-2;
}

.tab-item {
  @apply flex items-center px-4 py-2 rounded-lg cursor-pointer transition-all text-gray-600;
}

.tab-item:hover {
  @apply bg-gray-100;
}

.tab-item.active {
  @apply bg-primary text-white;
}

.sort-select {
  @apply w-32;
}

.article-list {
  @apply grid grid-cols-2 gap-4;
}

.question-list {
  @apply space-y-4;
}

.empty-state {
  @apply flex flex-col items-center justify-center py-16 text-gray-400;
}

.pagination-container {
  @apply flex justify-center mt-8;
}
</style>
