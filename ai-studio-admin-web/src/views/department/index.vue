<template>
  <div>
    <el-card>
      <el-form inline>
        <el-form-item>
          <el-button type="success" @click="openCreate">新建部门</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="dept_name" label="部门名称" min-width="150" />
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="created_at" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" :type="row.status === 1 ? 'danger' : 'success'" @click="toggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新建/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editId ? '编辑部门' : '新建部门'" width="400px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="部门名称" prop="dept_name">
          <el-input v-model="form.dept_name" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { departmentApi } from '@/api'
import type { FormInstance } from 'element-plus'

const loading = ref(false), submitting = ref(false)
const list = ref<any[]>([])
const dialogVisible = ref(false), editId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const form = reactive({ dept_name: '', sort: 0, status: 1 })
const rules = {
  dept_name: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
}

async function loadList() {
  loading.value = true
  try {
    const res = await departmentApi.list() as any
    list.value = res.data || []
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editId.value = null
  Object.assign(form, { id: undefined, dept_name: '', sort: 0, status: 1 })
  dialogVisible.value = true
}

function openEdit(row: any) {
  editId.value = row.id
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitting.value = true
  try {
    if (editId.value) {
      await departmentApi.update(editId.value, form)
    } else {
      await departmentApi.create(form)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadList()
  } finally {
    submitting.value = false
  }
}

async function toggleStatus(row: any) {
  const newStatus = row.status === 1 ? 0 : 1
  const actionText = newStatus === 1 ? '启用' : '禁用'
  try {
    await ElMessageBox.confirm(`确认${actionText}部门 "${row.dept_name}"？`, '提示', { type: 'warning' })
    await departmentApi.update(row.id, { ...row, status: newStatus })
    ElMessage.success(`${actionText}成功`)
    loadList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  }
}

onMounted(loadList)
</script>
