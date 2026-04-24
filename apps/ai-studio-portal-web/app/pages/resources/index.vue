<template>
  <div class="resources-page">
    <section class="csdn-card filter-bar">
      <div class="type-tabs">
        <button
          v-for="tab in resourceTabs"
          :key="tab.type"
          class="tab-btn"
          :class="{ active: currentType === tab.type }"
          @click="switchType(tab.type)"
        >
          <Icon :name="tab.icon" size="17" />
          <span>{{ tab.name }}</span>
        </button>
      </div>

      <div class="search-sort">
        <el-input
          v-model="keyword"
          placeholder="搜索资源名称、描述"
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
          <el-option label="最新发布" value="latest" />
          <el-option label="热门资源" value="hot" />
          <el-option label="推荐优先" value="recommend" />
        </el-select>
      </div>
    </section>

    <section class="content-section">
      <div v-if="loading" class="csdn-empty">加载中...</div>
      <div v-else-if="resourceList.length === 0" class="csdn-card csdn-empty empty-card">暂无资源</div>
      <div v-else class="resource-grid">
        <ResourceCard
          v-for="resource in resourceList"
          :key="resource.id"
          :resource="resource"
          :type="currentType"
        />
      </div>

      <div v-if="totalCount > pageSize" class="pagination-wrap">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="totalCount"
          layout="prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import type { Resource } from '~~/shared/types/resource'
import type { ResourceType } from '~/composables/useResource'

const route = useRoute()
const router = useRouter()
const { getResourceList } = useResource()

const resourceTabs: Array<{ name: string; type: ResourceType; icon: string }> = [
  { name: 'Skill', type: 'skill', icon: 'material-symbols:psychology-alt-outline' },
  { name: 'Plugin', type: 'plugin', icon: 'material-symbols:extension-outline' },
  { name: 'Tutorial', type: 'tutorial', icon: 'material-symbols:play-lesson-outline' }
]

const currentType = ref<ResourceType>((route.query.type as ResourceType) || 'skill')
const currentSort = ref((route.query.sort as string) || 'latest')
const keyword = ref((route.query.keyword as string) || '')
const currentPage = ref(Number(route.query.page || 1))
const pageSize = 12

const loading = ref(false)
const totalCount = ref(0)
const resourceList = ref<Resource[]>([])

async function loadResources() {
  loading.value = true
  try {
    const res = await getResourceList({
      type: currentType.value,
      sort: currentSort.value as 'latest' | 'hot' | 'recommend',
      keyword: keyword.value || undefined,
      page: currentPage.value,
      pageSize
    })
    resourceList.value = res.records || []
    totalCount.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function updateQuery() {
  router.replace({
    path: '/resources',
    query: {
      type: currentType.value,
      sort: currentSort.value,
      keyword: keyword.value || undefined,
      page: currentPage.value
    }
  })
}

function switchType(type: ResourceType) {
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
  updateQuery()
  loadResources()
}

onMounted(loadResources)
</script>

<style scoped>
.resources-page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.filter-bar {
  padding: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.type-tabs {
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
  width: 300px;
}

.sort-select {
  width: 130px;
}

.content-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.resource-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
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

@media (max-width: 1024px) {
  .resource-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
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
