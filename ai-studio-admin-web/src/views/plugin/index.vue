<template>
  <div>
    <el-card>
      <el-form inline>
        <el-form-item label="关键词"><el-input v-model="query.keyword" clearable @keyup.enter="loadList" /></el-form-item>
        <el-form-item label="类型"><el-input v-model="query.type" clearable style="width:120px" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width:100px">
            <el-option label="正常" :value="1" /><el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList">搜索</el-button>
          <el-button type="success" @click="openCreate">上传插件</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="name" label="名称" min-width="150" />
        <el-table-column prop="type" label="类型" width="100" />
        <el-table-column prop="version" label="版本" width="100" />
        <el-table-column prop="fileSize" label="大小" width="100">
          <template #default="{ row }">{{ row.fileSize ? (row.fileSize / 1024 / 1024).toFixed(2) + 'MB' : '-' }}</template>
        </el-table-column>
        <el-table-column prop="downloadCount" label="下载次数" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleDownload(row)">下载</el-button>
            <el-button size="small" type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.page" v-model:page-size="query.size" :total="total" layout="total, prev, pager, next" class="mt-4" @change="loadList" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editId ? '编辑插件' : '上传插件'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="类型"><el-input v-model="form.type" placeholder="editor/build/lint" /></el-form-item>
        <el-form-item label="版本" prop="version"><el-input v-model="form.version" placeholder="1.0.0" /></el-form-item>
        <el-form-item label="插件文件">
          <el-upload :before-upload="handleFileUpload" :show-file-list="false">
            <el-button>{{ form.fileUrl ? '重新上传' : '上传文件' }}</el-button>
            <span v-if="form.fileUrl" class="ml-2 text-green-500">已上传</span>
          </el-upload>
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pluginApi } from '@/api'
import { uploadToOss } from '@/utils/oss'
import type { FormInstance } from 'element-plus'

const loading = ref(false), submitting = ref(false)
const list = ref<any[]>([]), total = ref(0)
const dialogVisible = ref(false), editId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const query = reactive({ page: 1, size: 10, keyword: '', type: '', status: undefined as number | undefined })
const form = reactive({ name: '', description: '', type: '', version: '', fileOssKey: '', fileUrl: '', fileSize: 0, status: 1 })
const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  version: [{ required: true, message: '请输入版本号', trigger: 'blur' }],
}

async function loadList() {
  loading.value = true
  try { const res = await pluginApi.list(query) as any; list.value = res.data.records; total.value = res.data.total }
  finally { loading.value = false }
}
function openCreate() { editId.value = null; Object.assign(form, { name: '', description: '', type: '', version: '', fileOssKey: '', fileUrl: '', fileSize: 0, status: 1 }); dialogVisible.value = true }
function openEdit(row: any) { editId.value = row.id; Object.assign(form, row); dialogVisible.value = true }
async function handleFileUpload(file: File) {
  const result = await uploadToOss(file, 'plugins/')
  form.fileOssKey = result.ossKey; form.fileUrl = result.url; form.fileSize = file.size
  return false
}
async function handleSubmit() {
  await formRef.value?.validate(); submitting.value = true
  try { editId.value ? await pluginApi.update(editId.value, form) : await pluginApi.create(form); ElMessage.success('操作成功'); dialogVisible.value = false; loadList() }
  finally { submitting.value = false }
}
async function handleDelete(row: any) {
  await ElMessageBox.confirm(`确认删除 "${row.name}"？`, '提示', { type: 'warning' })
  await pluginApi.delete(row.id); ElMessage.success('删除成功'); loadList()
}
async function handleDownload(row: any) {
  const res = await pluginApi.download(row.id) as any; window.open(res.data, '_blank')
}
onMounted(loadList)
</script>
