<template>
  <div class="skill-management">
    <el-card>
      <!-- 头部操作栏 -->
      <div class="header-actions mb-4">
        <div class="left">
          <el-form inline>
            <el-form-item label="关键词">
              <el-input v-model="query.keyword" placeholder="搜索名称" clearable @keyup.enter="loadList" />
            </el-form-item>
            <el-form-item label="分类">
              <el-select style="width:120px" v-model="query.category" placeholder="选择分类" clearable>
                <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadList">搜索</el-button>
              <el-button @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
        <div class="right">
          <el-button type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon>
            创建技能
          </el-button>
        </div>
      </div>

      <!-- 表格 -->
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="name" label="名称" width="180">
          <template #default="{ row }">
            <el-link type="primary" @click="handleDetail(row)">{{ row.name }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="latest_version" label="最新版本" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.latest_version" type="success" size="small">{{ row.latest_version }}</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="total_versions" label="版本数" width="80" align="center" />
        <el-table-column prop="creator_name" label="创建者" width="100" />
        <el-table-column prop="download_count" label="下载" width="80" align="center" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleDetail(row)">详情</el-button>
            <el-button size="small" type="primary" @click="handleDownload(row)">下载</el-button>
            <el-dropdown  v-if="canEdit(row)" size="small" style="margin-left:14px;margin-top:2px" trigger="click" @command="(cmd) => handleCommand(cmd, row)">
              <el-button size="small" type="info">
                更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="publish">发布新版本</el-dropdown-item>
                  <el-dropdown-item command="edit">编辑信息</el-dropdown-item>
                  <el-dropdown-item command="delete" type="danger">删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="total"
        layout="total, sizes, prev, pager, next"
        :page-sizes="[10, 20, 50]"
        class="mt-4"
        @change="loadList"
      />
    </el-card>

    <!-- 创建/编辑技能弹窗 -->
    <SkillFormDialog
      v-model="formVisible"
      :is-edit="isEdit"
      :edit-data="editData"
      @success="loadList"
    />

    <!-- 发布新版本弹窗 -->
    <VersionPublishDialog
      v-model="versionVisible"
      :skill="currentSkill"
      @success="loadList"
    />

    <!-- 技能详情弹窗 -->
    <SkillDetailDialog
      v-model="detailVisible"
      :skill-id="currentSkill?.id"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, ArrowDown } from '@element-plus/icons-vue'
import { skillApi } from '@/api'
import { useUserStore } from '@/stores/user'
import SkillFormDialog from './components/SkillFormDialog.vue'
import VersionPublishDialog from './components/VersionPublishDialog.vue'
import SkillDetailDialog from './components/SkillDetailDialog.vue'

const userStore = useUserStore()
const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const categories = ref<string[]>(['工具', '文档', '设计', '开发','效能', '其他'])

// 查询条件
const query = reactive({
  page: 1,
  size: 10,
  keyword: '',
  category: ''
})

// 弹窗控制
const formVisible = ref(false)
const versionVisible = ref(false)
const detailVisible = ref(false)
const isEdit = ref(false)
const editData = ref<any>(null)
const currentSkill = ref<any>(null)

// 当前用户角色
const isAdmin = computed(() => userStore.isAdmin())
const isSuperAdmin = computed(() => userStore.isSuperAdmin())
const currentUserId = computed(() => userStore.userInfo?.user_id)

// 加载列表
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

// 重置查询
function resetQuery() {
  query.page = 1
  query.keyword = ''
  query.category = ''
  loadList()
}

// 创建技能
function handleCreate() {
  isEdit.value = false
  editData.value = null
  formVisible.value = true
}

// 编辑技能信息
function handleEdit(row: any) {
  isEdit.value = true
  editData.value = row
  formVisible.value = true
}

// 查看详情
function handleDetail(row: any) {
  currentSkill.value = row
  detailVisible.value = true
}

// 发布新版本
function handlePublish(row: any) {
  currentSkill.value = row
  versionVisible.value = true
}

// 下载技能
async function handleDownload(row: any) {
  try {
    if (!row.oss_url) {
      ElMessage.error('暂无可用下载链接')
      return
    }
    // 直接使用列表中的 oss_url 下载
    window.open(row.oss_url, '_blank')
    ElMessage.success('开始下载')
  } catch (e: any) {
    ElMessage.error(e.message || '下载失败')
  }
}

// 删除技能
async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(
      `确定要删除技能 "${row.name}" 吗？\n删除后技能将不再显示，但历史版本数据会保留。`,
      '确认删除',
      { type: 'warning' }
    )
    await skillApi.delete(row.id)
    ElMessage.success('删除成功')
    loadList()
  } catch (e) {
    // 取消删除
  }
}

// 更多操作
function handleCommand(command: string, row: any) {
  currentSkill.value = row
  switch (command) {
    case 'edit':
      handleEdit(row)
      break
    case 'publish':
      handlePublish(row)
      break
    case 'delete':
      handleDelete(row)
      break
  }
}

// 权限检查
function canEdit(row: any) {
  if (isAdmin.value || isSuperAdmin.value) return true
  return row.created_by === currentUserId.value
}

onMounted(() => {
  loadList()
})
</script>

<style scoped>
.skill-management {
  padding: 20px;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.header-actions .left {
  flex: 1;
}

.header-actions .right {
  margin-left: 16px;
}
</style>
