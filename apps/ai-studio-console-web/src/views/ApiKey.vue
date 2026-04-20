<template>
  <div class="apikey-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>API Key 管理</span>
          <el-button type="primary" @click="createApiKey">申请新 Key</el-button>
        </div>
      </template>

      <el-table :data="apiKeys" style="width: 100%">
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="apiKey" label="API Key" width="300">
          <template #default="{ row }">
            <code class="api-key">{{ row.show ? row.apiKey : '••••••••••••••••' }}</code>
            <el-button link type="primary" @click="row.show = !row.show">
              {{ row.show ? '隐藏' : '显示' }}
            </el-button>
            <el-button link type="primary" @click="copyKey(row.apiKey)">复制</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" @click="resetKey(row)">重置</el-button>
            <el-button link type="danger" @click="deleteKey(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="申请 API Key" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="Key 名称">
          <el-input v-model="form.name" placeholder="请输入 Key 名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitKey">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const apiKeys = ref([
  { id: 1, name: '开发测试 Key', apiKey: 'sk-xxxxx-xxxxx-xxxxx', show: false, createdAt: '2026-04-10', status: 1 },
])

const dialogVisible = ref(false)
const form = ref({ name: '', description: '' })

const createApiKey = () => {
  dialogVisible.value = true
}

const submitKey = () => {
  ElMessage.success('API Key 申请成功')
  dialogVisible.value = false
}

const copyKey = (key: string) => {
  navigator.clipboard.writeText(key)
  ElMessage.success('已复制到剪贴板')
}

const resetKey = (_row: any) => {
  ElMessage.info('重置功能开发中')
}

const deleteKey = (_row: any) => {
  ElMessage.info('删除功能开发中')
}
</script>

<style scoped>
.apikey-page {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-weight: 600;
  color: var(--ai-text-primary);
}

.api-key {
  background: var(--ai-fill-light);
  padding: 4px 8px;
  border-radius: var(--ai-radius-sm);
  font-size: 12px;
  color: var(--ai-text-secondary);
  font-family: 'Courier New', monospace;
  border: 1px solid var(--ai-border-subtle);
}

:deep(.el-card__header) {
  border-bottom: 1px solid var(--ai-border-default);
}

/* Table styling */
:deep(.el-table) {
  border-radius: var(--ai-radius-md);
  overflow: hidden;
}

:deep(.el-table th.el-table__cell) {
  color: var(--ai-text-tertiary);
  font-weight: 500;
}

:deep(.el-table td.el-table__cell) {
  color: var(--ai-text-secondary);
}

/* Button styling */
:deep(.el-button) {
  font-weight: 500;
}

/* Tag styling */
:deep(.el-tag--success) {
  --el-tag-bg-color: hsla(142, 76%, 36%, 0.1);
  --el-tag-border-color: hsla(142, 76%, 36%, 0.2);
  --el-tag-text-color: var(--ai-success);
}

:deep(.el-tag--danger) {
  --el-tag-bg-color: hsla(0, 72%, 51%, 0.1);
  --el-tag-border-color: hsla(0, 72%, 51%, 0.2);
  --el-tag-text-color: var(--ai-error);
}
</style>
