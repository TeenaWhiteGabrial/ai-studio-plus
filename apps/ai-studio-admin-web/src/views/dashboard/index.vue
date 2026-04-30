<template>
  <div class="dashboard-page">
    <section class="filter-panel">
      <div class="filter-title">
        <div class="eyebrow">Admin Analytics</div>
        <h2>数据看板</h2>
        <p>{{ dateRange[0] }} 至 {{ dateRange[1] }} · {{ records.length }} 条产出记录</p>
      </div>

      <div class="filter-grid">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          value-format="YYYY-MM-DD"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :clearable="false"
        />
        <el-select v-model="selectedDeptIds" multiple clearable collapse-tags placeholder="部门" @change="handleDeptChange">
          <el-option v-for="dept in departments" :key="dept.id" :label="dept.dept_name" :value="dept.id" />
        </el-select>
        <el-select v-model="selectedTeamIds" multiple clearable collapse-tags placeholder="团队">
          <el-option v-for="team in filteredTeams" :key="team.id" :label="team.team_name" :value="team.id" />
        </el-select>
        <el-select v-model="selectedProjectNames" multiple clearable filterable collapse-tags placeholder="项目">
          <el-option v-for="project in projectOptions" :key="project" :label="project" :value="project" />
        </el-select>
        <el-select v-model="selectedUserIds" multiple clearable filterable collapse-tags placeholder="随机或指定成员">
          <el-option
            v-for="user in filteredUsers"
            :key="user.id"
            :label="displayUser(user)"
            :value="user.id"
          />
        </el-select>
        <el-segmented v-model="metricScope" :options="metricScopeOptions" />
      </div>

      <div class="filter-actions">
        <el-button @click="pickRandomUsers">
          <el-icon><User /></el-icon>
          随机选择 5 人
        </el-button>
        <el-button @click="resetFilters">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
        <el-button type="primary" :loading="loading" @click="loadDashboard">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
      </div>
    </section>

    <section class="metric-grid">
      <div v-for="card in metricCards" :key="card.label" class="metric-card">
        <div class="metric-label">{{ card.label }}</div>
        <div class="metric-value">{{ formatNumber(card.value) }}</div>
        <div class="metric-help">{{ card.help }}</div>
      </div>
    </section>

    <el-tabs v-model="activeTab" class="dashboard-tabs">
      <el-tab-pane label="综合概览" name="overview">
        <section class="chart-grid overview-grid">
          <div class="panel wide">
            <div class="panel-header">
              <h3>{{ metricScope === 'frontend' ? '前端产出趋势' : '产出趋势' }}</h3>
              <span>按时间聚合</span>
            </div>
            <v-chart v-if="records.length" class="chart large" :option="trendOption" autoresize />
            <el-empty v-else description="暂无数据" />
          </div>
          <div class="panel">
            <div class="panel-header">
              <h3>指标扇形图</h3>
              <span>{{ metricScopeLabel }}</span>
            </div>
            <v-chart v-if="records.length" class="chart" :option="metricRoseOption" autoresize />
            <el-empty v-else description="暂无数据" />
          </div>
          <div class="panel">
            <div class="panel-header">
              <h3>指标柱状图</h3>
              <span>{{ metricScopeLabel }}</span>
            </div>
            <v-chart v-if="records.length" class="chart" :option="metricBarOption" autoresize />
            <el-empty v-else description="暂无数据" />
          </div>
          <div class="panel">
            <div class="panel-header">
              <h3>项目饼图</h3>
              <span>按 {{ primaryMetricLabel }}</span>
            </div>
            <v-chart v-if="projectSummaries.length" class="chart" :option="projectPieOption" autoresize />
            <el-empty v-else description="暂无项目数据" />
          </div>
        </section>
      </el-tab-pane>

      <el-tab-pane label="部门 / 团队" name="org">
        <section class="chart-grid">
          <div class="panel wide">
            <div class="panel-header">
              <h3>部门排行</h3>
              <span>按 {{ primaryMetricLabel }}</span>
            </div>
            <v-chart v-if="departmentSummaries.length" class="chart large" :option="departmentBarOption" autoresize />
            <el-empty v-else description="暂无部门数据" />
          </div>
          <div class="panel">
            <div class="panel-header">
              <h3>团队排行</h3>
              <span>按 {{ primaryMetricLabel }}</span>
            </div>
            <v-chart v-if="teamSummaries.length" class="chart" :option="teamBarOption" autoresize />
            <el-empty v-else description="暂无团队数据" />
          </div>
          <div class="panel table-panel">
            <div class="panel-header">
              <h3>组织汇总</h3>
              <span>部门与团队</span>
            </div>
            <el-table :data="orgTableData" border height="320">
              <el-table-column prop="type" label="类型" width="80" />
              <el-table-column prop="name" label="名称" min-width="140" show-overflow-tooltip />
              <el-table-column prop="memberCount" label="人数" width="80" align="center" />
              <el-table-column :prop="primaryMetricKey" :label="primaryMetricLabel" width="130" align="center" sortable />
            </el-table>
          </div>
        </section>
      </el-tab-pane>

      <el-tab-pane label="项目 / 人员" name="projectUser">
        <section class="chart-grid">
          <div class="panel wide">
            <div class="panel-header">
              <h3>项目排行</h3>
              <span>按 {{ primaryMetricLabel }}</span>
            </div>
            <v-chart v-if="projectSummaries.length" class="chart large" :option="projectBarOption" autoresize />
            <el-empty v-else description="暂无项目数据" />
          </div>
          <div class="panel">
            <div class="panel-header">
              <h3>人员排行</h3>
              <span>可随机选择人员查看</span>
            </div>
            <v-chart v-if="userSummaries.length" class="chart" :option="userBarOption" autoresize />
            <el-empty v-else description="暂无人员数据" />
          </div>
          <div class="panel table-panel">
            <div class="panel-header">
              <h3>人员明细</h3>
              <span>{{ selectedUserIds.length ? '已筛选人员' : '全部人员' }}</span>
            </div>
            <el-table :data="userSummaries" border height="320">
              <el-table-column prop="name" label="姓名" min-width="110" show-overflow-tooltip />
              <el-table-column prop="department" label="部门" min-width="120" show-overflow-tooltip />
              <el-table-column prop="teamName" label="团队" min-width="120" show-overflow-tooltip />
              <el-table-column :prop="primaryMetricKey" :label="primaryMetricLabel" width="130" align="center" sortable />
            </el-table>
          </div>
        </section>
      </el-tab-pane>

      <el-tab-pane label="明细数据" name="details">
        <section class="panel detail-panel">
          <div class="panel-header">
            <h3>产出明细</h3>
            <span>{{ metricScopeLabel }}</span>
          </div>
          <el-table :data="records" v-loading="loading" border height="560">
            <el-table-column prop="statDate" label="日期" width="110" fixed />
            <el-table-column prop="userName" label="用户名" width="120" />
            <el-table-column prop="realName" label="姓名" width="110" />
            <el-table-column prop="department" label="部门" width="130" />
            <el-table-column prop="teamName" label="团队" width="130" />
            <el-table-column prop="projectRootName" label="项目" min-width="160" show-overflow-tooltip />
            <el-table-column v-if="metricScope === 'all'" label="文档" align="center">
              <el-table-column prop="prdDocCount" label="PRD" width="80" align="center" />
              <el-table-column prop="dataModelDocCount" label="数据模型" width="100" align="center" />
              <el-table-column prop="apiDocCount" label="API 文档" width="100" align="center" />
            </el-table-column>
            <el-table-column v-if="metricScope === 'all'" label="后端" align="center">
              <el-table-column prop="javaCodeLines" label="Java 行数" width="110" align="center" />
              <el-table-column prop="apiCount" label="接口" width="80" align="center" />
              <el-table-column prop="coreBizServiceCount" label="服务" width="80" align="center" />
              <el-table-column prop="entityCount" label="实体" width="80" align="center" />
            </el-table-column>
            <el-table-column label="前端" align="center">
              <el-table-column prop="frontendComponentCount" label="组件" width="80" align="center" />
              <el-table-column prop="frontendPageCount" label="页面" width="80" align="center" />
              <el-table-column prop="frontendCommonComponentCount" label="公共组件" width="100" align="center" />
              <el-table-column prop="tsCodeLines" label="TS/JS 行数" width="110" align="center" />
              <el-table-column prop="frontendCodeLines" label="前端行数" width="110" align="center" />
            </el-table-column>
            <el-table-column v-if="metricScope === 'all'" label="其他" align="center">
              <el-table-column prop="sqlScriptCount" label="SQL" width="80" align="center" />
              <el-table-column prop="testFileCount" label="测试" width="80" align="center" />
              <el-table-column prop="totalCodeLines" label="总代码" width="110" align="center" />
            </el-table-column>
          </el-table>
        </section>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import dayjs from 'dayjs'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { BarChart, LineChart, PieChart } from 'echarts/charts'
