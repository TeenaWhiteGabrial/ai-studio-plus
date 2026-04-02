<template>
  <div>
    <el-card>
      <!-- 同步状态栏 -->
      <div class="sync-status-bar mb-4">
        <el-alert v-if="lastSyncInfo" :type="syncStatusType" :closable="false">
          <template #default>
            <span>上次同步时间: {{ formatDate(lastSyncInfo.created_at) }}</span>
            <span v-if="userStore.isAdmin()" class="ml-4">
              新增: {{ lastSyncInfo.created_count }},
              更新: {{ lastSyncInfo.updated_count }},
              删除: {{ lastSyncInfo.deleted_count }}
            </span>
          </template>
        </el-alert>
      </div>

      <!-- 搜索栏 -->
      <el-form inline>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="搜索名称" clearable @keyup.enter="loadList" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList">搜索</el-button>
          <el-button v-if="userStore.isAdmin()" type="success" :loading="syncing" @click="handleSync">
            <el-icon><Refresh /></el-icon>
            从 GitLab 同步
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="name" label="名称" width="180" />
        <el-table-column prop="description" label="描述" :show-overflow-tooltip="{ popperClass: 'description-tooltip' }" />
        <el-table-column prop="author" label="作者" width="100" />
        <el-table-column prop="download_count" label="下载次数" width="100" align="center" />
        <el-table-column prop="updated_at" label="更新时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.updated_at) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">预览</el-button>
            <el-button size="small" type="primary" @click="handleDownload(row)">下载</el-button>

            <el-dropdown size="small" style="margin-left: 8px" @command="(cmd) => handleCommand(cmd, row)">
              <el-button size="small" type="success">
                复制命令<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="copyClaudeCommand">复制 Claude 命令</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="total"
        layout="total, prev, pager, next"
        class="mt-4"
        @change="loadList"
      />
    </el-card>

    <!-- 详情预览弹窗 -->
    <el-dialog v-model="detailVisible" title="Skill 详情" width="800px">
      <div v-if="currentSkill">
        <h3>{{ currentSkill.name }}</h3>
        <p><strong>作者:</strong> {{ currentSkill.author || '-' }}</p>
        <p><strong>描述:</strong> {{ currentSkill.description || '-' }}</p>
        <p><strong>下载次数:</strong> {{ currentSkill.download_count }}</p>
        <p><strong>最后同步:</strong> {{ formatDate(currentSkill.last_sync_at) }}</p>
        <el-divider />
        <div v-if="markdownContent" class="markdown-preview">
          <pre>{{ markdownContent }}</pre>
        </div>
      </div>
    </el-dialog>

    <!-- Claude 命令弹窗 -->
    <el-dialog v-model="commandVisible" title="Claude Code 命令" width="600px">
      <div v-if="currentSkill">
        <p class="mb-2">请确定配置好skill仓库, </p>
        <p class="mb-2">复制以下命令在终端执行，即可将此 Skill 添加到 Claude Code:</p>
        <el-input
          v-model="claudeCommand"
          type="textarea"
          :rows="2"
          readonly
          class="mb-4"
        />
        <el-button type="primary" @click="copyToClipboard(claudeCommand)">
          复制到剪贴板
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { skillApi } from '@/api'
import { useUserStore } from '@/stores/user'
const loading = ref(false)
const syncing = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const detailVisible = ref(false)
const commandVisible = ref(false)
const currentSkill = ref<any>(null)
const markdownContent = ref('')
const lastSyncInfo = ref<any>(null)

const userStore = useUserStore()

const query = reactive({ page: 1, size: 10, keyword: '', category: '' })


const syncStatusType = computed(() => {
  if (!lastSyncInfo.value) return 'info'
  if (lastSyncInfo.value.error_count > 0) return 'warning'
  return 'success'
})

const claudeCommand = computed(() => {
  if (!currentSkill.value) return ''
  return `/skills install ${currentSkill.value.name} -- marketplace iip-skill-hub`
  
})

async function loadList() {
  loading.value = true
  try {
    const res = await skillApi.list(query) as any
    list.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function loadSyncLog() {
  if (!userStore.isAdmin()) return
  try {
    const res = await skillApi.syncLog(1) as any
    if (res.data && res.data.length > 0) {
      lastSyncInfo.value = res.data[0]
    }
  } catch (e) {
    // 忽略错误
  }
}

async function handleSync() {
  syncing.value = true
  try {
    const res = await skillApi.sync() as any
    ElMessage.success(res.data || '同步任务已启动，请稍后刷新查看结果')
    // 延迟 5 秒后刷新，等待后台同步完成
    setTimeout(() => {
      loadList()
      loadSyncLog()
      syncing.value = false
    }, 5000)
  } catch (e: any) {
    ElMessage.error(e.message || '同步失败')
    syncing.value = false
  }
}

async function handleView(row: any) {
  currentSkill.value = row
  detailVisible.value = true
  // 加载 Markdown 内容
  try {
    const res = await skillApi.raw(row.id) as any
    
    markdownContent.value = res || '无法加载内容'
  } catch (e) {
    markdownContent.value = '加载失败'
  }
}

async function handleDownload(row: any) {
  try {
    const res = await skillApi.download(row.id) as any
    // res 是 axios response 对象，res.data 是 blob
    const blob = res.data || res
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${row.name}.zip`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('下载成功')
  } catch (e: any) {
    ElMessage.error(e.message || '下载失败')
  }
}

function handleCommand(command: string, row: any) {
  if (command === 'copyClaudeCommand') {
    currentSkill.value = row
    commandVisible.value = true
  }
}

function copyToClipboard(text: string) {
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('命令已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败，请手动复制')
  })
}

function formatDate(date: string) {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

onMounted(() => {
  loadList()
  loadSyncLog()
})
</script>

<style scoped>
.sync-status-bar {
  margin-bottom: 16px;
}
.markdown-preview {
  max-height: 400px;
  overflow: auto;
  background: #f5f5f5;
  padding: 16px;
  border-radius: 4px;
}
.markdown-preview pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  margin: 0;
}
</style>

<style>
.description-tooltip {
  max-width: 400px !important;
}
</style>
