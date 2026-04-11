<template>
  <el-dialog
    :title="isEdit ? '编辑技能' : '创建技能'"
    v-model="dialogVisible"
    width="600px"
    :close-on-click-modal="false"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="技能名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入技能名称" :disabled="isEdit" />
      </el-form-item>

      <el-form-item label="描述" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="3"
          placeholder="请输入技能描述"
        />
      </el-form-item>

      <el-form-item label="分类" prop="category">
        <el-select v-model="form.category" placeholder="选择分类" style="width: 100%">
          <el-option v-for="cat in categories" :key="cat" :label="cat" :value="cat" />
        </el-select>
      </el-form-item>

      <!-- 创建时显示文件上传 -->
      <template v-if="!isEdit">
        <el-form-item label="技能文件" required>
          <el-upload
            ref="uploadRef"
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :before-upload="() => false"
            accept=".zip"
            drag
            :limit="1"
            :on-exceed="handleExceed"
          >
            <el-icon class="el-icon--upload"><Upload /></el-icon>
            <div class="el-upload__text">
              拖拽文件到此处或 <em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                请上传 ZIP 格式的技能文件，大小不超过 100MB
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item label="变更日志">
          <el-input
            v-model="form.changelog"
            type="textarea"
            :rows="2"
            placeholder="请输入初始版本的变更日志"
          />
        </el-form-item>
      </template>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">
        {{ isEdit ? '保存' : '创建' }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'
import { skillApi } from '@/api'

const props = defineProps<{
  modelValue: boolean
  isEdit: boolean
  editData: any
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'success': []
}>()

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const formRef = ref()
const uploadRef = ref()
const submitting = ref(false)
const categories = ['工具', '文档', '设计', '开发','效能', '其他']
const selectedFile = ref<File | null>(null)

const form = reactive({
  name: '',
  description: '',
  category: '',
  ossKey: '',
  fileSize: 0,
  changelog: '初始版本'
})

const rules = {
  name: [{ required: true, message: '请输入技能名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }]
  // 文件上传在提交时单独检查，不通过表单验证
}

// 编辑时初始化表单
watch(() => props.editData, (data) => {
  if (data && props.isEdit) {
    form.name = data.name
    form.description = data.description
    form.category = data.category
  }
}, { immediate: true })

// 文件选择
function handleFileChange(uploadFile: any) {
  console.log('文件选择事件:', uploadFile)
  const file = uploadFile.raw
  if (!file) {
    console.error('没有获取到文件')
    return
  }

  console.log('文件信息:', file.name, file.size)

  if (file.size > 100 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过 100MB')
    uploadRef.value?.clearFiles()
    selectedFile.value = null
    return
  }
  form.fileSize = file.size
  selectedFile.value = file
  console.log('文件已保存，size:', form.fileSize)
}

// 超出限制
function handleExceed() {
  ElMessage.warning('只能上传一个文件')
}

// 上传文件到 OSS（通过后端代理）
async function uploadToOss(file: File, skillName: string, version: string): Promise<string> {
  console.log('开始调用上传接口...')
  // 调用后端上传接口
  const res = await skillApi.upload(file, skillName, version) as any
  console.log('上传接口返回:', res)

  if (!res) {
    throw new Error('上传接口返回空数据')
  }

  if (res.code !== 200) {
    throw new Error(res.message || '上传失败')
  }

  // 后端使用 SNAKE_CASE，字段名为 oss_key
  const ossKey = res.data?.oss_key
  if (!ossKey) {
    console.error('上传响应数据:', res.data)
    throw new Error('上传失败：未获取到文件key')
  }

  // 同时获取 file_size 并设置到 form
  const fileSize = res.data?.file_size
  if (fileSize) {
    form.fileSize = fileSize
    console.log('从响应获取 fileSize:', fileSize)
  }

  form.ossKey = ossKey
  console.log('上传成功，ossKey:', ossKey)
  return ossKey
}

// 提交表单
async function handleSubmit() {
  console.log('=== 开始提交表单 ===')
  const valid = await formRef.value?.validate()
  console.log('表单验证结果:', valid)
  if (!valid) return

  submitting.value = true
  try {
    if (props.isEdit) {
      // 编辑
      await skillApi.update(props.editData.id, {
        description: form.description,
        category: form.category
      })
      ElMessage.success('更新成功')
    } else {
      // 创建
      console.log('检查文件...')
      if (!selectedFile.value) {
        ElMessage.error('请上传技能文件')
        return
      }
      const file = selectedFile.value
      console.log('文件信息:', file.name, file.size)

      console.log('开始上传文件到 OSS...')
      // 先上传文件到 OSS
      const ossKey = await uploadToOss(file, form.name, '1.0.0')
      console.log('上传完成，ossKey:', ossKey)

      if (!ossKey) {
        ElMessage.error('文件上传失败')
        return
      }

      console.log('准备创建技能...')
      console.log('参数:', {
        name: form.name,
        description: form.description,
        category: form.category,
        ossKey: ossKey,
        fileSize: form.fileSize,
        changelog: form.changelog
      })

      // 创建技能
      await skillApi.create({
        name: form.name,
        description: form.description,
        category: form.category,
        oss_key: ossKey,
        file_size: form.fileSize,
        changelog: form.changelog
      })
      ElMessage.success('创建成功')
    }

    dialogVisible.value = false
    resetForm()
    emit('success')
  } catch (e: any) {
    console.error('提交失败:', e)
    ElMessage.error(e.message || '操作失败')
  } finally {
    submitting.value = false
    console.log('=== 提交结束 ===')
  }
}

// 重置表单
function resetForm() {
  form.name = ''
  form.description = ''
  form.category = ''
  form.ossKey = ''
  form.fileSize = 0
  form.changelog = '初始版本'
  selectedFile.value = null
  uploadRef.value?.clearFiles()
  formRef.value?.resetFields()
}
</script>
