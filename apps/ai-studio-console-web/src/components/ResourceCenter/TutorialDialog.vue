<template>
  <el-dialog
    v-model="visible"
    :title="form.id ? '编辑教程' : '新建教程'"
    width="700px"
    :close-on-click-modal="false"
    append-to-body
  >
    <el-form :model="form" label-width="100px">
      <el-form-item label="标题">
        <el-input v-model="form.title" placeholder="请输入教程标题" />
      </el-form-item>
      <el-form-item label="简介">
        <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入简介" />
      </el-form-item>
      <el-form-item label="分类">
        <el-select v-model="form.category" placeholder="请选择分类">
          <el-option label="入门教程" value="入门教程" />
          <el-option label="进阶教程" value="进阶教程" />
          <el-option label="最佳实践" value="最佳实践" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>
      <el-form-item label="封面图">
        <el-input v-model="form.coverImage" placeholder="封面图 OSS 地址" />
      </el-form-item>
      <el-form-item label="内容类型">
        <el-select v-model="form.contentType" placeholder="请选择内容类型">
          <el-option label="富文本" value="richText" />
          <el-option label="Markdown" value="markdown" />
        </el-select>
      </el-form-item>
      <el-form-item label="内容">
        <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入教程内容" />
      </el-form-item>
      <el-form-item label="视频文件" v-if="!form.id">
        <el-upload
          :auto-upload="false"
          :show-file-list="true"
          :limit="1"
          accept=".mp4,.mov,.avi"
          @change="handleVideoFileChange"
        >
          <el-button size="small">选择视频文件</el-button>
        </el-upload>
      </el-form-item>
      <el-form-item label="附件 ZIP" v-if="!form.id">
        <el-upload
          :auto-upload="false"
          :show-file-list="true"
          :limit="1"
          accept=".zip"
          @change="handleZipFileChange"
        >
          <el-button size="small">选择附件 ZIP</el-button>
        </el-upload>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { tutorialApi } from '@/api'

const props = defineProps<{
  modelValue: boolean
  editData?: any
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'saved': []
}>()

const visible = ref(false)
const saving = ref(false)
const videoFile = ref<File | null>(null)
const zipFile = ref<File | null>(null)

const defaultForm = () => ({
  id: null as number | null,
  title: '',
  description: '',
  category: '',
  coverImage: '',
  contentType: 'richText',
  content: '',
  videoUrl: '',
  zipFileUrl: '',
  zipFileName: '',
})

const form = ref(defaultForm())

watch(() => props.modelValue, (val) => { visible.value = val })
watch(visible, (val) => { emit('update:modelValue', val) })

watch(() => props.editData, (data) => {
  if (data) {
    form.value = { ...data }
  } else {
    form.value = defaultForm()
  }
  videoFile.value = null
  zipFile.value = null
}, { immediate: true })

const handleVideoFileChange = (uploadFile: any) => {
  if (uploadFile.raw) videoFile.value = uploadFile.raw
}

const handleZipFileChange = (uploadFile: any) => {
  if (uploadFile.raw) zipFile.value = uploadFile.raw
}

const handleSave = async () => {
  if (!form.value.title) {
    ElMessage.warning('请填写标题')
    return
  }

  saving.value = true
  try {
    let videoUrl = form.value.videoUrl
    let zipFileUrl = form.value.zipFileUrl
    let zipFileName = form.value.zipFileName

    if (videoFile.value) {
      const uploadRes = await tutorialApi.uploadVideo(videoFile.value)
      if (uploadRes.code === 200) {
        videoUrl = uploadRes.data?.oss_url || uploadRes.data?.ossUrl
      }
    }

    if (zipFile.value) {
      const uploadRes = await tutorialApi.uploadZip(zipFile.value)
      if (uploadRes.code === 200) {
        zipFileUrl = uploadRes.data?.oss_url || uploadRes.data?.ossUrl
        zipFileName = uploadRes.data?.fileName
      }
    }

    const data = {
      title: form.value.title,
      description: form.value.description,
      category: form.value.category,
      coverImage: form.value.coverImage,
      contentType: form.value.contentType,
      content: form.value.content,
      videoUrl,
      zipFileUrl,
      zipFileName,
    }

    let res
    if (form.value.id) {
      res = await tutorialApi.update(form.value.id, data)
    } else {
      res = await tutorialApi.create(data)
    }

    if (res.code === 200) {
      ElMessage.success(form.value.id ? '更新成功' : '创建成功')
      visible.value = false
      emit('saved')
    }
  } catch (error) {
    console.error('保存教程失败:', error)
  } finally {
    saving.value = false
  }
}
</script>
