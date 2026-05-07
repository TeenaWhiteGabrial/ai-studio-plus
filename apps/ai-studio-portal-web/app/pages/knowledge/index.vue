<template>
  <div class="knowledge-page">
    <div class="stage-grid portal-theme-immersive-only"></div>
    <PortalLandingTopbar />
    <section class="search-stage">
      <div class="stage-content">
        <div class="page-kicker">Knowledge Base</div>
        <h1>知识库检索</h1>
        <form class="search-panel" @submit.prevent="runSearch">
          <Icon name="material-symbols:manage-search" size="26" />
          <input
            v-model="query"
            type="search"
            placeholder="输入问题、关键词或文档线索"
            aria-label="知识库搜索"
          >
          <el-input-number v-model="topK" :min="1" :max="20" size="small" controls-position="right" />
          <button type="submit" :disabled="searching || !query.trim()">
            <Icon name="material-symbols:arrow-forward" size="22" />
          </button>
        </form>
        <div class="service-status" :class="serviceStatus">
          <span></span>
          {{ serviceText }}
        </div>
      </div>
    </section>

    <section v-if="searching || hasSearched" class="result-shell">
      <div class="result-head">
        <div>
          <h2>检索结果</h2>
          <p>{{ resultSummary }}</p>
        </div>
        <button class="refresh-btn" type="button" :disabled="loading" @click="checkHealth">
          <Icon name="material-symbols:refresh" size="18" />
          <span>刷新状态</span>
        </button>
      </div>

      <div v-if="searching" class="empty-state">检索中...</div>
      <div v-else-if="hasSearched && searchResults.length === 0" class="empty-state">没有匹配到相关知识片段</div>
      <div v-else class="result-list">
        <article v-for="item in searchResults" :key="item.id" class="result-item">
          <div class="result-meta">
            <span class="score">{{ formatScore(item.score) }}</span>
            <span>{{ getResultFileName(item) }}</span>
            <span>片段 {{ item.metadata?.chunkIndex ?? '-' }}</span>
          </div>
          <p>{{ item.metadata?.content || '暂无片段内容' }}</p>
        </article>
      </div>
    </section>
    <FooterApollo :transparent="isTransparentChrome" />
  </div>
</template>

<script setup lang="ts">
import type { KnowledgeSearchResult } from '~/composables/useKnowledgeBase'

definePageMeta({
  layout: false,
  middleware: ['auth'],
})

useHead({
  title: '知识库检索',
})

const route = useRoute()
const knowledgeBase = useKnowledgeBase()
const { currentPortalTheme, getPortalTheme } = useTheme()

const loading = ref(false)
const searching = ref(false)
const hasSearched = ref(false)
const serviceStatus = ref<'checking' | 'online' | 'offline'>('checking')
const searchResults = ref<KnowledgeSearchResult[]>([])
const query = ref('')
const topK = ref(8)
const isTransparentChrome = computed(() => getPortalTheme(currentPortalTheme.value).immersive)

const serviceText = computed(() => {
  if (serviceStatus.value === 'online') return '知识库服务已连接'
  if (serviceStatus.value === 'offline') return '知识库服务未连接'
  return '正在检查知识库服务'
})

const resultSummary = computed(() => {
  return `共找到 ${searchResults.value.length} 条相关片段`
})

watch(
  () => route.query.query,
  async value => {
    query.value = typeof value === 'string' ? value : ''
    if (query.value.trim()) {
      await runSearch()
    }
  },
  { immediate: true }
)

async function checkHealth() {
  loading.value = true
  try {
    await knowledgeBase.health()
    serviceStatus.value = 'online'
  } catch {
    serviceStatus.value = 'offline'
  } finally {
    loading.value = false
  }
}

async function runSearch() {
  const text = query.value.trim()
  if (!text) return

  searching.value = true
  hasSearched.value = true
  try {
    searchResults.value = await knowledgeBase.search(text, topK.value)
    if (searchResults.value.length === 0) {
      ElMessage.info('没有匹配到相关知识片段')
    }
  } catch (error) {
    searchResults.value = []
    ElMessage.error(error instanceof Error ? error.message : '检索失败')
  } finally {
    searching.value = false
  }
}

function getResultFileName(item: KnowledgeSearchResult) {
  const meta = item.metadata || {}
  if (meta.fileName) return meta.fileName
  if (meta.filePath) return String(meta.filePath).split(/[\\/]/).pop() || meta.filePath
  return meta.documentId || item.id
}

function formatScore(score: number) {
  return `${Math.round((score || 0) * 100)}%`
}

