<template>
  <div>
    <el-card>
      <el-form inline>
        <el-form-item label="关键词"><el-input v-model="query.keyword" clearable /></el-form-item>
        <el-form-item label="部门">
          <el-select v-model="query.dept_id" clearable placeholder="全部" style="width:120px">
            <el-option v-for="dept in departments" :key="dept.id" :label="dept.dept_name" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList">搜索</el-button>
          <el-button type="success" @click="openCreate">新建用户</el-button>
          <!-- <el-button type="info" @click="openImportDialog">批量导入</el-button> -->
        </el-form-item>
      </el-form>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column label="头像" width="80" align="center">
          <template #default="{ row }">
            <el-image v-if="row.avatar" :src="row.avatar" fit="cover" class="table-avatar" :preview-src-list="[row.avatar]" />
            <el-icon v-else :size="24" class="default-avatar"><User /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="账号" width="120" />
        <el-table-column prop="git_name" label="Git用户名" width="140" />
        <el-table-column prop="real_name" label="姓名" min-width="100" />
        <el-table-column prop="dept_name" label="部门" width="150" />
        <el-table-column label="团队" min-width="150">
          <template #default="{ row }">
            <span v-if="row.team_names">{{ row.team_names }}</span>
            <span v-else class="text-gray">-</span>
          </template>
        </el-table-column>
        <el-table-column label="角色" width="150">
          <template #default="{ row }">
            <el-tag :type="getRoleTagType(row)" v-for="roleId in row.role_ids" :key="roleId" size="small" class="role-tag">
              {{ getRoleName(roleId) }}
            </el-tag>
          </template>
        </el-table-column>
        <!-- <el-table-column prop="email" label="邮箱" min-width="150" /> -->
        <el-table-column prop="status" label="状态" width="200" align="center">
          <template #default="{ row }">
            <el-switch
              v-if="isAdmin && !row.role_ids.includes(1)"
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              active-text="启用"
              inactive-text="禁用"
              @change="(val) => handleStatusChange(row, val as number)"
            />
            <el-tag v-else type="success">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <!-- <el-table-column prop="created_at" label="创建时间" width="200" /> -->
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <div v-if="!row.role_ids.includes(1)">
              <el-button size="small" @click="openAssignRole(row)">分配角色</el-button>
              <el-button size="small" type="primary" @click="openEdit(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.page" v-model:page-size="query.size" :total="total" layout="total, prev, pager, next" class="mt-4" @change="loadList" />
    </el-card>

    <!-- 新建/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑用户' : '新建用户'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="账号" prop="username"><el-input v-model="form.username"  placeholder="请输入账号" :disabled="!!editId" /></el-form-item>
        <el-form-item label="Git用户名" prop="git_name"><el-input v-model="form.git_name" placeholder="请输入 Git 用户名" /></el-form-item>
        <el-form-item v-if="!editId" label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
          <div class="password-hint">密码至少8位，需包含大小写字母、数字和特殊字符</div>
        </el-form-item>
        <el-form-item label="姓名" ><el-input v-model="form.real_name"  placeholder="请输入姓名" /></el-form-item>
        <el-form-item label="部门">
          <el-select v-model="form.dept_id" placeholder="请选择部门" :disabled="!isSuperAdmin" clearable @change="handleDeptChange">
            <el-option v-for="dept in activeDepartments" :key="dept.id" :label="dept.dept_name" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="团队">
          <el-select
            v-model="form.team_id"
            :placeholder="(!isSuperAdmin || form.dept_id) ? '请选择团队（可选）' : '请先选择部门'"
            :disabled="isSuperAdmin && !form.dept_id"
            clearable
            style="width: 100%"
          >
            <el-option v-for="team in teams" :key="team.id" :label="team.team_name" :value="team.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" placeholder="请输入邮箱" /></el-form-item>
        <el-form-item label="头像">
          <div class="avatar-upload-container">
            <el-upload
              class="avatar-uploader"
              :show-file-list="false"
              :before-upload="handleAvatarUpload"
              accept="image/*"
              :disabled="uploadingAvatar"
            >
              <div v-if="form.avatar" class="avatar-preview-wrapper">
                <el-image :src="form.avatar" fit="cover" class="avatar-preview" :preview-src-list="[form.avatar]" />
                <div class="avatar-mask">
                  <el-icon :size="20"><ZoomIn /></el-icon>
                  <span class="mask-text">点击更换</span>
                </div>
              </div>
              <div v-else class="avatar-placeholder">
                <el-icon :size="32"><Plus /></el-icon>
                <div class="placeholder-text">上传头像</div>
              </div>
            </el-upload>
            <div v-if="form.avatar" class="avatar-actions">
              <el-button size="small" type="danger" plain @click="form.avatar = ''" :disabled="uploadingAvatar">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status"><el-radio :value="1">正常</el-radio><el-radio :value="0">禁用</el-radio></el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配角色弹窗 -->
    <el-dialog v-model="roleDialogVisible" title="分配角色" width="400px">
      <el-radio-group v-model="selectedRole">
        <el-radio v-for="role in filteredRoles" :key="role.id" :value="role.id">{{ role.role_name }}</el-radio>
      </el-radio-group>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignRole">确定</el-button>
      </template>
    </el-dialog>

    <!-- 批量导入弹窗 -->
    <el-dialog v-model="importDialogVisible" title="批量导入用户" width="700px">
      <div class="import-section">
        <!-- 模板下载 -->
        <div class="template-download">
          <el-link type="primary" @click="downloadTemplate" :underline="false">
            <el-icon><Download /></el-icon> 下载导入模板
          </el-link>
          <span class="hint">（支持 Excel .xlsx, .xls 和 CSV 格式）</span>
        </div>

        <!-- 文件上传 -->
        <el-upload
          ref="uploadRef"
          drag
          :auto-upload="false"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          :limit="1"
          accept=".xlsx,.xls,.csv"
          class="w-full"
        >
          <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              仅支持 Excel (.xlsx, .xls) 和 CSV 文件
            </div>
          </template>
        </el-upload>

        <!-- 数据预览 -->
        <div v-if="previewData.length > 0" class="preview-section">
          <h4>数据预览（共 {{ previewData.length }} 条）</h4>
          <el-table :data="previewData" max-height="300" border size="small">
            <el-table-column prop="username" label="账号" width="100" />
            <el-table-column prop="password" label="密码" width="100" />
            <el-table-column prop="real_name" label="姓名" width="80" />
            <el-table-column prop="dept_name" label="部门" width="120" />
            <el-table-column prop="team_name" label="团队" width="120" />
            <el-table-column prop="email" label="邮箱" min-width="150" />
          </el-table>
        </div>

        <!-- 错误提示 -->
        <div v-if="importErrors.length > 0" class="error-section">
          <h4>数据校验失败（{{ importErrors.length }} 条）</h4>
          <el-table :data="importErrors" max-height="200" border size="small">
            <el-table-column type="index" label="#" width="50" />
            <el-table-column prop="row" label="行号" width="60" />
            <el-table-column prop="data" label="数据" min-width="200" />
            <el-table-column prop="error" label="错误原因" min-width="150" />
          </el-table>
        </div>
      </div>

      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="importing" @click="handleImport" :disabled="!canImport">
          开始导入
        </el-button>
      </template>
    </el-dialog>

    <!-- 导入结果弹窗 -->
    <el-dialog v-model="resultDialogVisible" title="导入结果" width="500px">
      <div class="result-summary">
        <el-result :icon="allSuccess ? 'success' : 'warning'">
          <template #title>
            <span v-if="allSuccess">导入成功</span>
            <span v-else>部分导入成功</span>
          </template>
          <template #sub-title>
            <div class="result-stats">
              <el-tag type="success" size="large">成功 {{ successCount }} 条</el-tag>
              <el-tag type="danger" size="large" v-if="failCount > 0">失败 {{ failCount }} 条</el-tag>
            </div>
          </template>
        </el-result>
      </div>

      <!-- 失败明细 -->
      <div v-if="importResults.some(r => !r.success)" class="failure-details">
        <h4>失败明细</h4>
        <el-table :data="importResults.filter(r => !r.success)" max-height="300" border size="small">
          <el-table-column prop="data.username" label="账号" width="100" />
          <el-table-column prop="data.real_name" label="姓名" width="80" />
          <el-table-column prop="error" label="失败原因" min-width="200" />
        </el-table>
      </div>

      <template #footer>
        <el-button type="primary" @click="resultDialogVisible = false">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Download, UploadFilled, User, Plus, ZoomIn, Delete } from '@element-plus/icons-vue'