import { GridComponent, LegendComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { Refresh, Search, User } from '@element-plus/icons-vue'
import { departmentApi, outputApi, teamApi, userApi } from '@/api'

use([BarChart, CanvasRenderer, GridComponent, LegendComponent, LineChart, PieChart, TooltipComponent])

type MetricScope = 'all' | 'frontend'
type NumberKey = typeof numberFields[number]

interface OutputRecord {
  id: number
  userId: number
  userName: string
  realName: string
  gitName?: string
  statDate: string
  projectRootName: string
  department: string
  deptId?: number
  teamId?: number
  teamName: string
  prdDocCount: number
  dataModelDocCount: number
  apiDocCount: number
  javaFileCount: number
  javaCodeLines: number
  apiCount: number
  coreBizServiceCount: number
  entityCount: number
  frontendComponentCount: number
  frontendPageCount: number
  frontendCommonComponentCount: number
  tsCodeLines: number
  frontendCodeLines: number
  sqlScriptCount: number
  testFileCount: number
  totalCodeLines: number
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

interface SummaryItem extends Record<NumberKey, number> {
  id?: number
  name: string
  department?: string
  teamName?: string
  memberIds: Set<number>
  memberCount: number
  recordCount: number
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

const allMetricDefs = [
  { key: 'prdDocCount', label: 'PRD 文档' },
  { key: 'dataModelDocCount', label: '数据模型' },
  { key: 'apiDocCount', label: 'API 文档' },
  { key: 'javaCodeLines', label: 'Java 行数' },
  { key: 'apiCount', label: '接口数' },
  { key: 'coreBizServiceCount', label: '核心服务' },
  { key: 'frontendComponentCount', label: '前端组件' },
  { key: 'frontendPageCount', label: '前端页面' },
  { key: 'tsCodeLines', label: 'TS/JS 行数' },
  { key: 'frontendCodeLines', label: '前端行数' },
  { key: 'sqlScriptCount', label: 'SQL 脚本' },
  { key: 'testFileCount', label: '测试文件' },
] as const

const frontendMetricDefs = [
  { key: 'frontendComponentCount', label: '前端组件' },
  { key: 'frontendPageCount', label: '前端页面' },
  { key: 'frontendCommonComponentCount', label: '公共组件' },
  { key: 'tsCodeLines', label: 'TS/JS 行数' },
  { key: 'frontendCodeLines', label: '前端行数' },
] as const

const palette = ['#2563eb', '#16a34a', '#f59e0b', '#7c3aed', '#db2777', '#0891b2', '#ea580c']

const loading = ref(false)
const activeTab = ref('overview')
const metricScope = ref<MetricScope>('all')
const dateRange = ref<[string, string]>([dayjs().startOf('month').format('YYYY-MM-DD'), dayjs().format('YYYY-MM-DD')])
const selectedDeptIds = ref<number[]>([])
const selectedTeamIds = ref<number[]>([])
const selectedProjectNames = ref<string[]>([])
const selectedUserIds = ref<number[]>([])
const records = ref<OutputRecord[]>([])
const departments = ref<any[]>([])
const teams = ref<any[]>([])
const users = ref<UserOption[]>([])
const projectOptions = ref<string[]>([])

const metricScopeOptions = [
  { label: '全部指标', value: 'all' },
  { label: '仅前端指标', value: 'frontend' },
]

const metricScopeLabel = computed(() => metricScope.value === 'frontend' ? '仅前端指标' : '全部指标')
const metricDefs = computed(() => metricScope.value === 'frontend' ? frontendMetricDefs : allMetricDefs)
const primaryMetricKey = computed(() => metricScope.value === 'frontend' ? 'frontendCodeLines' : 'totalCodeLines')
const primaryMetricLabel = computed(() => metricScope.value === 'frontend' ? '前端代码行数' : '总代码行数')

const filteredTeams = computed(() => {
  if (!selectedDeptIds.value.length) return teams.value
  return teams.value.filter((team) => selectedDeptIds.value.includes(team.dept_id ?? team.deptId))
})

const filteredUsers = computed(() => users.value.filter((user) => {
  const deptId = user.dept_id ?? user.deptId
  const teamId = user.team_id ?? user.teamId
  const deptMatched = !selectedDeptIds.value.length || selectedDeptIds.value.includes(deptId)
  const teamMatched = !selectedTeamIds.value.length || selectedTeamIds.value.includes(teamId)
  return deptMatched && teamMatched
}))

const totals = computed(() => aggregate(records.value))

const metricCards = computed(() => {
  if (metricScope.value === 'frontend') {
    return [
      { label: '前端代码行数', value: totals.value.frontendCodeLines, help: 'frontend_code_lines' },
      { label: 'TS/JS 行数', value: totals.value.tsCodeLines, help: 'ts_code_lines' },
      { label: '前端组件', value: totals.value.frontendComponentCount, help: 'component_count' },
      { label: '前端页面', value: totals.value.frontendPageCount, help: 'page_count' },
    ]
  }
  return [
    { label: '总代码行数', value: totals.value.totalCodeLines, help: '所有代码产出汇总' },
    { label: '文档数量', value: totals.value.prdDocCount + totals.value.dataModelDocCount + totals.value.apiDocCount, help: 'PRD / 数据模型 / API 文档' },
    { label: '接口与服务', value: totals.value.apiCount + totals.value.coreBizServiceCount, help: 'API 与核心业务服务' },
    { label: '参与成员', value: userSummaries.value.length, help: '当前筛选范围内有产出的成员' },
  ]
})

const dailySummaries = computed(() => {
  const map = new Map<string, SummaryItem>()
  records.value.forEach((record) => addToSummary(map, record.statDate, record, { name: record.statDate }))
  return sortSummaries(Array.from(map.values()), 'name', false)
})

const departmentSummaries = computed(() => {
  const map = new Map<string, SummaryItem>()
  records.value.forEach((record) => {
    const key = record.department || '未归属部门'
    addToSummary(map, key, record, { id: record.deptId, name: key })
  })
  return sortSummaries(Array.from(map.values()))
})

const teamSummaries = computed(() => {
  const map = new Map<string, SummaryItem>()
  records.value.forEach((record) => {
    const key = record.teamName || '未归属团队'
    addToSummary(map, key, record, { id: record.teamId, name: key })
  })
  return sortSummaries(Array.from(map.values()))
})

const projectSummaries = computed(() => {
  const map = new Map<string, SummaryItem>()
  records.value.forEach((record) => {
    const key = record.projectRootName || '未归属项目'
    addToSummary(map, key, record, { name: key })
  })
  return sortSummaries(Array.from(map.values()))
})

const userSummaries = computed(() => {
  const map = new Map<string, SummaryItem>()
  records.value.forEach((record) => {
    const key = String(record.userId)
    addToSummary(map, key, record, {
      id: record.userId,
      name: record.realName || record.userName || record.gitName || '未知用户',
      department: record.department,
      teamName: record.teamName,
    })
  })
  return sortSummaries(Array.from(map.values()))
})

const orgTableData = computed(() => [
  ...departmentSummaries.value.map((item) => ({ ...item, type: '部门' })),
  ...teamSummaries.value.map((item) => ({ ...item, type: '团队' })),
])

const trendOption = computed(() => ({
  color: palette,
  tooltip: { trigger: 'axis' },
  legend: { top: 0, right: 0, itemWidth: 10, itemHeight: 10 },
  grid: { left: 48, right: 24, top: 46, bottom: 34 },
  xAxis: { type: 'category', boundaryGap: false, data: dailySummaries.value.map((item) => item.name) },
  yAxis: { type: 'value', splitLine: { lineStyle: { color: '#eef2f7' } } },
  series: metricScope.value === 'frontend'
    ? [
        lineSeries('前端行数', dailySummaries.value.map((item) => item.frontendCodeLines)),
        lineSeries('TS/JS 行数', dailySummaries.value.map((item) => item.tsCodeLines)),
        lineSeries('前端组件', dailySummaries.value.map((item) => item.frontendComponentCount)),
      ]
    : [
        lineSeries('总代码', dailySummaries.value.map((item) => item.totalCodeLines)),
        lineSeries('文档数', dailySummaries.value.map((item) => item.prdDocCount + item.dataModelDocCount + item.apiDocCount)),
        lineSeries('接口数', dailySummaries.value.map((item) => item.apiCount)),
      ],
}))

const metricRoseOption = computed(() => ({
  color: palette,
  tooltip: { trigger: 'item' },
  series: [{
    name: '指标',
    type: 'pie',
    roseType: 'radius',
    radius: ['22%', '72%'],
    center: ['50%', '52%'],
    data: metricDefs.value.map((metric) => ({
      name: metric.label,
      value: totals.value[metric.key],
    })).filter((item) => item.value > 0),
  }],
}))

const metricBarOption = computed(() => ({
  color: [palette[0]],
  tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
  grid: { left: 48, right: 16, top: 18, bottom: 58 },
  xAxis: { type: 'category', data: metricDefs.value.map((item) => item.label), axisLabel: { rotate: 28 } },
  yAxis: { type: 'value', splitLine: { lineStyle: { color: '#eef2f7' } } },
  series: [{ type: 'bar', barMaxWidth: 28, borderRadius: [6, 6, 0, 0], data: metricDefs.value.map((item) => totals.value[item.key]) }],
}))

const projectPieOption = computed(() => pieOption('项目占比', projectSummaries.value))
const departmentBarOption = computed(() => rankingBarOption(departmentSummaries.value, '部门'))
const teamBarOption = computed(() => rankingBarOption(teamSummaries.value, '团队'))
const projectBarOption = computed(() => rankingBarOption(projectSummaries.value, '项目'))
const userBarOption = computed(() => rankingBarOption(userSummaries.value, '人员'))

function lineSeries(name: string, data: number[]) {
  return { name, type: 'line', smooth: true, symbolSize: 6, areaStyle: { opacity: 0.08 }, data }
}

function pieOption(name: string, data: SummaryItem[]) {
  return {
    color: palette,
    tooltip: { trigger: 'item' },
    legend: { orient: 'vertical', right: 0, top: 'center', itemWidth: 10, itemHeight: 10, formatter: shortName },
    series: [{
      name,
      type: 'pie',
      radius: ['45%', '72%'],
      center: ['34%', '50%'],
      label: { show: false },
      labelLine: { show: false },
      data: data.slice(0, 8).map((item) => ({ name: item.name, value: item[primaryMetricKey.value] })),
    }],
  }
}

function rankingBarOption(data: SummaryItem[], label: string) {
  const rows = data.slice(0, 12)
  return {
    color: [palette[0]],
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 116, right: 20, top: 18, bottom: 28 },
    xAxis: { type: 'value', splitLine: { lineStyle: { color: '#eef2f7' } } },
    yAxis: {
      type: 'category',
      inverse: true,
      data: rows.map((item) => item.name),
      axisLabel: { formatter: shortName },
    },
    series: [{
      name: `${label}${primaryMetricLabel.value}`,
      type: 'bar',
      barMaxWidth: 18,
      borderRadius: [0, 6, 6, 0],
      data: rows.map((item) => item[primaryMetricKey.value]),
    }],
  }
}

function aggregate(list: OutputRecord[]) {
  const result = emptyNumberRecord()
  list.forEach((record) => {
    numberFields.forEach((field) => {
      result[field] += valueOf(record[field])
    })
  })
  return result
}

function addToSummary(map: Map<string, SummaryItem>, key: string, record: OutputRecord, base: Partial<SummaryItem>) {
  if (!map.has(key)) {
    map.set(key, {
      ...emptyNumberRecord(),
      id: base.id,
      name: base.name || key,
      department: base.department,
      teamName: base.teamName,
      memberIds: new Set<number>(),
      memberCount: 0,
      recordCount: 0,
    })
  }
  const summary = map.get(key)!
  summary.recordCount += 1
  summary.memberIds.add(record.userId)
  summary.memberCount = summary.memberIds.size
  numberFields.forEach((field) => {
    summary[field] += valueOf(record[field])
  })
}

function sortSummaries<T extends SummaryItem>(list: T[], key: keyof T | 'name' = primaryMetricKey.value, desc = true) {
  return list.sort((a, b) => {
    const left = a[key as keyof T] as any
    const right = b[key as keyof T] as any
    if (typeof left === 'number' && typeof right === 'number') {
      return desc ? right - left : left - right
    }
    return String(left || '').localeCompare(String(right || ''))
  })
}

function emptyNumberRecord() {
  return Object.fromEntries(numberFields.map((field) => [field, 0])) as Record<NumberKey, number>
}

function valueOf(value: number | null | undefined) {
  return Number(value || 0)
}

function formatNumber(value: number) {
  return new Intl.NumberFormat('zh-CN').format(value)
}

function shortName(name: string) {
  return name.length > 12 ? `${name.slice(0, 12)}...` : name
}

function displayUser(user: UserOption) {
  return `${user.real_name || user.realName || user.username}（${user.username}）`
}

function normalizeRecord(item: any): OutputRecord {
  return {
    id: item.id,
    userId: item.user_id ?? item.userId,
    userName: item.user_name ?? item.username ?? '',
    realName: item.real_name ?? item.realName ?? '',
    gitName: item.git_name ?? item.gitName,
    statDate: item.stat_date ?? item.statDate,
    projectRootName: item.project_root_name ?? item.projectRootName ?? '未归属项目',
    department: item.department ?? item.dept_name ?? item.deptName ?? '未归属部门',
    deptId: item.dept_id ?? item.deptId,
    teamId: item.team_id ?? item.teamId,
    teamName: item.team_name ?? item.teamName ?? '未归属团队',
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

function handleDeptChange() {
  selectedTeamIds.value = selectedTeamIds.value.filter((id) => filteredTeams.value.some((team) => team.id === id))
  selectedUserIds.value = selectedUserIds.value.filter((id) => filteredUsers.value.some((user) => user.id === id))
}

function pickRandomUsers() {
  const pool = [...filteredUsers.value]
  const selected: number[] = []
  while (pool.length && selected.length < 5) {
    const index = Math.floor(Math.random() * pool.length)
    const [user] = pool.splice(index, 1)
    selected.push(user.id)
  }
  selectedUserIds.value = selected
}

function resetFilters() {
  selectedDeptIds.value = []
  selectedTeamIds.value = []
  selectedProjectNames.value = []
  selectedUserIds.value = []
  metricScope.value = 'all'
  dateRange.value = [dayjs().startOf('month').format('YYYY-MM-DD'), dayjs().format('YYYY-MM-DD')]
  loadDashboard()
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

async function loadDashboard() {
  loading.value = true
  try {
    const params: any = {
      startDate: dateRange.value[0],
      endDate: dateRange.value[1],
    }
    if (selectedDeptIds.value.length) params.deptIds = selectedDeptIds.value
    if (selectedTeamIds.value.length) params.teamIds = selectedTeamIds.value
    if (selectedProjectNames.value.length) params.projectNames = selectedProjectNames.value
    if (selectedUserIds.value.length) params.userIds = selectedUserIds.value

    const res: any = await outputApi.dashboardDetails(params)
    records.value = (res.data || []).map(normalizeRecord)
    const projects = Array.from(new Set(records.value.map((item) => item.projectRootName).filter(Boolean))).sort()
    projectOptions.value = selectedProjectNames.value.length
      ? Array.from(new Set([...selectedProjectNames.value, ...projects])).sort()
      : projects
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadOptions()
  await loadDashboard()
})
</script>

<style scoped>
.dashboard-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.filter-panel,
.panel,
.metric-card {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #fff;
}

.filter-panel {
  padding: 18px;
}

.filter-title {
  margin-bottom: 14px;
}

.eyebrow {
  color: #1677ff;
  font-size: 12px;
  font-weight: 700;
}

.filter-title h2 {
  margin: 4px 0;
  color: #111827;
  font-size: 22px;
}

.filter-title p,
.panel-header span,
.metric-help {
  color: #6b7280;
  font-size: 13px;
}

.filter-grid {
  display: grid;
  grid-template-columns: minmax(280px, 1.5fr) repeat(5, minmax(160px, 1fr));
  gap: 12px;
}

.filter-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 14px;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.metric-card {
  padding: 16px;
}

.metric-label {
  color: #6b7280;
  font-size: 13px;
}

.metric-value {
  margin: 6px 0;
  color: #111827;
  font-size: 28px;
  font-weight: 750;
}

.dashboard-tabs {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #fff;
  padding: 0 14px 14px;
}

.chart-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.25fr) minmax(360px, 0.9fr);
  gap: 12px;
}

.overview-grid {
  grid-template-columns: minmax(0, 1.3fr) minmax(320px, 0.85fr);
}

.panel {
  padding: 14px;
}

.panel.wide {
  grid-row: span 2;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 8px;
}

.panel-header h3 {
  margin: 0;
  color: #111827;
  font-size: 16px;
}

.chart {
  width: 100%;
  height: 320px;
}

.chart.large {
  height: 430px;
}

.table-panel {
  min-width: 0;
}

.detail-panel {
  padding-bottom: 14px;
}

:deep(.el-tabs__header) {
  margin-bottom: 14px;
}

:deep(.el-table th) {
  background: #f8fafc;
  color: #374151;
  font-weight: 700;
}

:deep(.el-segmented) {
  --el-segmented-item-selected-bg-color: #1677ff;
  --el-segmented-item-selected-color: #fff;
}

@media (max-width: 1280px) {
  .filter-grid,
  .chart-grid,
  .overview-grid {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 900px) {
  .filter-grid,
  .chart-grid,
  .overview-grid,
  .metric-grid {
    grid-template-columns: 1fr;
  }

  .filter-actions {
    justify-content: flex-start;
    flex-wrap: wrap;
  }

  .panel.wide {
    grid-row: auto;
  }
}
</style>
