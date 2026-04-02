<!--
========================================================
AI生成标记 [Claude Code]
生成时间: 2026-03-31
组件功能：部门统计页面
包含功能：部门汇总卡片、排行图表、成员明细表格
修改历史:
  - 2026-03-31: 创建页面，实现部门维度统计展示
========================================================
-->
<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>部门统计</span>
          <div class="filters">
            <TimeRangePicker v-model="dateRange" @change="loadData" />
            <DeptSelector v-model="selectedDepts" @change="loadData" />
            <el-button type="primary" @click="loadData">
              <el-icon><Search /></el-icon>搜索
            </el-button>
            <!-- <el-button type="primary" @click="exportData" :loading="exporting">
              <el-icon><Download /></el-icon>导出
            </el-button> -->
          </div>
        </div>
      </template>

      <!-- 部门汇总卡片 -->
      <el-row :gutter="16" class="summary-cards">
        <el-col :span="6" v-for="dept in summaryData" :key="dept.deptId">
          <el-card shadow="hover" class="dept-card">
            <div class="dept-name">{{ dept.deptName }}</div>
            <div class="dept-stats">
              <div class="stat-item">
                <div class="stat-value">{{ dept.memberCount || 0 }}</div>
                <div class="stat-label">成员数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ dept.totalCodeLines || 0 }}</div>
                <div class="stat-label">代码行</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ (dept.prdDocCount || 0) + (dept.apiDocCount || 0) }}</div>
                <div class="stat-label">文档数</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 部门排行图表 -->
      <el-row :gutter="16" class="mt-4">
        <el-col :span="12">
          <el-card>
            <template #header>部门代码行排行</template>
            <v-chart :option="codeLinesOption" style="height: 300px" autoresize />
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <template #header>部门文档产出排行</template>
            <v-chart :option="docOption" style="height: 300px" autoresize />
          </el-card>
        </el-col>
      </el-row>

      <!-- 部门成员明细 -->
      <el-card class="mt-4">
        <template #header>
          <div class="card-header">
            <span>部门成员产出明细</span>
            <el-select v-model="selectedDeptForMembers" placeholder="选择部门" @change="loadMembers" style="width: 200px">
              <el-option v-for="d in summaryData" :key="d.deptId" :label="d.deptName" :value="d.deptId" />
            </el-select>
          </div>
        </template>
        <el-table :data="membersData" border height="400">
          <el-table-column prop="username" label="用户名"  />
          <el-table-column prop="realName" label="姓名"  />
          <el-table-column prop="totalCodeLines" label="代码行" align="center" sortable />
          <el-table-column prop="prdDocCount" label="PRD" align="center" />
          <el-table-column prop="apiDocCount" label="API文档" align="center" />
          <el-table-column prop="javaCodeLines" label="Java代码" align="center" />
          <el-table-column prop="frontendCodeLines" label="前端代码" align="center" />
        </el-table>
      </el-card>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import VChart from 'vue-echarts'
// import { Download } from '@element-plus/icons-vue'
import { outputApi } from '@/api'
import TimeRangePicker from '@/components/TimeRangePicker.vue'
import DeptSelector from '@/components/DeptSelector.vue'
import dayjs from 'dayjs'

use([CanvasRenderer, BarChart, GridComponent, TooltipComponent, LegendComponent])

const dateRange = ref<[string, string]>(['', ''])
const selectedDepts = ref<number[]>([])
const summaryData = ref<any[]>([])
const membersData = ref<any[]>([])
const selectedDeptForMembers = ref<number>()

// 图表配置
const codeLinesOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  xAxis: { type: 'category', data: summaryData.value.map(d => d.deptName) },
  yAxis: { type: 'value' },
  series: [{
    data: summaryData.value.map(d => d.totalCodeLines || 0),
    type: 'bar',
    itemStyle: { color: '#409eff' }
  }]
}))

const docOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  xAxis: { type: 'category', data: summaryData.value.map(d => d.deptName) },
  yAxis: { type: 'value' },
  series: [{
    data: summaryData.value.map(d => (d.prdDocCount || 0) + (d.apiDocCount || 0)),
    type: 'bar',
    itemStyle: { color: '#67c23a' }
  }]
}))

async function loadData() {
  if (!dateRange.value[0] || !dateRange.value[1]) return
  try {
    const params = {
      startDate: dateRange.value[0],
      endDate: dateRange.value[1],
      deptIds: selectedDepts.value
    }
    const res: any = await outputApi.deptSummary(params)
    summaryData.value = res.data || []

    // 默认选中第一个部门查看成员
    if (summaryData.value.length > 0 && !selectedDeptForMembers.value) {
      selectedDeptForMembers.value = summaryData.value[0].deptId
      loadMembers()
    }
  } catch (e) {
    console.error('加载部门统计失败', e)
  }
}

async function loadMembers() {
  if (!selectedDeptForMembers.value || !dateRange.value[0]) return
  try {
    const params = {
      deptId: selectedDeptForMembers.value,
      startDate: dateRange.value[0],
      endDate: dateRange.value[1]
    }
    const res: any = await outputApi.deptMembers(params)
    membersData.value = res.data || []
  } catch (e) {
    console.error('加载部门成员失败', e)
  }
}

// async function exportData() {
//   if (!dateRange.value[0] || !dateRange.value[1]) return
//   exporting.value = true
//   try {
//     const params = {
//       type: 'summary',
//       deptIds: selectedDepts.value,
//       startDate: dateRange.value[0],
//       endDate: dateRange.value[1]
//     }
//     const res: any = await outputApi.export(params)
//     const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
//     const link = document.createElement('a')
//     link.href = URL.createObjectURL(blob)
//     link.download = `产出统计_部门_${dateRange.value[0]}_${dateRange.value[1]}.xlsx`
//     link.click()
//   } catch (e) {
//     console.error('导出失败', e)
//   } finally {
//     exporting.value = false
//   }
// }

onMounted(() => {
  // 默认加载本周
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
.dept-card {
  text-align: center;
}
.dept-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 12px;
}
.dept-stats {
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
