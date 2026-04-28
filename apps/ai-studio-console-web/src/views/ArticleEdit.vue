<template>
  <div class="article-edit">
    <el-card class="edit-card">
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="article-form">
        <div class="form-main">
          <el-form-item label="文章标题" prop="title">
            <el-input v-model="form.title" maxlength="100" show-word-limit placeholder="请输入文章标题" />
          </el-form-item>

          <el-form-item label="文章摘要">
            <el-input v-model="form.summary" type="textarea" :rows="3" maxlength="200" placeholder="请输入文章摘要" />
          </el-form-item>

          <div class="side-mode-panel">
            <div class="side-section-label">编辑模式</div>
            <div class="side-mode-buttons">
              <button type="button" class="side-mode-btn" :class="{ active: editorMode === 'rich' }" @click="setEditorMode('rich')">
                富文本
              </button>
              <button type="button" class="side-mode-btn" :class="{ active: editorMode === 'markdown' }" @click="setEditorMode('markdown')">
                Markdown
              </button>
            </div>
          </div>

          <el-form-item label="所属文件夹">
            <el-tree-select
              v-model="form.folderId"
              :data="folderTree"
              check-strictly
              clearable
              placeholder="未分类"
              style="width: 100%"
            />
          </el-form-item>

          <el-form-item label="封面图">
            <div class="cover-uploader" @click="handleCoverClick">
              <input ref="coverInput" type="file" accept="image/*" class="cover-file-input" @change="handleCoverChange" />
              <div class="cover-preview" :class="{ empty: !form.coverImage }">
                <img v-if="form.coverImage" :src="form.coverImage" alt="文章封面" />
                <div v-else class="cover-placeholder">
                  <el-icon><Picture /></el-icon>
                  <strong>上传文章封面</strong>
                  <span>支持 JPG、PNG，大小不超过 2MB</span>
                </div>
                <div v-if="coverUploading" class="cover-mask">
                  <el-icon><UploadFilled /></el-icon>
                  上传中...
                </div>
              </div>
              <div class="cover-side" @click.stop>
                <div class="cover-actions">
                  <el-button class="soft-button" :loading="coverUploading" @click="handleCoverClick">
                    <el-icon><Camera /></el-icon>
                    {{ form.coverImage ? '更换封面' : '上传封面' }}
                  </el-button>
                  <el-button v-if="form.coverImage" class="danger-soft-button" @click="clearCover">
                    <el-icon><Delete /></el-icon>
                    移除
                  </el-button>
                </div>
                <el-input
                  v-model="form.coverImage"
                  class="cover-url-input"
                  clearable
                  placeholder="也可以粘贴图片地址"
                  @click.stop
                />
              </div>
            </div>
          </el-form-item>

          <el-form-item label="文章标签">
            <el-select v-model="form.tagIds" multiple filterable placeholder="请选择标签" style="width: 100%">
              <el-option v-for="tag in tags" :key="tag.id" :label="tag.name" :value="tag.id" />
            </el-select>
          </el-form-item>

          <el-form-item label="文章内容" prop="content" class="content-form-item">
            <div v-if="editorMode === 'rich'" class="rich-editor">
              <QuillEditor
                v-model:content="form.content"
                content-type="html"
                :toolbar="toolbarOptions"
                placeholder="请输入文章内容..."
                theme="snow"
              />
            </div>

            <div v-else class="markdown-editor">
              <div class="markdown-pane">
                <div class="pane-title">Markdown</div>
                <el-input v-model="form.markdown" type="textarea" resize="none" class="markdown-input" placeholder="# 标题&#10;&#10;输入 Markdown 内容..." />
              </div>
              <div class="markdown-pane">
                <div class="pane-title">预览</div>
                <div class="markdown-preview" v-html="markdownPreviewHtml" />
              </div>
            </div>
          </el-form-item>
        </div>
      </el-form>
    </el-card>

    <div class="floating-actions">
      <button class="fab secondary" title="返回文章列表" @click="goArticleList">
        <el-icon><ArrowLeft /></el-icon>
        <span>返回</span>
      </button>
      <el-popover placement="left" trigger="click" width="260" popper-class="publish-mode-popover">
        <template #reference>
          <button class="fab publish" :class="{ active: form.publishType !== 0 }" :title="`发布模式：${getPublishModeText()}`">
            <span>{{ getPublishModeText() }}</span>
          </button>
        </template>
        <div class="publish-popover">
          <div class="publish-title">发布模式</div>
          <button class="publish-option" :class="{ active: form.publishType === 0 }" @click="setPublishType(0)">
            <strong>保存为草稿</strong>
            <span>暂不发布，保存在我的文章里</span>
          </button>
          <button class="publish-option" :class="{ active: form.publishType === 1 }" @click="setPublishType(1)">
            <strong>立即发布</strong>
            <span>保存后马上在 Portal 展示</span>
          </button>
          <button class="publish-option" :class="{ active: form.publishType === 2 }" @click="setPublishType(2)">
            <strong>定时发布</strong>
            <span>保存后按指定时间发布</span>
          </button>
          <el-date-picker
            v-if="form.publishType === 2"
            v-model="form.scheduledPublishTime"
            type="datetime"
            value-format="YYYY-MM-DDTHH:mm:ss"
            placeholder="选择发布时间"
            style="width: 100%; margin-top: 10px"
            :disabled-date="disabledDate"
          />
        </div>
      </el-popover>
      <button class="fab primary" :class="{ loading: saving }" title="保存" @click="handleSubmit">
        <el-icon><Check /></el-icon>
        <span>保存</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Camera, Check, Delete, Picture, UploadFilled } from '@element-plus/icons-vue'
