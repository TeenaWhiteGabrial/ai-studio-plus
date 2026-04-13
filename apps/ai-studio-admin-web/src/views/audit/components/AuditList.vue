<template>
  <div class="audit-list">
    <el-form inline class="filter-form">
      <el-form-item label="审核状态">
        <el-select v-model="filterStatus" placeholder="选择状态" clearable @change="fetchList">
          <el-option label="待审核" value="PENDING" />
          <el-option label="已通过" value="APPROVED" />
          <el-option label="已拒绝" value="REJECTED" />
        </el-select>
      </el-form-item>
      <el-form-item label="资源名称">
        <el-input v-model="filterName" placeholder="搜索资源名称" clearable @change="fetchList" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="fetchList">搜索</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" style="width: 100%">
      <el-table-column prop="name" label="资源名称" />
      <el-table-column prop="type" label="类型" width="100" />
      <el-table-column prop="author" label="提交人" width="120" />
      <el-table-column prop="deptName" label="所属部门" width="150" />
      <el-table-column prop="submittedAt" label="提交时间" width="180" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button link type="primary" @click="viewDetail(row)">查看</el-button>
          <template v-if="row.status === 'PENDING'">
            <el-button link type="success" @click="approve(row)">通过</el-button>
            <el-button link type="danger" @click="reject(row)">拒绝</el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="currentPage"
      :page-size="pageSize"
      :total="total"
      layout="total, prev, pager, next"
      @current-change="fetchList"
      class="pagination"
    />

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="资源详情" width="700px">
      <div v-if="currentItem" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="资源名称">{{ currentItem.name }}</el-descriptions-item>
          <el-descriptions-item label="类型">{{ currentItem.type }}</el-descriptions-item>
          <el-descriptions-item label="提交人">{{ currentItem.author }}</el-descriptions-item>
          <el-descriptions-item label="所属部门">{{ currentItem.deptName }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ currentItem.submittedAt }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentItem.status)">{{ getStatusText(currentItem.status) }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <div class="version-section">
          <h4>版本信息</h4>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="版本号">{{ currentItem.version }}</el-descriptions-item>
            <el-descriptions-item label="版本说明">{{ currentItem.changeLog || '无' }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div v-if="currentItem.rejectReason" class="reject-section">
          <h4>拒绝原因</h4>
          <el-alert type="error" :closable="false">{{ currentItem.rejectReason }}</el-alert>
        </div>
      </div>
      <template #footer v-if="currentItem?.status === 'PENDING'">
        <el-button @click="detailVisible = false">取消</el-button>
        <el-button type="danger" @click="rejectDialogVisible = true">拒绝</el-button>
        <el-button type="success" @click="approveCurrent">通过</el-button>
      </template>
    </el-dialog>

    <!-- 拒绝原因弹窗 -->
    <el-dialog v-model="rejectDialogVisible" title="拒绝原因" width="500px">
      <el-form :model="rejectForm" label-width="100px">
        <el-form-item label="拒绝原因" required>
          <el-input v-model="rejectForm.reason" type="textarea" placeholder="请输入拒绝原因" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReject">确定拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

defineProps<{
  type: string
}>()

const loading = ref(false)
const list = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const filterStatus = ref()
const filterName = ref('')

const detailVisible = ref(false)
const currentItem = ref<any>(null)

const rejectDialogVisible = ref(false)
const rejectForm = ref({ reason: '' })

const getStatusType = (status: string) => {
  const map: Record<string, any> = {
    PENDING: 'warning',
    APPROVED: 'success',
    REJECTED: 'danger',
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    PENDING: '待审核',
    APPROVED: '已通过',
    REJECTED: '已拒绝',
  }
  return map[status] || status
}

const fetchList = async () => {
  loading.value = true
  try {
    // TODO: 调用后端 API
    // const res = await auditApi.list({ type: props.type, status: filterStatus.value, name: filterName.value, page: currentPage.value, size: pageSize.value })
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const viewDetail = (row: any) => {
  currentItem.value = row
  detailVisible.value = true
}

const approve = async (_row: any) => {
  // TODO: 调用后端 API
  ElMessage.success('已通过审核')
  fetchList()
}

const reject = (_row: any) => {
  currentItem.value = _row
  rejectForm.value.reason = ''
  rejectDialogVisible.value = true
}

const approveCurrent = async () => {
  if (!currentItem.value) return
  // TODO: 调用后端 API
  ElMessage.success('已通过审核')
  detailVisible.value = false
  fetchList()
}

const submitReject = async () => {
  if (!rejectForm.value.reason) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  // TODO: 调用后端 API
  ElMessage.success('已拒绝')
  rejectDialogVisible.value = false
  detailVisible.value = false
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

.version-section,
.reject-section {
  margin-top: 20px;
}

.version-section h4,
.reject-section h4 {
  margin-bottom: 12px;
  font-size: 14px;
  color: #303133;
}
</style>
