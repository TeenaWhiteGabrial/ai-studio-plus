<template>
  <div class="resource-detail-page">
    <div class="container">
      <el-breadcrumb separator="/" class="mb-20">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="`/resource/${type}`">{{ pageTitle }}</el-breadcrumb-item>
        <el-breadcrumb-item>{{ resource.name }}</el-breadcrumb-item>
      </el-breadcrumb>

      <el-row :gutter="40">
        <el-col :span="16">
          <el-card>
            <div class="resource-header">
              <h1 class="resource-title">{{ resource.name }}</h1>
              <div class="resource-meta">
                <span>作者：{{ resource.author }}</span>
                <span>上传时间：{{ resource.createdAt }}</span>
                <span>下载次数：{{ resource.downloadCount || 0 }}</span>
              </div>
            </div>

            <div class="resource-description">
              <h3>资源描述</h3>
              <p>{{ resource.description || '暂无描述' }}</p>
            </div>

            <div class="resource-content">
              <h3>资源详情</h3>
              <div class="content-body">
                <p>资源详细信息加载中...</p>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card class="action-card">
            <div class="action-section">
              <el-button type="primary" size="large" class="download-btn">
                立即下载
              </el-button>
              <div class="action-icons">
                <el-button circle @click="handleLike">
                  <span>{{ liked ? '❤️' : '🤍' }}</span>
                </el-button>
                <el-button circle @click="handleFavorite">
                  <span>{{ favorited ? '⭐' : '☆' }}</span>
                </el-button>
              </div>
            </div>

            <el-divider />

            <div class="version-info">
              <h4>版本信息</h4>
              <p>当前版本：{{ resource.currentVersion || '1.0.0' }}</p>
              <el-select v-model="selectedVersion" placeholder="选择版本" size="small">
                <el-option
                  v-for="v in versions"
                  :key="v.id"
                  :label="v.version"
                  :value="v.id"
                />
              </el-select>
            </div>

            <el-divider />

            <div class="author-info">
              <h4>发布者信息</h4>
              <div class="author-detail">
                <el-avatar :size="48" icon="UserFilled" />
                <div class="author-text">
                  <span class="author-name">{{ resource.author }}</span>
                  <span class="author-dept">{{ resource.deptName }}</span>
                </div>
              </div>
            </div>
          </el-card>

          <el-card class="mt-20">
            <template #header>
              <span>版本历史</span>
            </template>
            <el-timeline>
              <el-timeline-item
                v-for="(v, index) in versions"
                :key="index"
                :timestamp="v.publishedAt"
                placement="top"
              >
                <el-card>
                  <h4>{{ v.version }}</h4>
                  <p>{{ v.changeLog || '无变更说明' }}</p>
                </el-card>
              </el-timeline-item>
            </el-timeline>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
const route = useRoute()
const type = computed(() => route.params.type as string)
const id = computed(() => route.params.id as string)

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
  title: computed(() => `${resource.value?.name || '加载中'} - AI Studio`),
  meta: [
    { name: 'description', content: computed(() => resource.value?.description || '') },
    { property: 'og:title', content: computed(() => `${resource.value?.name || ''} - AI Studio`) },
    { property: 'og:description', content: computed(() => resource.value?.description || '') },
  ],
})

const resource = ref<any>({
  name: '加载中...',
  author: '',
  createdAt: '',
  downloadCount: 0,
  description: '',
  currentVersion: '1.0.0',
})

const versions = ref<any[]>([])
const selectedVersion = ref()
const liked = ref(false)
const favorited = ref(false)

const handleLike = () => {
  liked.value = !liked.value
}

const handleFavorite = () => {
  favorited.value = !favorited.value
}

onMounted(() => {
  // TODO: 获取资源详情
})
</script>

<style scoped>
.resource-detail-page {
  padding: 40px 0;
}

.mb-20 {
  margin-bottom: 20px;
}

.mt-20 {
  margin-top: 20px;
}

.resource-title {
  font-size: 28px;
  margin-bottom: 16px;
}

.resource-meta {
  display: flex;
  gap: 24px;
  color: #909399;
  font-size: 14px;
  margin-bottom: 24px;
}

.resource-description h3,
.resource-content h3 {
  font-size: 18px;
  margin-bottom: 12px;
  color: #303133;
}

.resource-description p {
  color: #606266;
  line-height: 1.8;
}

.content-body {
  color: #606266;
  line-height: 1.8;
}

.action-card {
  position: sticky;
  top: 80px;
}

.action-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.download-btn {
  width: 100%;
}

.action-icons {
  display: flex;
  gap: 16px;
}

.version-info h4,
.author-info h4 {
  font-size: 14px;
  margin-bottom: 12px;
  color: #303133;
}

.author-detail {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-text {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-weight: 600;
  color: #303133;
}

.author-dept {
  font-size: 12px;
  color: #909399;
}
</style>