import { QuillEditor } from '@vueup/vue-quill'
import { articleApi, authApi, type ArticleFolder } from '@/api'
import '@vueup/vue-quill/dist/vue-quill.snow.css'

type EditorMode = 'rich' | 'markdown'

interface FolderNode {
  id: number
  value: number
  label: string
  children?: FolderNode[]
}

const router = useRouter()
const route = useRoute()
const formRef = ref()
const coverInput = ref<HTMLInputElement | null>(null)
const saving = ref(false)
const coverUploading = ref(false)
const isNew = route.params.id === 'new'
const editorMode = ref<EditorMode>('rich')
const tags = ref<any[]>([])
const folders = ref<ArticleFolder[]>([])

const form = reactive({
  id: null as number | null,
  title: '',
  summary: '',
  content: '',
  markdown: '',
  coverImage: '',
  folderId: route.query.folderId ? Number(route.query.folderId) : null as number | null,
  tagIds: [] as number[],
  publishType: 0,
  scheduledPublishTime: null as string | null,
})

const rules = {
  title: [{ required: true, message: '请输入文章标题', trigger: 'blur' }],
  content: [{ validator: validateContent, trigger: 'blur' }],
}

const folderTree = computed<FolderNode[]>(() => buildFolderTree(folders.value))
const markdownPreviewHtml = computed(() => markdownToHtml(form.markdown))
const toolbarOptions = [
  ['bold', 'italic', 'underline'],
  ['blockquote', 'code-block'],
  [{ list: 'ordered' }, { list: 'bullet' }],
  ['link', 'image'],
  ['clean'],
]

function buildFolderTree(items: ArticleFolder[], parentId: number | null = null): FolderNode[] {
  return items
    .filter(item => (item.parentId ?? null) === parentId)
    .map(item => ({
      id: item.id,
      value: item.id,
      label: item.folderName,
      children: buildFolderTree(items, item.id),
    }))
}

function validateContent(_rule: any, _value: string, callback: any) {
  if (!getActiveContentText()) {
    callback(new Error('请输入文章内容'))
    return
  }
  callback()
}

function disabledDate(time: Date) {
  return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

function handleCoverClick() {
  if (coverUploading.value) return
  coverInput.value?.click()
}

function handleCoverChange(event: Event) {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (file && beforeCoverUpload(file)) {
    handleCoverUpload(file)
  }
  target.value = ''
}

function beforeCoverUpload(file: File) {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('封面图片大小不能超过 2MB')
    return false
  }
  return true
}

async function handleCoverUpload(file: File) {
  try {
    coverUploading.value = true
    const res = await authApi.uploadAvatar(file) as any
    const url = res?.data?.oss_url || res?.data?.ossUrl || res?.oss_url || res?.url
    if (res?.code === 200 && url) {
      form.coverImage = url
      ElMessage.success('封面上传成功')
      return
    }
    ElMessage.error(res?.message || '封面上传失败')
  } catch (error: any) {
    ElMessage.error(error.message || '封面上传失败')
  } finally {
    coverUploading.value = false
  }
}

