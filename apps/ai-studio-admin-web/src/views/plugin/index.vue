<template>
  <div>
    <el-card>
      <el-form inline>
        <el-form-item label="关键词"><el-input v-model="query.keyword" clearable @keyup.enter="loadList" /></el-form-item>
        <el-form-item label="分类"><el-input v-model="query.category" clearable style="width:120px" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width:100px">
            <el-option label="待审核" :value="0" />
            <el-option label="通过" :value="1" />
            <el-option label="拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList">搜索</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="name" label="名称" min-width="150" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="latestVersion" label="版本" width="100" />
        <el-table-column prop="fileSize" label="大小" width="100">
          <template #default="{ row }">{{ row.fileSize ? (row.fileSize / 1024 / 1024).toFixed(2) + 'MB' : '-' }}</template>
        </el-table-column>
        <el-table-column prop="downloadCount" label="下载次数" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning">待审核</el-tag>
            <el-tag v-else-if="row.status === 1" type="success">通过</el-tag>
            <el-tag v-else-if="row.status === 2" type="danger">拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reviewComment" label="审核意见" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleDetail(row)">详情</el-button>
            <el-button size="small" @click="handleDownload(row)">下载</el-button>
            <el-button v-if="row.status === 0" size="small" type="success" @click="openAuditDialog(row, 1)">通过</el-button>
            <el-button v-if="row.status === 0" size="small" type="danger" @click="openAuditDialog(row, 2)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.page" v-model:page-size="query.size" :total="total" layout="total, prev, pager, next" class="mt-4" @change="loadList" />
    </el-card>

    <el-dialog v-model="detailVisible" title="Plugin 详情" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="名称">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="描述">{{ detailData.description || '-' }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ detailData.category || '-' }}</el-descriptions-item>
        <el-descriptions-item label="版本">{{ detailData.latestVersion }}</el-descriptions-item>
        <el-descriptions-item label="文件大小">{{ detailData.fileSize ? (detailData.fileSize / 1024 / 1024).toFixed(2) + 'MB' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="下载次数">{{ detailData.downloadCount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="detailData.status === 0" type="warning">待审核</el-tag>
          <el-tag v-else-if="detailData.status === 1" type="success">通过</el-tag>
          <el-tag v-else-if="detailData.status === 2" type="danger">拒绝</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核意见">{{ detailData.reviewComment || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createdAt || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="auditVisible" title="审核 Plugin" width="500px">
      <el-form label-width="80px">
        <el-form-item label="插件名称">
          <span>{{ currentPlugin?.name }}</span>
        </el-form-item>
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.status">
            <el-radio :value="1">通过</el-radio>
            <el-radio :value="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input v-model="auditForm.reviewComment" type="textarea" :rows="3" placeholder="请输入审核意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmitAudit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { pluginApi } from '@/api'

const loading = ref(false), submitting = ref(false)
const list = ref<any[]>([]), total = ref(0)
const detailVisible = ref(false), auditVisible = ref(false)
const detailData = ref<any>({})
const currentPlugin = ref<any>(null)
const query = reactive({ page: 1, size: 10, keyword: '', category: '', status: undefined as number | undefined })
const auditForm = reactive({ status: 1, reviewComment: '' })

async function loadList() {
  loading.value = true
  try {
    const res = await pluginApi.list(query) as any
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

function handleDetail(row: any) {
  detailData.value = { ...row }
  detailVisible.value = true
}

function openAuditDialog(row: any, status: number) {
  currentPlugin.value = row
  auditForm.status = status
  auditForm.reviewComment = ''
  auditVisible.value = true
}

async function handleSubmitAudit() {
  if (!currentPlugin.value) return
  submitting.value = true
  try {
    await pluginApi.audit(currentPlugin.value.id, {
      status: auditForm.status,
      reviewComment: auditForm.reviewComment
    })
    ElMessage.success('审核成功')
    auditVisible.value = false
    loadList()
  } finally {
    submitting.value = false
  }
}

async function handleDownload(row: any) {
  const res = await pluginApi.download(row.id) as any
  window.open(res.data, '_blank')
}

onMounted(loadList)
</script>
