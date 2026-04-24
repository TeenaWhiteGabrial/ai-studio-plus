<template>
  <div class="community-page">
    <section class="csdn-card filter-bar">
      <div class="channel-tabs">
        <button
          v-for="tab in tabs"
          :key="tab.type"
          class="tab-btn"
          :class="{ active: currentTab === tab.type }"
          @click="switchTab(tab.type)"
        >
          <Icon :name="tab.icon" size="17" />
          <span>{{ tab.name }}</span>
        </button>
      </div>

      <div class="search-sort">
        <el-input
          v-model="keyword"
          placeholder="搜索文章/问题"
          class="keyword-input"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">
              <Icon name="material-symbols:search" size="18" />
            </el-button>
          </template>
        </el-input>

        <el-select v-model="currentSort" class="sort-select" @change="handleSortChange">
          <el-option
            v-for="option in sortOptions"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          />
        </el-select>

        <el-button @click="goWriteArticle">
          <Icon name="material-symbols:article-outline" size="18" />
          发布文章
        </el-button>
        <el-button type="primary" @click="goAsk">
          <Icon name="material-symbols:edit-square-outline" size="18" />
          发布问题
        </el-button>
      </div>
    </section>

    <section class="result-section">
      <div v-if="loading" class="csdn-empty">加载中...</div>

      <template v-else>
        <div v-if="currentTab === 'article'" class="list-wrap">
          <ArticleCard
            v-for="article in articleList"
            :key="article.id"
            :article="article"
          />
        </div>

        <div v-else class="list-wrap">
          <QuestionCard
            v-for="question in questionList"
            :key="question.id"
            :question="question"
          />
        </div>

        <div v-if="showEmpty" class="csdn-card csdn-empty empty-card">暂无内容，换个条件试试。</div>

        <div v-if="totalCount > pageSize" class="pagination-wrap">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="totalCount"
            layout="prev, pager, next"
            @current-change="handlePageChange"
          />
        </div>
      </template>
    </section>
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
  { name: '文章', type: 'article', icon: 'material-symbols:article-outline' },
  { name: '问答', type: 'question', icon: 'material-symbols:help-outline' }
]

const currentTab = ref((route.query.type as string) || 'article')
const currentSort = ref((route.query.sort as string) || 'latest')
const keyword = ref((route.query.keyword as string) || '')
const currentPage = ref(Number(route.query.page || 1))
const pageSize = 10

const loading = ref(false)
const totalCount = ref(0)
const articleList = ref<Article[]>([])
const questionList = ref<Question[]>([])

const sortOptions = computed(() => {
  if (currentTab.value === 'question') {
    return [
      { label: '最新发布', value: 'latest' },
      { label: '热门问答', value: 'hot' },
      { label: '待回答', value: 'unanswered' }
    ]
  }
  return [
    { label: '最新发布', value: 'latest' },
    { label: '热门文章', value: 'hot' }
  ]
})

const showEmpty = computed(() => {
  return currentTab.value === 'article' ? articleList.value.length === 0 : questionList.value.length === 0
})

async function loadData() {
  loading.value = true
  try {
    if (currentTab.value === 'article') {
      const res = await getArticleList({
        page: currentPage.value,
        pageSize,
        keyword: keyword.value || undefined,
        sort: currentSort.value as 'latest' | 'hot',
        tagId: route.query.tagId as string
      })
      articleList.value = res.records || []
      totalCount.value = res.total || 0
      return
    }

    const res = await getQuestionList({
      page: currentPage.value,
      pageSize,
      keyword: keyword.value || undefined,
      sort: currentSort.value as 'latest' | 'hot' | 'unanswered',
      tagId: route.query.tagId as string
    })
    questionList.value = res.records || []
    totalCount.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function updateQuery() {
  router.replace({
    path: '/community',
    query: {
      ...route.query,
      type: currentTab.value,
      sort: currentSort.value,
      keyword: keyword.value || undefined,
      page: currentPage.value
    }
  })
}

function switchTab(type: string) {
  currentTab.value = type
  currentSort.value = 'latest'
  currentPage.value = 1
  updateQuery()
  loadData()
}

function handleSortChange() {
  currentPage.value = 1
  updateQuery()
  loadData()
}

function handleSearch() {
  currentPage.value = 1
  updateQuery()
  loadData()
}

function handlePageChange(page: number) {
  currentPage.value = page
  updateQuery()
  loadData()
}

function goAsk() {
  if (!authStore.token) {
    goLoginPage()
    return
  }
  navigateTo('/community/ask')
}

function goWriteArticle() {
  if (!authStore.token) {
    goLoginPage()
    return
  }
  navigateTo('/community/write')
}

onMounted(loadData)
</script>

<style scoped>
.community-page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.filter-bar {
  padding: 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.channel-tabs {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.tab-btn {
  height: 36px;
  border: 1px solid var(--csdn-line);
  border-radius: 10px;
  background: #fff;
  color: var(--csdn-subtext);
  padding: 0 12px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
}

.tab-btn:hover {
  color: var(--csdn-primary);
  border-color: #bdd2ff;
}

.tab-btn.active {
  color: var(--csdn-primary);
  background: var(--csdn-primary-soft);
  border-color: #a9c5ff;
  font-weight: 600;
}

.search-sort {
  display: inline-flex;
  align-items: center;
  gap: 10px;
}

.keyword-input {
  width: 280px;
}

.sort-select {
  width: 130px;
}

.result-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.list-wrap {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.empty-card {
  padding: 24px;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 12px 0 2px;
}

@media (max-width: 900px) {
  .search-sort {
    width: 100%;
    flex-wrap: wrap;
  }

  .keyword-input {
    width: 100%;
  }
}
</style>