function clearCover() {
  form.coverImage = ''
}

function setRichContent(value: string) {
  form.content = value || ''
}

function escapeHtml(value: string) {
  return value.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#39;')
}

function inlineMarkdown(value: string) {
  return value
    .replace(/!\[([^\]]*)\]\((https?:\/\/[^)\s]+)\)/g, '<img src="$2" alt="$1">')
    .replace(/\[([^\]]+)\]\((https?:\/\/[^)\s]+)\)/g, '<a href="$2" target="_blank" rel="noopener noreferrer">$1</a>')
    .replace(/`([^`]+)`/g, '<code>$1</code>')
    .replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
    .replace(/\*([^*]+)\*/g, '<em>$1</em>')
}

function markdownToHtml(markdown: string) {
  const html: string[] = []
  const list: string[] = []
  let code: string[] = []
  let inCode = false
  const flushList = () => {
    if (list.length) {
      html.push(`<ul>${list.map(item => `<li>${inlineMarkdown(item)}</li>`).join('')}</ul>`)
      list.length = 0
    }
  }
  for (const raw of escapeHtml(markdown).split(/\r?\n/)) {
    const line = raw.trim()
    if (line.startsWith('```')) {
      if (inCode) {
        html.push(`<pre><code>${code.join('\n')}</code></pre>`)
        code = []
      }
      inCode = !inCode
      continue
    }
    if (inCode) {
      code.push(raw)
      continue
    }
    if (!line) {
      flushList()
      continue
    }
    const heading = line.match(/^(#{1,6})\s+(.+)$/)
    if (heading) {
      flushList()
      html.push(`<h${heading[1].length}>${inlineMarkdown(heading[2])}</h${heading[1].length}>`)
      continue
    }
    const unordered = line.match(/^[-*]\s+(.+)$/)
    if (unordered) {
      list.push(unordered[1])
      continue
    }
    if (line.startsWith('> ')) {
      flushList()
      html.push(`<blockquote>${inlineMarkdown(line.slice(2))}</blockquote>`)
      continue
    }
    flushList()
    html.push(`<p>${inlineMarkdown(line)}</p>`)
  }
  if (inCode) html.push(`<pre><code>${code.join('\n')}</code></pre>`)
  flushList()
  return html.join('\n') || '<p class="empty">预览内容将在这里显示</p>'
}

function getActiveContentText() {
  if (editorMode.value === 'markdown') return form.markdown.trim()
  return form.content.replace(/<[^>]*>/g, '').trim()
}

function getSubmitContent() {
  if (editorMode.value === 'markdown') return markdownToHtml(form.markdown)
  return form.content
}

async function loadOptions() {
  const [folderList, tagList] = await Promise.all([articleApi.folders(), articleApi.tags()])
  folders.value = folderList
  tags.value = tagList
}

async function loadArticle() {
  if (isNew) {
    setRichContent('')
    return
  }
  const article = await articleApi.detail(Number(route.params.id))
  Object.assign(form, {
    id: article.id,
    title: article.title,
    summary: article.summary || '',
    content: article.content || '',
    coverImage: article.coverImage || '',
    folderId: article.folderId || null,
    tagIds: article.tagIds || [],
    publishType: article.status === 1 ? 1 : 0,
    scheduledPublishTime: article.status === 0 && article.publishedAt ? article.publishedAt : null,
  })
  setRichContent(form.content)
}

function setEditorMode(mode: EditorMode) {
  editorMode.value = mode
}

function setPublishType(type: number) {
  form.publishType = type
  if (type !== 2) form.scheduledPublishTime = null
}

function getPublishModeText() {
  return ({ 0: '保存为草稿', 1: '立即发布', 2: '定时发布' } as Record<number, string>)[form.publishType] || '保存为草稿'
}