import { userApi, departmentApi, teamApi } from '@/api'
import { useUserStore } from '@/stores/user'
import type { FormInstance } from 'element-plus'
import * as XLSX from 'xlsx'
import request from '@/utils/request'

const userStore = useUserStore()
const isAdmin = computed(() => userStore.isAdmin())
const isSuperAdmin = computed(() => userStore.isSuperAdmin())

// 过滤掉超级管理员角色的列表
const filteredRoles = computed(() => {
  return allRoles.value.filter(role => role.role_code !== 'SUPER_ADMIN')
})

const loading = ref(false), submitting = ref(false)
const uploadingAvatar = ref(false)
const list = ref<any[]>([]), total = ref(0)
const dialogVisible = ref(false), editId = ref<number | null>(null)
const roleDialogVisible = ref(false), currentUserId = ref<number | null>(null)
const allRoles = ref<any[]>([]), selectedRole = ref<number | undefined>(undefined)
const formRef = ref<FormInstance>()
const query = reactive({ page: 1, size: 10, keyword: '', dept_id: null as number | null })
const form = reactive({ username: '', git_name: '', password: '', real_name: '', dept_id: null as number | null, email: '', avatar: '', status: 1, team_id: null as number | null })
const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { validator: validatePasswordComplexity, trigger: 'blur' }
  ],
}

