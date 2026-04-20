<template>
  <div class="article-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>文章管理</span>
        </div>
      </template>
      <el-table :data="articleList" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="authorName" label="作者" width="120" />
        <el-table-column prop="status" label="状态" width="100100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.publishTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button
              v-if="row.status === 1"
              link
              type="danger"
              @click="handleTakedown(row)"
            >
              下架
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 下架对话框 -->
    <el-dialog v-model="takedownDialogVisible" title="下架文章" width="500px">
      <el-form :model="takedownForm" label-width="80px">
        <el-form-item label="下架原因" required>
          <el-input v-model="takedownForm.reason" type="textarea" :rows="4" placeholder="请输入下架原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="takedownDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmTakedown">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adminArticleApi } from '@/api'

const loading = ref(false)
const articleList = ref<any[]>([])
const takedownDialogVisible = ref(false)
const takedownForm = ref<{ reason: string }>({ reason: '' })
const currentArticle = ref<any>(null)

const getStatusText = (status: number) => {
  const map = { 0: '草稿', 1: '已发布', 2: '已下架' }
  return map[status] || '未知'
}

const getStatusType = (status: number) => {
  const map = { 0: 'info', 1: 'success', 2: 'danger' }
  return map[status] || ''
}

const formatTime = (time: string) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

const loadArticles = async () => {
  loading.value = true
  try {
    const res = await adminArticleApi.list()
    articleList.value = res || []
  } catch (error) {
    console.error('加载文章列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleView = (article: any) => {
  ElMessage.info('查看功能暂未实现')
}

const handleTakedown = (article: any) => {
  currentArticle.value = article
  t.takedownForm.value.reason = ''
  takedownDialogVisible.value = true
}

const confirmTakedown = async () => {
  if (!takedownForm.value.reason.trim()) {
    ElMessage.warning('请输入下架原因')
    return
  }
  try {
    await adminArticleApi.takedown(currentArticle.value.id, takedownForm.value.reason)
    ElMessage.success('下架成功')
    takedownDialogVisible.value = false
    loadArticles()
  } catch (error) {
    console.error('下架失败:', error)
  }
}

onMounted(() => {
  loadArticles()
})
</script>

<style scoped>
.article-manage {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
