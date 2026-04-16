<template>
  <div class="team-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>团队管理</span>
          <el-button type="primary" @click="openCreate">新建团队</el-button>
        </div>
      </template>

      <el-form inline class="filter-form">
        <el-form-item label="部门">
          <el-select style="min-width:160px" v-model="query.dept_id" placeholder="选择部门" clearable @change="loadList">
            <el-option v-for="dept in departments" :key="dept.id" :label="dept.dept_name" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="团队名称">
          <el-input v-model="query.team_name" placeholder="搜索团队名称" clearable @keyup.enter="loadList" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="list" v-loading="loading" border stripe style="width: 100%">
        <el-table-column prop="team_name" label="团队名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="dept_id" label="所属部门" width="150">
          <template #default="{ row }">
            {{ getDeptName(row.dept_id) }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="created_at" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="primary" @click="openMemberDialog(row)">成员管理</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @change="loadList"
        class="pagination"
      />
    </el-card>

    <!-- 创建/编辑团队弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="团队名称" prop="teamName">
          <el-input v-model="form.team_name" placeholder="请输入团队名称" />
        </el-form-item>
        <el-form-item label="所属部门" prop="deptId" v-if="isSuperAdmin">
          <el-select v-model="form.dept_id" placeholder="选择部门" style="width: 100%">
            <el-option v-for="dept in activeDepartments" :key="dept.id" :label="dept.dept_name" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 成员管理弹窗 -->
    <el-dialog v-model="memberDialogVisible" title="成员管理" width="700px">
      <div class="member-header">
        <el-input
          v-model="memberSearch"
          placeholder="搜索成员姓名或账号"
          clearable
          style="width: 200px"
          @input="filterMembers"
        />
        <el-button type="primary" size="small" @click="openAddMemberDialog">添加成员</el-button>
      </div>

      <el-table :data="filteredMembers" size="small" border class="member-table">
        <el-table-column prop="username" label="账号" width="120" />
        <el-table-column prop="realName" label="姓名" min-width="100" />
        <el-table-column prop="email" label="邮箱" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
            <el-button link type="danger" size="small" @click="handleRemoveMember(row)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="members.length === 0" class="empty-members">
        <el-empty description="暂无成员" />
      </div>
    </el-dialog>

    <!-- 添加成员弹窗 -->
    <el-dialog v-model="addMemberDialogVisible" title="添加成员" width="600px">
      <div class="add-member-filter">
        <el-input
          v-model="userSearch"
          placeholder="搜索用户姓名或账号"
          clearable
          @input="filterAddableUsers"
        />
      </div>
      <el-table
        ref="addMemberTableRef"
        :data="filterableUsers"
        size="small"
        border
        @selection-change="handleUserSelectionChange"
        class="add-member-table"
        max-height="350"
      >
        <el-table-column type="selection" width="40" />
        <el-table-column prop="username" label="账号" width="120" />
        <el-table-column prop="realName" label="姓名" min-width="100" />
        <el-table-column prop="deptName" label="部门" width="120" />
      </el-table>
      <template #footer>
        <el-button @click="addMemberDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="addMemberLoading" @click="handleAddMembers" :disabled="selectedUsers.length === 0">
          添加 ({{ selectedUsers.length }})
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { teamApi, departmentApi, userApi } from '@/api'
import { useUserStore } from '@/stores/user'
import type { FormInstance } from 'element-plus'

const userStore = useUserStore()
const isSuperAdmin = computed(() => userStore.isSuperAdmin())

const loading = ref(false)
const submitting = ref(false)
const list = ref<any[]>([])
const departments = ref<any[]>([])
const activeDepartments = ref<any[]>([])
const total = ref(0)

const query = reactive({
  page: 1,
  size: 20,
  dept_id: null as number | null,
  team_name: '',
})

// 创建/编辑弹窗
const dialogVisible = ref(false)
const dialogTitle = ref('创建团队')
const editId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const form = reactive({
  team_name: '',
  dept_id: null as number | null,
  description: '',
  status: 1,
})
const rules = {
  team_name: [{ required: true, message: '请输入团队名称', trigger: 'blur' }],
  dept_id: [{ required: true, message: '请选择部门', trigger: 'change' }],
}

// 成员管理弹窗
const memberDialogVisible = ref(false)
const members = ref<any[]>([])
const currentTeamId = ref<number | null>(null)
const memberSearch = ref('')

// 添加成员弹窗
const addMemberDialogVisible = ref(false)
const addMemberLoading = ref(false)
const userSearch = ref('')
const selectedUsers = ref<any[]>([])
const addableUsers = ref<any[]>([])

// 计算属性：过滤后的成员列表
const filteredMembers = computed(() => {
  if (!memberSearch.value) return members.value
  const search = memberSearch.value.toLowerCase()
  return members.value.filter(m =>
    m.username?.toLowerCase().includes(search) ||
    m.realName?.toLowerCase().includes(search)
  )
})

// 计算属性：可添加用户列表（已过滤）
const filterableUsers = computed(() => {
  if (!userSearch.value) return addableUsers.value
  const search = userSearch.value.toLowerCase()
  return addableUsers.value.filter(u =>
    u.username?.toLowerCase().includes(search) ||
    u.realName?.toLowerCase().includes(search)
  )
})

// 获取部门名称
function getDeptName(deptId: number): string {
  const dept = departments.value.find(d => d.id === deptId)
  return dept?.dept_name || String(deptId)
}

// 加载部门列表
async function loadDepartments() {
  try {
    const res = await departmentApi.list() as any
    departments.value = res.data || []
  } catch (error) {
    console.error('加载部门列表失败', error)
  }
}

async function loadActiveDepartments() {
  try {
    const res = await departmentApi.active() as any
    activeDepartments.value = res.data || []
  } catch (error) {
    console.error('加载可用部门列表失败', error)
  }
}

// 加载团队列表
async function loadList() {
  loading.value = true
  try {
    const params: any = {}
    if (query.dept_id) params.deptId = query.dept_id
    const res = await teamApi.list(params) as any
    // 后端返回的是数组或分页对象，根据实际情况处理
    if (Array.isArray(res.data)) {
      list.value = res.data
      total.value = res.data.length
    } else {
      list.value = res.data?.records || res.data?.list || []
      total.value = res.data?.total || 0
    }
  } catch (error: any) {
    ElMessage.error(error.message || '加载团队列表失败')
  } finally {
    loading.value = false
  }
}

// 重置查询
function resetQuery() {
  query.dept_id = null
  query.team_name = ''
  query.page = 1
  loadList()
}

// 打开创建弹窗
function openCreate() {
  dialogTitle.value = '创建团队'
  editId.value = null
  Object.assign(form, { team_name: '', dept_id: null, description: '', status: 1 })
  loadActiveDepartments()
  dialogVisible.value = true
}

// 打开编辑弹窗
function openEdit(row: any) {
  dialogTitle.value = '编辑团队'
  editId.value = row.id
  Object.assign(form, {
    team_name: row.team_name,
    dept_id: row.dept_id,
    description: row.description,
    status: row.status,
  })
  loadActiveDepartments()
  dialogVisible.value = true
}

// 提交表单
async function handleSubmit() {
  await formRef.value?.validate()
  submitting.value = true
  try {
    if (editId.value) {
      await teamApi.update(editId.value, {
        team_name: form.team_name,
        description: form.description,
        status: form.status,
      })
      ElMessage.success('更新成功')
    } else {
      await teamApi.create({
        team_name: form.team_name,
        dept_id: form.dept_id,
        description: form.description,
      })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadList()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

// 删除团队
async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(`确定删除团队 "${row.team_name}" 吗？删除后无法恢复。`, '提示', { type: 'warning' })
    await teamApi.delete(row.id)
    ElMessage.success('删除成功')
    loadList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 打开成员管理弹窗
async function openMemberDialog(row: any) {
  currentTeamId.value = row.id
  memberSearch.value = ''
  await loadMembers(row.id)
  memberDialogVisible.value = true
}

// 加载团队成员
async function loadMembers(teamId: number) {
  try {
    const res = await teamApi.members(teamId) as any
    members.value = res.data || []
  } catch (error: any) {
    ElMessage.error(error.message || '加载成员列表失败')
    members.value = []
  }
}

// 过滤成员
function filterMembers() {
  // 通过 computed 自动处理
}

// 移除成员
async function handleRemoveMember(row: any) {
  try {
    await ElMessageBox.confirm(`确定移除成员 "${row.realName || row.username}" 吗？`, '提示', { type: 'warning' })
    await teamApi.removeMember(currentTeamId.value!, row.id || row.userId)
    ElMessage.success('移除成功')
    loadMembers(currentTeamId.value!)
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '移除失败')
    }
  }
}

// 打开添加成员弹窗
async function openAddMemberDialog() {
  userSearch.value = ''
  selectedUsers.value = []
  addMemberDialogVisible.value = true
  // 加载可添加的用户列表（所有同部门用户）
  try {
    // 获取当前团队的部门 ID
    const currentTeam = list.value.find(t => t.id === currentTeamId.value)
    const params: any = { page: 1, size: 1000 }
    if (currentTeam?.dept_id) {
      params.deptId = currentTeam.dept_id
    }
    const res = await userApi.list(params) as any
    // 过滤掉已经是团队成员的用户
    const memberIds = members.value.map(m => m.id || m.userId)
    addableUsers.value = (res.data?.records || res.data || []).filter((u: any) => !memberIds.includes(u.id))
  } catch (error) {
    console.error('加载用户列表失败', error)
    addableUsers.value = []
  }
}

// 过滤可添加用户
function filterAddableUsers() {
  // 通过 computed 自动处理
}

// 选择用户变化
function handleUserSelectionChange(selection: any[]) {
  selectedUsers.value = selection
}

// 添加成员
async function handleAddMembers() {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请选择要添加的成员')
    return
  }
  addMemberLoading.value = true
  try {
    const userIds = selectedUsers.value.map(u => u.id)
    await teamApi.addMembers(currentTeamId.value!, userIds)
    ElMessage.success(`成功添加 ${userIds.length} 名成员`)
    addMemberDialogVisible.value = false
    loadMembers(currentTeamId.value!)
  } catch (error: any) {
    ElMessage.error(error.message || '添加成员失败')
  } finally {
    addMemberLoading.value = false
  }
}

onMounted(() => {
  loadList()
  loadDepartments()
})
</script>

<style scoped>
.team-page {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.member-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  gap: 12px;
}

.member-table {
  margin-top: 8px;
}

.empty-members {
  padding: 20px 0;
}

.add-member-filter {
  margin-bottom: 12px;
}

.add-member-table {
  margin-top: 8px;
}
</style>
