<template>
  <div class="knowledge-page">
    <section class="knowledge-header csdn-card">
      <div>
        <div class="page-kicker">Knowledge Base</div>
        <h1 class="page-title">知识库检索</h1>
        <p class="page-desc">{{ serviceText }} · {{ knowledgeApiBase }}</p>
      </div>

      <div class="header-actions">
        <button class="plain-btn" type="button" @click="openBackend">
          <Icon name="material-symbols:open-in-new" size="18" />
          <span>后端服务</span>
        </button>
        <button class="primary-btn" type="button" :disabled="loading" @click="refreshAll">
          <Icon name="material-symbols:refresh" size="18" />
          <span>刷新</span>
        </button>
      </div>
    </section>

    <section class="metrics-grid">
      <div v-for="card in metricCards" :key="card.label" class="metric-card csdn-card">
        <span class="metric-label">{{ card.label }}</span>
        <strong>{{ card.value }}</strong>
        <small>{{ card.help }}</small>
      </div>
    </section>

    <section class="workspace-grid">
      <div class="search-panel csdn-card">
        <div class="panel-head">
          <div>
            <h2>语义检索</h2>
            <p>输入问题或关键词，返回向量相似度最高的片段。</p>
          </div>
          <el-input-number v-model="topK" :min="1" :max="20" size="small" />
        </div>

        <el-input
          v-model="query"
          type="textarea"
          :rows="4"
          resize="none"
          placeholder="搜索制度、接口文档、项目说明或任意知识片段"
          @keydown.ctrl.enter.prevent="runSearch"
        />

        <div class="panel-actions">
          <button class="primary-btn" type="button" :disabled="searching || !query.trim()" @click="runSearch">
            <Icon name="material-symbols:search" size="18" />
            <span>{{ searching ? '检索中' : '检索' }}</span>
          </button>
        </div>
      </div>

      <div class="upload-panel csdn-card">
        <div class="panel-head">
          <div>
            <h2>文档上传</h2>
            <p>支持后端已配置的 PDF、DOCX、XLSX、TXT、MD 等格式。</p>
          </div>
        </div>

        <label class="file-picker">
          <input ref="fileInputRef" type="file" @change="handleFileChange">
          <Icon name="material-symbols:upload-file-outline" size="26" />
          <span>{{ selectedFile?.name || '选择文档' }}</span>
          <small>{{ selectedFile ? formatFileSize(selectedFile.size) : '文件会上传到独立知识库服务' }}</small>
        </label>

        <div class="panel-actions">
          <button class="plain-btn" type="button" @click="clearFile">清空</button>
          <button class="primary-btn" type="button" :disabled="uploading || !selectedFile" @click="uploadFile">
            <Icon name="material-symbols:cloud-upload-outline" size="18" />
            <span>{{ uploading ? '上传中' : '上传并索引' }}</span>
          </button>
        </div>
      </div>
    </section>

    <section class="results-grid">
      <div class="csdn-card result-panel">
        <div class="section-head">
          <h2>检索结果</h2>
          <span>{{ searchResults.length }} 条</span>
        </div>

        <div v-if="searching" class="csdn-empty">检索中...</div>
        <div v-else-if="searchResults.length === 0" class="csdn-empty">暂无检索结果</div>
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
      </div>

      <div class="csdn-card docs-panel">
        <div class="section-head">
          <h2>文档列表</h2>
          <span>{{ documents.length }} 个文档</span>
        </div>

        <el-table :data="documents" v-loading="loadingDocs" height="420">
          <el-table-column prop="fileName" label="文件" min-width="180" show-overflow-tooltip />
          <el-table-column prop="fileType" label="类型" width="76" align="center" />
          <el-table-column prop="chunkCount" label="片段" width="76" align="center" />
          <el-table-column label="状态" width="96" align="center">
            <template #default="{ row }">
              <el-tag :type="statusTagType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" align="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="reindex(row.id)">重建</el-button>
              <el-button link type="danger" @click="remove(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import type { KnowledgeDocument, KnowledgeSearchResult, KnowledgeStats } from '~/composables/useKnowledgeBase'

