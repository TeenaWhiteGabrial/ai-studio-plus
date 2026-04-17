<template>
  <div class="ask-page">
    <div class="ask-container bg-white rounded-lg p-6">
      <h1 class="page-title">发布问题</h1>

      <!-- 标题 -->
      <div class="form-item">
        <label class="form-label">问题标题</label>
        <el-input
          v-model="formData.title"
          placeholder="请输入问题标题，简洁明了"
          maxlength="100"
          show-word-limit
        />
      </div>

      <!-- 标签 -->
      <div class="form-item">
        <label class="form-label">标签</label>
        <div class="tag-selector">
          <el-select
            v-model="formData.tags"
            multiple
            filterable
            allow-create
            placeholder="选择或输入标签"
            style="width: 100%"
          >
            <el-option
              v-for="tag in availableTags"
              :key="tag.id"
              :label="tag.name"
              :value="tag.name"
            />
          </el-select>
        </div>
      </div>

      <!-- 内容 -->
      <div class="form-item">
        <label class="form-label">问题详情</label>
        <div class="editor-container">
          <QuillEditor
            v-model:content="formData.content"
            contentType="html"
            :toolbar="toolbarOptions"
            placeholder="请详细描述您的问题..."
            theme="snow"
          />
        </div>
      </div>

      <!-- 提示 -->
      <div class="tips-box">
        <Icon name="material-symbols:info-outline" class="mr-2 text-blue-500" />
        <span>提问建议：</span>
        <ul class="tips-list">
          <li>确保问题描述清晰、具体</li>
          <li>附上相关的代码、错误信息或截图</li>
          <li>选择合适的标签，便于他人找到您的问题</li>
        </ul>
      </div>

      <!-- 提交按钮 -->
      <div class="form-actions">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">发布问题</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'

definePageMeta({
  middleware: ['auth']
})

const { createQuestion } = useQuestion()
const { getTagList } = useCommunity()

const formData = ref({
  title: '',
  tags: [] as string[],
  content: ''
})

const availableTags = ref<{ id: string; name: string }[]>([])
const submitting = ref(false)

const toolbarOptions = [
  ['bold', 'italic', 'underline'],
  ['blockquote', 'code-block'],
  [{ list: 'ordered' }, { list: 'bullet' }],
  ['link', 'image'],
  ['clean']
]

async function loadTags() {
  try {
    const res = await getTagList()
    availableTags.value = res || []
  }
  catch (err) {
    console.error('加载标签失败:', err)
  }
}

async function handleSubmit() {
  if (!formData.value.title.trim()) {
    ElMessage.warning('请输入问题标题')
    return
  }
  if (!formData.value.content.trim()) {
    ElMessage.warning('请输入问题详情')
    return
  }
  submitting.value = true
  try {
    await createQuestion({
      title: formData.value.title,
      tags: formData.value.tags,
      content: formData.value.content
    })
    ElMessage.success('问题发布成功')
    navigateTo('/community')
  }
  catch (err) {
    ElMessage.error('发布失败')
  }
  finally {
    submitting.value = false
  }
}

function handleCancel() {
  navigateTo('/community')
}

onMounted(() => {
  loadTags()
})
</script>

<style scoped>
.ask-page {
  @apply max-w-4xl mx-auto;
}

.ask-container {
  @apply p-8;
}

.page-title {
  @apply text-2xl font-bold text-gray-800 mb-6;
}

.form-item {
  @apply mb-6;
}

.form-label {
  @apply block text-sm font-medium text-gray-700 mb-2;
}

.tag-selector {
  @apply w-full;
}

.editor-container {
  @apply border border-gray-200 rounded-lg overflow-hidden;
}

.editor-container :deep(.ql-toolbar) {
  border: none;
  border-bottom: 1px solid #eee;
  @apply bg-gray-50;
}

.editor-container :deep(.ql-container) {
  border: none;
  @apply text-base;
}

.editor-container :deep(.ql-editor) {
  @apply min-h-[250px];
}

.tips-box {
  @apply bg-blue-50 p-4 rounded-lg mb-6;
}

.tips-box ul {
  @apply mt-2 ml-6 text-sm text-blue-600 space-y-1;
}

.form-actions {
  @apply flex justify-end gap-4 pt-6 border-t border-gray-100;
}
</style>
