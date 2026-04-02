<template>
  <div>
    <el-card>
      <el-form inline>
        <el-form-item label="关键词"><el-input v-model="query.keyword" clearable @keyup.enter="loadList" /></el-form-item>
        <el-form-item label="分类"><el-input v-model="query.category" clearable style="width:120px" /></el-form-item>
        <el-form-item label="标签"><el-input v-model="query.tag" clearable style="width:120px" /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList">搜索</el-button>
          <el-button type="success" @click="openCreate">新建教程</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="tags" label="标签" width="150" show-overflow-tooltip />
        <el-table-column prop="viewCount" label="浏览次数" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="router.push(`/tutorial/${row.id}/read`)">阅读</el-button>
            <el-button size="small" type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.page" v-model:page-size="query.size" :total="total" layout="total, prev, pager, next" class="mt-4" @change="loadList" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editId ? '编辑教程' : '新建教程'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="分类"><el-input v-model="form.category" /></el-form-item>
        <el-form-item label="标签"><el-input v-model="form.tags" placeholder="逗号分隔" /></el-form-item>
        <el-form-item label="Markdown文件">
          <el-upload :before-upload="handleFileUpload" :show-file-list="false" accept=".md">
            <el-button>{{ form.contentUrl ? '重新上传' : '上传文件' }}</el-button>
            <span v-if="form.contentUrl" class="ml-2 text-green-500">已上传</span>
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { tutorialApi } from '@/api'
import { uploadToOss } from '@/utils/oss'
import type { FormInstance } from 'element-plus'

const router = useRouter()
const loading = ref(false), submitting = ref(false)
const list = ref<any[]>([]), total = ref(0)
const dialogVisible = ref(false), editId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const query = reactive({ page: 1, size: 10, keyword: '', category: '', tag: '', status: undefined as number | undefined })
const form = reactive({ title: '', category: '', tags: '', contentOssKey: '', contentUrl: '', status: 1 })
const rules = { title: [{ required: true, message: '请输入标题', trigger: 'blur' }] }

async function loadList() {
  loading.value = true
  try { const res = await tutorialApi.list(query) as any; list.value = res.data.records; total.value = res.data.total }
  finally { loading.value = false }
}
function openCreate() { editId.value = null; Object.assign(form, { title: '', category: '', tags: '', contentOssKey: '', contentUrl: '', status: 1 }); dialogVisible.value = true }
function openEdit(row: any) { editId.value = row.id; Object.assign(form, row); dialogVisible.value = true }
async function handleFileUpload(file: File) {
  const result = await uploadToOss(file, 'tutorials/')
  form.contentOssKey = result.ossKey; form.contentUrl = result.url
  return false
}
async function handleSubmit() {
  await formRef.value?.validate(); submitting.value = true
  try { editId.value ? await tutorialApi.update(editId.value, form) : await tutorialApi.create(form); ElMessage.success('操作成功'); dialogVisible.value = false; loadList() }
  finally { submitting.value = false }
}
async function handleDelete(row: any) {
  await ElMessageBox.confirm(`确认删除 "${row.title}"？`, '提示', { type: 'warning' })
  await tutorialApi.delete(row.id); ElMessage.success('删除成功'); loadList()
}
onMounted(loadList)
</script>
