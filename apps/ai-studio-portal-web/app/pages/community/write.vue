<template>
  <div class="write-page">
    <div class="write-container bg-white rounded-lg p-6">
      <h1 class="page-title">发布文章</h1>

      <!-- 标题 -->
      <div class="form-item">
        <label class="form-label">标题</label>
        <el-input
          v-model="formData.title"
          placeholder="请输入文章标题"
          maxlength="100"
          show-word-limit
        />
      </div>

      <!-- 摘要 -->
      <div class="form-item">
        <label class="form-label">摘要</label>
        <el-input
          v-model="formData.summary"
          type="textarea"
          :rows="2"
          placeholder="请输入文章摘要（可选）"
          maxlength="200"
        />
      </div>

      <!-- 封面图 -->
      <div class="form-item">
        <label class="form-label">封面图</label>
        <ImageUpload v-model="formData.coverImage" :limit="1" />
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
        <label class="form-label">内容</label>
        <div class="editor-container">
          <QuillEditor
            v-model:content="formData.content"
            contentType="html"
            :toolbar="toolbarOptions"
            placeholder="请输入文章内容..."
            theme="snow"
          />
        </div>
      </div>

      <!-- 提交按钮 -->
      <div class="form-actions">
        <el-button @click="handleSaveDraft">保存草稿</el-button>
        <el-button type="primary" :loading="submitting" @click="handlePublish">发布</el-button>
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

const { createArticle, updateArticle } = useArticle()
const { getTagList } = useCommunity()

const formData = ref({
  title: '',
  summary: '',
  coverImage: '',
  tags: [] as string[],
  content: ''
})

const availableTags = ref<{ id: string; name: string }[]>([])
const submitting = ref(false)

const toolbarOptions = [
  ['bold', 'italic', 'underline', 'strike'],
  ['blockquote', 'code-block'],
  [{ header: 1 }, { header: 2 }],
  [{ list: 'ordered' }, { list: 'bullet' }],
  [{ color: [] }, { background: [] }],
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

async function handleSaveDraft() {
  await publishArticle('draft')
}

async function handlePublish() {
  if (!formData.value.title.trim()) {
    ElMessage.warning('请输入文章标题')
    return
  }
  if (!formData.value.content.trim()) {
    ElMessage.warning('请输入文章内容')
    return
  }
  await publishArticle('published')
}

async function publishArticle(status: 'draft' | 'published') {
  submitting.value = true
  try {
    await createArticle({
      title: formData.value.title,
      summary: formData.value.summary,
      coverImage: formData.value.coverImage,
      tags: formData.value.tags,
      content: formData.value.content
    })
    ElMessage.success(status === 'draft' ? '草稿保存成功' : '发布成功')
    navigateTo('/community')
  }
  catch (err) {
    ElMessage.error('操作失败')
  }
  finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadTags()
})
</script>

<style scoped>
.write-page {
  @apply max-w-4xl mx-auto;
}

.write-container {
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
  @apply min-h-[300px];
}

.form-actions {
  @apply flex justify-end gap-4 mt-8 pt-6 border-t border-gray-100;
}
</style>
