<template>
  <div>
    <!-- 筛选器区域 -->
    <el-card class="mb-4">
      <template #header>
        <span>数据筛选</span>
      </template>
      <el-form inline>
        <el-form-item label="时间范围">
          <TimeRangePicker v-model="dateRange" @change="loadStats" />
        </el-form-item>
        <el-form-item label="部门">
          <DeptSelector style="width:300px" v-model="selectedDepts" @change="loadStats" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadStats">
            <el-icon><Search /></el-icon>搜索
          </el-button>
        </el-form-item>
        <!-- <el-form-item>
          <el-button type="primary" @click="exportData" :loading="exporting">
            <el-icon><Download /></el-icon>导出
          </el-button>
        </el-form-item> -->
      </el-form>
    </el-card>

    <!-- 概览卡片 - 16 个指标汇总 -->
    <el-row :gutter="16" class="mb-4">
      <el-col :span="6" v-for="card in overviewCards" :key="card.label">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-value">{{ card.value }}</div>
            <div class="stat-label">{{ card.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="16">
      <!-- 产出分布饼图 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>产出分布</span>
          </template>
          <v-chart :option="pieOption" style="height:350px" autoresize />
        </el-card>
      </el-col>

      <!-- 产出构成柱状图 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>产出构成</span>
          </template>
          <v-chart :option="barOption" style="height:350px" autoresize />
        </el-card>
      </el-col>
    </el-row>

    <!-- 详细指标卡片 -->
    <el-row :gutter="16" class="mt-4">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>详细指标数据</span>
          </template>
          <el-descriptions :column="4" border>
            <el-descriptions-item label="PRD 文档数">{{ stats.prdDocCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="数据模型文档数">{{ stats.dataModelDocCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="API 接口文档数">{{ stats.apiDocCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="Java 文件数">{{ stats.javaFileCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="Java 代码行数">{{ stats.javaCodeLines || 0 }}</el-descriptions-item>
            <el-descriptions-item label="API 接口数">{{ stats.apiCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="核心业务服务数">{{ stats.coreBizServiceCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="数据库实体数">{{ stats.entityCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="前端组件数">{{ stats.frontendComponentCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="前端页面数">{{ stats.frontendPageCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="前端公共组件数">{{ stats.frontendCommonComponentCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="TypeScript 代码行数">{{ stats.tsCodeLines || 0 }}</el-descriptions-item>
            <el-descriptions-item label="前端代码行数">{{ stats.frontendCodeLines || 0 }}</el-descriptions-item>
            <el-descriptions-item label="SQL 脚本数">{{ stats.sqlScriptCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="测试文件数">{{ stats.testFileCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="总代码行数">{{ stats.totalCodeLines || 0 }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import * as echarts from 'echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart, PieChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent, TitleComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { Search } from '@element-plus/icons-vue'
import { outputApi } from '@/api'
import TimeRangePicker from '@/components/TimeRangePicker.vue'
import DeptSelector from '@/components/DeptSelector.vue'
import dayjs from 'dayjs'

use([CanvasRenderer, BarChart, PieChart, GridComponent, TooltipComponent, LegendComponent, TitleComponent])

const stats = ref<any>({})
const dateRange = ref<[string, string]>(['', ''])
const selectedDepts = ref<number[]>([])
const selectedProjects = ref<string[]>([])

const overviewCards = computed(() => [
  { label: '产品文档总数', value: (stats.value.prdDocCount || 0) + (stats.value.dataModelDocCount || 0) + (stats.value.apiDocCount || 0) },
  { label: 'Java 文件数', value: stats.value.javaFileCount || 0 },
  { label: '前端组件数', value: stats.value.frontendComponentCount || 0 },
  { label: '总代码行数', value: stats.value.totalCodeLines || 0 },
])

// 饼图配置 - 展示各类产出占比
const pieOption = computed(() => {
  const data = [
    { value: stats.value.prdDocCount || 0, name: 'PRD 文档' },
    { value: stats.value.dataModelDocCount || 0, name: '数据模型文档' },
    { value: stats.value.apiDocCount || 0, name: 'API 接口文档' },
    { value: stats.value.javaFileCount || 0, name: 'Java 文件' },
    { value: stats.value.frontendComponentCount || 0, name: '前端组件' },
    { value: stats.value.sqlScriptCount || 0, name: 'SQL 脚本' },
    { value: stats.value.testFileCount || 0, name: '测试文件' },
  ].filter(item => item.value > 0)

  return {
    title: { text: '产出分布', left: 'center' },
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left', top: 'middle' },
    series: [
      {
        name: '产出分布',
        type: 'pie',
        radius: '60%',
        data: data,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
})

// 柱状图配置 - 展示代码行数和文件数
const barOption = computed(() => {
  const categories = ['Java\n文件', 'Java\n代码行', 'TS\n代码行', '前端\n代码行', '总\n代码行', 'API\n接口', 'SQL\n脚本', '测试\n文件']
  const data = [
    stats.value.javaFileCount || 0,
    stats.value.javaCodeLines || 0,
    stats.value.tsCodeLines || 0,
    stats.value.frontendCodeLines || 0,
    stats.value.totalCodeLines || 0,
    stats.value.apiCount || 0,
    stats.value.sqlScriptCount || 0,
    stats.value.testFileCount || 0,
  ]

  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    xAxis: { type: 'category', data: categories, axisLabel: { interval: 0, rotate: 0 } },
    yAxis: { type: 'value' },
    series: [
      {
        name: '数量',
        type: 'bar',
        data: data,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 1, color: '#188df0' }
          ])
        },
        label: {
          show: true,
          position: 'top',
          fontSize: 10
        }
      }
    ]
  }
})

async function loadStats() {
  if (!dateRange.value[0] || !dateRange.value[1]) return
  try {
    const params: any = { startDate: dateRange.value[0], endDate: dateRange.value[1] }
    if (selectedDepts.value.length > 0) {
      params.deptIds = selectedDepts.value
    }
    if (selectedProjects.value.length > 0) {
      params.projectNames = selectedProjects.value
    }
    const res: any = await outputApi.stats(params)
    stats.value = res.data || {}
  } catch (e) {
    console.error('加载统计数据失败', e)
  }
}

// async function exportData() {
//   if (!dateRange.value[0] || !dateRange.value[1]) return
//   exporting.value = true
//   try {
//     const params: any = {
//       type: 'summary',
//       startDate: dateRange.value[0],
//       endDate: dateRange.value[1]
//     }
//     if (selectedDepts.value.length > 0) {
//       params.deptIds = selectedDepts.value
//     }
//     if (selectedProjects.value.length > 0) {
//       params.projectNames = selectedProjects.value
//     }
//     const res: any = await outputApi.export(params)
//     const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
//     const link = document.createElement('a')
//     link.href = URL.createObjectURL(blob)
//     link.download = `产出统计_看板_${dateRange.value[0]}_${dateRange.value[1]}.xlsx`
//     link.click()
//   } catch (e) {
//     console.error('导出失败', e)
//   } finally {
//     exporting.value = false
//   }
// }

onMounted(() => {
  // 默认加载本月
  dateRange.value = [dayjs().startOf('month').format('YYYY-MM-DD'), dayjs().format('YYYY-MM-DD')]
  loadStats()
})
</script>

<style scoped>
.mb-4 { margin-bottom: 16px; }
.mt-4 { margin-top: 16px; }
.stat-card { text-align: center; padding: 8px 0; }
.stat-value { font-size: 28px; font-weight: bold; color: #409eff; }
.stat-label { font-size: 14px; color: #909399; margin-top: 4px; }
</style>
