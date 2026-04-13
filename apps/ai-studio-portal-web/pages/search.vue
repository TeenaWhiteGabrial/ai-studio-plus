<template>
  <div class="search-page">
    <div class="container">
      <div class="page-header">
        <h1>搜索结果</h1>
        <p class="search-tip">找到 {{ total }} 个与"{{ keyword }}"相关的结果</p>
      </div>

      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="filter-card">
            <template #header>
              <span>筛选条件</span>
            </template>

            <div class="filter-section">
              <h4>资源类型</h4>
              <el-checkbox-group v-model="selectedTypes" @change="handleSearch">
                <el-checkbox label="skill">Skill</el-checkbox>
                <el-checkbox label="mcp">MCP</el-checkbox>
                <el-checkbox label="plugin">Plugin</el-checkbox>
                <el-checkbox label="tutorial">教程</el-checkbox>
                <el-checkbox label="installer">安装包</el-checkbox>
                <el-checkbox label="video">视频</el-checkbox>
              </el-checkbox-group>
            </div>

            <el-divider />

            <div class="filter-section">
              <h4>排序方式</h4>
              <el-radio-group v-model="sortBy" @change="handleSearch">
                <el-radio label="relevance">相关度</el-radio>
                <el-radio label="downloads">下载量</el-radio>
                <el-radio label="latest">最新上传</el-radio>
              </el-radio-group>
            </div>
          </el-card>
        </el-col>

        <el-col :span="18">
          <div class="result-list" v-if="results.length > 0">
            <el-card
              v-for="(item, index) in results"
              :key="index"
              class="result-item"
              @click="goToDetail(item)"
            >
              <div class="result-content">
                <div class="result-cover">
                  <img :src="item.cover || defaultCover" :alt="item.name" />
                </div>
                <div class="result-info">
                  <h3 class="result-name">{{ item.name }}</h3>
                  <p class="result-desc">{{ item.description }}</p>
                  <div class="result-meta">
                    <el-tag size="small">{{ getTypeName(item.type) }}</el-tag>
                    <span class="author">作者：{{ item.author }}</span>
                    <span class="downloads">{{ item.downloadCount || 0 }} 下载</span>
                    <span class="date">{{ item.createdAt }}</span>
                  </div>
                </div>
              </div>
            </el-card>
          </div>

          <el-empty v-else description="未找到相关资源" />

          <el-pagination
            v-if="total > 0"
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            @current-change="handlePageChange"
            class="pagination"
          />
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const keyword = ref((route.query.q as string) || '')
const selectedTypes = ref<string[]>([])
const sortBy = ref('relevance')
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const results = ref<any[]>([])

const defaultCover = 'https://via.placeholder.com/120x80/667eea/ffffff?text=Resource'

const typeNames: Record<string, string> = {
  skill: 'Skill',
  mcp: 'MCP',
  plugin: 'Plugin',
  tutorial: '教程',
  installer: '安装包',
  video: '视频',
}

const getTypeName = (type: string) => typeNames[type] || type

const handleSearch = () => {
  currentPage.value = 1
  fetchResults()
}

const handlePageChange = () => {
  fetchResults()
}

const fetchResults = async () => {
  // TODO: 调用后端搜索 API
  // const res = await searchApi.search({
  //   q: keyword.value,
  //   types: selectedTypes.value.join(','),
  //   sort: sortBy.value,
  //   page: currentPage.value,
  //   size: pageSize.value,
  // })
  results.value = []
  total.value = 0
}

const goToDetail = (item: any) => {
  router.push(`/resource/${item.type}/${item.id}`)
}

watch(() => route.query.q, (newQ) => {
  keyword.value = (newQ as string) || ''
  fetchResults()
})

useHead({
  title: computed(() => `搜索"${keyword.value}" - AI Studio 资源门户`),
  meta: [
    { name: 'description', content: computed(() => `AI Studio 资源门户搜索结果：${keyword.value}`) },
  ],
})

onMounted(() => {
  if (keyword.value) {
    fetchResults()
  }
})
</script>

<style scoped>
.search-page {
  padding: 40px 0;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  margin-bottom: 8px;
}

.search-tip {
  color: #909399;
  font-size: 14px;
}

.filter-card {
  position: sticky;
  top: 80px;
}

.filter-section {
  margin-bottom: 16px;
}

.filter-section h4 {
  font-size: 14px;
  color: #303133;
  margin-bottom: 12px;
}

.filter-section .el-checkbox,
.filter-section .el-radio {
  display: block;
  margin-bottom: 8px;
}

.result-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.result-item {
  cursor: pointer;
  transition: all 0.3s;
}

.result-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.result-content {
  display: flex;
  gap: 16px;
}

.result-cover {
  width: 120px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.result-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.result-info {
  flex: 1;
  min-width: 0;
}

.result-name {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.result-desc {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.result-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 12px;
  color: #909399;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}
</style>
