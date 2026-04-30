<template>
  <div class="stats-page">
    <section class="toolbar-panel">
      <div class="title-block">
        <div class="eyebrow">个人产出</div>
        <h2>产出统计</h2>
        <p>{{ rangeLabel }} · {{ records.length }} 条记录 · {{ projectCount }} 个项目</p>
      </div>
      <div class="toolbar-actions">
        <el-segmented v-model="statMode" :options="statModeOptions" />
        <el-segmented v-model="quickRange" :options="quickRangeOptions" @change="applyQuickRange" />
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          value-format="YYYY-MM-DD"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :clearable="false"
          @change="handleDateRangeChange"
        />
      </div>
    </section>

    <section class="metric-grid">
      <div class="metric-card primary">
        <div class="metric-icon"><el-icon><TrendCharts /></el-icon></div>
        <span>总代码行数</span>
        <strong>{{ formatNumber(totals.totalCodeLines) }}</strong>
        <small>Java {{ formatNumber(totals.javaCodeLines) }} · 前端 {{ formatNumber(totals.frontendCodeLines + totals.tsCodeLines) }}</small>
      </div>
      <div class="metric-card">
        <div class="metric-icon"><el-icon><Document /></el-icon></div>
        <span>文档产出</span>
        <strong>{{ formatNumber(documentTotal) }}</strong>
        <small>PRD / 数据模型 / API 文档</small>
      </div>
      <div class="metric-card">
        <div class="metric-icon"><el-icon><Connection /></el-icon></div>
        <span>接口与服务</span>
        <strong>{{ formatNumber(totals.apiCount + totals.coreBizServiceCount) }}</strong>
        <small>接口 {{ formatNumber(totals.apiCount) }} · 服务 {{ formatNumber(totals.coreBizServiceCount) }}</small>
      </div>
      <div class="metric-card">
        <div class="metric-icon"><el-icon><Grid /></el-icon></div>
        <span>{{ isProjectMode ? '参与项目' : '前端资产' }}</span>
        <strong>{{ isProjectMode ? projectCount : formatNumber(frontendAssetTotal) }}</strong>
        <small>{{ isProjectMode ? '按项目聚合当前时间范围' : '组件 / 页面 / 公共组件' }}</small>
      </div>
    </section>

    <section class="chart-layout">
      <div class="chart-panel trend-panel">
        <div class="panel-header">
          <div>
            <h3>{{ isProjectMode ? '项目代码排行' : '产出趋势' }}</h3>
            <p>{{ isProjectMode ? '按项目汇总代码行数，展示 Top 10' : '按日期汇总代码、文档与接口' }}</p>
          </div>
        </div>
        <div class="chart-wrap">
          <v-chart v-if="records.length" class="chart" :option="mainChartOption" autoresize />
          <el-empty v-else description="暂无产出数据" :image-size="80" />
        </div>
      </div>

      <div class="chart-panel">
        <div class="panel-header">
          <div>
            <h3>{{ isProjectMode ? '项目产出结构' : '代码构成' }}</h3>
            <p>{{ isProjectMode ? '比较各项目的文档、后端、前端产出' : '后端、前端与脚本占比' }}</p>
          </div>
        </div>
        <div class="chart-wrap compact">
          <v-chart v-if="records.length" class="chart" :option="secondaryChartOption" autoresize />
          <el-empty v-else description="暂无产出数据" :image-size="80" />
        </div>
      </div>

      <div class="chart-panel">
        <div class="panel-header">
          <div>
            <h3>项目分布</h3>
            <p>按项目汇总代码行数</p>
          </div>
        </div>
        <div class="chart-wrap compact">
          <v-chart v-if="projectDistribution.length" class="chart" :option="projectOption" autoresize />
          <el-empty v-else description="暂无项目数据" :image-size="80" />
        </div>
      </div>
    </section>

    <section class="table-panel">
      <div class="panel-header">
        <div>
          <h3>{{ isProjectMode ? '项目汇总' : '明细记录' }}</h3>
          <p>{{ isProjectMode ? '按项目聚合当前时间范围内的产出数据' : '按日期倒序展示每次上传的产出' }}</p>
        </div>
      </div>

      <el-table
        v-if="isProjectMode"
        :data="projectSummaries"
        v-loading="loading"
        class="output-table"
        border
        stripe
      >
        <el-table-column prop="projectName" label="项目" min-width="180" fixed show-overflow-tooltip />
        <el-table-column prop="recordCount" label="记录数" width="90" align="center" />
        <el-table-column prop="activeDays" label="活跃天数" width="100" align="center" />
        <el-table-column prop="lastDate" label="最近产出" width="120" align="center" />
        <el-table-column label="文档" align="center">
          <el-table-column prop="prdDocCount" label="PRD" width="80" align="center" />
          <el-table-column prop="dataModelDocCount" label="数据模型" width="100" align="center" />
          <el-table-column prop="apiDocCount" label="API 文档" width="100" align="center" />
        </el-table-column>
        <el-table-column label="后端" align="center">
          <el-table-column prop="javaFileCount" label="Java 文件" width="100" align="center" />
          <el-table-column prop="javaCodeLines" label="Java 行数" width="110" align="center" />
          <el-table-column prop="apiCount" label="接口" width="80" align="center" />
          <el-table-column prop="coreBizServiceCount" label="服务" width="80" align="center" />
        </el-table-column>
        <el-table-column label="前端" align="center">
          <el-table-column prop="frontendComponentCount" label="组件" width="80" align="center" />
          <el-table-column prop="frontendPageCount" label="页面" width="80" align="center" />
          <el-table-column prop="tsCodeLines" label="TS/JS 行数" width="110" align="center" />
          <el-table-column prop="frontendCodeLines" label="前端行数" width="110" align="center" />
        </el-table-column>
        <el-table-column prop="totalCodeLines" label="总代码" width="110" align="center" />
      </el-table>

      <el-table v-else :data="records" v-loading="loading" class="output-table" border stripe>
        <el-table-column prop="statDate" label="日期" width="120" fixed />
        <el-table-column prop="projectRootName" label="项目" min-width="170" show-overflow-tooltip />
        <el-table-column prop="outputType" label="来源" width="150" show-overflow-tooltip />
        <el-table-column label="文档" align="center">
          <el-table-column prop="prdDocCount" label="PRD" width="80" align="center" />
          <el-table-column prop="dataModelDocCount" label="数据模型" width="100" align="center" />
          <el-table-column prop="apiDocCount" label="API 文档" width="100" align="center" />
        </el-table-column>
        <el-table-column label="后端" align="center">
          <el-table-column prop="javaFileCount" label="Java 文件" width="100" align="center" />
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
        <el-table-column label="其他" align="center">
          <el-table-column prop="sqlScriptCount" label="SQL" width="80" align="center" />
          <el-table-column prop="testFileCount" label="测试" width="80" align="center" />
          <el-table-column prop="totalCodeLines" label="总代码" width="110" align="center" />
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="180" show-overflow-tooltip />
      </el-table>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import dayjs from 'dayjs'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { BarChart, LineChart, PieChart } from 'echarts/charts'
