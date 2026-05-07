<template>
  <div class="knowledge-admin">
    <section class="hero-band">
      <div>
        <p class="eyebrow">Knowledge Ops</p>
        <h1>知识库管理</h1>
        <p class="hero-subtitle">{{ serviceText }}</p>
      </div>
      <div class="hero-actions">
        <el-button :loading="refreshing" @click="refreshAll">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </section>

    <section class="metrics-row">
      <div v-for="item in metrics" :key="item.label" class="metric-tile">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
        <small>{{ item.help }}</small>
      </div>
    </section>

    <section class="upload-band">
      <div class="upload-copy">
        <h2>上传文档</h2>
        <p>上传后会进入知识库索引队列，portal 端只展示检索结果。</p>
      </div>
      <el-upload
        class="upload-zone"
        drag
        action=""
        :show-file-list="false"
        :http-request="uploadDocument"
        :disabled="uploading"
        accept=".pdf,.doc,.docx,.xls,.xlsx,.txt,.md"
      >
        <el-icon class="upload-icon"><UploadFilled /></el-icon>
        <div class="el-upload__text">
          拖拽文件到这里，或 <em>点击上传</em>
        </div>
      </el-upload>
    </section>

    <section class="table-band">
      <div class="section-head">
        <div>
          <h2>文档列表</h2>
          <p>{{ documents.length }} 个文档</p>
        </div>
      </div>

      <el-table :data="documents" v-loading="loadingDocs" height="460">
        <el-table-column prop="fileName" label="文件名称" min-width="220" show-overflow-tooltip />
        <el-table-column prop="fileType" label="类型" width="90" align="center" />
        <el-table-column label="大小" width="110" align="center">
          <template #default="{ row }">{{ formatFileSize(row.fileSize) }}</template>
        </el-table-column>
        <el-table-column prop="chunkCount" label="片段" width="90" align="center" />
        <el-table-column label="状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="indexedAt" label="索引时间" min-width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="170" align="right" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="reindex(row.id)">重建索引</el-button>
            <el-button link type="danger" @click="remove(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadRequestOptions } from 'element-plus'
import { knowledgeAdminApi, type KnowledgeDocument, type KnowledgeStats } from '@/api'

const refreshing = ref(false)
const loadingDocs = ref(false)
const uploading = ref(false)
const serviceStatus = ref<'checking' | 'online' | 'offline'>('checking')
const stats = ref<KnowledgeStats | null>(null)
const documents = ref<KnowledgeDocument[]>([])

const serviceText = computed(() => {
  if (serviceStatus.value === 'online') return '知识库服务在线，可以上传和维护文档。'
  if (serviceStatus.value === 'offline') return '知识库服务未连接，请确认 knowledge 后端运行在 3002。'
  return '正在检查知识库服务状态。'
})

const metrics = computed(() => [
  {
    label: '文档数',
    value: String(stats.value?.fileCount ?? documents.value.length),
    help: '已接入知识库',
  },
  {
    label: '知识片段',
    value: String(stats.value?.chunkCount ?? 0),
    help: '切分后的检索片段',
  },
  {
    label: '向量数',
    value: String(stats.value?.vectorCount ?? 0),
    help: `${stats.value?.vectorDimension ?? 0} 维`,
  },
  {
    label: '索引队列',
    value: String(stats.value?.queueStatus?.queueSize ?? 0),
    help: `${stats.value?.queueStatus?.running ?? 0} 个任务运行中`,
  },
])

async function refreshAll() {
  refreshing.value = true
  try {
    await Promise.all([checkHealth(), loadStats(), loadDocuments()])
  } finally {
    refreshing.value = false
  }
}

async function checkHealth() {
  try {
    await knowledgeAdminApi.health()
    serviceStatus.value = 'online'
  } catch {
    serviceStatus.value = 'offline'
  }
}

async function loadStats() {
  try {
    stats.value = await knowledgeAdminApi.stats()
  } catch {
    stats.value = null
  }
}

async function loadDocuments() {
  loadingDocs.value = true
  try {
    documents.value = await knowledgeAdminApi.documents()
  } catch {
    documents.value = []
  } finally {
    loadingDocs.value = false
  }
}

async function uploadDocument(options: UploadRequestOptions) {
  const file = options.file as File
  uploading.value = true
  try {
    await knowledgeAdminApi.upload(file)
    ElMessage.success('上传成功，正在后台索引')
    options.onSuccess?.({})
    await refreshAll()
  } catch (error) {
    const message = error instanceof Error ? error.message : '上传失败'
    options.onError?.(error as Error)
    ElMessage.error(message)
  } finally {
    uploading.value = false
  }
}

async function reindex(id: string) {
  try {
    await knowledgeAdminApi.reindex(id)
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
    await knowledgeAdminApi.remove(id)
    ElMessage.success('文档已删除')
    await refreshAll()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error instanceof Error ? error.message : '删除失败')
    }
  }
}

function formatFileSize(size: number) {
  if (!size) return '-'
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
.knowledge-admin {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.hero-band {
  min-height: 168px;
  border-radius: 8px;
  padding: 28px;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18px;
  color: #fff;
  background:
    radial-gradient(circle at 16% 18%, rgba(103, 232, 249, 0.32), transparent 28%),
    radial-gradient(circle at 86% 20%, rgba(163, 230, 53, 0.22), transparent 22%),
    linear-gradient(135deg, #07111f, #111827 58%, #182132);
  box-shadow: 0 18px 46px rgba(15, 23, 42, 0.18);
}

.eyebrow {
  margin: 0 0 8px;
  color: #a3e635;
  font-size: 12px;
  font-weight: 800;
}

.hero-band h1 {
  margin: 0;
  font-size: 30px;
}

.hero-subtitle {
  margin: 10px 0 0;
  color: #cbd5e1;
}

.hero-actions {
  flex: 0 0 auto;
}

.metrics-row {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.metric-tile,
.upload-band,
.table-band {
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.06), 0 4px 12px rgba(0, 21, 41, 0.04);
}

.metric-tile {
  min-height: 116px;
  padding: 18px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 4px;
}

.metric-tile span,
.metric-tile small,
.section-head p {
  color: #64748b;
  font-size: 13px;
}

.metric-tile strong {
  color: #111827;
  font-size: 28px;
}

.upload-band {
  display: grid;
  grid-template-columns: minmax(260px, 0.72fr) minmax(0, 1fr);
  gap: 20px;
  padding: 20px;
  align-items: center;
}

.upload-copy h2,
.section-head h2 {
  margin: 0;
  color: #111827;
}

.upload-copy p,
.section-head p {
  margin: 8px 0 0;
}

.upload-zone {
  min-width: 0;
}

.upload-icon {
  color: #1677ff;
  font-size: 42px;
}

.table-band {
  padding: 20px;
}

.section-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

@media (max-width: 1100px) {
  .metrics-row {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .upload-band {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 720px) {
  .hero-band,
  .section-head {
    flex-direction: column;
  }

  .metrics-row {
    grid-template-columns: 1fr;
  }
}
</style>
