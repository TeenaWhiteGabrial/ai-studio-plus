<template>
  <div class="comment-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>评论管理</span>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索内容"
            style="width: 200px"
            clearable
            @clear="loadComments"
            @keyup.enter="loadComments"
          />
          <el-select v-model="filterTargetType" placeholder="类型筛选" clearable style="width: 120px" @change="loadComments">
            <el-option label="全部" value="" />
            <el-option label="文章" value="article" />
            <el-option label="问题" value="question" />
            <el-option label="回答" value="answer" />
          </el-select>
          <el-button type="primary" @click="loadComments">查询</el-button>
        </div>
      </template>
      <el-table :data="commentList" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="content" label="内容" min-width="150" show-overflow-tooltip />
        <el-table-column prop="targetType" label="类型" width="100">
          <template #default="{ row }">
            {{ getTargetTypeText(row.targetType) }}
          </template>
        </el-table-column>
        <el-table-column prop="authorName" label="作者" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        layout="total, prev, pager, next"
        @current-change="loadComments"
        @size-change="loadComments"
      />
    </el-card>

    <!-- 删除对话框 -->
    <el-dialog v-model="deleteDialogVisible" title="删除评论" width="500px">
      <el-form :model="deleteForm" label-width="80px">
        <el-form-item label="删除原因" required>
          <el-input v-model="deleteForm.reason" type="textarea" :rows="4" placeholder="请输入删除原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmDelete">确认删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminCommentApi } from '@/api'

const loading = ref(false)
const searchKeyword = ref('')
const filterTargetType = ref('')
const commentList = ref<any[]>([])
const pagination = ref({ page: 1, size: 20, total: 0 })
const deleteDialogVisible = ref(false)
const deleteForm = ref<{ reason: string }>({ reason: '' })
const currentComment = ref<any>(null)

const getTargetTypeText = (type: string) => {
  const map = { article: '文章', question: '问题', answer: '回答' }
  return map[type] || type
}

const formatTime = (time: string) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

const loadComments = async () => {
  loading.value = true
  try {
    const res = await adminCommentApi.list({
      keyword: searchKeyword.value,
      targetType: filterTargetType.value,
      page: pagination.value.page,
      size: pagination.value.size
    })
    commentList.value = res?.records || []
    pagination.value.total = res?.total || 0
  } catch (error) {
    console.error('加载评论列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleView = (comment: any) => {
  ElMessage.info('查看功能暂未实现')
}

const handleDelete = (comment: any) => {
  currentComment.value = comment
  deleteForm.value.reason = ''
  deleteDialogVisible.value = true
}

const confirmDelete = async () => {
  if (!deleteForm.value.reason.trim()) {
    ElMessage.warning('请输入删除原因')
    return
  }
  try {
    await ElMessageBox.confirm('确认删除该评论吗？', '提示', {
      type: 'warning'
    })
    await adminCommentApi.delete(currentComment.value.id, deleteForm.value.reason)
    ElMessage.success('删除成功')
    deleteDialogVisible.value = false
    loadComments()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

onMounted(() => {
  loadComments()
})
</script>

<style scoped>
.comment-manage {
  padding: 20px;
}
.card-header {
  display: flex;
  gap: 10px;
  align-items: center;
}
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
