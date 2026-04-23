<template>
  <el-dialog v-model="visible" title="发布新版本" width="500px" :close-on-click-modal="false" append-to-body>
    <el-form :model="form" label-width="100px">
      <el-form-item label="资源名称">
        <el-input :value="form.resourceName" disabled />
      </el-form-item>
      <el-form-item label="当前版本">
        <el-input :value="form.currentVersion" disabled />
      </el-form-item>
      <el-form-item label="版本类型" required>
        <el-select v-model="form.bumpType" @change="handleBumpTypeChange">
          <el-option value="PATCH" label="修订版本" />
          <el-option value="MINOR" label="次要版本" />
          <el-option value="MAJOR" label="主要版本" />
        </el-select>
      </el-form-item>
      <el-form-item label="新版本" required>
        <el-input :value="form.version" disabled />
      </el-form-item>
      <el-form-item label="变更日志">
        <el-input v-model="form.changelog" type="textarea" :rows="3" placeholder="请输入版本变更说明" />
      </el-form-item>
      <el-form-item label="文件" v-if="form.type !== 'tutorial'" required>
        <el-upload
          ref="uploadRef"
          :auto-upload="false"
          :show-file-list="true"
          :limit="1"
          accept=".zip"
          @change="handleFileChange"
        >
          <el-button size="small">选择 ZIP 文件</el-button>
        </el-upload>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="saving" @click="handlePublish">发布</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { skillApi, pluginApi, tutorialApi } from '@/api'
import { useResourceUtils } from '@/composables/useResourceCenter'

const props = defineProps<{
  modelValue: boolean
  formData: any
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'published': [type: string]
}>()

const { generateNextVersion } = useResourceUtils()

const visible = ref(false)
const saving = ref(false)
const file = ref<File | null>(null)

const defaultForm = () => ({
  type: 'skill' as 'skill' | 'plugin' | 'tutorial',
  resourceId: 0,
  resourceName: '',
  currentVersion: '',
  version: '',
  bumpType: 'PATCH' as 'PATCH' | 'MINOR' | 'MAJOR',
  changelog: '',
  ossUrl: '',
  fileSize: 0,
})

const form = ref(defaultForm())

watch(() => props.modelValue, (val) => { visible.value = val })
watch(visible, (val) => { emit('update:modelValue', val) })

watch(() => props.formData, (data) => {
  if (data) {
    form.value = {
      ...data,
      bumpType: 'PATCH' as const,
      version: generateNextVersion(data.currentVersion, 'PATCH'),
    }
  } else {
    form.value = defaultForm()
  }
  file.value = null
}, { immediate: true })

const handleBumpTypeChange = (bumpType: 'PATCH' | 'MINOR' | 'MAJOR') => {
  form.value.version = generateNextVersion(form.value.currentVersion, bumpType)
}

const handleFileChange = (uploadFile: any) => {
  if (uploadFile.raw) file.value = uploadFile.raw
}

const handlePublish = async () => {
  if (!file.value && form.value.type !== 'tutorial') {
    ElMessage.warning('请上传文件')
    return
  }

  saving.value = true
  try {
    let ossUrl = form.value.ossUrl
    let fileSize = form.value.fileSize

    if (file.value && form.value.type !== 'tutorial') {
      let uploadRes
      if (form.value.type === 'skill') {
        uploadRes = await skillApi.upload(file.value, form.value.resourceName, form.value.version)
      } else if (form.value.type === 'plugin') {
        uploadRes = await pluginApi.upload(file.value)
      }

      if (uploadRes && uploadRes.code === 200) {
        ossUrl = uploadRes.data?.oss_key || uploadRes.data?.ossUrl || uploadRes.data?.ossKey
        fileSize = uploadRes.data?.file_size || uploadRes.data?.fileSize || uploadRes.data?.fileSize
      }
    }

    let res
    if (form.value.type === 'skill') {
      res = await skillApi.publishVersion(form.value.resourceId, {
        ossKey: ossUrl,
        fileSize,
        changelog: form.value.changelog,
        bumpType: form.value.bumpType,
      })
    } else if (form.value.type === 'plugin') {
      res = await pluginApi.publishVersion(form.value.resourceId, {
        ossKey: ossUrl,
        fileSize,
        changelog: form.value.changelog,
        bumpType: form.value.bumpType,
      })
    } else if (form.value.type === 'tutorial') {
      res = await tutorialApi.publishVersion(form.value.resourceId, {
        changelog: form.value.changelog,
        bumpType: form.value.bumpType,
      })
    }

    if (res && res.code === 200) {
      ElMessage.success('发布成功')
      visible.value = false
      emit('published', form.value.type)
    }
  } catch (error) {
    console.error('发布版本失败:', error)
  } finally {
    saving.value = false
  }
}
</script>
