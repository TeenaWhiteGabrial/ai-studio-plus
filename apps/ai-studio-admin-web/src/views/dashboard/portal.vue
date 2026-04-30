<template>
  <div class="portal-dashboard">
    <section class="toolbar">
      <div>
        <div class="eyebrow">Portal Analytics</div>
        <h2>网站数据看板</h2>
        <p>{{ dateRange[0] }} 至 {{ dateRange[1] }} · {{ rankingTitle }}</p>
      </div>

      <div class="toolbar-actions">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          value-format="YYYY-MM-DD"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :clearable="false"
        />
        <el-segmented v-model="rankingType" :options="rankingOptions" />
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

    <section class="chart-grid">
      <div class="panel wide">
        <div class="panel-header">
          <h3>访问与内容趋势</h3>
          <span>按日汇总</span>
        </div>
        <v-chart v-if="trendRows.length" class="chart large" :option="trendOption" autoresize />
        <el-empty v-else description="暂无趋势数据" />
      </div>

      <div class="panel">
        <div class="panel-header">
          <h3>内容结构</h3>
          <span>文章 / Skill / MCP / Plugin / 教程 / 问答</span>
        </div>
        <v-chart class="chart" :option="contentPieOption" autoresize />
      </div>

      <div class="panel">
        <div class="panel-header">
          <h3>互动指标</h3>
          <span>浏览、下载、评论与回答</span>
        </div>
        <v-chart class="chart" :option="interactionBarOption" autoresize />
      </div>
    </section>

    <section class="chart-grid lower">
      <div class="panel">
        <div class="panel-header">
          <h3>{{ rankingTitle }}</h3>
          <span>{{ rankingPrimaryLabel }}</span>
        </div>
        <v-chart v-if="rankings.length" class="chart" :option="rankingBarOption" autoresize />
        <el-empty v-else description="暂无排行数据" />
      </div>

      <div class="panel table-panel">
        <div class="panel-header">
          <h3>热门内容</h3>
          <span>Top 10</span>
        </div>
        <el-table :data="rankings" v-loading="loading" border height="320">
          <el-table-column type="index" label="#" width="56" align="center" />
          <el-table-column prop="name" label="名称" min-width="180" show-overflow-tooltip />
          <el-table-column prop="ownerName" label="作者" width="120" show-overflow-tooltip />
          <el-table-column prop="primaryValue" :label="rankingPrimaryLabel" width="120" align="center" sortable />
          <el-table-column prop="secondaryValue" :label="rankingSecondaryLabel" width="120" align="center" sortable />
        </el-table>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import dayjs from 'dayjs'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { BarChart, LineChart, PieChart } from 'echarts/charts'
