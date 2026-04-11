<!--
========================================================
AI生成标记 [Claude Code]
生成时间: 2026-03-31
组件功能：时间范围选择器组件
包含功能：支持固定周期（日/周/月/季度/年）和自定义日期范围
修改历史:
  - 2026-03-31: 创建组件，实现时间范围选择功能
========================================================
-->
<template>
  <div class="time-range-picker">
    <el-radio-group v-model="periodType" @change="onPeriodChange" size="small">
      <el-radio-button label="day">本日</el-radio-button>
      <el-radio-button label="week">本周</el-radio-button>
      <el-radio-button label="month">本月</el-radio-button>
      <el-radio-button label="quarter">本季度</el-radio-button>
      <el-radio-button label="year">本年</el-radio-button>
      <el-radio-button label="custom">自定义</el-radio-button>
    </el-radio-group>
    <el-date-picker
      v-if="periodType === 'custom'"
      v-model="dateRange"
      type="daterange"
      value-format="YYYY-MM-DD"
      range-separator="至"
      start-placeholder="开始日期"
      end-placeholder="结束日期"
      size="small"
      @change="onDateRangeChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import dayjs from 'dayjs'
import quarterOfYear from 'dayjs/plugin/quarterOfYear'

// 扩展 dayjs 以支持季度
dayjs.extend(quarterOfYear)

const props = defineProps<{
  modelValue?: [string, string]
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: [string, string]): void
  (e: 'change', value: [string, string]): void
}>()

const periodType = ref('week')
const dateRange = ref<[string, string]>(['', ''])

// 根据周期类型计算日期范围
function calcDateRange(type: string): [string, string] {
  const today = dayjs()
  switch (type) {
    case 'day':
      return [today.format('YYYY-MM-DD'), today.format('YYYY-MM-DD')]
    case 'week':
      return [today.startOf('week').add(1, 'day').format('YYYY-MM-DD'), today.format('YYYY-MM-DD')]
    case 'month':
      return [today.startOf('month').format('YYYY-MM-DD'), today.format('YYYY-MM-DD')]
    case 'quarter':
      return [today.startOf('quarter').format('YYYY-MM-DD'), today.format('YYYY-MM-DD')]
    case 'year':
      return [today.startOf('year').format('YYYY-MM-DD'), today.format('YYYY-MM-DD')]
    default:
      return dateRange.value
  }
}

function onPeriodChange(val: string | number | boolean | undefined) {
  if (typeof val === 'string' && val !== 'custom') {
    const range = calcDateRange(val)
    dateRange.value = range
    emit('update:modelValue', range)
    emit('change', range)
  }
}

function onDateRangeChange(val: [string, string] | null) {
  if (val) {
    emit('update:modelValue', val)
    emit('change', val)
  }
}

// 初始化
watch(() => props.modelValue, (val) => {
  if (val) {
    dateRange.value = val
  }
}, { immediate: true })

// 默认触发一次
const initRange = calcDateRange('week')
dateRange.value = initRange
emit('update:modelValue', initRange)
</script>

<style scoped>
.time-range-picker {
  display: flex;
  align-items: center;
  gap: 12px;
}
</style>
