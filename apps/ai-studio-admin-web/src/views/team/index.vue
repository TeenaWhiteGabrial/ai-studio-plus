<template>
  <div class="team-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>团队管理</span>
          <el-button type="primary" @click="createTeam">新建团队</el-button>
        </div>
      </template>

      <el-form inline class="filter-form">
        <el-form-item label="部门">
          <el-select v-model="filterDeptId" placeholder="选择部门" clearable @change="fetchTeams">
            <el-option v-for="dept in departments" :key="dept.id" :label="dept.deptName" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="团队名称">
          <el-input v-model="filterName" placeholder="搜索团队名称" clearable @change="fetchTeams" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchTeams">搜索</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="teams" v-loading="loading" style="width: 100%">
        <el-table-column prop="teamName" label="团队名称" />
        <el-table-column prop="deptName" label="所属部门" width="150" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="memberCount" label="成员数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button link type="primary" @click="editTeam(row)">编辑</el-button>
            <el-button link type="primary" @click="manageMembers(row)">成员管理</el-button>
            <el-button link type="danger" @click="deleteTeam(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="fetchTeams"
        class="pagination"
      />
    </el-card>

    <!-- 创建/编辑团队弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="团队名称" required>
          <el-input v-model="form.teamName" placeholder="请输入团队名称" />
        </el-form-item>
        <el-form-item label="所属部门" required>
          <el-select v-model="form.deptId" placeholder="选择部门" style="width: 100%">
            <el-option v-for="dept in departments" :key="dept.id" :label="dept.deptName" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
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
        <el-button type="primary" @click="submitTeam">确定</el-button>
      </template>
    </el-dialog>

    <!-- 成员管理弹窗 -->
    <el-dialog v-model="memberDialogVisible" title="成员管理" width="600px">
      <div class="member-header">
        <el-button type="primary" size="small" @click="addMember">添加成员</el-button>
      </div>
      <el-table :data="members" size="small">
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="role" label="角色">
          <template #default="{ row }">
            <el-tag v-if="row.role === 'LEADER'" type="warning">组长</el-tag>
            <el-tag v-else type="info">成员</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="joinedAt" label="加入时间" width="180" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="danger" size="small" @click="removeMember(row)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const teams = ref<any[]>([])
const departments = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const filterDeptId = ref()
const filterName = ref('')

const dialogVisible = ref(false)
const dialogTitle = ref('创建团队')
const form = ref({
  id: null as number | null,
  teamName: '',
  deptId: null as number | null,
  description: '',
  status: 1,
})

const memberDialogVisible = ref(false)
const members = ref<any[]>([])
const currentTeamId = ref<number | null>(null)

const fetchTeams = async () => {
  loading.value = true
  try {
    // TODO: 调用后端 API
    // const res = await teamApi.list({ page: currentPage.value, size: pageSize.value, deptId: filterDeptId.value, name: filterName.value })
    teams.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const fetchDepartments = async () => {
  // TODO: 调用后端 API
  // departments.value = await departmentApi.active()
  departments.value = [
    { id: 1, deptName: '技术研发部' },
    { id: 2, deptName: '产品运营部' },
  ]
}

const createTeam = () => {
  dialogTitle.value = '创建团队'
  form.value = { id: null, teamName: '', deptId: null, description: '', status: 1 }
  dialogVisible.value = true
}

const editTeam = (row: any) => {
  dialogTitle.value = '编辑团队'
  form.value = { ...row }
  dialogVisible.value = true
}

const submitTeam = async () => {
  if (!form.value.teamName || !form.value.deptId) {
    ElMessage.warning('请填写必填项')
    return
  }
  ElMessage.success(form.value.id ? '更新成功' : '创建成功')
  dialogVisible.value = false
  fetchTeams()
}

const deleteTeam = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定删除团队"${row.teamName}"吗？`, '提示', { type: 'warning' })
    ElMessage.success('删除成功')
    fetchTeams()
  } catch {
    // 用户取消
  }
}

const manageMembers = (row: any) => {
  currentTeamId.value = row.id
  members.value = row.members || []
  memberDialogVisible.value = true
}

const addMember = () => {
  ElMessage.info('添加成员功能开发中')
}

const removeMember = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定移除成员"${row.realName}"吗？`, '提示', { type: 'warning' })
    ElMessage.success('移除成功')
    if (currentTeamId.value) {
      // 刷新成员列表
    }
  } catch {
    // 用户取消
  }
}

onMounted(() => {
  fetchTeams()
  fetchDepartments()
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
  margin-bottom: 16px;
}
</style>
