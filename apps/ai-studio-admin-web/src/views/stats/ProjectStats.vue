<!--
========================================================
AI生成标记 [Claude Code]
生成时间: 2026-03-31
组件功能：项目统计页面
包含功能：项目汇总、成员分布、产出趋势图表
修改历史:
  - 2026-03-31: 创建页面，实现项目维度统计展示
========================================================
-->
<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>项目统计</span>
          <div class="filters">
            <TimeRangePicker v-model="dateRange" @change="loadData" />
            <el-button type="primary" @click="exportData" :loading="exporting">
              <el-icon><Download /></el-icon>导出
            </el-button>
          </div>
        </div>
      </template>

      <!-- 项目汇总卡片 -->
      <el-row :gutter="16" class="summary-cards">
        <el-col :span="6" v-for="proj in summaryData" :key="proj.projectName">
          <el-card shadow="hover" class="project-card">
            <div class="project-name">{{ proj.projectName }}</div>
            <div class="project-stats">
              <div class="stat-item">
                <div class="stat-value">{{ proj.memberCount || 0 }}</div>
                <div class="stat-label">参与人数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ proj.totalCodeLines || 0 }}</div>
                <div class="stat-label">代码行</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ proj.javaFileCount || 0 }}</div>
                <div class="stat-label">Java文件</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 项目产出分布 -->
      <el-row :gutter="16" class="mt-4">
        <el-col :span="12">
          <el-card>
            <template #header>项目代码行分布</template>
            <v-chart :option="pieOption" style="height: 300px" autoresize />
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <template #header>项目排行</template>
            <el-table :data="summaryData" border height="300">
              <el-table-column prop="projectName" label="项目" />
              <el-table-column prop="totalCodeLines" label="代码行" align="center" sortable />
              <el-table-column prop="memberCount" label="参与人数" align="center" />
            </el-table>
          </el-card>
        </el-col>
      </el-row>

      <!-- 项目成员明细 -->
      <el-card class="mt-4">
        <template #header>
          <div class="card-header">
            <span>项目成员产出明细</span>
            <el-select v-model="selectedProject" placeholder="选择项目" @change="loadMembers" style="width: 200px">
              <el-option v-for="p in summaryData" :key="p.projectName" :label="p.projectName" :value="p.projectName" />
            </el-select>
          </div>
        </template>
        <el-table :data="membersData" border height="400">
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="realName" label="姓名" />
          <el-table-column prop="totalCodeLines" label="代码行" align="center" sortable />
          <el-table-column prop="javaCodeLines" label="Java代码" align="center" />
          <el-table-column prop="frontendCodeLines" label="前端代码" align="center" />
          <el-table-column prop="apiCount" label="API数" align="center" />
        </el-table>
      </el-card>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart } from 'echarts/charts'
import { TooltipComponent, LegendComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { Download } from '@element-plus/icons-vue'
import { outputApi } from '@/api'
import TimeRangePicker from '@/components/TimeRangePicker.vue'
import dayjs from 'dayjs'

use([CanvasRenderer, PieChart, TooltipComponent, LegendComponent])

const dateRange = ref<[string, string]>(['', ''])
const summaryData = ref<any[]>([])
const membersData = ref<any[]>([])
const selectedProject = ref<string>()
const exporting = ref(false)

// 饼图配置
const pieOption = computed(() => ({
  tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
  legend: { orient: 'vertical', left: 'left' },
  series: [{
    type: 'pie',
    radius: '60%',
    data: summaryData.value.map(p => ({
      name: p.projectName,
      value: p.totalCodeLines || 0
    })).filter(p => p.value > 0)
  }]
}))

async function loadData() {
  if (!dateRange.value[0] || !dateRange.value[1]) return
  try {
    const params = {
      startDate: dateRange.value[0],
      endDate: dateRange.value[1]
    }
    const res: any = await outputApi.projectSummary(params)
    summaryData.value = res.data || []

    // 更新项目列表
    if (summaryData.value.length > 0 && !selectedProject.value) {
      selectedProject.value = summaryData.value[0].projectName
      loadMembers()
    }
  } catch (e) {
    console.error('加载项目统计失败', e)
  }
}

async function loadMembers() {
  if (!selectedProject.value || !dateRange.value[0]) return
  try {
    const params = {
      projectName: selectedProject.value,
      startDate: dateRange.value[0],
      endDate: dateRange.value[1]
    }
    const res: any = await outputApi.projectMembers(params)
    membersData.value = res.data || []
  } catch (e) {
    console.error('加载项目成员失败', e)
  }
}

async function exportData() {
  if (!dateRange.value[0] || !dateRange.value[1]) return
  exporting.value = true
  try {
    const params = {
      type: 'summary',
      projectNames: summaryData.value.map(p => p.projectName),
      startDate: dateRange.value[0],
      endDate: dateRange.value[1]
    }
    const res: any = await outputApi.export(params)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = `产出统计_项目_${dateRange.value[0]}_${dateRange.value[1]}.xlsx`
    link.click()
  } catch (e) {
    console.error('导出失败', e)
  } finally {
    exporting.value = false
  }
}

onMounted(() => {
  dateRange.value = [dayjs().startOf('week').add(1, 'day').format('YYYY-MM-DD'), dayjs().format('YYYY-MM-DD')]
  loadData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.filters {
  display: flex;
  gap: 12px;
  align-items: center;
}
.summary-cards {
  margin-top: 16px;
}
.project-card {
  text-align: center;
}
.project-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 12px;
}
.project-stats {
  display: flex;
  justify-content: space-around;
}
.stat-item {
  text-align: center;
}
.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
}
.stat-label {
  font-size: 12px;
  color: #909399;
}
.mt-4 {
  margin-top: 16px;
}
</style>
