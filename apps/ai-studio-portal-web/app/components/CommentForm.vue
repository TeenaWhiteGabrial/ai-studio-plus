<template>
  <div class="comment-form">
    <div class="form-header">
      <Icon name="material-symbols:edit-note" class="mr-2" />
      发表评论
    </div>
    <el-input
      v-model="content"
      type="textarea"
      :rows="4"
      placeholder="请输入评论内容..."
      maxlength="1000"
      show-word-limit
    />
    <div class="form-footer">
      <div class="tips">
        <span v-if="!authStore.token" class="text-gray-400">登录后即可发表评论</span>
      </div>
      <el-button type="primary" :loading="submitting" :disabled="!authStore.token" @click="handleSubmit">
        提交评论
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  targetType: 'article' | 'question' | 'answer'
  targetId: string
}>()

const emit = defineEmits<{
  success: []
}>()

const { createComment } = useCommunity()
const authStore = useAuthStore()

const content = ref('')
const submitting = ref(false)

async function handleSubmit() {
  if (!content.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  if (!authStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  submitting.value = true
  try {
    await createComment({
      targetType: props.targetType,
      targetId: props.targetId,
      content: content.value
    })
    ElMessage.success('评论成功')
    content.value = ''
    emit('success')
  }
  catch (err) {
    ElMessage.error('评论失败')
  }
  finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.comment-form {
  @apply mt-6;
}

.form-header {
  @apply text-lg font-medium text-gray-800 mb-4 flex items-center;
}

.form-footer {
  @apply flex items-center justify-between mt-3;
}

.tips {
  @apply text-sm;
}
</style>
