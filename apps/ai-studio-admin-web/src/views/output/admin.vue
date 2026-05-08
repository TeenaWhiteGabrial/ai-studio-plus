<template>
  <div class="output-page">
    <el-card>
      <el-form class="filter-form" inline>
        <el-form-item label="日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :clearable="false"
            @change="loadList"
          />
        </el-form-item>
        <el-form-item label="部门">
          <el-select v-model="selectedDeptIds" multiple clearable filterable collapse-tags placeholder="选择部门" @change="handleDeptChange">
            <el-option v-for="dept in departments" :key="dept.id" :label="dept.dept_name" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="团队">
          <el-select v-model="selectedTeamIds" multiple clearable filterable collapse-tags placeholder="选择团队" @change="handleTeamChange">
            <el-option v-for="team in filteredTeams" :key="team.id" :label="team.team_name" :value="team.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="项目">
          <el-select v-model="selectedProjectNames" multiple clearable filterable collapse-tags placeholder="选择项目" @change="loadList">
            <el-option v-for="project in projectOptions" :key="project" :label="project" :value="project" />
          </el-select>
        </el-form-item>
        <el-form-item label="成员">
          <el-select v-model="selectedUserIds" multiple clearable filterable collapse-tags placeholder="指定成员" @change="loadList">
            <el-option v-for="user in filteredUsers" :key="user.id" :label="displayUser(user)" :value="user.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="指标">
          <el-segmented v-model="metricScope" :options="metricScopeOptions" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="loadList">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="resetFilters">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
          <el-button type="primary" plain @click="exportData" :loading="exporting">
            <el-icon><Download /></el-icon>
            导出
          </el-button>
        </el-form-item>
      </el-form>

      <el-table :data="list" v-loading="loading" border height="560" show-summary :summary-method="getSummaries">
        <el-table-column prop="statDate" label="日期" width="110" fixed />
        <el-table-column prop="userName" label="用户名" width="120" />
        <el-table-column prop="gitName" label="Git用户名" width="140" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="department" label="部门" width="140" />
        <el-table-column prop="teamName" label="团队" width="140" />
        <el-table-column prop="projectRootName" label="项目" min-width="160" show-overflow-tooltip />
        <el-table-column v-if="metricScope === 'all'" label="文档类" align="center">
          <el-table-column prop="prdDocCount" label="PRD文档" width="90" align="center" />
          <el-table-column prop="dataModelDocCount" label="数据模型文档" width="120" align="center" />
          <el-table-column prop="apiDocCount" label="API文档" width="90" align="center" />
        </el-table-column>
        <el-table-column v-if="metricScope === 'all' || metricScope === 'backend'" label="Java 后端" align="center">
          <el-table-column prop="javaFileCount" label="Java文件数" width="100" align="center" />
          <el-table-column prop="javaCodeLines" label="Java代码行" width="110" align="center" />
          <el-table-column prop="apiCount" label="API接口数" width="100" align="center" />
          <el-table-column prop="coreBizServiceCount" label="核心服务数" width="100" align="center" />
          <el-table-column prop="entityCount" label="实体数量" width="100" align="center" />
        </el-table-column>
        <el-table-column v-if="metricScope === 'all' || metricScope === 'frontend'" label="前端" align="center">
          <el-table-column prop="frontendComponentCount" label="组件数量" width="90" align="center" />
          <el-table-column prop="frontendPageCount" label="页面数量" width="90" align="center" />
          <el-table-column prop="frontendCommonComponentCount" label="公共组件数" width="100" align="center" />
          <el-table-column prop="tsCodeLines" label="TS代码行" width="95" align="center" />
          <el-table-column prop="frontendCodeLines" label="前端代码行" width="105" align="center" />
        </el-table-column>
        <el-table-column v-if="metricScope === 'all' || metricScope === 'backend'" label="其他" align="center">
          <el-table-column prop="sqlScriptCount" label="SQL脚本" width="85" align="center" />
          <el-table-column prop="testFileCount" label="测试文件" width="90" align="center" />
          <el-table-column prop="totalCodeLines" label="总代码行" width="105" align="center" />
        </el-table-column>
        <el-table-column prop="remark" label="备注" width="150" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { Download, Refresh, Search } from '@element-plus/icons-vue'
import { departmentApi, outputApi, teamApi, userApi } from '@/api'
import dayjs from 'dayjs'

type MetricScope = 'all' | 'frontend' | 'backend'
type NumberKey = typeof numberFields[number]

