<template>
  <div class="component-upload-image">
    <el-upload
      ref="imageUpload"
      :action="uploadAction"
      :multiple="limit > 1"
      :disabled="disabled"
      list-type="picture-card"
      :http-request="handleUpload"
      :before-upload="handleBeforeUpload"
      :on-success="handleUploadSuccess"
      :on-remove="handleRemove"
      :on-error="handleUploadError"
      :on-exceed="handleExceed"
      :limit="limit"
      :show-file-list="true"
      :file-list="fileList"
      :on-preview="handlePictureCardPreview"
      :class="{ hide: fileList.length >= limit }"
    >
      <Icon name="material-symbols:upload" size="44" />
    </el-upload>

    <div v-if="showTip && !disabled" class="el-upload__tip">
      请上传
      <template v-if="fileSize">
        大小不超过 <b style="color: #f56c6c">{{ fileSize }}MB</b>
      </template>
      <template v-if="fileType?.length">
        格式为 <b style="color: #f56c6c">{{ fileType.join('/') }}</b>
      </template>
      的图片
    </div>

    <el-dialog
      v-model="dialogVisible"
      title="预览"
      width="800px"
      append-to-body
    >
      <img
        :src="dialogImageUrl"
        style="display: block; max-width: 100%; margin: 0 auto"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import type { UploadFile, UploadFiles, UploadRequestOptions, UploadUserFile } from 'element-plus'

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

const config = useRuntimeConfig()
const authStore = useAuthStore()

const props = withDefaults(defineProps<{
  modelValue?: string | string[] | Record<string, any> | null
  limit?: number
  fileSize?: number
  fileType?: string[]
  isShowTip?: boolean
  disabled?: boolean
  keyPrefix?: string
}>(), {
  modelValue: '',
  limit: 5,
  fileSize: 5,
  fileType: () => ['png', 'jpg', 'jpeg'],
  isShowTip: true,
  disabled: false,
  keyPrefix: 'portal/images'
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

const dialogImageUrl = ref('')
const dialogVisible = ref(false)
const fileList = ref<UploadUserFile[]>([])

const uploadAction = '#'
const showTip = computed(() => props.isShowTip && (props.fileType?.length || props.fileSize))

function getAuthHeaders() {
  if (!authStore.token) {
    return undefined
  }
  const tokenType = config.public.tokenType
  const authorization = tokenType ? `${tokenType} ${authStore.token}` : authStore.token
  return { Authorization: authorization }
}

function normalizeToUrls(value: typeof props.modelValue): string[] {
  if (!value) {
    return []
  }
  if (Array.isArray(value)) {
    return value
      .map((item) => {
        if (typeof item === 'string') return item
        if (item && typeof item === 'object') return item.url || item.oss_url || ''
        return ''
      })
      .filter(Boolean)
  }
  if (typeof value === 'string') {
    return value.split(',').map(item => item.trim()).filter(Boolean)
  }
  if (typeof value === 'object') {
    const url = value.url || value.oss_url
    return url ? [url] : []
  }
  return []
}

function urlToFile(url: string): UploadUserFile {
  const name = url.split('/').pop() || url
  return { name, url }
}

function buildModelValue(): string {
  const urls = fileList.value
    .map(item => item.url || '')
    .filter(Boolean)

  if (!urls.length) {
    return ''
  }
  if (props.limit <= 1) {
    return urls[0] || ''
  }
  return urls.join(',')
}

function syncFromModel(value: typeof props.modelValue) {
  fileList.value = normalizeToUrls(value).map(urlToFile)
}

function emitModelValue() {
  emit('update:modelValue', buildModelValue())
}

watch(() => props.modelValue, (value) => {
  syncFromModel(value)
}, { immediate: true, deep: true })

function handleBeforeUpload(file: File) {
  let isImage = false
  const acceptedTypes = props.fileType || []

  if (acceptedTypes.length) {
    const extension = file.name.includes('.') ? file.name.split('.').pop()?.toLowerCase() || '' : ''
    isImage = acceptedTypes.some((type) => {
      const lower = type.toLowerCase()
      return file.type.toLowerCase().includes(lower) || extension === lower
    })
  }
  else {
    isImage = file.type.toLowerCase().startsWith('image/')
  }

  if (!isImage) {
    ElMessage.error(`图片格式不正确，请上传 ${acceptedTypes.join('/')} 格式图片`)
    return false
  }

  if (file.name.includes(',')) {
    ElMessage.error('图片名不能包含英文逗号')
    return false
  }

  if (props.fileSize && file.size / 1024 / 1024 > props.fileSize) {
    ElMessage.error(`上传图片大小不能超过 ${props.fileSize}MB`)
    return false
  }
  return true
}

function handleExceed() {
  ElMessage.error(`上传图片数量不能超过 ${props.limit} 张`)
}

function handleUploadSuccess(response: any, file: UploadFile, files: UploadFiles) {
  const fileUrl = response?.fileUrl || response?.oss_url || response?.url
  if (!fileUrl) {
    ElMessage.error(response?.message || '上传失败')
    return
  }

  file.url = fileUrl
  file.name = response?.fileName || file.name

  fileList.value = files
    .map(item => ({
      name: item.name,
      url: item.url
    }))
    .filter(item => !!item.url)

  emitModelValue()
  ElMessage.success('上传图片成功')
}

function handleRemove(_file: UploadFile, files: UploadFiles) {
  fileList.value = files
    .map(item => ({
      name: item.name,
      url: item.url
    }))
    .filter(item => !!item.url)
  emitModelValue()
}

function handleUploadError() {
  ElMessage.error('上传图片失败')
}

function handlePictureCardPreview(file: UploadFile) {
  dialogImageUrl.value = file.url || ''
  dialogVisible.value = true
}

async function handleUpload(options: UploadRequestOptions) {
  try {
    const formData = new FormData()
    formData.append('file', options.file)

    const res = await $fetch<UploadApiResponse>('/oss/upload', {
      method: 'POST',
      baseURL: config.public.apiBase,
      body: formData,
      params: { keyPrefix: props.keyPrefix },
      headers: getAuthHeaders()
    })

    if (res?.code !== 200 || !res?.data?.oss_url) {
      options.onError(new Error(res?.message || 'upload failed') as any)
      return
    }

    options.onSuccess({
      fileName: res.data.file_name || options.file.name,
      fileUrl: res.data.oss_url,
      ossKey: res.data.oss_key
    } as any)
  }
  catch (error: any) {
    options.onError(error as any)
  }
}
</script>

<style scoped>
:deep(.hide .el-upload--picture-card) {
  display: none;
}

:deep(.el-upload.el-upload--picture-card.is-disabled) {
  display: none !important;
}
</style>
