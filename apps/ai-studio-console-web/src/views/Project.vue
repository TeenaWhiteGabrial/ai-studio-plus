<template>
  <div class="project-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>项目管理</span>
          <el-button type="primary" @click="openCreate">新建项目</el-button>
        </div>
      </template>

      <el-form inline class="filter-form">
        <el-form-item label="项目名称">
          <el-input
            v-model="query.keyword"
            placeholder="搜索项目名称"
            clearable
            @keyup.enter="loadList"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 140px" @change="loadList">
            <el-option label="进行中" value="ACTIVE" />
            <el-option label="已结束" value="ENDED" />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人">
          <el-select
            v-model="query.ownerId"
            placeholder="全部负责人"
            clearable
            filterable
            style="width: 180px"
            @change="loadList"
          >
            <el-option
              v-for="user in ownerOptions"
              :key="user.id"
              :label="user.label"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="list" v-loading="loading" border stripe style="width: 100%">
        <el-table-column prop="projectName" label="项目名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="description" label="描述" min-width="220" show-overflow-tooltip />
        <el-table-column prop="deptName" label="所属部门" width="150" show-overflow-tooltip />
        <el-table-column prop="teamName" label="所属团队" width="150" show-overflow-tooltip />
        <el-table-column prop="ownerName" label="负责人" width="130" />
        <el-table-column prop="status" label="状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">
              {{ row.status === 'ACTIVE' ? '进行中' : '已结束' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startedAt" label="开始时间" width="170">
          <template #default="{ row }">{{ formatDateTime(row.startedAt) }}</template>
        </el-table-column>
        <el-table-column prop="endedAt" label="结束时间" width="170">
          <template #default="{ row }">{{ formatDateTime(row.endedAt) || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button
              size="small"
              :type="row.status === 'ACTIVE' ? 'warning' : 'success'"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 'ACTIVE' ? '结束' : '启用' }}
            </el-button>
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
        class="pagination"
        @change="loadList"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="620px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="form.projectName" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="所属部门" prop="deptId">
          <el-input
            v-if="!editId"
            :model-value="currentDeptName"
            disabled
            placeholder="当前登录用户未设置所属部门"
          />
          <el-select
            v-else
            v-model="form.deptId"
            placeholder="请选择所属部门"
            filterable
            style="width: 100%"
            @change="handleDeptChange"
          >
            <el-option
              v-for="dept in departmentOptions"
              :key="dept.id"
              :label="dept.deptName"
              :value="dept.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属团队" prop="teamId">
          <el-input
            v-if="!editId"
            :model-value="currentTeamName"
            disabled
            placeholder="当前登录用户未设置所属团队"
          />
          <el-select
            v-else
            v-model="form.teamId"
            placeholder="请选择所属团队"
            filterable
            style="width: 100%"
            :disabled="!form.deptId"
            @change="handleTeamChange"
          >
            <el-option
              v-for="team in teamOptions"
              :key="team.id"
              :label="team.teamName"
              :value="team.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人" prop="ownerId">
          <el-select
            v-model="form.ownerId"
            placeholder="请选择负责人"
            filterable
            style="width: 100%"
            :disabled="!form.teamId"
          >
            <el-option
              v-for="user in formOwnerOptions"
              :key="user.id"
              :label="user.label"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="startedAt">
          <el-date-picker
            v-model="form.startedAt"
            type="datetime"
            value-format="YYYY-MM-DDTHH:mm:ss"
            placeholder="选择开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="ACTIVE">进行中</el-radio>
            <el-radio label="ENDED">已结束</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入项目描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { departmentApi, projectApi, teamApi, userApi, type ConsoleProject } from '@/api'
import { useUserStore } from '@/stores/user'

interface OwnerOption {
  id: number
  label: string
  deptId?: number
  teamId?: number
}

interface DepartmentOption {
  id: number
  deptName: string
}

interface TeamOption {
  id: number
  teamName: string
  deptId?: number
}

const loading = ref(false)
const submitting = ref(false)
const list = ref<ConsoleProject[]>([])
const total = ref(0)
const ownerOptions = ref<OwnerOption[]>([])
const departmentOptions = ref<DepartmentOption[]>([])
const teamOptions = ref<TeamOption[]>([])
const userStore = useUserStore()
const { userInfo } = storeToRefs(userStore)

const query = reactive({
  page: 1,
  size: 20,
  keyword: '',
  status: '',
  ownerId: null as number | null,
})

const dialogVisible = ref(false)
const dialogTitle = ref('新建项目')
const editId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const form = reactive({
  projectName: '',
  description: '',
  ownerId: null as number | null,
  deptId: null as number | null,
  teamId: null as number | null,
  status: 'ACTIVE' as 'ACTIVE' | 'ENDED',
  startedAt: '',
})

