<template>
  <div class="comment-form">
    <el-input
      v-model="content"
      type="textarea"
      :rows="3"
      placeholder="写下你的评论..."
      maxlength="2000"
      show-word-limit
    />
    <div class="form-footer">
      <span class="tip">支持 Markdown 简化语法</span>
      <el-button type="primary" size="small" @click="handleSubmit">发表评论</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
const emit = defineEmits(['submit'])
const content = ref('')

function handleSubmit() {
  if (!content.value.trim()) return
  emit('submit', content.value)
  content.value = ''
}
</script>

<style scoped>
.comment-form { margin-bottom: 24px; }
.form-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; }
.tip { font-size: 12px; color: #c0c4cc; }
</style>
