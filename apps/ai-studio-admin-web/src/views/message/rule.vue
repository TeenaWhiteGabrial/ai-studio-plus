<template>
  <div class="rule-page">
    <el-card class="group-card">
      <template #header>
        <div class="card-header">
          <span>规则组</span>
          <el-button type="primary" size="small" @click="openCreate">新建规则组</el-button>
        </div>
      </template>
      <el-table :data="groups" v-loading="loading" border @row-click="selectGroup" highlight-current-row>
        <el-table-column prop="name" label="名称" min-width="140" />
        <el-table-column prop="is_default" label="默认" width="80" align="center">
          <template #default="{ row }">{{ (row.is_default ?? row.isDefault) ? '是' : '否' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="170">
          <template #default="{ row }">
            <el-button size="small" @click.stop="openEdit(row)">编辑</el-button>
            <el-button v-if="!(row.is_default ?? row.isDefault)" size="small" type="danger" @click.stop="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card class="detail-card" v-if="currentGroup">
      <template #header>{{ currentGroup.name }}</template>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="规则开关" name="rules">
          <el-table :data="rules" border>
            <el-table-column prop="rule_type" label="规则" min-width="180">
              <template #default="{ row }">{{ ruleLabel(row.rule_type ?? row.ruleType) }}</template>
            </el-table-column>
            <el-table-column prop="enabled" label="启用" width="100" align="center">
              <template #default="{ row }"><el-switch v-model="row.enabled" :active-value="1" :inactive-value="0" /></template>
            </el-table-column>
          </el-table>
          <div class="actions"><el-button type="primary" @click="saveRules">保存规则</el-button></div>
        </el-tab-pane>
        <el-tab-pane label="适用用户" name="users">
          <el-transfer
            v-model="boundUserIds"
            filterable
            :titles="['未绑定', '已绑定']"
            :data="userOptions"
          />
          <div class="actions"><el-button type="primary" @click="saveUsers">保存用户</el-button></div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="groupForm.id ? '编辑规则组' : '新建规则组'" width="460px">
      <el-form label-width="80px">
        <el-form-item label="名称"><el-input v-model="groupForm.name" /></el-form-item>
        <el-form-item label="说明"><el-input v-model="groupForm.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="groupForm.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveGroup">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { notificationRuleApi, userApi } from '@/api'

const ruleTypes = [
  { value: 'DAILY_TASK_MISSING', label: '每日任务未录入提醒' },
  { value: 'TASK_ASSIGNED', label: '任务登记到自己名下提醒' },
  { value: 'ARTICLE_LIKED', label: '自己的文章被点赞' },
  { value: 'ARTICLE_COMMENTED', label: '自己的文章被评论' },
  { value: 'ARTICLE_TAKEN_DOWN', label: '自己的文章被下架' },
  { value: 'RESOURCE_APPROVED', label: '自己的资源审核通过' },
  { value: 'RESOURCE_TAKEN_DOWN', label: '自己的资源未通过/下架' },
]

const loading = ref(false)
const groups = ref<any[]>([])
const currentGroup = ref<any>(null)
const rules = ref<any[]>([])
const users = ref<any[]>([])
const boundUserIds = ref<number[]>([])
const activeTab = ref('rules')
const dialogVisible = ref(false)
const groupForm = reactive<any>({ id: null, name: '', description: '', status: 1 })

const userOptions = computed(() => users.value.map(user => ({
  key: user.id,
  label: `${user.real_name || user.realName || user.username}（${user.username}）`,
})))

function ruleLabel(type: string) {
  return ruleTypes.find(item => item.value === type)?.label || type
}

async function loadGroups() {
  loading.value = true
  try {
    const res: any = await notificationRuleApi.groups()
    groups.value = res.data || []
    if (!currentGroup.value && groups.value.length) await selectGroup(groups.value[0])
  } finally {
    loading.value = false
  }
}

async function selectGroup(row: any) {
  currentGroup.value = row
  const [ruleRes, userRes] = await Promise.all([
    notificationRuleApi.rules(row.id) as any,
    notificationRuleApi.users(row.id) as any,
  ])
  rules.value = ruleRes.data || []
  boundUserIds.value = userRes.data || []
}

async function loadUsers() {
  const res: any = await userApi.list({ page: 1, size: 1000 })
  users.value = res.data?.records || []
}

function openCreate() {
  Object.assign(groupForm, { id: null, name: '', description: '', status: 1 })
  dialogVisible.value = true
}

function openEdit(row: any) {
  Object.assign(groupForm, row)
  dialogVisible.value = true
}

async function saveGroup() {
  if (!groupForm.name?.trim()) {
    ElMessage.warning('请填写规则组名称')
    return
  }
  if (groupForm.id) await notificationRuleApi.updateGroup(groupForm.id, groupForm)
  else await notificationRuleApi.createGroup(groupForm)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  currentGroup.value = null
  loadGroups()
}

async function remove(row: any) {
  await ElMessageBox.confirm(`确认删除规则组「${row.name}」？`, '提示', { type: 'warning' })
  await notificationRuleApi.deleteGroup(row.id)
  ElMessage.success('删除成功')
  currentGroup.value = null
  loadGroups()
}

async function saveRules() {
  await notificationRuleApi.updateRules(currentGroup.value.id, rules.value.map(rule => ({
    rule_type: rule.rule_type ?? rule.ruleType,
    enabled: rule.enabled,
    config_json: rule.config_json ?? rule.configJson,
  })))
  ElMessage.success('规则已保存')
}

async function saveUsers() {
  await notificationRuleApi.bindUsers(currentGroup.value.id, boundUserIds.value)
  ElMessage.success('用户绑定已保存')
}

onMounted(async () => {
  await loadUsers()
  await loadGroups()
})
</script>

<style scoped>
.rule-page {
  display: grid;
  grid-template-columns: 420px minmax(0, 1fr);
  gap: 16px;
}

.card-header,
.actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.card-header {
  justify-content: space-between;
  align-items: center;
}

.actions {
  margin-top: 14px;
}

:deep(.el-transfer) {
  display: flex;
  align-items: center;
}

@media (max-width: 1100px) {
  .rule-page {
    grid-template-columns: 1fr;
  }
}
</style>
