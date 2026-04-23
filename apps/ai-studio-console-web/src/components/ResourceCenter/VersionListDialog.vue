<template>
  <el-dialog v-model="visible" title="Skill 版本列表" width="600px" append-to-body>
    <el-table :data="versionList" stripe>
      <el-table-column prop="version" label="版本号" width="120" />
      <el-table-column prop="changelog" label="更新日志" />
      <el-table-column label="创建时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useResourceUtils } from '@/composables/useResourceCenter'

const props = defineProps<{
  modelValue: boolean
  versionList: any[]
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
}>()

const { formatDate } = useResourceUtils()

const visible = ref(false)

watch(() => props.modelValue, (val) => { visible.value = val })
watch(visible, (val) => { emit('update:modelValue', val) })
</script>
