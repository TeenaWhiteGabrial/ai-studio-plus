<!--
========================================================
AI生成标记 [Claude Code]
生成时间: 2026-03-31
组件功能：项目选择器组件
包含功能：支持多选、搜索、全选/反选功能
修改历史:
  - 2026-03-31: 创建组件，实现项目选择功能
========================================================
-->
<template>
  <el-select-v2
    v-model="selected"
    :options="projectOptions"
    placeholder="选择项目"
    multiple
    clearable
    collapse-tags
    :max-collapse-tags="2"
    style="width: 240px"
    @change="onChange"
  />
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps<{
  modelValue?: string[]
  projects?: string[]  // 可选的项目列表
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string[]): void
  (e: 'change', value: string[]): void
}>()

const selected = ref<string[]>([])
const projectOptions = ref<Array<{ label: string; value: string }>>([])

function updateOptions() {
  if (props.projects) {
    projectOptions.value = props.projects.map(p => ({ label: p, value: p }))
  }
}

function onChange(val: string[]) {
  emit('update:modelValue', val)
  emit('change', val)
}

watch(() => props.projects, updateOptions, { immediate: true })
watch(() => props.modelValue, (val) => {
  if (val) selected.value = val
}, { immediate: true })
</script>
