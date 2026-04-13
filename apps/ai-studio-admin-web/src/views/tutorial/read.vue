<template>
  <div>
    <el-card>
      <el-button @click="router.back()" style="margin-bottom:16px">返回</el-button>
      <h2>{{ tutorial?.title }}</h2>
      <div class="meta">
        <el-tag v-if="tutorial?.category">{{ tutorial.category }}</el-tag>
        <el-tag v-for="tag in tags" :key="tag" type="info" style="margin-left:4px">{{ tag }}</el-tag>
        <span style="margin-left:16px;color:#909399">浏览 {{ tutorial?.viewCount }} 次</span>
      </div>
      <el-divider />
      <div v-if="content" v-html="renderedContent" class="markdown-body" />
      <el-empty v-else description="内容加载中..." />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { tutorialApi } from '@/api'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const tutorial = ref<any>(null)
const content = ref('')

const tags = computed(() => tutorial.value?.tags?.split(',').filter(Boolean) || [])

// 简单 Markdown 渲染（生产环境建议用 marked 或 md-editor-v3）
const renderedContent = computed(() => {
  return content.value
    .replace(/^### (.+)$/gm, '<h3>$1</h3>')
    .replace(/^## (.+)$/gm, '<h2>$1</h2>')
    .replace(/^# (.+)$/gm, '<h1>$1</h1>')
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/`(.+?)`/g, '<code>$1</code>')
    .replace(/\n/g, '<br/>')
})

onMounted(async () => {
  const id = Number(route.params.id)
  const res = await tutorialApi.detail(id) as any
  tutorial.value = res.data
  if (tutorial.value?.contentUrl) {
    const r = await axios.get(tutorial.value.contentUrl)
    content.value = r.data
  }
})
</script>

<style scoped>
.meta { margin: 8px 0; }
.markdown-body { line-height: 1.8; }
.markdown-body :deep(h1), .markdown-body :deep(h2), .markdown-body :deep(h3) { margin: 16px 0 8px; }
.markdown-body :deep(code) { background: #f5f5f5; padding: 2px 6px; border-radius: 4px; }
</style>
