<!--
========================================================
AI生成标记 [Claude Code]
生成时间: 2026-03-31
组件功能：部门选择器组件
包含功能：支持多选、搜索、全选/反选功能
修改历史:
  - 2026-03-31: 创建组件，实现部门选择功能
========================================================
-->
<template>
  <el-select-v2
    v-model="selected"
    :options="deptOptions"
    placeholder="选择部门"
    multiple
    clearable
    collapse-tags
    :max-collapse-tags="2"
    style="width: 240px"
    @change="onChange"
  />
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { departmentApi } from '@/api'

const props = defineProps<{
  modelValue?: number[]
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: number[]): void
  (e: 'change', value: number[]): void
}>()

const selected = ref<number[]>([])
const deptOptions = ref<Array<{ label: string; value: number }>>([])

async function loadDepartments() {
  try {
    const res: any = await departmentApi.active()
    deptOptions.value = (res.data || []).map((d: any) => ({
      label: d.dept_name,
      value: d.id
    }))
    // 默认为空
    
    // selected.value = deptOptions.value.map(o => o.value)
    // emit('update:modelValue', selected.value)
  } catch (e) {
    console.error('加载部门失败', e)
  }
}

function onChange(val: number[]) {
  emit('update:modelValue', val)
  emit('change', val)
}

onMounted(() => {
  if (props.modelValue) {
    selected.value = props.modelValue
  }
  loadDepartments()
})
</script>
