<template>
  <div class="resources-page">
    <!-- 主内容区 -->
    <div class="main-area">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">资源中心</h1>
      </div>

      <!-- Tab 切换 -->
      <div class="tab-container">
        <div class="tab-list">
          <span
            v-for="tab in resourceTabs"
            :key="tab.type"
            class="tab-item"
            :class="{ active: currentType === tab.type }"
            @click="switchType(tab.type)"
          >
            <Icon :name="tab.icon" class="mr-1" />
            {{ tab.name }}
          </span>
        </div>
        <!-- 排序 -->
        <div class="sort-select">
          <el-select v-model="currentSort" @change="handleSortChange">
            <el-option label="最新发布" value="latest" />
            <el-option label="最热门" value="hot" />
            <el-option label="推荐" value="recommend" />
          </el-select>
        </div>
      </div>

      <!-- 搜索 -->
      <div class="search-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索资源..."
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">
              <Icon name="material-symbols:search" />
            </el-button>
          </template>
        </el-input>
      </div>

      <!-- 资源列表 -->
      <div v-if="loading" class="text-center py-12 text-gray-400">
        加载中...
      </div>
      <div v-else-if="resourceList.length > 0" class="resource-grid">
        <ResourceCard
          v-for="resource in resourceList"
          :key="resource.id"
          :resource="resource"
          :type="currentType as 'skill' | 'plugin' | 'tutorial'"
        />
      </div>
      <div v-else class="empty-state">
        <Icon name="material-symbols:folder-open" class="text-6xl text-gray-300 mb-4" />
        <p>暂无资源</p>
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
import type { Resource } from '~~/shared/types/resource'

const route = useRoute()
const router = useRouter()
const { getResourceList } = useResource()

const resourceTabs = [
  { name: '技能', type: 'skill', icon: 'material-symbols:smart-toy' },
  { name: '插件', type: 'plugin', icon: 'material-symbols:extension' }
]

const currentType = ref((route.query.type as string) || 'skill')
const currentSort = ref((route.query.sort as string) || 'latest')
const keyword = ref((route.query.keyword as string) || '')
const currentPage = ref(1)
const pageSize = 12
const totalCount = ref(0)
const loading = ref(false)
const resourceList = ref<Resource[]>([])

async function loadResources() {
  loading.value = true
  try {
    const res = await getResourceList({
      type: currentType.value as 'skill' | 'plugin',
      sort: currentSort.value as 'latest' | 'hot' | 'recommend',
      keyword: keyword.value || undefined,
      page: currentPage.value,
      pageSize
    })
    resourceList.value = res.records || []
    totalCount.value = res.total || 0
  }
  catch (err) {
    console.error('加载资源失败:', err)
  }
  finally {
    loading.value = false
  }
}

function switchType(type: string) {
  currentType.value = type
  currentPage.value = 1
  updateQuery()
  loadResources()
}

function handleSortChange() {
  currentPage.value = 1
  updateQuery()
  loadResources()
}

function handleSearch() {
  currentPage.value = 1
  updateQuery()
  loadResources()
}

function handlePageChange(page: number) {
  currentPage.value = page
  loadResources()
}

function updateQuery() {
  router.replace({
    query: {
      type: currentType.value,
      sort: currentSort.value,
      keyword: keyword.value,
      page: currentPage.value
    }
  })
}

onMounted(() => {
  loadResources()
})
</script>

<style scoped>
.resources-page {
  @apply flex gap-6;
}

.main-area {
  @apply flex-1 min-w-0;
}

.page-header {
  @apply mb-6;
}

.page-title {
  @apply text-2xl font-bold text-gray-800;
}

.tab-container {
  @apply flex items-center justify-between mb-4 bg-white p-2 rounded-lg;
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

.search-bar {
  @apply mb-6;
}

.resource-grid {
  @apply grid grid-cols-4 gap-4;
}

.empty-state {
  @apply flex flex-col items-center justify-center py-16 text-gray-400;
}

.pagination-container {
  @apply flex justify-center mt-8;
}

@media (max-width: 1200px) {
  .resource-grid {
    @apply grid-cols-3;
  }
}

@media (max-width: 900px) {
  .resource-grid {
    @apply grid-cols-2;
  }
}
</style>
