<template>
  <el-dialog
    v-model="visible"
    :title="form.id ? '编辑 Plugin' : '新建 Plugin'"
    width="600px"
    :close-on-click-modal="false"
    append-to-body
  >
    <el-form :model="form" label-width="100px">
      <el-form-item label="名称">
        <el-input v-model="form.name" placeholder="请输入 Plugin 名称" />
      </el-form-item>
      <el-form-item label="描述">
        <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
      </el-form-item>
      <el-form-item label="分类">
        <el-select v-model="form.category" placeholder="请选择分类">
          <el-option label="效能统计" value="效能统计" />
          <el-option label="数据分析" value="数据分析" />
          <el-option label="代码开发" value="代码开发" />
          <el-option label="办公工具" value="办公工具" />
          <el-option label="文件操作" value="文件操作" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>
      <el-form-item label="图标 URL">
        <el-input v-model="form.icon" placeholder="图标 OSS 地址" />
      </el-form-item>
      <el-form-item label="ZIP 文件" v-if="!form.id">
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
import { pluginApi } from '@/api'

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
  icon: '',
  fileOssKey: '',
  fileSize: 0,
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
  file.value = null
}, { immediate: true })

const handleFileChange = (uploadFile: any) => {
  if (uploadFile.raw) file.value = uploadFile.raw
}

const handleSave = async () => {
  if (!form.value.name) {
    ElMessage.warning('请填写必填项')
    return
  }

  saving.value = true
  try {
    let fileOssKey = ''
    let fileSize = 0
    if (file.value && !form.value.id) {
      const uploadRes = await pluginApi.upload(file.value)
      if (uploadRes.code === 200) {
        fileOssKey = uploadRes.data?.oss_key || uploadRes.data?.ossKey
        fileSize = uploadRes.data?.file_size || uploadRes.data?.fileSize || 0
      }
    }

    const data = {
      name: form.value.name,
      description: form.value.description,
      category: form.value.category,
      icon: form.value.icon,
      fileOssKey,
      fileSize,
    }

    let res
    if (form.value.id) {
      res = await pluginApi.update(form.value.id, data)
    } else {
      res = await pluginApi.create(data)
    }

    if (res.code === 200) {
      ElMessage.success(form.value.id ? '更新成功' : '创建成功')
      visible.value = false
      emit('saved')
    }
  } catch (error) {
    console.error('保存 Plugin 失败:', error)
  } finally {
    saving.value = false
  }
}
</script>