interface OutputRecord extends Record<NumberKey, number> {
  id: number
  userId: number
  userName: string
  gitName?: string
  realName: string
  statDate: string
  projectRootName: string
  department: string
  deptId?: number
  teamId?: number
  teamName: string
  remark?: string
}

interface UserOption {
  id: number
  username: string
  real_name?: string
  realName?: string
  dept_id?: number
  deptId?: number
  team_id?: number
  teamId?: number
}

const numberFields = [
  'prdDocCount',
  'dataModelDocCount',
  'apiDocCount',
  'javaFileCount',
  'javaCodeLines',
  'apiCount',
  'coreBizServiceCount',
  'entityCount',
  'frontendComponentCount',
  'frontendPageCount',
  'frontendCommonComponentCount',
  'tsCodeLines',
  'frontendCodeLines',
  'sqlScriptCount',
  'testFileCount',
  'totalCodeLines',
] as const

const loading = ref(false)
const exporting = ref(false)
const list = ref<OutputRecord[]>([])
const dateRange = ref<[string, string]>([dayjs().startOf('month').format('YYYY-MM-DD'), dayjs().format('YYYY-MM-DD')])
const selectedDeptIds = ref<number[]>([])
const selectedTeamIds = ref<number[]>([])
const selectedProjectNames = ref<string[]>([])
const selectedUserIds = ref<number[]>([])
const metricScope = ref<MetricScope>('all')
const departments = ref<any[]>([])
const teams = ref<any[]>([])
const users = ref<UserOption[]>([])
const projectOptions = ref<string[]>([])

const metricScopeOptions = [
  { label: '全部指标', value: 'all' },
  { label: '仅前端指标', value: 'frontend' },
  { label: '仅后端指标', value: 'backend' },
]

const summaryFields = computed(() => {
  if (metricScope.value === 'frontend') {
    return ['frontendComponentCount', 'frontendPageCount', 'frontendCommonComponentCount', 'tsCodeLines', 'frontendCodeLines']
  }
  if (metricScope.value === 'backend') {
    return ['javaFileCount', 'javaCodeLines', 'apiCount', 'coreBizServiceCount', 'entityCount', 'sqlScriptCount', 'testFileCount', 'totalCodeLines']
  }
  return [...numberFields]
})

const filteredTeams = computed(() => {
  if (!selectedDeptIds.value.length) return teams.value
  return teams.value.filter((team) => selectedDeptIds.value.includes(team.dept_id ?? team.deptId))
})

const filteredUsers = computed(() => users.value.filter((user) => {
  const deptId = user.dept_id ?? user.deptId
  const teamId = user.team_id ?? user.teamId
  const deptMatched = !selectedDeptIds.value.length || (deptId != null && selectedDeptIds.value.includes(deptId))
  const teamMatched = !selectedTeamIds.value.length || (teamId != null && selectedTeamIds.value.includes(teamId))
  return deptMatched && teamMatched
}))

function handleDeptChange() {
  selectedTeamIds.value = selectedTeamIds.value.filter((id) => filteredTeams.value.some((team) => team.id === id))
  selectedUserIds.value = selectedUserIds.value.filter((id) => filteredUsers.value.some((user) => user.id === id))
  loadList()
}

function handleTeamChange() {
  selectedUserIds.value = selectedUserIds.value.filter((id) => filteredUsers.value.some((user) => user.id === id))
  loadList()
}

function resetFilters() {
  dateRange.value = [dayjs().startOf('month').format('YYYY-MM-DD'), dayjs().format('YYYY-MM-DD')]
  selectedDeptIds.value = []
  selectedTeamIds.value = []
  selectedProjectNames.value = []
  selectedUserIds.value = []
  metricScope.value = 'all'
  loadList()
}

async function loadOptions() {
  const [deptRes, teamRes, userRes] = await Promise.all([
    departmentApi.active() as any,
    teamApi.list() as any,
    userApi.list({ page: 1, size: 1000 }) as any,
  ])
  departments.value = deptRes.data || []
  teams.value = teamRes.data || []
  users.value = userRes.data?.records || []
}

async function loadList() {
  loading.value = true
  try {
    const params = buildQueryParams()
    const res: any = await outputApi.dashboardDetails(params)
    list.value = (res.data || []).map(normalizeRecord)
    const projects = Array.from(new Set(list.value.map((item) => item.projectRootName).filter(Boolean))).sort()
    projectOptions.value = selectedProjectNames.value.length
      ? Array.from(new Set([...selectedProjectNames.value, ...projects])).sort()
      : projects
  } finally {
    loading.value = false
  }
}