definePageMeta({
  middleware: ['auth'],
})

useHead({
  title: '知识库检索',
})

const knowledgeBase = useKnowledgeBase()
const knowledgeApiBase = knowledgeBase.baseURL

const loading = ref(false)
const loadingDocs = ref(false)
const searching = ref(false)
const uploading = ref(false)
const serviceStatus = ref<'checking' | 'online' | 'offline'>('checking')
const stats = ref<KnowledgeStats | null>(null)
const documents = ref<KnowledgeDocument[]>([])
const searchResults = ref<KnowledgeSearchResult[]>([])
const query = ref('')
const topK = ref(8)
const selectedFile = ref<File | null>(null)
const fileInputRef = ref<HTMLInputElement | null>(null)

const serviceText = computed(() => {
  if (serviceStatus.value === 'online') return '服务已连接'
  if (serviceStatus.value === 'offline') return '服务未连接'
  return '服务检查中'
})

const metricCards = computed(() => [
  {
    label: '文档数',
    value: String(stats.value?.fileCount ?? documents.value.length),
    help: '已纳入知识库的文件',
  },
  {
    label: '知识片段',
    value: String(stats.value?.chunkCount ?? 0),
    help: '切分后的向量片段',
  },
  {
    label: '向量数',
    value: String(stats.value?.vectorCount ?? 0),
    help: `${stats.value?.vectorDimension ?? 0} 维向量`,
  },
  {
    label: '索引队列',
    value: String(stats.value?.queueStatus?.queueSize ?? 0),
    help: `${stats.value?.queueStatus?.running ?? 0} 个任务运行中`,
  },
])

async function refreshAll() {
  loading.value = true
  try {
    await Promise.all([checkHealth(), loadStats(), loadDocuments()])
    if (serviceStatus.value === 'offline') {
      ElMessage.warning('知识库后端服务未连接')
    }
  } finally {
    loading.value = false
  }
}

async function checkHealth() {
  try {
    await knowledgeBase.health()
    serviceStatus.value = 'online'
  } catch {
    serviceStatus.value = 'offline'
  }
}

async function loadStats() {
  try {
    stats.value = await knowledgeBase.getStats()
  } catch (error) {
    stats.value = null
  }
}

async function loadDocuments() {
  loadingDocs.value = true
  try {
    documents.value = await knowledgeBase.listDocuments()
  } catch (error) {
    documents.value = []
  } finally {
    loadingDocs.value = false
  }
}

async function runSearch() {
  const text = query.value.trim()
  if (!text) return

  searching.value = true
  try {
    searchResults.value = await knowledgeBase.search(text, topK.value)
    if (searchResults.value.length === 0) {
      ElMessage.info('没有匹配到相关知识片段')
    }
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '检索失败')
  } finally {
    searching.value = false
  }
}

function handleFileChange(event: Event) {
  const input = event.target as HTMLInputElement
  selectedFile.value = input.files?.[0] || null
}

function clearFile() {
  selectedFile.value = null
  if (fileInputRef.value) {
    fileInputRef.value.value = ''
  }
}

async function uploadFile() {
  if (!selectedFile.value) return

  uploading.value = true
  try {
    await knowledgeBase.upload(selectedFile.value)
    ElMessage.success('上传成功，正在后台索引')
    clearFile()
    await refreshAll()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '上传失败')
  } finally {
    uploading.value = false
  }
}

async function reindex(id: string) {
  try {
    await knowledgeBase.reindexDocument(id)
    ElMessage.success('已加入重建索引队列')
    await refreshAll()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '重建索引失败')
  }
}

async function remove(id: string) {
  try {
    await ElMessageBox.confirm('确认删除该文档及其索引吗？', '删除文档', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await knowledgeBase.removeDocument(id)
    ElMessage.success('文档已删除')
    await refreshAll()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error instanceof Error ? error.message : '删除失败')
    }
  }
}

