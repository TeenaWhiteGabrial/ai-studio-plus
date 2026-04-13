<template>
  <div class="stats-page">
    <el-card>
      <template #header>
        <span>产出统计</span>
      </template>

      <el-row :gutter="20">
        <el-col :span="12">
          <div ref="chartRef" class="chart-container"></div>
        </el-col>
        <el-col :span="12">
          <div ref="pieChartRef" class="chart-container"></div>
        </el-col>
      </el-row>

      <el-table :data="tableData" class="mt-20">
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="type" label="产出类型" width="120" />
        <el-table-column prop="content" label="产出内容" />
        <el-table-column prop="project" label="所属项目" width="150" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const chartRef = ref()
const pieChartRef = ref()

const tableData = ref([])

onMounted(() => {
  // ECharts 图表初始化
})
</script>

<style scoped>
.stats-page {
  padding: 0;
}

.chart-container {
  height: 300px;
  background: #f5f7fa;
  border-radius: 8px;
}

.mt-20 {
  margin-top: 20px;
}
</style>
