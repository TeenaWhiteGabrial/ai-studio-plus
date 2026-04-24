<template>
  <div class="audit-list">
    <el-form inline class="filter-form">
      <el-form-item label="审核状态">
        <el-select v-model="filterStatus" style="width:120px;" placeholder="选择状态" clearable @change="fetchList">
          <el-option label="待审核" :value="0" />
          <el-option label="已通过" :value="1" />
          <el-option label="已拒绝" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="资源名称">
        <el-input v-model="filterName" placeholder="搜索资源名称" clearable @clear="handleNameClear" @keyup.enter="fetchList" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="fetchList">搜索</el-button>
        <el-button @click="resetFilter">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" style="width: 100%" stripe>
      <el-table-column prop="name" label="资源名称" min-width="150" />
      <el-table-column prop="type" label="类型" width="100">
        <template #default="{ row }">
          {{ getTypeText(row) }}
        </template>
      </el-table-column>
      <el-table-column label="提交人" width="120">
        <template #default="{ row }">
          {{ row.creator_name || row.createdBy || '-' }}
        </template>
      </el-table-column>
      <!-- <el-table-column prop="dept_name" label="所属部门" width="150">
        <template #default="{ row }">
          {{ row.dept_name || '-' }}
        </template>
      </el-table-column> -->
      <el-table-column prop="created_at" label="提交时间" width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.created_at) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="warning" @click="viewDetail(row)">查看</el-button>
          <template v-if="row.status === 0">
            <el-button link type="success" @click="approve(row)">通过</el-button>
            <el-button link type="danger" @click="openReject(row)">拒绝</el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="[10, 20, 50, 100]"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="fetchList"
      @current-change="fetchList"
      class="pagination"
    />

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" :title="`资源详情 - ${getTypeLabel()}`" width="800px" destroy-on-close>
      <div v-if="currentItem" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="资源ID">{{ currentItem.id }}</el-descriptions-item>
          <el-descriptions-item label="资源名称">{{ currentItem.name || currentItem.title }}</el-descriptions-item>
          <el-descriptions-item label="类型">{{ getTypeText(currentItem) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentItem.status)">{{ getStatusText(currentItem.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="提交人">{{ currentItem.creator_name || currentItem.createdBy }}</el-descriptions-item>
          <!-- <el-descriptions-item label="所属部门">{{ currentItem.dept_name || '-' }}</el-descriptions-item> -->
          <el-descriptions-item label="创建时间" :span="2">{{ formatDateTime(currentItem.createdAt) }}</el-descriptions-item>
          <el-descriptions-item v-if="currentItem.category" label="分类" :span="2">{{ currentItem.category }}</el-descriptions-item>
          <el-descriptions-item v-if="currentItem.serverType" label="服务器类型">{{ currentItem.serverType }}</el-descriptions-item>
          <el-descriptions-item v-if="currentItem.apiEndpoint" label="API端点">
            <div class="url-text">{{ currentItem.apiEndpoint }}</div>
          </el-descriptions-item>
          <el-descriptions-item v-if="currentItem.authType" label="认证类型">{{ currentItem.authType }}</el-descriptions-item>
          <el-descriptions-item v-if="currentItem.description" label="描述" :span="2">
            <div class="description-text">{{ currentItem.description }}</div>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 审核信息 -->
        <div class="audit-section">
          <h4>审核信息</h4>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="审核状态">
              <el-tag :type="getStatusType(currentItem.status)">{{ getStatusText(currentItem.status) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="审核时间">
              {{ currentItem.reviewTime ? formatDateTime(currentItem.reviewTime) : '-' }}
            </el-descriptions-item>
            <el-descriptions-item v-if="currentItem.reviewComment || currentItem.rejectReason" label="审核备注" :span="2">
              <el-alert v-if="currentItem.status === 2" type="error" :closable="false">{{ currentItem.reviewComment || currentItem.rejectReason }}</el-alert>
              <span v-else>{{ currentItem.reviewComment || currentItem.rejectReason }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 版本信息（仅 Skill、Plugin、Tutorial、MCP） -->
        <div v-if="['skill', 'plugin', 'tutorial', 'mcp'].includes(props.type)" class="version-section">
          <h4>版本信息</h4>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="最新版本">{{ currentItem.latest_version || '-' }}</el-descriptions-item>
            <el-descriptions-item label="版本总数">{{ currentItem.total_versions || 0 }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
      <template #footer v-if="currentItem?.status === 0">
        <el-button @click="detailVisible = false">取消</el-button>
        <el-button type="danger" @click="handleRejectFromDetail">拒绝</el-button>
        <el-button type="success" @click="approveCurrent">通过</el-button>
      </template>
    </el-dialog>

    <!-- 拒绝原因弹窗 -->
    <el-dialog v-model="rejectDialogVisible" title="拒绝原因" width="500px" destroy-on-close>
      <el-form :model="rejectForm" label-width="100px">
        <el-form-item label="拒绝原因" required>
          <el-input v-model="rejectForm.reason" type="textarea" placeholder="请输入拒绝原因" :rows="4" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReject">确定拒绝</el-button>
      </template>
    </el-dialog>

    <!-- MCP 和 Video 提示 -->
    <el-empty v-if="!supportsAudit && list.length === 0" description="该资源类型暂不支持审核功能" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { skillApi, pluginApi, tutorialApi, mcpApi } from '@/api'
import type { AxiosResponse } from 'axios'

const props = defineProps<{
  type: string
}>()

const emit = defineEmits<{
  updatePending: [count: number]
}>()

const loading = ref(false)
const list = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const filterStatus = ref<number>()
const filterName = ref('')

const detailVisible = ref(false)
const currentItem = ref<any>(null)

const rejectDialogVisible = ref(false)
const rejectForm = ref({ reason: '' })

// 判断资源类型是否支持审核
const supportsAudit = computed(() => ['skill', 'plugin', 'tutorial', 'mcp'].includes(props.type))

const getStatusType = (status: number) => {
  const map: Record<number, any> = {
    0: 'warning',
    1: 'success',
    2: 'danger',
  }
  return map[status] || 'info'
}

const getStatusText = (status: number) => {
  const map: Record<number, string> = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝',
  }
  return map[status] || '未知'
}

const getTypeText = (row: any) => {
  const typeMap: Record<string, string> = {
    skill: 'Skill',
    mcp: 'MCP服务器',
    plugin: 'Plugin',
    tutorial: '教程',
    installer: '安装包',
    video: '视频',
  }
  return typeMap[props.type] || props.type
}

const getTypeLabel = () => {
  const labelMap: Record<string, string> = {
    skill: 'Skill',
    mcp: 'MCP服务器',
    plugin: 'Plugin',
    tutorial: '教程',
    installer: '安装包',
    video: '视频',
  }
  return labelMap[props.type] || props.type
}

const formatDateTime = (date: string | null) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

const getPendingCount = () => {
  return list.value.filter(item => item.status === 0).length
}

const fetchList = async () => {
  if (!supportsAudit.value) {
    list.value = []
    total.value = 0
    emit('updatePending', 0)
    return
  }

  loading.value = true
  try {
    let response: AxiosResponse<any>
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: filterName.value || undefined,
      status: filterStatus.value,
    }

    switch (props.type) {
      case 'skill':
        response = await skillApi.list(params)
        break
      case 'mcp':
        response = await mcpApi.list(params)
        break
      case 'plugin':
        response = await pluginApi.list(params)
        break
      case 'tutorial':
        response = await tutorialApi.list(params)
        break
      default:
        list.value = []
        total.value = 0
        emit('updatePending', 0)
        return
    }

    if (response?.code === 200) {
      list.value = response.data?.records || response.data?.list || []
      total.value = response.data?.total || 0
      emit('updatePending', getPendingCount())
    }
  } catch (error: any) {
    console.error('获取列表失败:', error)
    ElMessage.error(error.response?.data?.message || '获取列表失败')
  } finally {
    loading.value = false
  }
}

const viewDetail = async (row: any) => {
  currentItem.value = row
  detailVisible.value = true
}

const approve = async (row: any) => {
  try {
    await ElMessageBox.confirm('确认通过该资源吗？', '确认通过', {
      type: 'warning',
    })
    await performAudit(row.id, 1, '')
    ElMessage.success('已通过审核')
    fetchList()
  } catch {
    // 用户取消
  }
}

const openReject = (row: any) => {
  currentItem.value = row
  rejectForm.value.reason = ''
  rejectDialogVisible.value = true
}

const handleRejectFromDetail = () => {
  detailVisible.value = false
  rejectDialogVisible.value = true
}

const approveCurrent = async () => {
  if (!currentItem.value) return
  try {
    await ElMessageBox.confirm('确认通过该资源吗？', '确认通过', {
      type: 'warning',
    })
    await performAudit(currentItem.value.id, 1, '')
    ElMessage.success('已通过审核')
    detailVisible.value = false
    fetchList()
  } catch {
    // 用户取消
  }
}

const submitReject = async () => {
  if (!rejectForm.value.reason) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  if (!currentItem.value) return

  try {
    await performAudit(currentItem.value.id, 2, rejectForm.value.reason)
    ElMessage.success('已拒绝')
    rejectDialogVisible.value = false
    detailVisible.value = false
    fetchList()
  } catch (error: any) {
    console.error('拒绝失败:', error)
  }
}

const performAudit = async (id: number, status: number, reviewComment: string) => {
  const data = { status, reviewComment }

  switch (props.type) {
    case 'skill':
      await skillApi.audit(id, data)
      break
    case 'mcp':
      await mcpApi.audit(id, data)
      break
    case 'plugin':
      await pluginApi.audit(id, data)
      break
    case 'tutorial':
      await tutorialApi.audit(id, data)
      break
    default:
      throw new Error('不支持该资源类型的审核')
  }
}

const handleNameClear = () => {
  fetchList()
}

const resetFilter = () => {
  filterStatus.value = undefined
  filterName.value = ''
  currentPage.value = 1
  fetchList()
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.audit-list {
  padding: 0;
}

.filter-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.detail-content {
  padding: 10px 0;
}

.description-text {
  white-space: pre-wrap;
  word-break: break-word;
  max-height: 200px;
  overflow-y: auto;
}

.url-text {
  word-break: break-all;
  font-size: 12px;
  color: #606266;
}

.audit-section,
.version-section {
  margin-top: 20px;
}

.audit-section h4,
.version-section h4 {
  margin-bottom: 12px;
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}
</style>