onMounted(checkHealth)
</script>

<style scoped>
.knowledge-page {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background: var(--portal-page-bg);
  color: var(--portal-text);
}

.search-stage {
  position: relative;
  z-index: 1;
  min-height: 360px;
}

.stage-grid {
  position: fixed;
  inset: 0;
  z-index: 0;
  background-image:
    linear-gradient(rgba(148, 163, 184, 0.12) 1px, transparent 1px),
    linear-gradient(90deg, rgba(148, 163, 184, 0.12) 1px, transparent 1px);
  background-size: 42px 42px;
  mask-image: radial-gradient(circle at center, black, transparent 78%);
  pointer-events: none;
}

.stage-content {
  position: relative;
  z-index: 1;
  width: min(980px, calc(100% - 32px));
  margin: 0 auto;
  padding: 72px 0 56px;
}

.page-kicker {
  width: fit-content;
  margin-bottom: 10px;
  border-radius: 4px;
  padding: 4px 10px;
  color: var(--portal-secondary);
  background: var(--color-primary-subtle);
  font-size: 12px;
  font-weight: 900;
}

.stage-content h1 {
  margin: 0 0 28px;
  color: var(--portal-text);
  font-size: 42px;
}

.search-panel {
  min-height: 70px;
  display: grid;
  grid-template-columns: 58px minmax(0, 1fr) 122px 58px;
  align-items: center;
  gap: 10px;
  border: 1px solid var(--portal-line);
  border-radius: 8px;
  padding: 0 10px 0 18px;
  background: var(--portal-surface-glass);
  box-shadow: var(--portal-shadow);
  backdrop-filter: var(--portal-backdrop-filter);
}

.search-panel > :deep(.iconify) {
  color: var(--portal-primary);
}

.search-panel input {
  width: 100%;
  min-width: 0;
  height: 100%;
  border: 0;
  outline: none;
  background: transparent;
  color: var(--portal-text);
  font-size: 20px;
}

.search-panel input::placeholder {
  color: var(--portal-muted);
}

.search-panel button,
.refresh-btn {
  border: 0;
  cursor: pointer;
}

.search-panel button {
  width: 46px;
  height: 46px;
  border-radius: 8px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: var(--color-primary-text);
  background: var(--portal-gradient);
}

.search-panel button:disabled,
.refresh-btn:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.service-status {
  margin-top: 16px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--portal-muted);
  font-size: 13px;
}

.service-status span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--portal-muted);
}

.service-status.online span {
  background: var(--portal-secondary);
  box-shadow: 0 0 16px var(--color-primary-transparent);
}

.service-status.offline span {
  background: #fb7185;
}

.result-shell {
  width: min(1100px, calc(100% - 32px));
  margin: -40px auto 0;
  position: relative;
  z-index: 2;
  border: 1px solid var(--portal-line);
  border-radius: 8px;
  padding: 18px;
  background: var(--portal-surface-glass);
  color: var(--portal-text);
  box-shadow: var(--portal-shadow);
  backdrop-filter: var(--portal-backdrop-filter);
}

.result-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  padding-bottom: 14px;
  border-bottom: 1px solid var(--portal-line);
}

.result-head h2 {
  margin: 0;
  color: var(--portal-text);
  font-size: 20px;
}

.result-head p {
  margin: 4px 0 0;
  color: var(--portal-muted);
  font-size: 13px;
}

.refresh-btn {
  height: 36px;
  border-radius: 6px;
  padding: 0 12px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--portal-text);
  background: var(--portal-surface-soft);
  border: 1px solid var(--portal-line);
  font-weight: 800;
}

.empty-state {
  min-height: 260px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--portal-muted);
}

.result-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-top: 14px;
}

.result-item {
  border: 1px solid var(--portal-line);
  border-radius: 8px;
  padding: 14px;
  background: var(--portal-surface-soft);
}

.result-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 8px;
  color: var(--portal-muted);
  font-size: 12px;
}

.score {
  border-radius: 4px;
  padding: 2px 8px;
  color: var(--color-primary-text);
  background: var(--portal-gradient);
  font-weight: 900;
}

.result-item p {
  margin: 0;
  color: var(--portal-subtext);
  line-height: 1.75;
  font-size: 14px;
}

@media (max-width: 760px) {
  .stage-content h1 {
    font-size: 32px;
  }

  .search-panel {
    grid-template-columns: 42px minmax(0, 1fr) 50px;
    padding-left: 12px;
  }

  .search-panel :deep(.el-input-number) {
    display: none;
  }

  .result-head {
    flex-direction: column;
  }
}
</style>