async function handleSubmit() {
  try {
    await formRef.value?.validate()
  } catch {
    return
  }
  if (form.publishType === 2 && !form.scheduledPublishTime) {
    ElMessage.warning('请选择定时发布时间')
    return
  }

  saving.value = true
  try {
    const data = {
      title: form.title,
      summary: form.summary,
      content: getSubmitContent(),
      coverImage: form.coverImage,
      folderId: form.folderId,
      tagIds: form.tagIds,
      publishType: form.publishType,
      scheduledPublishTime: form.publishType === 2 ? form.scheduledPublishTime : null,
    }
    if (isNew) {
      await articleApi.create(data)
      ElMessage.success('创建成功')
    } else {
      await articleApi.update(form.id!, data)
      ElMessage.success('更新成功')
    }
    goArticleList()
  } finally {
    saving.value = false
  }
}

function goArticleList() {
  router.push({ name: 'Article', query: form.folderId ? { folderId: form.folderId } : {} })
}

onMounted(async () => {
  await loadOptions()
  await loadArticle()
})
</script>

<style scoped>
.article-edit {
  width: 100%;
  margin: -20px -20px 0;
  min-height: calc(100vh - 84px);
}

.edit-card {
  background: hsl(var(--card));
  border: 1px solid hsl(var(--border));
  border-radius: 8px;
}

.edit-card {
  margin: 0 20px 20px;
  overflow: visible;
}

.edit-card :deep(.el-card__body) {
  padding: 18px;
}

.article-form {
  display: block;
}

.form-main {
  min-width: 0;
  width: 100%;
}

.content-form-item {
  margin-bottom: 0;
}

.side-mode-panel {
  margin-bottom: 18px;
  padding: 12px;
  border-radius: 8px;
  background: hsl(var(--card));
  border: 1px solid hsl(var(--border));
}

.side-section-label {
  margin-bottom: 10px;
  font-size: 13px;
  font-weight: 700;
  color: hsl(var(--foreground));
}

.side-mode-buttons {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.side-mode-btn {
  height: 38px;
  border: 1px solid hsl(var(--border));
  border-radius: 8px;
  background: hsl(var(--secondary));
  color: hsl(var(--muted-foreground));
  cursor: pointer;
  font: inherit;
  font-weight: 700;
  transition: all 0.18s ease;
}

.side-mode-btn:hover {
  color: hsl(var(--primary));
  border-color: color-mix(in srgb, hsl(var(--primary)) 32%, hsl(var(--border)));
}

.side-mode-btn.active {
  color: white;
  background: hsl(var(--primary));
  border-color: hsl(var(--primary));
  box-shadow: 0 0 0 4px color-mix(in srgb, hsl(var(--primary)) 14%, transparent);
}

.cover-uploader {
  width: 100%;
  display: grid;
  grid-template-columns: minmax(260px, 360px) minmax(0, 1fr);
  align-items: stretch;
  gap: 12px;
  padding: 14px;
  border: 1px solid hsl(var(--border));
  border-radius: 8px;
  background: hsl(var(--card));
  cursor: pointer;
}

.cover-file-input {
  display: none;
}

.cover-preview {
  position: relative;
  aspect-ratio: 16 / 9;
  border-radius: 8px;
  overflow: hidden;
  background: hsl(var(--card));
  border: 1px solid hsl(var(--border));
}

.cover-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-preview.empty {
  border-style: dashed;
}

.cover-placeholder,
.cover-mask {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  gap: 8px;
}

.cover-placeholder {
  color: hsl(var(--muted-foreground));
}

.cover-placeholder .el-icon {
  font-size: 32px;
  color: hsl(var(--primary));
}

.cover-placeholder strong {
  color: hsl(var(--foreground));
}

.cover-placeholder span,
.cover-side-desc {
  font-size: 12px;
  color: hsl(var(--muted-foreground));
}

.cover-mask {
  color: white;
  background: rgba(15, 23, 42, 0.55);
}

.cover-mask .el-icon {
  font-size: 28px;
}

.cover-side {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 12px;
  min-width: 0;
}

.cover-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.soft-button {
  background: hsl(var(--secondary));
  border-color: hsl(var(--border));
}

.danger-soft-button {
  color: hsl(var(--destructive));
  background: hsl(var(--card));
  border-color: hsl(var(--border));
}

.cover-url-input :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px hsl(var(--border)) inset;
}

.rich-editor,
.markdown-pane {
  border: 1px solid hsl(var(--border));
  border-radius: 8px;
  overflow: hidden;
  width: 100%;
}

