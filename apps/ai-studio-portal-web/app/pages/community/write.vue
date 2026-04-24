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
        <ToolsImageUpload
          v-model="formData.coverImage"
          :limit="1"
          key-prefix="portal/articles/covers"
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
              :value="tag.id"
            />
          </el-select>
        </div>
      </div>

      <!-- 内容 -->
      <div class="form-item">
        <label class="form-label">内容</label>
        <div class="editor-container">
          <ClientOnly>
            <QuillEditor
              ref="editorRef"
              v-model:content="formData.content"
              contentType="html"
              :toolbar="toolbarOptions"
              placeholder="请输入文章内容..."
              theme="snow"
              @ready="handleEditorReady"
            />
            <template #fallback>
              <div class="editor-fallback">编辑器加载中...</div>
            </template>
          </ClientOnly>
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
import { defineAsyncComponent } from 'vue'

import '@vueup/vue-quill/dist/vue-quill.snow.css'

const QuillEditor = defineAsyncComponent(() =>
  import('@vueup/vue-quill').then(module => module.QuillEditor)
)

interface UploadApiData {
  file_name?: string
  oss_key?: string
  oss_url?: string
  file_size?: number
}

interface UploadApiResponse {
  code?: number
  message?: string
  data?: UploadApiData
}

definePageMeta({
  middleware: ['auth']
})

const { createArticle } = useArticle()
const { getTagList } = useCommunity()
const config = useRuntimeConfig()
const authStore = useAuthStore()

const formData = ref({
  title: '',
  summary: '',
  coverImage: '',
  tags: [] as string[],
  content: ''
})

const availableTags = ref<{ id: string; name: string }[]>([])
const submitting = ref(false)
const editorRef = ref<any>(null)
const quillInstance = ref<any>(null)

const toolbarOptions = [
  ['bold', 'italic', 'underline', 'strike'],
  ['blockquote', 'code-block'],
  [{ header: 1 }, { header: 2 }],
  [{ list: 'ordered' }, { list: 'bullet' }],
  [{ color: [] }, { background: [] }],
  ['link', 'image', 'video'],
  ['clean']
]

function getAuthHeaders() {
  if (!authStore.token) {
    return undefined
  }
  const tokenType = config.public.tokenType
  const authorization = tokenType ? `${tokenType} ${authStore.token}` : authStore.token
  return { Authorization: authorization }
}

async function uploadMediaFile(file: File, keyPrefix: string) {
  const formData = new FormData()
  formData.append('file', file)

  const res = await $fetch<UploadApiResponse>('/oss/upload', {
    method: 'POST',
    baseURL: config.public.apiBase,
    body: formData,
    params: { keyPrefix },
    headers: getAuthHeaders()
  })

  if (res?.code !== 200 || !res?.data?.oss_url) {
    throw new Error(res?.message || '上传失败')
  }
  return res.data.oss_url
}

function pickLocalFile(accept: string) {
  return new Promise<File | null>((resolve) => {
    const input = document.createElement('input')
    input.type = 'file'
    input.accept = accept
    input.onchange = () => resolve(input.files?.[0] || null)
    input.click()
  })
}

function getEditorInstance() {
  return quillInstance.value || editorRef.value?.getQuill?.()
}

async function handleInsertImage() {
  const quill = getEditorInstance()
  if (!quill) {
    return
  }

  const file = await pickLocalFile('image/*')
  if (!file) {
    return
  }
  if (!file.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件')
    return
  }

  const url = await uploadMediaFile(file, 'portal/articles/content/images')
  const range = quill.getSelection(true)
  const insertIndex = range ? range.index : quill.getLength()
  quill.insertEmbed(insertIndex, 'image', url, 'user')
  quill.setSelection(insertIndex + 1, 0, 'silent')
}

async function handleInsertVideo() {
  const quill = getEditorInstance()
  if (!quill) {
    return
  }

  const file = await pickLocalFile('video/*')
  if (!file) {
    return
  }
  if (!file.type.startsWith('video/')) {
    ElMessage.warning('请选择视频文件')
    return
  }

  const url = await uploadMediaFile(file, 'portal/articles/content/videos')
  const range = quill.getSelection(true)
  const insertIndex = range ? range.index : quill.getLength()
  quill.insertEmbed(insertIndex, 'video', url, 'user')
  quill.setSelection(insertIndex + 1, 0, 'silent')
}

function handleEditorReady(quill: any) {
  quillInstance.value = quill
  const toolbar = quill.getModule('toolbar')
  if (!toolbar) {
    return
  }

  toolbar.addHandler('image', () => {
    handleInsertImage().catch((err) => {
      ElMessage.error(err?.message || '图片上传失败')
    })
  })
  toolbar.addHandler('video', () => {
    handleInsertVideo().catch((err) => {
      ElMessage.error(err?.message || '视频上传失败')
    })
  })
}

function dataUrlToFile(dataUrl: string, prefix: string) {
  const separatorIndex = dataUrl.indexOf(',')
  if (separatorIndex < 0) {
    throw new Error('无效的数据格式')
  }
  const meta = dataUrl.slice(0, separatorIndex)
  const data = dataUrl.slice(separatorIndex + 1)
  const mimeMatch = meta.match(/data:(.*?);base64/)
  const mime = mimeMatch?.[1] || 'application/octet-stream'
  const extension = mime.split('/')[1] || 'bin'
  const binary = atob(data)
  const bytes = new Uint8Array(binary.length)
  for (let i = 0; i < binary.length; i += 1) {
    bytes[i] = binary.charCodeAt(i)
  }
  return new File([bytes], `${prefix}_${Date.now()}.${extension}`, { type: mime })
}

async function normalizeBase64MediaToUrl(html: string) {
  if (!html || !html.includes('data:')) {
    return html
  }

  const doc = new DOMParser().parseFromString(html, 'text/html')
  const mediaElements = Array.from(doc.querySelectorAll<HTMLElement>('[src^="data:"]'))
  if (!mediaElements.length) {
    return html
  }

  for (const element of mediaElements) {
    const src = element.getAttribute('src')
    if (!src || !src.startsWith('data:')) {
      continue
    }
    if (src.startsWith('data:image/')) {
      const file = dataUrlToFile(src, 'image')
      const url = await uploadMediaFile(file, 'portal/articles/content/images')
      element.setAttribute('src', url)
      continue
    }
    if (src.startsWith('data:video/')) {
      const file = dataUrlToFile(src, 'video')
      const url = await uploadMediaFile(file, 'portal/articles/content/videos')
      element.setAttribute('src', url)
    }
  }

  return doc.body.innerHTML
}

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
    const normalizedContent = await normalizeBase64MediaToUrl(formData.value.content)
    formData.value.content = normalizedContent

    await createArticle({
      title: formData.value.title,
      summary: formData.value.summary,
      coverImage: formData.value.coverImage,
      tags: formData.value.tags,
      content: formData.value.content,
      publishType: status === 'draft' ? 0 : 1
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
  @apply border border-gray-200 rounded-lg;
  overflow: visible;
}

.editor-fallback {
  @apply min-h-[300px] flex items-center justify-center text-gray-400 bg-gray-50;
}

.editor-container :deep(.ql-toolbar) {
  position: sticky;
  top: 78px;
  z-index: 30;
  border: none;
  border-bottom: 1px solid #eee;
  @apply bg-gray-50 shadow-sm;
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
