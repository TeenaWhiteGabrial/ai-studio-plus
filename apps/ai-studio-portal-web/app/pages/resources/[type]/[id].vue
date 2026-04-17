<template>
  <div class="resource-detail-page">
    <!-- 主内容区 -->
    <div class="main-area">
      <div v-if="loading" class="text-center py-12">
        加载中...
      </div>
      <div v-else-if="!resource" class="text-center py-12">
        资源不存在
      </div>
      <template v-else>
        <!-- 资源详情 -->
        <div class="resource-container bg-white rounded-lg p-6">
          <div class="resource-header">
            <div class="resource-cover">
              <img v-if="resource.photo" :src="resource.photo" :alt="resource.name" />
              <div v-else class="default-cover">
                <Icon :name="resourceIcon" class="text-6xl text-primary" />
              </div>
            </div>
            <div class="resource-info">
              <h1 class="resource-title">{{ resource.name }}</h1>
              <div class="resource-meta">
                <span v-if="resource.categoryName" class="meta-item">
                  <Icon name="material-symbols:category" class="mr-1" />
                  {{ resource.categoryName }}
                </span>
                <span class="meta-item">
                  <Icon name="material-symbols:schedule" class="mr-1" />
                  {{ formatTime(resource.publishTime || resource.createTime) }}
                </span>
              </div>
              <p v-if="resource.introduction" class="resource-intro">{{ resource.introduction }}</p>
              <!-- 操作按钮 -->
              <div class="resource-actions">
                <el-button type="primary" @click="handleDownload">
                  <Icon name="material-symbols:download" class="mr-1" />
                  下载
                </el-button>
                <el-button @click="handleFavorite">
                  <Icon name="material-symbols:bookmark-outline" class="mr-1" />
                  收藏
                </el-button>
                <el-button @click="handleShare">
                  <Icon name="material-symbols:share" class="mr-1" />
                  分享
                </el-button>
              </div>
            </div>
          </div>

          <!-- 详细信息 -->
          <div class="resource-details mt-6">
            <el-tabs v-model="activeTab">
              <el-tab-pane label="资源详情" name="detail">
                <div class="detail-content">
                  <div v-if="resource.industryNames" class="detail-item">
                    <span class="detail-label">所属行业：</span>
                    <span class="detail-value">{{ resource.industryNames }}</span>
                  </div>
                  <div v-if="resource.domainNames" class="detail-item">
                    <span class="detail-label">所属领域：</span>
                    <span class="detail-value">{{ resource.domainNames }}</span>
                  </div>
                  <div v-if="resource.addressName" class="detail-item">
                    <span class="detail-label">资源地址：</span>
                    <span class="detail-value">{{ resource.addressName }}</span>
                  </div>
                  <div v-if="resource.price" class="detail-item">
                    <span class="detail-label">资源价格：</span>
                    <span class="detail-value">{{ resource.price }}</span>
                  </div>
                </div>
              </el-tab-pane>
              <el-tab-pane label="使用说明" name="usage">
                <div class="usage-content" v-html="resource.content || '<p>暂无使用说明</p>'"></div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Resource } from '~~/shared/types/resource'

const route = useRoute()
const { getResourceDetail } = useResource()
const { createBrowseHistory, createFavorite } = useCommunity()
const authStore = useAuthStore()

const resource = ref<Resource | null>(null)
const loading = ref(true)
const activeTab = ref('detail')

const resourceType = computed(() => route.params.type as string)

const resourceIcon = computed(() => {
  switch (resourceType.value) {
    case 'skill':
      return 'material-symbols:smart-toy'
    case 'plugin':
      return 'material-symbols:extension'
    case 'tutorial':
      return 'material-symbols:menu-book'
    default:
      return 'material-symbols:folder'
  }
})

async function loadResource() {
  loading.value = true
  const type = route.params.type as string
  const id = route.params.id as string
  try {
    const res = await getResourceDetail(type as 'skill' | 'plugin' | 'tutorial', id)
    resource.value = res
    // 创建浏览记录
    if (authStore.token) {
      createBrowseHistory({ targetType: 'resource', targetId: id })
    }
  }
  catch (err) {
    console.error('加载资源失败:', err)
  }
  finally {
    loading.value = false
  }
}

function handleDownload() {
  if (!authStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  if (resource.value?.link) {
    window.open(resource.value.link, '_blank')
  }
}

function handleFavorite() {
  if (!authStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  if (!resource.value)
    return
  createFavorite({ targetType: 'resource', targetId: resource.value.id }).then((res) => {
    if (res.code === 200) {
      ElMessage.success('收藏成功')
    }
  })
}

function handleShare() {
  navigator.clipboard.writeText(window.location.href)
  ElMessage.success('链接已复制到剪贴板')
}

function formatTime(time: string) {
  if (!time)
    return ''
  return new Date(time).toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadResource()
})
</script>

<style scoped>
.resource-detail-page {
  @apply flex gap-6;
}

.main-area {
  @apply flex-1 min-w-0;
}

.resource-container {
  @apply mb-6;
}

.resource-header {
  @apply flex gap-6;
}

.resource-cover {
  @apply w-64 h-48 rounded-lg overflow-hidden bg-gray-100 flex-shrink-0;
}

.resource-cover img {
  @apply w-full h-full object-cover;
}

.default-cover {
  @apply w-full h-full flex items-center justify-center bg-gradient-to-br from-primary-faint to-primary-light;
}

.resource-info {
  @apply flex-1;
}

.resource-title {
  @apply text-xl font-bold text-gray-800 mb-3;
}

.resource-meta {
  @apply flex items-center gap-4 text-sm text-gray-500 mb-4;
}

.meta-item {
  @apply flex items-center;
}

.resource-intro {
  @apply text-gray-600 mb-4;
}

.resource-actions {
  @apply flex items-center gap-3;
}

.resource-details {
  @apply pt-6 border-t border-gray-100;
}

.detail-content {
  @apply space-y-3;
}

.detail-item {
  @apply flex items-start;
}

.detail-label {
  @apply text-gray-500 w-24 flex-shrink-0;
}

.detail-value {
  @apply text-gray-800;
}

.usage-content {
  @apply text-gray-700 leading-relaxed;
}

.usage-content :deep(p) {
  @apply mb-4;
}

@media (max-width: 768px) {
  .resource-header {
    @apply flex-col;
  }

  .resource-cover {
    @apply w-full;
  }
}
</style>
