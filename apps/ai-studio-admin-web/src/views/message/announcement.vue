<template>
  <div>
    <el-card>
      <el-form inline>
        <el-form-item label="关键词"><el-input v-model="query.keyword" clearable placeholder="公告标题" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部" style="width: 140px">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已发布" value="PUBLISHED" />
            <el-option label="已下线" value="OFFLINE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList">查询</el-button>
          <el-button type="success" @click="openCreate">新建公告</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="pinned" label="置顶" width="80" align="center">
          <template #default="{ row }">{{ row.pinned ? '是' : '否' }}</template>
        </el-table-column>
        <el-table-column prop="published_at" label="发布时间" width="180" />
        <el-table-column prop="expired_at" label="过期时间" width="180" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button v-if="row.status !== 'PUBLISHED'" size="small" type="primary" @click="publish(row)">发布</el-button>
            <el-button v-if="row.status === 'PUBLISHED'" size="small" @click="offline(row)">下线</el-button>
            <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="query.page" v-model:page-size="query.size" :total="total" layout="total, prev, pager, next" class="mt-4" @change="loadList" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑公告' : '新建公告'" width="680px">
      <el-form label-width="90px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="8" /></el-form-item>
        <el-form-item label="置顶"><el-switch v-model="form.pinned" :active-value="1" :inactive-value="0" /></el-form-item>
        <el-form-item label="过期时间">
          <el-date-picker v-model="form.expired_at" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { announcementApi } from '@/api'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const query = reactive({ page: 1, size: 10, keyword: '', status: '' })
const form = reactive<any>({ id: null, title: '', content: '', pinned: 0, expired_at: null })

function statusText(status: string) {
  return ({ DRAFT: '草稿', PUBLISHED: '已发布', OFFLINE: '已下线' } as Record<string, string>)[status] || status
}

function statusType(status: string) {
  return status === 'PUBLISHED' ? 'success' : status === 'OFFLINE' ? 'info' : 'warning'
}

async function loadList() {
  loading.value = true
  try {
    const res: any = await announcementApi.list(query)
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

function openCreate() {
  Object.assign(form, { id: null, title: '', content: '', pinned: 0, expired_at: null })
  dialogVisible.value = true
}

function openEdit(row: any) {
  Object.assign(form, row)
  dialogVisible.value = true
}

async function save() {
  if (!form.title?.trim() || !form.content?.trim()) {
    ElMessage.warning('请填写标题和内容')
    return
  }
  saving.value = true
  try {
    if (form.id) await announcementApi.update(form.id, form)
    else await announcementApi.create({ ...form, status: 'DRAFT' })
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadList()
  } finally {
    saving.value = false
  }
}

async function publish(row: any) {
  await announcementApi.publish(row.id)
  ElMessage.success('发布成功')
  loadList()
}

async function offline(row: any) {
  await announcementApi.offline(row.id)
  ElMessage.success('下线成功')
  loadList()
}

async function remove(row: any) {
  await ElMessageBox.confirm(`确认删除公告「${row.title}」？`, '提示', { type: 'warning' })
  await announcementApi.delete(row.id)
  ElMessage.success('删除成功')
  loadList()
}

onMounted(loadList)
</script>