import { GridComponent, LegendComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { dashboardApi } from '@/api'

use([BarChart, LineChart, PieChart, GridComponent, LegendComponent, TooltipComponent, CanvasRenderer])

type RankingType = 'article' | 'skill' | 'question' | 'tutorial'

interface PortalOverview {
  visitorCount: number
  browseCount: number
  articleCount: number
  publishedArticleCount: number
  skillCount: number
  mcpCount: number
  pluginCount: number
  tutorialCount: number
  questionCount: number
  answerCount: number
  commentCount: number
  totalViews: number
  totalDownloads: number
}

interface TrendRow {
  statDate: string
  visitorCount: number
  browseCount: number
  articleCount: number
  skillCount: number
  questionCount: number
}

interface RankingRow {
  id: number
  name: string
  ownerName: string
  primaryValue: number
  secondaryValue: number
}

const loading = ref(false)
const dateRange = ref<[string, string]>([
  dayjs().subtract(29, 'day').format('YYYY-MM-DD'),
  dayjs().format('YYYY-MM-DD'),
])
const rankingType = ref<RankingType>('article')
const overview = ref<PortalOverview>({
  visitorCount: 0,
  browseCount: 0,
  articleCount: 0,
  publishedArticleCount: 0,
  skillCount: 0,
  mcpCount: 0,
  pluginCount: 0,
  tutorialCount: 0,
  questionCount: 0,
  answerCount: 0,
  commentCount: 0,
  totalViews: 0,
  totalDownloads: 0,
})
const trendRows = ref<TrendRow[]>([])
const rankings = ref<RankingRow[]>([])

const rankingOptions = [
  { label: '文章', value: 'article' },
  { label: 'Skill', value: 'skill' },
  { label: '问答', value: 'question' },
  { label: '教程', value: 'tutorial' },
]

const rankingMeta = computed(() => {
  const map = {
    article: { title: '热门文章排行', primary: '阅读数', secondary: '点赞数' },
    skill: { title: '热门 Skill 排行', primary: '下载数', secondary: '收藏数' },
    question: { title: '热门问题排行', primary: '浏览数', secondary: '回答数' },
    tutorial: { title: '热门教程排行', primary: '浏览数', secondary: '版本数' },
  }
  return map[rankingType.value]
})
const rankingTitle = computed(() => rankingMeta.value.title)
const rankingPrimaryLabel = computed(() => rankingMeta.value.primary)
const rankingSecondaryLabel = computed(() => rankingMeta.value.secondary)

const metricCards = computed(() => [
  { label: '访问人数', value: overview.value.visitorCount, help: '浏览历史去重用户数' },
  { label: '浏览记录', value: overview.value.browseCount, help: '站内内容浏览记录' },
  { label: '文章数', value: overview.value.articleCount, help: `${overview.value.publishedArticleCount} 篇已发布` },
  { label: 'Skill 数', value: overview.value.skillCount, help: '未删除 Skill 总量' },
  { label: 'MCP / Plugin', value: overview.value.mcpCount + overview.value.pluginCount, help: `${overview.value.mcpCount} 个 MCP · ${overview.value.pluginCount} 个 Plugin` },
  { label: '教程数', value: overview.value.tutorialCount, help: '教程内容总量' },
  { label: '问答互动', value: overview.value.questionCount + overview.value.answerCount, help: `${overview.value.questionCount} 个问题 · ${overview.value.answerCount} 个回答` },
  { label: '总曝光', value: overview.value.totalViews + overview.value.totalDownloads, help: `${overview.value.totalViews} 浏览 · ${overview.value.totalDownloads} 下载` },
])

const contentPieOption = computed(() => ({
  tooltip: { trigger: 'item' },
  legend: { bottom: 0 },
  color: ['#1677ff', '#15b8a6', '#f59e0b', '#ef4444', '#8b5cf6', '#64748b'],
  series: [
    {
      type: 'pie',
      radius: ['34%', '68%'],
      center: ['50%', '45%'],
      roseType: 'radius',
      data: [
        { name: '文章', value: overview.value.articleCount },
        { name: 'Skill', value: overview.value.skillCount },
        { name: 'MCP', value: overview.value.mcpCount },
        { name: 'Plugin', value: overview.value.pluginCount },
        { name: '教程', value: overview.value.tutorialCount },
        { name: '问题', value: overview.value.questionCount },
      ],
    },
  ],
}))

const interactionBarOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: 40, right: 16, top: 24, bottom: 42 },
  color: ['#1677ff'],
  xAxis: {
    type: 'category',
    data: ['浏览记录', '内容浏览', '下载', '评论', '回答'],
    axisLabel: { interval: 0 },
  },
  yAxis: { type: 'value' },
  series: [
    {
      type: 'bar',
      barWidth: 30,
      data: [
        overview.value.browseCount,
        overview.value.totalViews,
        overview.value.totalDownloads,
        overview.value.commentCount,
        overview.value.answerCount,
      ],
    },
  ],
}))

const trendOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  legend: { top: 0 },
  grid: { left: 48, right: 24, top: 44, bottom: 40 },
  color: ['#1677ff', '#15b8a6', '#f59e0b', '#ef4444', '#8b5cf6'],
  xAxis: { type: 'category', data: trendRows.value.map((item) => item.statDate) },
  yAxis: { type: 'value' },
  series: [
    { name: '访问人数', type: 'line', smooth: true, data: trendRows.value.map((item) => item.visitorCount) },
    { name: '浏览记录', type: 'line', smooth: true, data: trendRows.value.map((item) => item.browseCount) },
    { name: '新增文章', type: 'bar', data: trendRows.value.map((item) => item.articleCount) },
    { name: '新增 Skill', type: 'bar', data: trendRows.value.map((item) => item.skillCount) },
    { name: '新增问题', type: 'bar', data: trendRows.value.map((item) => item.questionCount) },
  ],
}))

const rankingBarOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: 72, right: 24, top: 24, bottom: 32 },
  color: ['#15b8a6'],
  xAxis: { type: 'value' },
  yAxis: {
    type: 'category',
    inverse: true,
    data: rankings.value.map((item) => item.name),
    axisLabel: { width: 110, overflow: 'truncate' },
  },
  series: [{ type: 'bar', barWidth: 18, data: rankings.value.map((item) => item.primaryValue) }],
}))