const rules: FormRules = {
  projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  deptId: [{ required: true, message: '请选择所属部门', trigger: 'change' }],
  teamId: [{ required: true, message: '请选择所属团队', trigger: 'change' }],
  ownerId: [{ required: true, message: '请选择负责人', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
}

const formOwnerOptions = computed(() => {
  return ownerOptions.value.filter((user) => {
    if (form.deptId && user.deptId !== form.deptId) return false
    if (form.teamId && user.teamId !== form.teamId) return false
    return true
  })
})

const currentDeptId = computed(() => userInfo.value?.dept_id ?? null)
const currentTeamId = computed(() => userInfo.value?.team_id ?? null)
const currentDeptName = computed(() => {
  if (userInfo.value?.dept_name) return userInfo.value.dept_name
  const dept = departmentOptions.value.find((item) => item.id === currentDeptId.value)
  return dept?.deptName || ''
})
const currentTeamName = computed(() => {
  if (userInfo.value?.team_name) return userInfo.value.team_name
  const team = teamOptions.value.find((item) => item.id === currentTeamId.value)
  return team?.teamName || ''
})

function normalizeDepartment(dept: any): DepartmentOption {
  return {
    id: dept.id,
    deptName: dept.deptName ?? dept.dept_name,
  }
}

function normalizeTeam(team: any): TeamOption {
  return {
    id: team.id,
    teamName: team.teamName ?? team.team_name,
    deptId: team.deptId ?? team.dept_id,
  }
}

function formatDateTime(value?: string) {
  if (!value) return ''
  return value.replace('T', ' ').slice(0, 16)
}

async function loadDepartments() {
  const res = await departmentApi.active() as any
  const records = res.data || []
  departmentOptions.value = records.map(normalizeDepartment)
}

async function loadTeams(deptId?: number | null) {
  if (!deptId) {
    teamOptions.value = []
    return
  }
  const res = await teamApi.list({ deptId }) as any
  const records = res.data || []
  teamOptions.value = records.map(normalizeTeam)
}

async function loadOwners() {
  const res = await userApi.list({ page: 1, size: 1000 }) as any
  const records = res.data?.records || res.data || []
  ownerOptions.value = records.map((user: any) => {
    const deptName = user.dept_name || user.deptName
    const teamName = user.team_name || user.teamName
    const extra = [deptName, teamName].filter(Boolean).join(' / ')
    return {
      id: user.id,
      deptId: user.dept_id ?? user.deptId,
      teamId: user.team_id ?? user.teamId,
      label: `${user.real_name || user.realName || user.username}${extra ? `（${extra}）` : ''}`,
    }
  })
}

async function loadList() {
  loading.value = true
  try {
    const params: any = {
      page: query.page,
      size: query.size,
    }
    if (query.keyword) params.keyword = query.keyword
    if (query.status) params.status = query.status
    if (query.ownerId) params.owner_id = query.ownerId

    const res = await projectApi.list(params) as any
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error: any) {
    ElMessage.error(error.message || '加载项目列表失败')
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.page = 1
  query.keyword = ''
  query.status = ''
  query.ownerId = null
  loadList()
}

function resetForm() {
  Object.assign(form, {
    projectName: '',
    description: '',
    ownerId: null,
    deptId: currentDeptId.value,
    teamId: currentTeamId.value,
    status: 'ACTIVE',
    startedAt: '',
  })
  formRef.value?.clearValidate()
}

async function openCreate() {
  if (!currentDeptId.value) return ElMessage.warning('当前登录用户未设置所属部门，无法创建项目')
  if (!currentTeamId.value) return ElMessage.warning('当前登录用户未设置所属团队，无法创建项目')
  dialogTitle.value = '新建项目'
  editId.value = null
  resetForm()
  await loadTeams(form.deptId)
  dialogVisible.value = true
}

async function openEdit(row: ConsoleProject) {
  dialogTitle.value = '编辑项目'
  editId.value = row.id
  Object.assign(form, {
    projectName: row.projectName,
    description: row.description || '',
    ownerId: row.ownerId,
    deptId: row.deptId || null,
    teamId: row.teamId || null,
    status: row.status,
    startedAt: row.startedAt ? row.startedAt.slice(0, 19) : '',
  })
  await loadTeams(form.deptId)
  dialogVisible.value = true
}

async function handleDeptChange() {
  form.teamId = null
  form.ownerId = null
  await loadTeams(form.deptId)
}

function handleTeamChange() {
  form.ownerId = null
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitting.value = true
  try {
    const payload = {
      projectName: form.projectName,
      description: form.description,
      ownerId: form.ownerId!,
      status: form.status,
      startedAt: form.startedAt || undefined,
    }
    if (editId.value) {
      Object.assign(payload, {
        deptId: form.deptId!,
        teamId: form.teamId!,
      })
    }
    if (editId.value) {
      await projectApi.update(editId.value, payload)
      ElMessage.success('更新成功')
    } else {
      await projectApi.create(payload)
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

async function handleToggleStatus(row: ConsoleProject) {
  const nextStatus = row.status === 'ACTIVE' ? 'ENDED' : 'ACTIVE'
  const label = nextStatus === 'ACTIVE' ? '启用' : '结束'
  try {
    await ElMessageBox.confirm(`确定${label}项目 "${row.projectName}" 吗？`, '提示', { type: 'warning' })
    await projectApi.updateStatus(row.id, nextStatus)
    ElMessage.success('状态已更新')
    loadList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '更新状态失败')
    }
  }
}

async function handleDelete(row: ConsoleProject) {
  try {
    await ElMessageBox.confirm(`确定删除项目 "${row.projectName}" 吗？删除后无法恢复。`, '提示', { type: 'warning' })
    await projectApi.delete(row.id)
    ElMessage.success('删除成功')
    loadList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

onMounted(async () => {
  await Promise.all([loadDepartments(), loadOwners()])
  await loadList()
})
</script>

<style scoped>
.project-page {
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
</style>