function buildQueryParams() {
  const params: any = {
    startDate: dateRange.value[0],
    endDate: dateRange.value[1],
  }
  if (selectedDeptIds.value.length) params.deptIds = selectedDeptIds.value
  if (selectedTeamIds.value.length) params.teamIds = selectedTeamIds.value
  if (selectedProjectNames.value.length) params.projectNames = selectedProjectNames.value
  if (selectedUserIds.value.length) params.userIds = selectedUserIds.value
  return params
}

function normalizeRecord(item: any): OutputRecord {
  return {
    id: item.id,
    userId: item.user_id ?? item.userId,
    userName: item.user_name ?? item.username ?? '',
    gitName: item.git_name ?? item.gitName,
    realName: item.real_name ?? item.realName ?? '',
    statDate: item.stat_date ?? item.statDate,
    projectRootName: item.project_root_name ?? item.projectRootName ?? '未归属项目',
    department: item.department ?? item.dept_name ?? item.deptName ?? '未归属部门',
    deptId: item.dept_id ?? item.deptId,
    teamId: item.team_id ?? item.teamId,
    teamName: item.team_name ?? item.teamName ?? '未归属团队',
    remark: item.remark,
    prdDocCount: valueOf(item.prd_doc_count ?? item.prdDocCount),
    dataModelDocCount: valueOf(item.data_model_doc_count ?? item.dataModelDocCount),
    apiDocCount: valueOf(item.api_doc_count ?? item.apiDocCount),
    javaFileCount: valueOf(item.java_file_count ?? item.javaFileCount),
    javaCodeLines: valueOf(item.java_code_lines ?? item.javaCodeLines),
    apiCount: valueOf(item.api_count ?? item.apiCount),
    coreBizServiceCount: valueOf(item.core_biz_service_count ?? item.coreBizServiceCount),
    entityCount: valueOf(item.entity_count ?? item.entityCount),
    frontendComponentCount: valueOf(item.frontend_component_count ?? item.frontendComponentCount),
    frontendPageCount: valueOf(item.frontend_page_count ?? item.frontendPageCount),
    frontendCommonComponentCount: valueOf(item.frontend_common_component_count ?? item.frontendCommonComponentCount),
    tsCodeLines: valueOf(item.ts_code_lines ?? item.tsCodeLines),
    frontendCodeLines: valueOf(item.frontend_code_lines ?? item.frontendCodeLines),
    sqlScriptCount: valueOf(item.sql_script_count ?? item.sqlScriptCount),
    testFileCount: valueOf(item.test_file_count ?? item.testFileCount),
    totalCodeLines: valueOf(item.total_code_lines ?? item.totalCodeLines),
  }
}

function valueOf(value: number | null | undefined) {
  return Number(value || 0)
}

function displayUser(user: UserOption) {
  return `${user.real_name || user.realName || user.username}（${user.username}）`
}

function getSummaries(param: any) {
  const { columns, data } = param
  const sums: string[] = []
  columns.forEach((column: any, index: number) => {
    if (index === 0) {
      sums[index] = '合计'
      return
    }
    const prop = column.property
    if (prop && summaryFields.value.includes(prop)) {
      const values = data.map((item: any) => Number(item[prop] || 0))
      sums[index] = String(values.reduce((prev: number, curr: number) => prev + curr, 0))
    } else {
      sums[index] = ''
    }
  })
  return sums
}

async function exportData() {
  exporting.value = true
  try {
    const params: any = {
      type: 'detail',
      startDate: dateRange.value[0],
      endDate: dateRange.value[1],
    }
    if (selectedDeptIds.value.length) params.deptIds = selectedDeptIds.value
    if (selectedProjectNames.value.length) params.projectNames = selectedProjectNames.value
    const res: any = await outputApi.export(params)
    const blob = new Blob([res?.data ?? res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = `产出统计_全员_${dateRange.value[0]}_${dateRange.value[1]}.xlsx`
    link.click()
  } finally {
    exporting.value = false
  }
}

onMounted(async () => {
  await loadOptions()
  await loadList()
})
</script>

<style scoped>
.output-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.filter-form {
  margin-bottom: 12px;
}

.filter-form :deep(.el-select) {
  width: 190px;
}

.filter-form :deep(.el-date-editor) {
  width: 260px;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: bold;
}
</style>
