<template>
  <div class="article-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的文章</span>
          <el-button type="primary" @click="handleCreate">新建文章</el-button>
        </div>
      </template>
      <el-table :data="articleList" v-loading="loading">
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishType" label="发布类型" width="120">
          <template #default="{ row }">
            {{ getPublishTypeText(row.publishType) }}
          </template>
        </el-table-column>
        <el-table-column prop="scheduledPublishTime" label="定时发布" width="180">
          <template #default="{ row }">
            {{ formatTime(row.scheduledPublishTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button
              v-if="row.status === 0"
              link
              type="success"
              @click="handlePublish(row)"
            >
              发布
            </el-button>
            <el-button
              v-if="row.publishType === 2"
              link
              type="warning"
              @click="handleCancelSchedule(row)"
            >
              取消定时
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        layout="total, prev, pager, next"
        @current-change="loadArticles"
        @size-change="loadArticles"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { articleApi } from '@/api'

const router = useRouter()
const loading = ref(false)
const articleList = ref<any[]>([])
const pagination = ref({ page: 1, size: 10, total: 0 })

const getStatusText = (status: number) => {
  const map = { 0: '草稿', 1: '已发布', 2: '已下架' }
  return map[status] || '未知'
}

const getStatusType = (status: number) => {
  const map = { 0: 'info', 1: 'success', 2: 'danger' }
  return map[status] || ''
}

const getPublishTypeText = (type: number) => {
  const map = { 0: '草稿', 1: '立即发布', 2: '定时发布' }
  return map[type] || '未知'
}

const formatTime = (time: string) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

const loadArticles = async () => {
  loading.value = true
  try {
    const res = await articleApi.list({
      page: pagination.value.page,
      size: pagination.value.size
    })
    articleList.value = res?.records || []
    pagination.value.total = res?.total || 0
  } catch (error) {
    console.error('加载文章列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleCreate = () => {
  router.push({ name: 'ArticleEdit', params: { id: 'new' } })
}

const handleEdit = (article: any) => {
  router.push({ name: 'ArticleEdit', params: { id: article.id } })
}

const handlePublish = async (article: any) => {
  try {
    await ElMessageBox.confirm('确认发布这篇文章吗？', '提示', {
      type: 'warning'
    })
    await articleApi.publish(article.id)
    ElMessage.success('发布成功')
    loadArticles()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('发布失败:', error)
    }
  }
}

const handleCancelSchedule = async (article: any) => {
  try {
    await ElMessageBox.confirm('确认取消定时发布吗？', '提示', {
      type: 'warning'
    })
    await articleApi.cancelSchedule(article.id)
    ElMessage.success('已取消定时发布')
    loadArticles()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('取消失败:', error)
    }
  }
}

const handleDelete = async (article: any) => {
  try {
    await ElMessageBox.confirm('确认删除这篇文章吗？', '提示', {
      type: 'warning'
    })
    await articleApi.delete(article.id)
    ElMessage.success('删除成功')
    loadArticles()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
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
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
