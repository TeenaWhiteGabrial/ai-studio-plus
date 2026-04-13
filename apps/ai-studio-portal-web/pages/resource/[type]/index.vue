<template>
  <div class="resource-list-page">
    <div class="container">
      <div class="page-header">
        <h1>{{ pageTitle }}资源</h1>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>{{ pageTitle }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <div class="filter-bar">
        <el-input
          v-model="searchQuery"
          placeholder="搜索资源..."
          class="search-input"
          clearable
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
      </div>

      <el-row :gutter="20" class="resource-grid">
        <el-col :span="6" v-for="(item, index) in resources" :key="index">
          <NuxtLink :to="`/resource/${type}/${item.id}`">
            <el-card class="resource-card" :body-style="{ padding: '0px' }">
              <div class="resource-cover">
                <img :src="item.cover || defaultCover" :alt="item.name" />
              </div>
              <div class="resource-info">
                <h3 class="resource-name">{{ item.name }}</h3>
                <p class="resource-desc">{{ item.description }}</p>
                <div class="resource-meta">
                  <span class="author">{{ item.author }}</span>
                  <span class="downloads">{{ item.downloadCount || 0 }} 下载</span>
                </div>
              </div>
            </el-card>
          </NuxtLink>
        </el-col>
      </el-row>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Search } from '@element-plus/icons-vue'

const route = useRoute()
const type = computed(() => route.params.type as string)

const typeMap: Record<string, string> = {
  skill: 'Skill',
  mcp: 'MCP',
  plugin: 'Plugin',
  tutorial: '教程',
  installer: '安装包',
  video: '视频',
}

const pageTitle = computed(() => typeMap[type.value] || '资源')

useHead({
  title: computed(() => `${pageTitle.value} - AI Studio 资源门户`),
})

const searchQuery = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const resources = ref<any[]>([])

const defaultCover = 'https://via.placeholder.com/300x200/667eea/ffffff?text=Resource'

const handleSearch = () => {
  currentPage.value = 1
  fetchResources()
}

const handlePageChange = () => {
  fetchResources()
}

const fetchResources = async () => {
  // TODO: 调用后端 API 获取资源列表
  resources.value = []
  total.value = 0
}

onMounted(() => {
  fetchResources()
})
</script>

<style scoped>
.resource-list-page {
  padding: 40px 0;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  margin-bottom: 12px;
}

.filter-bar {
  margin-bottom: 30px;
}

.search-input {
  max-width: 400px;
}

.resource-grid {
  margin-bottom: 30px;
}

.resource-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
  text-decoration: none;
  color: inherit;
  margin-bottom: 20px;
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

.pagination {
  display: flex;
  justify-content: center;
}
</style>