function numberOf(data: any, snakeKey: string, camelKey?: string) {
  return Number(data?.[snakeKey] ?? data?.[camelKey || snakeKey] ?? 0)
}

function normalizeOverview(data: any): PortalOverview {
  return {
    visitorCount: numberOf(data, 'visitor_count', 'visitorCount'),
    browseCount: numberOf(data, 'browse_count', 'browseCount'),
    articleCount: numberOf(data, 'article_count', 'articleCount'),
    publishedArticleCount: numberOf(data, 'published_article_count', 'publishedArticleCount'),
    skillCount: numberOf(data, 'skill_count', 'skillCount'),
    mcpCount: numberOf(data, 'mcp_count', 'mcpCount'),
    pluginCount: numberOf(data, 'plugin_count', 'pluginCount'),
    tutorialCount: numberOf(data, 'tutorial_count', 'tutorialCount'),
    questionCount: numberOf(data, 'question_count', 'questionCount'),
    answerCount: numberOf(data, 'answer_count', 'answerCount'),
    commentCount: numberOf(data, 'comment_count', 'commentCount'),
    totalViews: numberOf(data, 'total_views', 'totalViews'),
    totalDownloads: numberOf(data, 'total_downloads', 'totalDownloads'),
  }
}

function normalizeTrend(data: any): TrendRow {
  return {
    statDate: String(data?.stat_date ?? data?.statDate ?? ''),
    visitorCount: numberOf(data, 'visitor_count', 'visitorCount'),
    browseCount: numberOf(data, 'browse_count', 'browseCount'),
    articleCount: numberOf(data, 'article_count', 'articleCount'),
    skillCount: numberOf(data, 'skill_count', 'skillCount'),
    questionCount: numberOf(data, 'question_count', 'questionCount'),
  }
}

function normalizeRanking(data: any): RankingRow {
  return {
    id: Number(data?.id ?? 0),
    name: String(data?.name ?? ''),
    ownerName: String(data?.owner_name ?? data?.ownerName ?? '-'),
    primaryValue: numberOf(data, 'primary_value', 'primaryValue'),
    secondaryValue: numberOf(data, 'secondary_value', 'secondaryValue'),
  }
}

function formatNumber(value: number) {
  return Number(value || 0).toLocaleString()
}

async function loadRanking() {
  const res: any = await dashboardApi.portalRanking({ type: rankingType.value, top_n: 10 })
  rankings.value = (res.data || []).map(normalizeRanking)
}

async function loadDashboard() {
  loading.value = true
  try {
    const [overviewRes, trendRes] = await Promise.all([
      dashboardApi.portalOverview() as any,
      dashboardApi.portalTrend({
        start_date: dateRange.value[0],
        end_date: dateRange.value[1],
      }) as any,
    ])
    overview.value = normalizeOverview(overviewRes.data || {})
    trendRows.value = (trendRes.data || []).map(normalizeTrend)
    await loadRanking()
  } catch (error) {
    ElMessage.error('网站数据看板加载失败')
  } finally {
    loading.value = false
  }
}

watch(rankingType, () => {
  loadRanking()
})

onMounted(loadDashboard)
</script>

<style scoped>
.portal-dashboard {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.toolbar,
.panel,
.metric-card {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #fff;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  padding: 18px;
}

.eyebrow {
  color: #1677ff;
  font-size: 12px;
  font-weight: 700;
}

.toolbar h2 {
  margin: 4px 0;
  color: #111827;
  font-size: 22px;
}

.toolbar p,
.panel-header span,
.metric-help {
  color: #6b7280;
  font-size: 13px;
}

.toolbar-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-end;
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

.chart-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.3fr) minmax(320px, 0.85fr);
  gap: 12px;
}

.chart-grid.lower {
  grid-template-columns: minmax(0, 0.95fr) minmax(0, 1.05fr);
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

:deep(.el-table th) {
  background: #f8fafc;
  color: #374151;
  font-weight: 700;
}

:deep(.el-segmented) {
  --el-segmented-item-selected-bg-color: #1677ff;
  --el-segmented-item-selected-color: #fff;
}

@media (max-width: 1180px) {
  .toolbar {
    flex-direction: column;
  }

  .toolbar-actions {
    justify-content: flex-start;
  }

  .metric-grid,
  .chart-grid,
  .chart-grid.lower {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 820px) {
  .metric-grid,
  .chart-grid,
  .chart-grid.lower {
    grid-template-columns: 1fr;
  }

  .panel.wide {
    grid-row: auto;
  }
}
</style>