import {
  GridComponent,
  LegendComponent,
  TooltipComponent,
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { Connection, Document, Grid, TrendCharts } from '@element-plus/icons-vue'
import { outputApi } from '@/api'

use([
  BarChart,
  CanvasRenderer,
  GridComponent,
  LegendComponent,
  LineChart,
  PieChart,
  TooltipComponent,
])

interface OutputRecord {
  statDate: string
  projectRootName?: string
  outputType?: string
  prdDocCount?: number
  dataModelDocCount?: number
  apiDocCount?: number
  javaFileCount?: number
  javaCodeLines?: number
  apiCount?: number
  coreBizServiceCount?: number
  entityCount?: number
  frontendComponentCount?: number
  frontendPageCount?: number
  frontendCommonComponentCount?: number
  tsCodeLines?: number
  frontendCodeLines?: number
  sqlScriptCount?: number
  testFileCount?: number
  totalCodeLines?: number
  remark?: string
}

interface ProjectSummary extends Record<NumberField, number> {
  projectName: string
  recordCount: number
  activeDays: number
  lastDate: string
  documentTotal: number
  backendTotal: number
  frontendTotal: number
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

type NumberField = typeof numberFields[number]
type StatMode = 'daily' | 'project'

const palette = ['#2563eb', '#16a34a', '#f59e0b', '#7c3aed', '#db2777', '#0891b2']

const loading = ref(false)
const records = ref<OutputRecord[]>([])
const quickRange = ref('30')
const statMode = ref<StatMode>('daily')
const dateRange = ref<[string, string]>([
  dayjs().subtract(30, 'day').format('YYYY-MM-DD'),
  dayjs().format('YYYY-MM-DD'),
])

const statModeOptions = [
  { label: '时间明细', value: 'daily' },
  { label: '项目统计', value: 'project' },
]

const quickRangeOptions = [
  { label: '7天', value: '7' },
  { label: '30天', value: '30' },
  { label: '90天', value: '90' },
]

const isProjectMode = computed(() => statMode.value === 'project')

const totals = computed(() => {
  const result = emptyNumberRecord()
  records.value.forEach((record) => {
    numberFields.forEach((field) => {
      result[field] += valueOf(record[field])
    })
  })
  return result
})

const documentTotal = computed(() =>
  totals.value.prdDocCount + totals.value.dataModelDocCount + totals.value.apiDocCount
)

const frontendAssetTotal = computed(() =>
  totals.value.frontendComponentCount + totals.value.frontendPageCount + totals.value.frontendCommonComponentCount
)

const projectSummaries = computed<ProjectSummary[]>(() => {
  const map = new Map<string, ProjectSummary & { days: Set<string> }>()
  records.value.forEach((record) => {
    const projectName = record.projectRootName || '未归属项目'
    if (!map.has(projectName)) {
      map.set(projectName, {
        ...emptyNumberRecord(),
        projectName,
        recordCount: 0,
        activeDays: 0,
        lastDate: '',
        documentTotal: 0,
        backendTotal: 0,
        frontendTotal: 0,
        days: new Set<string>(),
      })
    }

    const summary = map.get(projectName)!
    summary.recordCount += 1
    summary.days.add(record.statDate)
    summary.lastDate = summary.lastDate && summary.lastDate > record.statDate ? summary.lastDate : record.statDate
    numberFields.forEach((field) => {
      summary[field] += valueOf(record[field])
    })
  })

  return Array.from(map.values())
    .map((summary) => ({
      ...summary,
      activeDays: summary.days.size,
      documentTotal: summary.prdDocCount + summary.dataModelDocCount + summary.apiDocCount,
      backendTotal: summary.javaCodeLines + summary.apiCount + summary.coreBizServiceCount + summary.entityCount,
      frontendTotal: summary.frontendCodeLines + summary.tsCodeLines,
    }))
    .sort((a, b) => b.totalCodeLines - a.totalCodeLines)
})

const projectCount = computed(() => projectSummaries.value.length)

const rangeLabel = computed(() => `${dateRange.value[0]} 至 ${dateRange.value[1]}`)

const dailySeries = computed(() => {
  const map = new Map<string, { date: string; code: number; docs: number; api: number }>()
  records.value.forEach((record) => {
    const date = record.statDate
    if (!map.has(date)) {
      map.set(date, { date, code: 0, docs: 0, api: 0 })
    }
    const item = map.get(date)!
    item.code += valueOf(record.totalCodeLines)
    item.docs += valueOf(record.prdDocCount) + valueOf(record.dataModelDocCount) + valueOf(record.apiDocCount)
    item.api += valueOf(record.apiCount)
  })
  return Array.from(map.values()).sort((a, b) => a.date.localeCompare(b.date))
})

const projectDistribution = computed(() => projectSummaries.value
  .map((item) => ({ name: item.projectName, value: item.totalCodeLines }))
  .slice(0, 8)
)

const topProjectSummaries = computed(() => projectSummaries.value.slice(0, 10))

const mainChartOption = computed(() => isProjectMode.value ? projectRankingOption.value : trendOption.value)
const secondaryChartOption = computed(() => isProjectMode.value ? projectMetricOption.value : codeBreakdownOption.value)

const trendOption = computed(() => ({
  color: palette,
  tooltip: { trigger: 'axis' },
  legend: {
    top: 0,
    right: 0,
    itemWidth: 10,
    itemHeight: 10,
    textStyle: { color: '#64748b' },
  },
  grid: { left: 48, right: 24, top: 48, bottom: 34 },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: dailySeries.value.map((item) => item.date),
    axisLine: { lineStyle: { color: '#cbd5e1' } },
    axisLabel: { color: '#64748b' },
  },
  yAxis: {
    type: 'value',
    axisLabel: { color: '#64748b' },
    splitLine: { lineStyle: { color: '#e5e7eb' } },
  },
  series: [
    {
      name: '代码行数',
      type: 'line',
      smooth: true,
      symbolSize: 7,
      areaStyle: { opacity: 0.12 },
      data: dailySeries.value.map((item) => item.code),
    },
    {
      name: '文档数',
      type: 'line',
      smooth: true,
      symbolSize: 7,
      data: dailySeries.value.map((item) => item.docs),
    },
    {
      name: '接口数',
      type: 'line',
      smooth: true,
      symbolSize: 7,
      data: dailySeries.value.map((item) => item.api),
    },
  ],
}))

const projectRankingOption = computed(() => ({
  color: [palette[0]],
  tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
  grid: { left: 120, right: 24, top: 20, bottom: 28 },
  xAxis: {
    type: 'value',
    axisLabel: { color: '#64748b' },
    splitLine: { lineStyle: { color: '#e5e7eb' } },
  },
  yAxis: {
    type: 'category',
    inverse: true,
    data: topProjectSummaries.value.map((item) => item.projectName),
    axisLine: { lineStyle: { color: '#cbd5e1' } },
    axisLabel: {
      color: '#64748b',
      formatter: formatAxisName,
    },
  },
  series: [{
    name: '代码行数',
    type: 'bar',
    barMaxWidth: 18,
    borderRadius: [0, 6, 6, 0],
    data: topProjectSummaries.value.map((item) => item.totalCodeLines),
  }],
}))

const codeBreakdownOption = computed(() => ({
  color: [palette[0], palette[1], palette[2], palette[5]],
  tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
  grid: { left: 44, right: 16, top: 24, bottom: 34 },
  xAxis: {
    type: 'category',
    data: ['Java', '前端', 'TS/JS', 'SQL'],
    axisLine: { lineStyle: { color: '#cbd5e1' } },
    axisLabel: { color: '#64748b' },
  },
  yAxis: {
    type: 'value',
    axisLabel: { color: '#64748b' },
    splitLine: { lineStyle: { color: '#e5e7eb' } },
  },
  series: [{
    type: 'bar',
    barMaxWidth: 34,
    borderRadius: [6, 6, 0, 0],
    data: [
      totals.value.javaCodeLines,
      totals.value.frontendCodeLines,
      totals.value.tsCodeLines,
      totals.value.sqlScriptCount,
    ],
  }],
}))

const projectMetricOption = computed(() => ({
  color: [palette[2], palette[0], palette[1]],
  tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
  legend: {
    top: 0,
    right: 0,
    itemWidth: 10,
    itemHeight: 10,
    textStyle: { color: '#64748b' },
  },
  grid: { left: 42, right: 16, top: 42, bottom: 48 },
  xAxis: {
    type: 'category',
    data: topProjectSummaries.value.slice(0, 6).map((item) => item.projectName),
    axisLine: { lineStyle: { color: '#cbd5e1' } },
    axisLabel: {
      color: '#64748b',
      interval: 0,
      formatter: formatAxisName,
    },
  },
  yAxis: {
    type: 'value',
    axisLabel: { color: '#64748b' },
    splitLine: { lineStyle: { color: '#e5e7eb' } },
  },
  series: [
    {
      name: '文档',
      type: 'bar',
      stack: 'project',
      data: topProjectSummaries.value.slice(0, 6).map((item) => item.documentTotal),
    },
    {
      name: '后端',
      type: 'bar',
      stack: 'project',
      data: topProjectSummaries.value.slice(0, 6).map((item) => item.backendTotal),
    },
    {
      name: '前端',
      type: 'bar',
      stack: 'project',
      data: topProjectSummaries.value.slice(0, 6).map((item) => item.frontendTotal),
    },
  ],
}))

const projectOption = computed(() => ({
  color: palette,
  tooltip: { trigger: 'item' },
  legend: {
    orient: 'vertical',
    right: 0,
    top: 'center',
    itemWidth: 10,
    itemHeight: 10,
    textStyle: { color: '#64748b' },
    formatter: formatLegendName,
  },
  series: [{
    name: '代码行数',
    type: 'pie',
    radius: ['48%', '72%'],
    center: ['34%', '50%'],
    avoidLabelOverlap: true,
    label: { show: false },
    labelLine: { show: false },
    data: projectDistribution.value,
  }],
}))

function emptyNumberRecord() {
  return Object.fromEntries(numberFields.map((field) => [field, 0])) as Record<NumberField, number>
}

function valueOf(value?: number | null) {
  return Number(value || 0)
}

function formatNumber(value: number) {
  return new Intl.NumberFormat('zh-CN').format(value)
}

function formatLegendName(name: string) {
  return name.length > 12 ? `${name.slice(0, 12)}...` : name
}

function formatAxisName(name: string) {
  return name.length > 8 ? `${name.slice(0, 8)}...` : name
}

function applyQuickRange(value: string | number) {
  const days = Number(value)
  dateRange.value = [
    dayjs().subtract(days, 'day').format('YYYY-MM-DD'),
    dayjs().format('YYYY-MM-DD'),
  ]
  loadHistory()
}

function handleDateRangeChange() {
  quickRange.value = ''
  loadHistory()
}

async function loadHistory() {
  if (!dateRange.value?.[0] || !dateRange.value?.[1]) return
  loading.value = true
  try {
    const res: any = await outputApi.history({
      startDate: dateRange.value[0],
      endDate: dateRange.value[1],
    })
    records.value = (res.data || []).sort((a: OutputRecord, b: OutputRecord) => b.statDate.localeCompare(a.statDate))
  } finally {
    loading.value = false
  }
}

onMounted(loadHistory)
</script>

<style scoped>
.stats-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.toolbar-panel,
.table-panel,
.chart-panel {
  border: 1px solid hsl(var(--border));
  border-radius: 8px;
  background: hsl(var(--card));
}

.toolbar-panel {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 18px 20px;
}

.title-block {
  min-width: 240px;
}

.eyebrow {
  margin-bottom: 6px;
  color: hsl(var(--primary));
  font-size: 12px;
  font-weight: 700;
}

.title-block h2 {
  margin: 0;
  color: hsl(var(--foreground));
  font-size: 22px;
  font-weight: 700;
}

.title-block p,
.panel-header p {
  margin: 6px 0 0;
  color: hsl(var(--muted-foreground));
  font-size: 13px;
}

.toolbar-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  flex-wrap: wrap;
  gap: 12px;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.metric-card {
  min-height: 118px;
  padding: 16px;
  border: 1px solid hsl(var(--border));
  border-radius: 8px;
  background: hsl(var(--card));
  color: hsl(var(--foreground));
}

.metric-card.primary {
  border-color: hsl(var(--primary) / 0.35);
}

.metric-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  border-radius: 8px;
  background: hsl(var(--secondary));
  color: hsl(var(--primary));
}

