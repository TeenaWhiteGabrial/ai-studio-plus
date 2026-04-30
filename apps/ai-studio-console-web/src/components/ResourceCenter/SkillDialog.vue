<template>
  <el-dialog
    v-model="visible"
    :title="form.id ? '编辑 Skill' : '新建 Skill'"
    width="600px"
    :close-on-click-modal="false"
    append-to-body
  >
    <el-form :model="form" label-width="100px">
      <el-form-item label="名称" required>
        <el-input v-model="form.name" placeholder="请输入 Skill 名称" />
      </el-form-item>
      <el-form-item label="描述">
        <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
      </el-form-item>
      <el-form-item label="分类" required>
        <el-select v-model="form.category" placeholder="请选择分类">
          <el-option label="效能统计" value="效能统计" />
          <el-option label="数据分析" value="数据分析" />
          <el-option label="代码开发" value="代码开发" />
          <el-option label="办公工具" value="办公工具" />
          <el-option label="文件操作" value="文件操作" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>
      <el-form-item label="版本号" required v-if="!form.id">
        <el-input v-model="form.version" placeholder="例如: 1.0.0" />
      </el-form-item>
      <el-form-item label="变更日志" v-if="!form.id">
        <el-input v-model="form.changelog" type="textarea" :rows="2" placeholder="请输入版本变更说明" />
      </el-form-item>
      <el-form-item label="ZIP 文件" required v-if="!form.id">
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
      <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { skillApi } from '@/api'

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
const file = ref<File | null>(null)

const defaultForm = () => ({
  id: null as number | null,
  name: '',
  description: '',
  category: '',
  version: '1.0.0',
  ossUrl: '',
  fileSize: 0 as number,
  changelog: '初始版本',
})

const form = ref(defaultForm())

watch(() => props.modelValue, (val) => {
  visible.value = val
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

watch(() => props.editData, (data) => {
  if (data) {
    form.value = { ...data }
  } else {
    form.value = defaultForm()
  }
  file.value = null
}, { immediate: true })

const handleFileChange = (uploadFile: any) => {
  if (uploadFile.raw) {
    file.value = uploadFile.raw
  }
}

const handleSave = async () => {
  if (!form.value.name || !form.value.version) {
    ElMessage.warning('请填写必填项')
    return
  }

  saving.value = true
  try {
    let ossUrl = form.value.ossUrl
    let fileSize = form.value.fileSize

    if (file.value && !form.value.id) {
      const uploadRes = await skillApi.upload(file.value, form.value.name, form.value.version)
      if (uploadRes.code === 200) {
        ossUrl = uploadRes.data?.oss_url || uploadRes.data?.ossUrl
        fileSize = uploadRes.data?.file_size || uploadRes.data?.fileSize
      }
    }

    const data = {
      name: form.value.name,
      description: form.value.description,
      category: form.value.category,
      oss_key: ossUrl,
      file_size: fileSize,
      changelog: form.value.changelog,
    }

    let res
    if (form.value.id) {
      res = await skillApi.update(form.value.id, data)
    } else {
      res = await skillApi.create(data)
    }

    if (res.code === 200) {
      ElMessage.success(form.value.id ? '更新成功' : '创建成功')
      visible.value = false
      emit('saved')
    }
  } catch (error) {
    console.error('保存 Skill 失败:', error)
  } finally {
    saving.value = false
  }
}
</script>
