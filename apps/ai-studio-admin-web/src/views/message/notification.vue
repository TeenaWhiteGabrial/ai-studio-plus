<template>
  <el-card>
    <el-form inline>
      <el-form-item label="用户">
        <el-select v-model="query.userId" filterable clearable placeholder="全部用户" style="width: 180px">
          <el-option v-for="user in users" :key="user.id" :label="displayUser(user)" :value="user.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="query.type" clearable placeholder="全部" style="width: 190px">
          <el-option v-for="item in ruleTypes" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.isRead" clearable placeholder="全部" style="width: 120px">
          <el-option label="未读" :value="0" />
          <el-option label="已读" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" @click="loadList">查询</el-button></el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="title" label="标题" min-width="160" show-overflow-tooltip />
      <el-table-column prop="content" label="内容" min-width="260" show-overflow-tooltip />
      <el-table-column prop="type" label="类型" width="170">
        <template #default="{ row }">{{ ruleLabel(row.type) }}</template>
      </el-table-column>
      <el-table-column prop="user_id" label="接收人" width="140">
        <template #default="{ row }">{{ userName(row.user_id ?? row.userId) }}</template>
      </el-table-column>
      <el-table-column prop="is_read" label="状态" width="90">
        <template #default="{ row }"><el-tag :type="(row.is_read ?? row.isRead) ? 'info' : 'warning'">{{ (row.is_read ?? row.isRead) ? '已读' : '未读' }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="created_at" label="创建时间" width="180" />
    </el-table>
    <el-pagination v-model:current-page="query.page" v-model:page-size="query.size" :total="total" layout="total, prev, pager, next" class="mt-4" @change="loadList" />
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { notificationApi, userApi } from '@/api'

const ruleTypes = [
  { value: 'DAILY_TASK_MISSING', label: '每日任务未录入' },
  { value: 'TASK_ASSIGNED', label: '任务登记提醒' },
  { value: 'ARTICLE_LIKED', label: '文章点赞' },
  { value: 'ARTICLE_COMMENTED', label: '文章评论' },
  { value: 'ARTICLE_TAKEN_DOWN', label: '文章下架' },
  { value: 'RESOURCE_APPROVED', label: '资源审核通过' },
  { value: 'RESOURCE_TAKEN_DOWN', label: '资源未通过/下架' },
]

const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const users = ref<any[]>([])
const query = reactive<any>({ page: 1, size: 10, userId: null, type: '', isRead: null })

function ruleLabel(type: string) {
  return ruleTypes.find(item => item.value === type)?.label || type
}

function displayUser(user: any) {
  return `${user.real_name || user.realName || user.username}（${user.username}）`
}

function userName(id: number) {
  const user = users.value.find(item => item.id === id)
  return user ? (user.real_name || user.realName || user.username) : id
}

async function loadUsers() {
  const res: any = await userApi.list({ page: 1, size: 1000 })
  users.value = res.data?.records || []
}

async function loadList() {
  loading.value = true
  try {
    const res: any = await notificationApi.list(query)
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadUsers()
  await loadList()
})
</script>
