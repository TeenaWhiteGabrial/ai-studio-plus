<template>
  <el-dialog
    title="技能详情"
    v-model="dialogVisible"
    width="900px"
    :close-on-click-modal="false"
  >
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="10" animated />
    </div>

    <div v-else-if="detail" class="skill-detail">
      <!-- 基本信息 -->
      <el-descriptions :column="3" border>
        <el-descriptions-item label="技能名称" :span="1">{{ detail.name }}</el-descriptions-item>
        <el-descriptions-item label="分类" :span="1">{{ detail.category }}</el-descriptions-item>
        <el-descriptions-item label="最新版本" :span="1">
          <el-tag type="success">{{ detail.latest_version || '无' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="描述" :span="3">{{ detail.description || '暂无描述' }}</el-descriptions-item>
        <el-descriptions-item label="创建者" :span="1">{{ detail.creator_name }}</el-descriptions-item>
        <el-descriptions-item label="下载次数" :span="1">{{ detail.download_count }}</el-descriptions-item>
      </el-descriptions>

      <!-- 版本列表 -->
      <div class="version-section mt-4">
        <div class="section-header">
          <h3>版本历史</h3>
          <el-tag v-if="isSuperAdmin" type="warning">超级管理员：可查看所有版本</el-tag>
          <el-tag v-else type="info">仅显示最新版本</el-tag>
        </div>

        <el-table :data="detail.versions" border stripe>
          <el-table-column prop="version" label="版本号" width="100">
            <template #default="{ row }">
              <el-tag v-if="row.version === detail.latest_version" type="success">
                {{ row.version }} <el-icon><Check /></el-icon>
              </el-tag>
              <span v-else>{{ row.version }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="file_size_text" label="文件大小" width="100" />
          <el-table-column prop="changelog" label="变更日志" min-width="180" show-overflow-tooltip />
          <el-table-column prop="creator_name" label="发布者" width="100" />
          <el-table-column prop="created_at" label="发布时间" width="160">
            <template #default="{ row }">
              {{ formatDate(row.created_at) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button size="small" type="primary" @click="handleDownload(row)">下载</el-button>
              <el-button
                v-if="isSuperAdmin"
                size="small"
                type="danger"
                @click="handleDeleteVersion(row)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check } from '@element-plus/icons-vue'
import { skillApi } from '@/api'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const props = defineProps<{
  modelValue: boolean
  skillId?: number
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
}>()

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const loading = ref(false)
const detail = ref<any>(null)

const isSuperAdmin = computed(() => userStore.isSuperAdmin())

// 加载详情
async function loadDetail() {
  if (!props.skillId) return
  loading.value = true
  try {
    const res = await skillApi.detail(props.skillId) as any
    detail.value = res.data
  } finally {
    loading.value = false
  }
}

// 监听弹窗打开
watch(() => dialogVisible.value, (visible) => {
  if (visible && props.skillId) {
    loadDetail()
  }
})

// 下载指定版本
function handleDownload(version: any) {
  const url = skillApi.getDownloadUrl(props.skillId!, version.version)
  window.open(url, '_blank')
}

// 删除版本（仅超管）
async function handleDeleteVersion(version: any) {
  try {
    await ElMessageBox.confirm(
      `确定要删除版本 ${version.version} 吗？\n此操作不可恢复，OSS 文件将被物理删除。`,
      '确认删除',
      { type: 'warning' }
    )
    await skillApi.deleteVersion(props.skillId!, version.version)
    ElMessage.success('版本删除成功')
    loadDetail()
  } catch (e) {
    // 取消删除
  }
}

function formatDate(date: string) {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}
</script>

<style scoped>
.loading-container {
  padding: 20px;
}

.skill-detail {
  padding: 10px;
}

.version-section {
  margin-top: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}
</style>
