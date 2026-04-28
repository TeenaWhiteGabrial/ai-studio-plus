<template>
  <div class="community-page">
    <section class="csdn-card filter-bar">
      <div class="filter-left">
        <h1 class="page-title">文章</h1>
        <div class="sort-tabs">
          <button
            v-for="option in sortOptions"
            :key="option.value"
            class="sort-tab"
            :class="{ active: currentSort === option.value }"
            @click="switchSort(option.value)"
          >
            {{ option.label }}
          </button>
        </div>
      </div>

      <div class="search-sort">
        <el-input
          v-model="keyword"
          placeholder="搜索文章"
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
      </div>
    </section>

    <section class="result-section csdn-card">
      <div v-if="loading" class="csdn-empty">加载中...</div>

      <template v-else>
        <div v-if="articleList.length > 0" class="list-wrap">
          <ArticleCard
            v-for="article in articleList"
            :key="article.id"
            :article="article"
          />
        </div>

        <div v-else class="csdn-empty empty-card">暂无内容，换个条件试试。</div>

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

type ArticleSort = 'latest' | 'hot' | 'likes' | 'favorites'

const route = useRoute()
const router = useRouter()
const { getArticleList } = useArticle()

const currentSort = ref<ArticleSort>((route.query.sort as ArticleSort) || 'latest')
const keyword = ref((route.query.keyword as string) || '')
const currentPage = ref(Number(route.query.page || 1))
const pageSize = 10

const loading = ref(false)
const totalCount = ref(0)
const articleList = ref<Article[]>([])

const sortOptions: Array<{ label: string; value: ArticleSort }> = [
  { label: '最新', value: 'latest' },
  { label: '热门', value: 'hot' },
  { label: '点赞', value: 'likes' },
  { label: '收藏', value: 'favorites' }
]

async function loadData() {
  loading.value = true
  try {
    const res = await getArticleList({
      page: currentPage.value,
      pageSize,
      keyword: keyword.value || undefined,
      sort: currentSort.value,
      tagId: route.query.tagId as string
    })
    articleList.value = res.records || []
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
      sort: currentSort.value,
      keyword: keyword.value || undefined,
      page: currentPage.value
    }
  })
}

function switchSort(sort: ArticleSort) {
  currentSort.value = sort
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

onMounted(loadData)
</script>

<style scoped>
.community-page {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.filter-bar {
  padding: 14px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-left {
  display: inline-flex;
  align-items: center;
  gap: 16px;
}

.page-title {
  margin: 0;
  font-size: 20px;
  line-height: 1.3;
  color: #111827;
}

.sort-tabs {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.sort-tab {
  height: 30px;
  border: 0;
  border-radius: 4px;
  background: transparent;
  color: var(--csdn-subtext);
  padding: 0 10px;
  font-size: 14px;
  cursor: pointer;
}

.sort-tab:hover,
.sort-tab.active {
  color: var(--portal-secondary);
  background: var(--portal-gradient-soft);
  font-weight: 700;
}

.search-sort {
  display: inline-flex;
  align-items: center;
  gap: 10px;
}

.keyword-input {
  width: 300px;
}

.result-section {
  padding: 10px 12px 14px;
}

.list-wrap {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.empty-card {
  padding: 32px 0;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 14px 0 2px;
}

@media (max-width: 900px) {
  .filter-left,
  .search-sort {
    width: 100%;
  }

  .filter-left {
    align-items: flex-start;
    flex-direction: column;
    gap: 10px;
  }

  .keyword-input {
    width: 100%;
  }
}
</style>
