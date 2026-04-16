<template>
  <div class="ask-page">
    <div class="ask-card">
      <h2 class="card-title">发布问题</h2>
      <el-form label-position="top">
        <el-form-item label="问题标题">
          <el-input v-model="form.title" placeholder="请输入问题标题，简洁明了" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="问题详情">
          <el-input v-model="form.content" type="textarea" :rows="8" placeholder="请详细描述您的问题..." />
        </el-form-item>
        <el-form-item label="标签">
          <el-select v-model="form.tags" multiple placeholder="选择相关标签" style="width:100%">
            <el-option v-for="tag in availableTags" :key="tag" :label="tag" :value="tag" />
          </el-select>
        </el-form-item>
      </el-form>
      <div class="form-actions">
        <el-button @click="$router.back()">取消</el-button>
        <el-button type="primary" @click="handleSubmit">发布</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth' })
useHead({ title: '发布问题 - AI Studio' })

const form = ref({ title: '', content: '', tags: [] as string[] })
const availableTags = ref(['AI Studio', 'Skill', 'Plugin', 'MCP', '开发问题', '使用咨询'])
async function handleSubmit() {
  if (!form.value.title || !form.value.content) { alert('标题和内容不能为空'); return }
  alert('问题发布成功')
}
</script>

<style scoped>
.ask-page { display: flex; flex-direction: column; }
.ask-card { background: #fff; border-radius: 8px; padding: 24px; box-shadow: 0 1px 4px rgba(0,0,0,0.04); }
.card-title { font-size: 18px; font-weight: 600; color: #1a1a2e; margin-bottom: 24px; padding-bottom: 16px; border-bottom: 2px solid #f0f2f5; }
.form-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 20px; }
</style>