.metric-card span {
  display: block;
  color: hsl(var(--muted-foreground));
  font-size: 13px;
}

.metric-card strong {
  display: block;
  margin: 5px 0;
  font-size: 27px;
  font-weight: 750;
}

.metric-card small {
  color: hsl(var(--muted-foreground));
  font-size: 12px;
}

.chart-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.55fr) minmax(320px, 0.95fr);
  gap: 12px;
}

.trend-panel {
  grid-row: span 2;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 16px 0;
}

.panel-header h3 {
  margin: 0;
  color: hsl(var(--foreground));
  font-size: 16px;
  font-weight: 700;
}

.chart-wrap {
  height: 390px;
  padding: 8px 12px 14px;
}

.chart-wrap.compact {
  height: 222px;
}

.chart {
  width: 100%;
  height: 100%;
}

.table-panel {
  padding-bottom: 12px;
}

.output-table {
  width: calc(100% - 24px);
  margin: 12px;
}

:deep(.el-table th) {
  background-color: hsl(var(--muted));
  color: hsl(var(--foreground));
  font-weight: 700;
}

:deep(.el-table td) {
  color: hsl(var(--foreground));
}

:deep(.el-segmented) {
  --el-segmented-item-selected-bg-color: hsl(var(--primary));
  --el-segmented-item-selected-color: white;
}

@media (max-width: 1180px) {
  .chart-layout {
    grid-template-columns: 1fr;
  }

  .trend-panel {
    grid-row: auto;
  }
}

@media (max-width: 900px) {
  .toolbar-panel {
    align-items: flex-start;
    flex-direction: column;
  }

  .toolbar-actions {
    width: 100%;
    justify-content: flex-start;
  }

  .metric-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 560px) {
  .metric-grid {
    grid-template-columns: 1fr;
  }

  .chart-wrap {
    height: 320px;
  }

  .chart-wrap.compact {
    height: 240px;
  }
}
</style>
