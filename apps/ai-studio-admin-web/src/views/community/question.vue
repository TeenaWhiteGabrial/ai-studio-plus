<template>
  <div class="question-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>问题管理</span>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索关键词"
            style="width: 200px"
            clearable
            @clear="loadQuestions"
            @keyup.enter="loadQuestions"
          />
          <el-select v-model="filterStatus" placeholder="状态筛选" clearable style="width: 120px" @change="loadQuestions">
            <el-option label="全部" :value="null" />
            <el-option label="正常" :value="0" />
            <el-option label="已下架" :value="1" />
          </el-select>
          <el-button type="primary" @click="loadQuestions">查询</el-button>
        </div>
      </template>
      <el-table :data="questionList" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="authorName" label="作者" width="120" />
        <el-table-column prop="takenDown" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.takenDown ? 'danger' : 'success'">
              {{ row.takenDown ? '已下架' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button
              v-if="!row.takenDown"
              link
              type="danger"
              @click="handleTakedown(row)"
            >
              下架
            </el-button>
            <el-button
              v-if="row.takenDown"
              link
              type="success"
              @click="handleRestore(row)"
            >
              恢复
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        layout="total, prev, pager, next"
        @current-change="loadQuestions"
        @size-change="loadQuestions"
      />
    </el-card>

    <!-- 下架对话框 -->
    <el-dialog v-model="takedownDialogVisible" title="下架问题" width="500px">
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
import { adminQuestionApi } from '@/api'

const loading = ref(false)
const searchKeyword = ref('')
const filterStatus = ref<number | null>(null)
const questionList = ref<any[]>([])
const pagination = ref({ page: 1, size: 10, total: 0 })
const takedownDialogVisible = ref(false)
const takedownForm = ref<{ reason: string }>({ reason: '' })
const currentQuestion = ref<any>(null)

const formatTime = (time: string) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

const loadQuestions = async () => {
  loading.value = true
  try {
    const res = await adminQuestionApi.list({
      keyword: searchKeyword.value,
      takenDown: filterStatus.value,
      page: pagination.value.page,
      size: pagination.value.size
    })
    questionList.value = res?.records || []
    pagination.value.total = res?.total || 0
  } catch (error) {
    console.error('加载问题列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleView = (question: any) => {
  ElMessage.info('查看功能暂未实现')
}

const handleTakedown = (question: any) => {
  currentQuestion.value = question
  takedownForm.value.reason = ''
  takedownDialogVisible.value = true
}

const handleRestore = async (question: any) => {
  try {
    await adminQuestionApi.restore(question.id)
    ElMessage.success('恢复成功')
    loadQuestions()
  } catch (error) {
    console.error('恢复失败:', error)
  }
}

const confirmTakedown = async () => {
  if (!takedownForm.value.reason.trim()) {
    ElMessage.warning('请输入下架原因')
    return
  }
  try {
    await adminQuestionApi.takedown(currentQuestion.value.id, takedownForm.value.reason)
    ElMessage.success('下架成功')
    takedownDialogVisible.value = false
    loadQuestions()
  } catch (error) {
    console.error('下架失败:', error)
  }
}

onMounted(() => {
  loadQuestions()
})
</script>

<style scoped>
.question-manage {
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