.pane-title {
  height: 40px;
  padding: 0 10px;
  display: flex;
  align-items: center;
  gap: 6px;
  background: hsl(var(--secondary));
  border-bottom: 1px solid hsl(var(--border));
}

.rich-editor {
  overflow: visible;
}

.rich-editor :deep(.ql-toolbar) {
  position: sticky;
  top: 0;
  z-index: 20;
  border: none;
  border-bottom: 1px solid hsl(var(--border));
  border-radius: 8px 8px 0 0;
  background: hsl(var(--secondary));
  box-shadow: 0 6px 18px rgba(15, 23, 42, 0.06);
}

.rich-editor :deep(.ql-container) {
  border: none;
  min-height: calc(100vh - 380px);
  font-size: 15px;
  background: hsl(var(--background));
  border-radius: 0 0 8px 8px;
}

.rich-editor :deep(.ql-editor) {
  min-height: calc(100vh - 380px);
  line-height: 1.8;
  padding: 18px;
}

.markdown-editor {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  width: 100%;
}

.markdown-input :deep(.el-textarea__inner) {
  height: calc(100vh - 380px);
  min-height: 460px;
  border: none;
  border-radius: 0;
  box-shadow: none;
  font-family: Consolas, Monaco, "Courier New", monospace;
  line-height: 1.7;
}

.markdown-preview {
  height: calc(100vh - 380px);
  min-height: 460px;
  padding: 16px;
  overflow: auto;
  line-height: 1.8;
}

.markdown-preview :deep(img),
.rich-editor :deep(.ql-editor img) {
  max-width: 100%;
}

.markdown-preview :deep(pre),
.rich-editor :deep(.ql-editor pre) {
  padding: 12px;
  background: hsl(var(--secondary));
  border-radius: 6px;
  overflow: auto;
}

.markdown-preview :deep(blockquote) {
  margin: 12px 0;
  padding-left: 12px;
  border-left: 3px solid hsl(var(--border));
  color: hsl(var(--muted-foreground));
}

.floating-actions {
  position: fixed;
  right: 28px;
  top: calc(50vh + 30px);
  z-index: 80;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 0;
  background: transparent;
  border: none;
  box-shadow: none;
}

.fab {
  min-width: 86px;
  height: 44px;
  padding: 0 14px;
  border: 0;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  cursor: pointer;
  font: inherit;
  font-size: 14px;
  font-weight: 700;
  transition: transform 0.18s ease, box-shadow 0.18s ease, background 0.18s ease;
}

.fab:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 22px rgba(15, 23, 42, 0.18);
}

.fab.primary {
  color: white;
  background: hsl(var(--primary));
}

.fab.secondary,
.fab.publish {
  color: hsl(var(--foreground));
  background: hsl(var(--secondary));
}

.fab.publish {
  color: hsl(var(--primary));
}

.fab.publish.active {
  color: white;
  background: hsl(var(--primary));
  box-shadow: 0 0 0 4px color-mix(in srgb, hsl(var(--primary)) 18%, transparent);
}

.fab.loading {
  pointer-events: none;
  opacity: 0.65;
}

@media (max-width: 900px) {
  .cover-uploader,
  .markdown-editor {
    grid-template-columns: 1fr;
  }

  .rich-editor :deep(.ql-toolbar) {
    position: static;
  }

  .floating-actions {
    top: auto;
    right: 16px;
    bottom: 16px;
    transform: none;
    flex-direction: row;
  }

  .fab {
    min-width: 72px;
    padding: 0 10px;
  }

}

:global(.publish-mode-popover) {
  border-radius: 8px;
}

:global(.publish-popover) {
  display: grid;
  gap: 8px;
}

:global(.publish-title) {
  font-weight: 700;
  margin-bottom: 2px;
}

:global(.publish-option) {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--el-border-color);
  border-radius: 8px;
  background: var(--el-bg-color);
  color: var(--el-text-color-primary);
  text-align: left;
  cursor: pointer;
  font: inherit;
  transition: all 0.18s ease;
}

:global(.publish-option strong) {
  display: block;
  font-size: 14px;
}

:global(.publish-option span) {
  display: block;
  margin-top: 3px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

:global(.publish-option:hover),
:global(.publish-option.active) {
  border-color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
}
</style>
