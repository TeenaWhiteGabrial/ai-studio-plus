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
          <el-button type="info" @click="openImportDialog">批量导入</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="username" label="账号" width="120" />
        <el-table-column prop="real_name" label="姓名" width="100" />
        <el-table-column prop="dept_name" label="部门" width="150" />
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
        <el-form-item label="账号" prop="username"><el-input v-model="form.username" :disabled="!!editId" /></el-form-item>
        <el-form-item v-if="!editId" label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password />
          <div class="password-hint">密码至少8位，需包含大小写字母、数字和特殊字符</div>
        </el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.real_name" /></el-form-item>
        <el-form-item label="部门">
          <el-select v-model="form.dept_id" placeholder="请选择部门" clearable>
            <el-option v-for="dept in activeDepartments" :key="dept.id" :label="dept.dept_name" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
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
            <el-table-column prop="department" label="部门" width="120" />
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
import { Download, UploadFilled } from '@element-plus/icons-vue'
import { userApi, departmentApi } from '@/api'
import { useUserStore } from '@/stores/user'
import type { FormInstance } from 'element-plus'
import * as XLSX from 'xlsx'

const userStore = useUserStore()
const isAdmin = computed(() => userStore.isAdmin())

// 过滤掉超级管理员角色的列表
const filteredRoles = computed(() => {
  return allRoles.value.filter(role => role.role_code !== 'SUPER_ADMIN')
})

const loading = ref(false), submitting = ref(false)
const list = ref<any[]>([]), total = ref(0)
const dialogVisible = ref(false), editId = ref<number | null>(null)
const roleDialogVisible = ref(false), currentUserId = ref<number | null>(null)
const allRoles = ref<any[]>([]), selectedRole = ref<number | undefined>(undefined)
const formRef = ref<FormInstance>()
const query = reactive({ page: 1, size: 10, keyword: '', dept_id: null as number | null })
const form = reactive({ username: '', password: '', real_name: '', dept_id: null as number | null, email: '', status: 1 })
const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { validator: validatePasswordComplexity, trigger: 'blur' }
  ],
}

const departments = ref<any[]>([])
const activeDepartments = ref<any[]>([])

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
  // 管理员（role_id为1）- 黄色
  if (roleIds.includes(2)) {
    return 'warning'
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

const canImport = computed(() => previewData.value.length > 0 && importErrors.value.length === 0)
const allSuccess = computed(() => importResults.value.every(r => r.success))
const successCount = computed(() => importResults.value.filter(r => r.success).length)
const failCount = computed(() => importResults.value.filter(r => !r.success).length)

function openImportDialog() {
  previewData.value = []
  importErrors.value = []
  importResults.value = []
  uploadFile.value = null
  importDialogVisible.value = true
}

function downloadTemplate() {
  window.open('/ai-studio-web/用户导入模板.xlsx', '_blank')
}

async function loadList() {
  loading.value = true
  try { const res = await userApi.list(query) as any; list.value = res.data.records; total.value = res.data.total }
  finally { loading.value = false }
}
function openCreate() { editId.value = null; Object.assign(form, { username: '', password: '', real_name: '', dept_id: null, email: '', status: 1 }); loadActiveDepartments(); dialogVisible.value = true }
function openEdit(row: any) { editId.value = row.id; Object.assign(form, row); loadActiveDepartments(); dialogVisible.value = true }
async function handleSubmit() {
  await formRef.value?.validate(); submitting.value = true
  try { editId.value ? await userApi.update(editId.value, form) : await userApi.create(form); ElMessage.success('操作成功'); dialogVisible.value = false; loadList() }
  finally { submitting.value = false }
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
            username: String(row['账号']),
            password: String(row['密码']),
            real_name: row['姓名'] ? String(row['姓名']) : '',
            department: row['部门'] ? String(row['部门']) : '',
            email: row['邮箱'] ? String(row['邮箱']) : '',
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
</style>
