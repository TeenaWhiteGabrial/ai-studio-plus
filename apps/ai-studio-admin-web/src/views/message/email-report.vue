<template>
  <div class="email-report-page">
    <el-card>
      <template #header>
        <div class="page-header">
          <span>邮件日报</span>
          <el-button type="primary" @click="openCreate">新建规则</el-button>
        </div>
      </template>

      <el-table :data="rules" border>
        <el-table-column prop="name" label="规则名称" min-width="150" />
        <el-table-column label="主送接收人" min-width="260">
          <template #default="{ row }">{{ recipientSummary(row.toRecipientUserIds, row.recipients) }}</template>
        </el-table-column>
        <el-table-column label="抄送接收人" min-width="220">
          <template #default="{ row }">{{ recipientSummary(row.ccRecipientUserIds, row.ccRecipients) }}</template>
        </el-table-column>
        <el-table-column label="统计成员" min-width="200">
          <template #default="{ row }">{{ memberNames(row.userIds) }}</template>
        </el-table-column>
        <el-table-column prop="sendTime" label="发送时间" width="110" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="360" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" @click="preview(row)">预览</el-button>
            <el-button size="small" @click="openLogs(row)">发送记录</el-button>
            <el-button size="small" type="primary" @click="sendNow(row)">立即发送</el-button>
            <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑日报规则' : '新建日报规则'" width="960px">
      <el-form label-width="100px">
        <el-form-item label="规则名称">
          <el-input v-model="form.name" placeholder="例如：产品研发日报" />
        </el-form-item>
        <el-form-item label="发送时间">
          <el-time-picker v-model="form.sendTime" format="HH:mm" value-format="HH:mm" placeholder="选择时间" />
        </el-form-item>
        <el-form-item label="主送接收人">
          <el-select
            v-model="form.toRecipientUserIds"
            multiple
            filterable
            collapse-tags
            collapse-tags-tooltip
            placeholder="选择主送接收人"
            class="recipient-select"
          >
            <el-option
              v-for="user in recipientUserOptions"
              :key="user.id"
              :label="user.label"
              :value="user.id"
              :disabled="!user.email || form.ccRecipientUserIds.includes(user.id)"
            />
          </el-select>
          <div class="email-hint">{{ selectedEmails(form.toRecipientUserIds) || '所选用户邮箱会自动作为主送地址' }}</div>
        </el-form-item>
        <el-form-item label="抄送接收人">
          <el-select
            v-model="form.ccRecipientUserIds"
            multiple
            filterable
            collapse-tags
            collapse-tags-tooltip
            placeholder="选择抄送接收人"
            class="recipient-select"
          >
            <el-option
              v-for="user in recipientUserOptions"
              :key="user.id"
              :label="user.label"
              :value="user.id"
              :disabled="!user.email || form.toRecipientUserIds.includes(user.id)"
            />
          </el-select>
          <div class="email-hint">{{ selectedEmails(form.ccRecipientUserIds) || '未选择抄送接收人' }}</div>
        </el-form-item>
        <el-form-item label="统计成员">
          <div class="member-tools">
            <el-select v-model="memberFilters.deptId" clearable placeholder="按部门筛选" @change="handleMemberDeptChange">
              <el-option
                v-for="dept in departments"
                :key="dept.id"
                :label="dept.dept_name || dept.deptName"
                :value="dept.id"
              />
            </el-select>
            <el-select v-model="memberFilters.teamId" clearable placeholder="按团队筛选">
              <el-option
                v-for="team in filteredTeams"
                :key="team.id"
                :label="team.team_name || team.teamName"
                :value="team.id"
              />
            </el-select>
            <el-input v-model="memberFilters.keyword" clearable placeholder="按姓名/账号筛选" />
            <el-button type="primary" plain :disabled="!memberFilters.teamId" @click="selectCurrentTeamMembers">
              选中团队所有人
            </el-button>
          </div>
          <el-transfer
            v-model="form.userIds"
            class="member-transfer"
            filterable
            :titles="['可选成员', '已选成员']"
            :data="filteredUserOptions"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="previewVisible" title="日报预览" width="900px">
      <div class="preview" v-html="previewHtml" />
    </el-dialog>

    <el-dialog v-model="sendDateVisible" title="立即发送日报" width="420px">
      <el-form label-width="90px">
        <el-form-item label="规则名称">
          <span>{{ currentSendRule?.name || '-' }}</span>
        </el-form-item>
        <el-form-item label="统计日期">
          <el-date-picker
            v-model="sendDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择统计日期"
            class="send-date-picker"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="sendDateVisible = false">取消</el-button>
        <el-button type="primary" :loading="sending" @click="confirmSendNow">发送</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="logVisible" :title="`${currentLogRuleName} - 发送记录`" width="980px">
      <el-table :data="sendLogs" border>
        <el-table-column prop="sentAt" label="发送时间" width="170" />
        <el-table-column prop="reportDate" label="日报日期" width="110" />
        <el-table-column label="触发方式" width="100">
          <template #default="{ row }">{{ row.triggerType === 'SCHEDULED' ? '定时' : '手动' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 'SUCCESS' ? 'success' : 'danger'">
              {{ row.status === 'SUCCESS' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="收件邮箱" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">{{ row.toRecipients?.join('，') || '-' }}</template>
        </el-table-column>
        <el-table-column label="抄送邮箱" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">{{ row.ccRecipients?.join('，') || '-' }}</template>
        </el-table-column>
        <el-table-column label="失败原因" min-width="240" show-overflow-tooltip>
          <template #default="{ row }">{{ row.errorMessage || '-' }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { departmentApi, emailReportApi, teamApi, userApi, type EmailReportRule, type EmailReportSendLog } from '@/api'

const rules = ref<EmailReportRule[]>([])
const sendLogs = ref<EmailReportSendLog[]>([])
const users = ref<any[]>([])
const departments = ref<any[]>([])
const teams = ref<any[]>([])
const dialogVisible = ref(false)
const previewVisible = ref(false)
const logVisible = ref(false)
const sendDateVisible = ref(false)
const previewHtml = ref('')
const currentLogRuleName = ref('')
const currentSendRule = ref<EmailReportRule | null>(null)
const sendDate = ref(todayString())
const sending = ref(false)
const form = reactive<any>({
  id: null,
  name: '',
  toRecipientUserIds: [],
  ccRecipientUserIds: [],
  userIds: [],
  sendTime: '18:00',
  status: 1,
})
const memberFilters = reactive({
  deptId: null as number | null,
  teamId: null as number | null,
  keyword: '',
})

const userOptions = computed(() => users.value.map(user => ({
  key: user.id,
  label: memberLabel(user),
})))

const filteredTeams = computed(() => teams.value.filter(team => {
  if (!memberFilters.deptId) return true
  return normalizeId(team.dept_id ?? team.deptId) === memberFilters.deptId
}))

const filteredUserOptions = computed(() => userOptions.value.filter(option => {
  const user = users.value.find(item => item.id === option.key)
  if (!user) return false
  if (memberFilters.deptId && normalizeId(user.dept_id ?? user.deptId) !== memberFilters.deptId) return false
  if (memberFilters.teamId && normalizeId(user.team_id ?? user.teamId) !== memberFilters.teamId) return false
  const keyword = memberFilters.keyword.trim().toLowerCase()
  if (!keyword) return true
  const haystack = [
    user.real_name,
    user.realName,
    user.username,
    user.email,
    user.git_name,
    user.gitName,
  ].filter(Boolean).join(' ').toLowerCase()
  return haystack.includes(keyword)
}))

const recipientUserOptions = computed(() => users.value.map(user => ({
  id: user.id,
  email: user.email || '',
  label: `${user.real_name || user.realName || user.username}（${user.email || '未配置邮箱'}）`,
})))

function normalizeId(value: any) {
  return value === null || value === undefined ? null : Number(value)
}

function todayString() {
  const now = new Date()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  return `${now.getFullYear()}-${month}-${day}`
}

function memberLabel(user: any) {
  const name = user.real_name || user.realName || user.username
  const deptName = user.dept_name || user.deptName
  const teamName = user.team_name || user.teamName
  const extra = [deptName, teamName].filter(Boolean).join(' / ')
  return extra ? `${name}（${user.username}｜${extra}）` : `${name}（${user.username}）`
}

function memberNames(ids: number[] = []) {
  const names = ids.map(id => {
    const user = users.value.find(item => item.id === id)
    return user?.real_name || user?.realName || user?.username
  }).filter(Boolean)
  return names.join('，') || '-'
}

function selectedEmails(ids: number[] = []) {
  return selectedEmailList(ids).join('，')
}

function selectedEmailList(ids: number[] = []) {
  return ids.map(id => users.value.find(item => item.id === id)?.email).filter(Boolean)
}

function allSelectedHaveEmail(ids: number[] = []) {
  return ids.every(id => Boolean(users.value.find(item => item.id === id)?.email))
}

function recipientSummary(ids: number[] = [], fallbackEmails: string[] = []) {
  const names = ids.map(id => {
    const user = users.value.find(item => item.id === id)
    if (!user) return ''
    const name = user.real_name || user.realName || user.username
    return user.email ? `${name}（${user.email}）` : `${name}（未配置邮箱）`
  }).filter(Boolean)
  if (names.length) return names.join('，')
  return fallbackEmails?.length ? fallbackEmails.join('，') : '-'
}

async function loadRules() {
  const res: any = await emailReportApi.rules()
  rules.value = res.data || []
}

async function loadUsers() {
  const res: any = await userApi.list({ page: 1, size: 1000 })
  users.value = res.data?.records || []
}

async function loadDepartments() {
  const res: any = await departmentApi.active()
  departments.value = res.data || []
}

async function loadTeams() {
  const res: any = await teamApi.list()
  teams.value = res.data || []
}

function handleMemberDeptChange() {
  if (memberFilters.teamId && !filteredTeams.value.some(team => team.id === memberFilters.teamId)) {
    memberFilters.teamId = null
  }
}

function selectCurrentTeamMembers() {
  if (!memberFilters.teamId) return
  const teamMemberIds = users.value
    .filter(user => normalizeId(user.team_id ?? user.teamId) === memberFilters.teamId)
    .map(user => user.id)
  form.userIds = Array.from(new Set([...form.userIds, ...teamMemberIds]))
  ElMessage.success(`已选中 ${teamMemberIds.length} 名团队成员`)
}

function openCreate() {
  Object.assign(form, { id: null, name: '产品研发日报', toRecipientUserIds: [], ccRecipientUserIds: [], userIds: [], sendTime: '18:00', status: 1 })
  Object.assign(memberFilters, { deptId: null, teamId: null, keyword: '' })
  dialogVisible.value = true
}

function openEdit(row: any) {
  Object.assign(form, {
    id: row.id,
    name: row.name,
    toRecipientUserIds: row.toRecipientUserIds || [],
    ccRecipientUserIds: row.ccRecipientUserIds || [],
    userIds: row.userIds || [],
    sendTime: row.sendTime || '18:00',
    status: row.status,
  })
  dialogVisible.value = true
}

async function save() {
  const payload = {
    name: form.name,
    toRecipientUserIds: form.toRecipientUserIds,
    ccRecipientUserIds: form.ccRecipientUserIds,
    recipients: selectedEmailList(form.toRecipientUserIds),
    userIds: form.userIds,
    sendTime: form.sendTime,
    status: form.status,
  }
  if (!payload.name?.trim()) return ElMessage.warning('请填写规则名称')
  if (!payload.toRecipientUserIds.length) return ElMessage.warning('请选择主送接收人')
  if (!allSelectedHaveEmail(payload.toRecipientUserIds)) return ElMessage.warning('主送接收人未配置邮箱')
  if (!allSelectedHaveEmail(payload.ccRecipientUserIds)) return ElMessage.warning('抄送接收人未配置邮箱')
  if (payload.toRecipientUserIds.some(id => payload.ccRecipientUserIds.includes(id))) return ElMessage.warning('主送和抄送接收人不能重复')
  if (!payload.userIds.length) return ElMessage.warning('请选择统计成员')
  if (form.id) await emailReportApi.updateRule(form.id, payload)
  else await emailReportApi.createRule(payload)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  await loadRules()
}

async function preview(row: any) {
  const res: any = await emailReportApi.preview(row.id)
  previewHtml.value = res.data || ''
  previewVisible.value = true
}

async function openLogs(row: EmailReportRule) {
  currentLogRuleName.value = row.name
  const res: any = await emailReportApi.logs(row.id!)
  sendLogs.value = res.data || []
  logVisible.value = true
}

function sendNow(row: EmailReportRule) {
  currentSendRule.value = row
  sendDate.value = todayString()
  sendDateVisible.value = true
}

async function confirmSendNow() {
  if (!currentSendRule.value?.id) return
  if (!sendDate.value) return ElMessage.warning('请选择统计日期')
  sending.value = true
  try {
    await emailReportApi.send(currentSendRule.value.id, sendDate.value)
    ElMessage.success('发送成功')
    sendDateVisible.value = false
    await loadRules()
  } catch (error: any) {
    ElMessage.error(`发送失败：${error?.message || '请查看发送记录'}`)
    await loadRules()
  } finally {
    sending.value = false
  }
}

async function remove(row: any) {
  await ElMessageBox.confirm(`确认删除规则「${row.name}」？`, '提示', { type: 'warning' })
  await emailReportApi.deleteRule(row.id)
  ElMessage.success('删除成功')
  await loadRules()
}

onMounted(async () => {
  await Promise.all([loadDepartments(), loadTeams(), loadUsers()])
  await loadRules()
})
</script>

<style scoped>
.email-report-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.preview {
  max-height: 70vh;
  overflow: auto;
}

.recipient-select {
  width: 100%;
}

.send-date-picker {
  width: 100%;
}

.email-hint {
  margin-top: 6px;
  color: #6b7280;
  font-size: 12px;
  line-height: 1.5;
}

.member-tools {
  display: grid;
  grid-template-columns: minmax(120px, 1fr) minmax(120px, 1fr) minmax(160px, 1.2fr) auto;
  gap: 8px;
  width: 100%;
  margin-bottom: 10px;
}

.member-transfer {
  display: flex;
  align-items: stretch;
  gap: 12px;
  width: 100%;
}

.member-transfer :deep(.el-transfer-panel) {
  flex: 1 1 0;
  width: auto;
  min-width: 0;
}

.member-transfer :deep(.el-transfer__buttons) {
  display: flex;
  flex: 0 0 auto;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 8px;
  padding: 0;
}

.member-transfer :deep(.el-transfer__button) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 32px;
  min-width: 36px;
  margin: 0;
  padding: 0;
}

.member-transfer :deep(.el-transfer__button + .el-transfer__button) {
  margin-left: 0;
}
</style>