const departments = ref<any[]>([])
const activeDepartments = ref<any[]>([])
const teams = ref<any[]>([])

async function loadDepartments() {
  try {
    const res = await departmentApi.list() as any
    departments.value = res.data || []
    // 建立部门名称到 ID 的映射
    const map: Record<string, number> = {}
    departments.value.forEach((dept: any) => {
      if (dept.dept_name) {
        map[dept.dept_name] = dept.id
      }
    })
    deptNameToIdMap.value = map
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

async function loadTeams(deptId?: number | null) {
  try {
    const res = await teamApi.list(deptId ? { deptId } : undefined) as any
    teams.value = res.data || []
    // 建立团队名称到 ID 的映射（用于批量导入校验，加载全量）
    if (!deptId) {
      const map: Record<string, number> = {}
      teams.value.forEach((team: any) => {
        if (team.team_name) map[team.team_name] = team.id
      })
      teamNameToIdMap.value = map
    }
  } catch (error) {
    console.error('加载团队列表失败', error)
  }
}

// 部门切换时清空团队并按部门重新加载
function handleDeptChange(deptId: number | null) {
  form.team_id = null
  teams.value = []
  if (deptId) {
    loadTeams(deptId)
  }
}
// 获取角色名称
function getRoleName(roleId: number): string {
  const role = allRoles.value.find(r => r.id === roleId)
  return role ? role.role_name : '未知角色'
}

// 根据角色标签类型
function getRoleTagType(row: any): 'primary' | 'success' | 'warning' | 'info' | 'danger' {
  const roleIds = row.role_ids || []
  // 超级管理员（role_id为0）- 红色
  if (roleIds.includes(1)) {
    return 'danger'
  }
  // 运营管理员（role_id为1）- 黄色
  if (roleIds.includes(2)) {
    return 'warning'
  }
  if (roleIds.includes(3)) {
    return 'success' // 部门管理员 - 绿色
  }
  
  return 'primary' // 普通用户 - 蓝色
}
function validatePasswordComplexity(_rule: any, value: string, callback: Function) {
  if (!value) {
    callback()
    return
  }
  const pattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/
  if (!pattern.test(value)) {
    callback(new Error('密码至少8位，需包含大小写字母、数字和特殊字符'))
  } else {
    callback()
  }
}

// 批量导入相关
const importDialogVisible = ref(false)
const resultDialogVisible = ref(false)
const previewData = ref<any[]>([])
const importErrors = ref<any[]>([])
const importing = ref(false)
const importResults = ref<any[]>([])
const uploadFile = ref<any>(null)
const deptNameToIdMap = ref<Record<string, number>>({})  // 部门名称 -> ID 映射
const teamNameToIdMap = ref<Record<string, number>>({})  // 团队名称 -> ID 映射

const canImport = computed(() => previewData.value.length > 0 && importErrors.value.length === 0)
const allSuccess = computed(() => importResults.value.every(r => r.success))
const successCount = computed(() => importResults.value.filter(r => r.success).length)
const failCount = computed(() => importResults.value.filter(r => !r.success).length)

function openImportDialog() {
  previewData.value = []
  importErrors.value = []
  importResults.value = []
  uploadFile.value = null
  loadTeams()  // 加载全量团队列表用于批量导入校验
  importDialogVisible.value = true
}

function downloadTemplate() {
  // 直接从 public 目录下载模板（public 目录文件通过根路径访问）
  window.open('/用户导入模板.xlsx', '_blank')
}

async function loadList() {
  loading.value = true
  try { const res = await userApi.list(query) as any; list.value = res.data.records; total.value = res.data.total }
  finally { loading.value = false }
}
async function handleAvatarUpload(file: any) {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }

  // 使用 FormData 上传文件
  const formData = new FormData()
  formData.append('file', file)

  uploadingAvatar.value = true
  try {
    const res = await request.post('/auth/avatar', formData) as any
    if (res.code === 200 && res.data) {
      // 使用 oss_url 获取完整 URL
      form.avatar = res.data.oss_url || res.data.ossUrl
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error(res.message || '头像上传失败')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '头像上传失败')
  } finally {
    uploadingAvatar.value = false
  }

  return false // 阻止 el-upload 默認上傳行为
}

function openCreate() {
  editId.value = null
  Object.assign(form, { username: '', git_name: '', password: '', real_name: '', dept_id: null, email: '', avatar: '', status: 1, team_id: null })
  teams.value = []
  loadActiveDepartments()
  // DEPT_ADMIN 部门固定，直接加载可用团队
  if (!isSuperAdmin.value) loadTeams()
  dialogVisible.value = true
}
function openEdit(row: any) {
  editId.value = row.id
  Object.assign(form, { ...row, roleIds: row.role_ids || [], team_id: row.team_id || null })
  teams.value = []
  loadActiveDepartments()
  if (row.dept_id) loadTeams(row.dept_id)
  dialogVisible.value = true
}
async function handleSubmit() {
  await formRef.value?.validate(); submitting.value = true
  try {
    if (editId.value) {
      // 编辑用户时排除 password 字段
      const { password, ...updateData } = form as any
      await userApi.update(editId.value, updateData)
    } else {
      await userApi.create(form)
    }
    ElMessage.success('操作成功'); dialogVisible.value = false; loadList()
  } finally { submitting.value = false }
}
async function handleDelete(row: any) {
  await ElMessageBox.confirm(`确认删除用户 "${row.username}"？`, '提示', { type: 'warning' })
  await userApi.delete(row.id); ElMessage.success('删除成功'); loadList()
}
async function openAssignRole(row: any) {
  // 禁止对超级管理员账号分配角色
  if (row.role_ids && row.role_ids.includes(1)) {
    ElMessage.warning('超级管理员账号不能被分配角色')
    return
  }
  currentUserId.value = row.id
  const res = await userApi.roles() as any
  allRoles.value = res.data
  // 单选：取第一个角色ID，如果没有则设为 null
  selectedRole.value = row.role_ids && row.role_ids.length > 0 ? row.role_ids[0] : null
  roleDialogVisible.value = true
}
async function handleAssignRole() {
  if (!selectedRole.value) {
    ElMessage.warning('请选择一个角色')
    return
  }
  await userApi.assignRoles(currentUserId.value!, [selectedRole.value])
  ElMessage.success('分配成功'); roleDialogVisible.value = false
  loadList()
}
async function handleStatusChange(row: any, newStatus: number) {
  try {
    await userApi.updateStatus(row.id, newStatus)
    ElMessage.success(newStatus === 1 ? '用户已启用' : '用户已禁用')
  } catch (error: any) {
    // 失败时恢复状态
    row.status = newStatus === 1 ? 0 : 1
    ElMessage.error(error.message || '操作失败')
  }
}
onMounted(async () => {
  await loadList()
  await loadDepartments()
  // 加载角色列表
  try {
    const res = await userApi.roles() as any
    allRoles.value = res.data || []
  } catch (error) {
    console.error('加载角色列表失败', error)
  }
})

function handleFileChange(file: any) {
  uploadFile.value = file
  const rawFile = file.raw as File
  previewData.value = []
  importErrors.value = []

  const reader = new FileReader()
  reader.onload = (e: ProgressEvent<FileReader>) => {
    try {
      const data = e.target?.result
      const workbook = XLSX.read(data, { type: 'array' })
      const firstSheetName = workbook.SheetNames[0]
      const worksheet = workbook.Sheets[firstSheetName]
      const jsonData = XLSX.utils.sheet_to_json(worksheet)

      if (jsonData.length === 0) {
        ElMessage.warning('文件中没有数据')
        return
      }

      // 数据校验
      const errors: any[] = []
      const validData: any[] = []

      jsonData.forEach((row: any, index: number) => {
        const rowErrors: string[] = []

        // 必填字段校验
        if (!row['账号']) rowErrors.push('缺少账号')
        if (!row['密码']) rowErrors.push('缺少密码')

        // 部门校验（如果填写了部门名称，必须能匹配到部门 ID）
        const deptName = row['部门'] ? String(row['部门']).trim() : ''
        if (deptName && !deptNameToIdMap.value[deptName]) {
          rowErrors.push(`部门"${deptName}"不存在`)
        }

        // 团队校验（如果填写了团队名称，必须能匹配到团队 ID）
        const teamName = row['团队'] ? String(row['团队']).trim() : ''
        if (teamName && !teamNameToIdMap.value[teamName]) {
          rowErrors.push(`团队"${teamName}"不存在`)
        }

        // 邮箱格式校验（如果有）
        if (row['邮箱'] && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(row['邮箱'])) {
          rowErrors.push('邮箱格式不正确')
        }

        if (rowErrors.length > 0) {
          errors.push({
            row: index + 2, // Excel 行号（从 1 开始，减去表头）
            data: row,
            error: rowErrors.join('，')
          })
        } else {
          validData.push({
            username: String(row['账号']).trim(),
            password: String(row['密码']),
            real_name: row['姓名'] ? String(row['姓名']).trim() : '',
            dept_name: deptName,  // 用于预览显示
            dept_id: deptName ? deptNameToIdMap.value[deptName] : null,  // 实际传给后端
            team_name: teamName,  // 用于预览显示
            team_id: teamName ? teamNameToIdMap.value[teamName] : null,  // 实际传给后端
            email: row['邮箱'] ? String(row['邮箱']).trim() : '',
            status: 1
          })
        }
      })

      previewData.value = validData
      importErrors.value = errors

      if (errors.length > 0) {
        ElMessage.warning(`数据校验失败：${errors.length} 条`)
      } else {
        ElMessage.success(`解析成功：${validData.length} 条数据`)
      }
    } catch (err) {
      ElMessage.error('文件解析失败，请确认文件格式正确')
      console.error(err)
    }
  }
  reader.readAsArrayBuffer(rawFile)
}

function handleFileRemove() {
  uploadFile.value = null
  previewData.value = []
  importErrors.value = []
}

async function handleImport() {
  if (previewData.value.length === 0) {
    ElMessage.warning('没有可导入的数据')
    return
  }

  // 限制单次最大导入数量
  if (previewData.value.length > 1000) {
    ElMessage.warning('单次最多导入 1000 条数据，请分批导入')
    return
  }

  importing.value = true
  try {
    const res = await userApi.batchImport(previewData.value) as any
    importResults.value = res.data || []
    resultDialogVisible.value = true
    importDialogVisible.value = false

    const success = importResults.value.filter(r => r.success).length
    const fail = importResults.value.filter(r => !r.success).length

    if (fail === 0) {
      ElMessage.success(`导入成功：${success} 条`)
    } else {
      ElMessage.warning(`导入完成：成功 ${success} 条，失败 ${fail} 条`)
    }

    // 如果有成功导入的数据，刷新列表
    if (success > 0) {
      loadList()
    }
  } catch (err: any) {
    ElMessage.error('导入失败：' + (err.message || '未知错误'))
  } finally {
    importing.value = false
  }
}
</script>

<style scoped>
.import-section {
  .template-download {
    margin-bottom: 16px;
    padding: 12px;
    background: #f5f7fa;
    border-radius: 4px;

    .hint {
      margin-left: 8px;
      color: #909399;
      font-size: 12px;
    }
  }

  .preview-section, .error-section {
    margin-top: 16px;

    h4 {
      margin: 0 0 8px 0;
      font-size: 14px;
      color: #303133;
    }
  }

  .error-section {
    margin-top: 12px;
    padding: 12px;
    background: #fef0f0;
    border-radius: 4px;
  }
}

.result-summary {
  text-align: center;
  padding: 20px 0;

  .result-stats {
    display: flex;
    justify-content: center;
    gap: 16px;
    margin-top: 16px;
  }
}

.failure-details {
  margin-top: 16px;

  h4 {
    margin: 0 0 8px 0;
    font-size: 14px;
    color: #303133;
  }
}

.password-hint {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}

.role-tag {
  margin-right: 4px;
  margin-bottom: 2px;
}

.text-gray {
  color: #909399;
}

.avatar-upload-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.avatar-uploader {
  :deep(.el-upload) {
    display: block;
    width: 100px;
    height: 100px;
    border-radius: 50%;
    overflow: hidden;
    cursor: pointer;
    border: 2px dashed #d9d9d9;
    background: #f5f7fa;
    transition: all 0.3s ease;

    &:hover {
      border-color: var(--el-color-primary);
      background: #ecf5ff;
    }
  }
}

.avatar-preview-wrapper {
  width: 100px;
  height: 100px;
  position: relative;
  border-radius: 50%;
  overflow: hidden;

  .avatar-preview {
    width: 100%;
    height: 100%;
    border-radius: 50%;
  }

  .avatar-mask {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.6);
    border-radius: 50%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.3s ease;
    color: #fff;

    .el-icon {
      margin-bottom: 4px;
    }

    .mask-text {
      font-size: 12px;
    }
  }

  &:hover .avatar-mask {
    opacity: 1;
  }
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  color: #8c939d;

  .el-icon {
    margin-bottom: 6px;
  }

  .placeholder-text {
    font-size: 12px;
  }
}

.avatar-actions {
  margin-top: 12px;
}

.table-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.default-avatar {
  color: #c0c4cc;
}
</style>
