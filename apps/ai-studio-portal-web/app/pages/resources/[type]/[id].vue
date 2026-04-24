<template>
  <div class="resource-page">
    <div v-if="loading" class="csdn-card csdn-empty">资源加载中...</div>
    <div v-else-if="!resource" class="csdn-card csdn-empty">资源不存在或未审核通过</div>

    <template v-else>
      <article class="csdn-card resource-card">
        <div class="cover-block">
          <img v-if="coverUrl" :src="coverUrl" :alt="resourceName" class="cover-img">
          <div v-else class="cover-fallback">
            <Icon :name="resourceIcon" size="42" />
          </div>
        </div>

        <div class="resource-main">
          <header class="title-row">
            <h1 class="resource-title">{{ resourceName }}</h1>
            <span class="type-tag">{{ resourceType.toUpperCase() }}</span>
          </header>

          <div class="meta-row">
            <span v-if="resource.categoryName || resource.category">
              <Icon name="material-symbols:category-outline" size="16" />
              {{ resource.categoryName || resource.category }}
            </span>
            <span v-if="timeValue">
              <Icon name="material-symbols:schedule-outline" size="16" />
              {{ formatTime(timeValue) }}
            </span>
            <span v-if="versionText">
              <Icon name="material-symbols:deployed-code-outline" size="16" />
              v{{ versionText }}
            </span>
          </div>

          <p class="desc">{{ introText || '暂无资源描述' }}</p>

          <div class="actions">
            <el-button type="primary" @click="handleDownload">
              <Icon name="material-symbols:download" size="17" />
              <span>下载资源</span>
            </el-button>
            <el-button v-if="resourceType === 'tutorial'" @click="loadVideo">
              <Icon name="material-symbols:play-circle-outline" size="17" />
              <span>播放教程视频</span>
            </el-button>
            <el-button @click="copyLink">
              <Icon name="material-symbols:link" size="17" />
              <span>复制链接</span>
            </el-button>
          </div>
        </div>
      </article>

      <section v-if="videoUrl" class="csdn-card video-card">
        <h3>教程视频</h3>
        <video class="video-player" controls :src="videoUrl" />
      </section>

      <section class="csdn-card content-card">
        <h3>资源详情</h3>
        <div v-if="resource.content" class="csdn-prose">
          <div v-if="resource.contentType === 'markdown'" class="markdown-raw">{{ resource.content }}</div>
          <div v-else v-html="resource.content" />
        </div>
        <div v-else class="csdn-empty">暂无详情内容</div>
      </section>
    </template>
  </div>
</template>

<script setup lang="ts">
import type { Resource } from '~~/shared/types/resource'
import type { ResourceType } from '~/composables/useResource'

const route = useRoute()
const authStore = useAuthStore()
const {
  getResourceDetail,
  downloadPlugin,
  downloadSkill,
  downloadTutorialZip,
  getTutorialVideoUrl
} = useResource()
const { createBrowseHistory } = useCommunity()

const loading = ref(true)
const resource = ref<Resource | null>(null)
const videoUrl = ref('')

const resourceType = computed(() => route.params.type as ResourceType)

const resourceIcon = computed(() => {
  if (resourceType.value === 'skill') return 'material-symbols:psychology-alt-outline'
  if (resourceType.value === 'plugin') return 'material-symbols:extension-outline'
  return 'material-symbols:play-lesson-outline'
})

const resourceName = computed(() => resource.value?.name || resource.value?.title || '未命名资源')
const introText = computed(() => resource.value?.introduction || resource.value?.description || '')
const coverUrl = computed(() => resource.value?.photo || resource.value?.coverImage || resource.value?.icon || '')
const timeValue = computed(() => resource.value?.publishTime || resource.value?.createTime || resource.value?.createdAt || '')
const versionText = computed(() => resource.value?.latestVersion || '')

async function loadResource() {
  loading.value = true
  try {
    const type = route.params.type as ResourceType
    const id = route.params.id as string
    resource.value = await getResourceDetail(type, id)

    if (authStore.token) {
      createBrowseHistory({ targetType: 'resource', targetId: id })
    }
  } finally {
    loading.value = false
  }
}

function handleDownload() {
  const id = route.params.id as string
  if (resourceType.value === 'skill') {
    downloadSkill(id)
    return
  }
  if (resourceType.value === 'plugin') {
    downloadPlugin(id)
    return
  }
  downloadTutorialZip(id)
}

async function loadVideo() {
  if (resourceType.value !== 'tutorial') return
  videoUrl.value = await getTutorialVideoUrl(route.params.id as string)
}

async function copyLink() {
  await navigator.clipboard.writeText(window.location.href)
  ElMessage.success('链接已复制')
}

function formatTime(value: string) {
  if (!value) return ''
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return ''
  return date.toLocaleString('zh-CN')
}

onMounted(loadResource)
</script>

<style scoped>
.resource-page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.resource-card {
  padding: 18px;
  display: grid;
  grid-template-columns: 280px minmax(0, 1fr);
  gap: 18px;
}

.cover-block {
  width: 100%;
  height: 188px;
  border-radius: 12px;
  overflow: hidden;
  background: #f1f5fb;
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-fallback {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--csdn-primary);
  background: linear-gradient(145deg, #eaf2ff, #f9fbff);
}

.resource-main {
  min-width: 0;
}

.title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
}

.resource-title {
  margin: 0;
  font-size: 30px;
  line-height: 1.35;
}

.type-tag {
  flex-shrink: 0;
  border-radius: 999px;
  background: var(--csdn-primary-soft);
  color: var(--csdn-primary);
  font-size: 12px;
  padding: 3px 10px;
}

.meta-row {
  margin-top: 10px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 14px;
  color: var(--csdn-muted);
  font-size: 13px;
}

.meta-row span {
  display: inline-flex;
  align-items: center;
  gap: 2px;
}

.desc {
  margin: 14px 0 16px;
  color: var(--csdn-subtext);
  line-height: 1.75;
}

.actions {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.video-card,
.content-card {
  padding: 18px;
}

.video-card h3,
.content-card h3 {
  margin: 0 0 10px;
  font-size: 20px;
}

.video-player {
  width: 100%;
  border-radius: 10px;
  background: #111827;
}

.markdown-raw {
  white-space: pre-wrap;
  font-family: "JetBrains Mono", "Consolas", monospace;
  font-size: 13px;
  line-height: 1.7;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 12px;
}

@media (max-width: 900px) {
  .resource-card {
    grid-template-columns: 1fr;
  }

  .resource-title {
    font-size: 24px;
  }
}
</style>