function openBackend() {
  window.open(knowledgeBase.baseURL.value, '_blank', 'noopener')
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

function formatFileSize(size: number) {
  if (size < 1024) return `${size} B`
  if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`
  return `${(size / 1024 / 1024).toFixed(1)} MB`
}

function statusText(status: string) {
  const map: Record<string, string> = {
    pending: '等待中',
    processing: '处理中',
    completed: '已完成',
    failed: '失败',
  }
  return map[status] || status || '-'
}

function statusTagType(status: string) {
  if (status === 'completed') return 'success'
  if (status === 'failed') return 'danger'
  if (status === 'processing') return 'warning'
  return 'info'
}

onMounted(refreshAll)
</script>

<style scoped>
.knowledge-page {
  min-height: calc(100vh - 56px);
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.knowledge-header {
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.page-kicker {
  width: fit-content;
  margin-bottom: 6px;
  border-radius: 4px;
  padding: 2px 8px;
  color: var(--portal-secondary);
  background: var(--portal-gradient-soft);
  font-size: 12px;
  font-weight: 700;
}

.page-title,
.panel-head h2,
.section-head h2 {
  margin: 0;
  color: #111827;
}

.page-title {
  font-size: 24px;
}

.page-desc,
.panel-head p {
  margin: 6px 0 0;
  color: var(--csdn-subtext);
  font-size: 13px;
}

.header-actions,
.panel-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
}

.primary-btn,
.plain-btn {
  height: 36px;
  border-radius: 4px;
  padding: 0 14px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  border: 0;
  cursor: pointer;
  font-size: 14px;
  font-weight: 700;
}

.primary-btn {
  color: #fff;
  background: var(--portal-gradient);
}

.plain-btn {
  border: 1px solid #d8ddf8;
  color: var(--portal-secondary);
  background: #fff;
}

.primary-btn:disabled,
.plain-btn:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.metric-card {
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.metric-label,
.metric-card small,
.section-head span,
.result-meta {
  color: var(--csdn-muted);
  font-size: 12px;
}

.metric-card strong {
  color: #111827;
  font-size: 26px;
}

.workspace-grid,
.results-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(360px, 0.8fr);
  gap: 12px;
}

.search-panel,
.upload-panel,
.result-panel,
.docs-panel {
  padding: 16px;
}

.panel-head,
.section-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.file-picker {
  min-height: 126px;
  border: 1px dashed #cfd6ea;
  border-radius: 6px;
  background: #f8fbff;
  color: var(--csdn-subtext);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  cursor: pointer;
}

.file-picker input {
  display: none;
}

.file-picker span {
  color: #111827;
  font-weight: 700;
}

.file-picker small {
  color: var(--csdn-muted);
}

.result-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-height: 420px;
  overflow-y: auto;
  padding-right: 4px;
}

.result-item {
  border: 1px solid var(--csdn-line);
  border-radius: 6px;
  padding: 12px;
  background: #fff;
}

.result-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 8px;
}

.score {
  border-radius: 4px;
  padding: 2px 8px;
  color: var(--portal-secondary);
  background: var(--portal-gradient-soft);
  font-weight: 800;
}

.result-item p {
  margin: 0;
  color: #30323a;
  line-height: 1.7;
  font-size: 14px;
}

:deep(.el-table th) {
  background: #f8fafc;
  color: #374151;
  font-weight: 700;
}

@media (max-width: 1100px) {
  .metrics-grid,
  .workspace-grid,
  .results-grid {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 780px) {
  .knowledge-header,
  .panel-head,
  .section-head {
    flex-direction: column;
  }

  .header-actions,
  .panel-actions {
    justify-content: flex-start;
    flex-wrap: wrap;
  }

  .metrics-grid,
  .workspace-grid,
  .results-grid {
    grid-template-columns: 1fr;
  }
}
</style>
