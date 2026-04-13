<template>
  <div>
    <el-card>
      <el-form inline>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="搜索名称" clearable @keyup.enter="loadList" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width:100px">
            <el-option label="正常" :value="1" /><el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList">搜索</el-button>
          <el-button type="success" @click="openCreate">新建</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="name" label="名称" min-width="150" />
        <el-table-column prop="apiEndpoint" label="API端点" min-width="200" show-overflow-tooltip />
        <el-table-column prop="authType" label="认证类型" width="120" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleTest(row)">测试连接</el-button>
            <el-button size="small" type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination v-model:current-page="query.page" v-model:page-size="query.size"
        :total="total" layout="total, prev, pager, next" class="mt-4" @change="loadList" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editId ? '编辑 MCP' : '新建 MCP'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="API端点" prop="apiEndpoint"><el-input v-model="form.apiEndpoint" /></el-form-item>
        <el-form-item label="认证类型" prop="authType">
          <el-select v-model="form.authType">
            <el-option label="Bearer" value="Bearer" />
            <el-option label="ApiKey" value="ApiKey" />
            <el-option label="None" value="None" />
          </el-select>
        </el-form-item>
        <el-form-item label="配置信息"><el-input v-model="form.configJson" type="textarea" :rows="3" placeholder="JSON格式" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio><el-radio :value="0">禁用</el-radio>
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
import { mcpApi } from '@/api'
import type { FormInstance } from 'element-plus'

const loading = ref(false), submitting = ref(false)
const list = ref<any[]>([]), total = ref(0)
const dialogVisible = ref(false), editId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const query = reactive({ page: 1, size: 10, keyword: '', status: undefined as number | undefined })
const form = reactive({ name: '', description: '', apiEndpoint: '', authType: 'None', configJson: '', status: 1 })
const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  apiEndpoint: [{ required: true, message: '请输入API端点', trigger: 'blur' }],
  authType: [{ required: true, message: '请选择认证类型', trigger: 'change' }],
}

async function loadList() {
  loading.value = true
  try { const res = await mcpApi.list(query) as any; list.value = res.data.records; total.value = res.data.total }
  finally { loading.value = false }
}
function openCreate() { editId.value = null; Object.assign(form, { name: '', description: '', apiEndpoint: '', authType: 'None', configJson: '', status: 1 }); dialogVisible.value = true }
function openEdit(row: any) { editId.value = row.id; Object.assign(form, row); dialogVisible.value = true }
async function handleSubmit() {
  await formRef.value?.validate(); submitting.value = true
  try { editId.value ? await mcpApi.update(editId.value, form) : await mcpApi.create(form); ElMessage.success('操作成功'); dialogVisible.value = false; loadList() }
  finally { submitting.value = false }
}
async function handleDelete(row: any) {
  await ElMessageBox.confirm(`确认删除 "${row.name}"？`, '提示', { type: 'warning' })
  await mcpApi.delete(row.id); ElMessage.success('删除成功'); loadList()
}
async function handleTest(row: any) {
  const res = await mcpApi.test(row.id) as any
  const d = res.data
  ElMessage[d.status === 'success' ? 'success' : 'error'](
    d.status === 'success' ? `连接成功，响应时间 ${d.responseTime}ms` : `连接失败：${d.error}`
  )
}
onMounted(loadList)
</script>
