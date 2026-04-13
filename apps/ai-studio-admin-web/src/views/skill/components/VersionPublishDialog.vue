<template>
  <el-dialog
    title="发布新版本"
    v-model="dialogVisible"
    width="600px"
    :close-on-click-modal="false"
  >
    <div v-if="skill" class="skill-info mb-4">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="技能名称">{{ skill.name }}</el-descriptions-item>
        <el-descriptions-item label="当前版本">
          <el-tag type="success">{{ skill.latestVersion || '无' }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
      <el-form-item label="版本升级" prop="bumpType">
        <el-radio-group v-model="form.bumpType">
          <el-radio-button label="PATCH">
            修订版本 {{ nextVersions.PATCH }}
          </el-radio-button>
          <el-radio-button label="MINOR">
            次要版本 {{ nextVersions.MINOR }}
          </el-radio-button>
          <el-radio-button label="MAJOR">
            主要版本 {{ nextVersions.MAJOR }}
          </el-radio-button>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="新版本号">
        <el-tag type="primary" size="large">{{ newVersion }}</el-tag>
      </el-form-item>

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
              请上传新版本的 ZIP 文件，大小不超过 100MB
            </div>
          </template>
        </el-upload>
      </el-form-item>

      <el-form-item label="变更日志" prop="changelog">
        <el-input
          v-model="form.changelog"
          type="textarea"
          :rows="4"
          placeholder="请输入本次更新的变更日志"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">
        发布版本
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
  skill: any
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
const selectedFile = ref<File | null>(null)

const form = reactive({
  bumpType: 'PATCH',
  ossKey: '',
  fileSize: 0,
  changelog: ''
})

const rules = {
  bumpType: [{ required: true, message: '请选择版本升级类型', trigger: 'change' }],
  changelog: [{ required: true, message: '请输入变更日志', trigger: 'blur' }]
  // 文件上传在提交时单独检查
}

// 计算下一个版本号
const currentVersion = computed(() => props.skill?.latestVersion || '0.0.0')

const nextVersions = computed(() => {
  const parts = currentVersion.value.split('.').map(Number)
  const major = parts[0] || 0
  const minor = parts[1] || 0
  const patch = parts[2] || 0

  return {
    PATCH: `${major}.${minor}.${patch + 1}`,
    MINOR: `${major}.${minor + 1}.0`,
    MAJOR: `${major + 1}.0.0`
  }
})

const newVersion = computed(() => nextVersions.value[form.bumpType as keyof typeof nextVersions.value])

// 重置表单
watch(() => dialogVisible.value, (visible) => {
  if (visible) {
    form.bumpType = 'PATCH'
    form.ossKey = ''
    form.fileSize = 0
    form.changelog = ''
    selectedFile.value = null
    uploadRef.value?.clearFiles()
  }
})

// 文件选择
function handleFileChange(uploadFile: any) {
  const file = uploadFile.raw
  if (!file) return

  if (file.size > 100 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过 100MB')
    uploadRef.value?.clearFiles()
    selectedFile.value = null
    return
  }
  form.fileSize = file.size
  selectedFile.value = file
}

// 超出限制
function handleExceed() {
  ElMessage.warning('只能上传一个文件')
}

// 上传文件到 OSS（通过后端代理）
async function uploadToOss(file: File): Promise<string> {
  // 调用后端上传接口
  const res = await skillApi.upload(file, props.skill.name, newVersion.value) as any
  if (res.data?.oss_key) {
    form.ossKey = res.data.oss_key
    return res.data.oss_key
  }
  throw new Error('上传失败')
}

// 提交表单
async function handleSubmit() {
  const valid = await formRef.value?.validate()
  if (!valid) return

  submitting.value = true
  try {
    if (!selectedFile.value) {
      ElMessage.error('请上传技能文件')
      return
    }
    const file = selectedFile.value

    // 先上传文件到 OSS
    const ossKey = await uploadToOss(file)

    // 发布新版本
    await skillApi.publishVersion(props.skill.id, {
      bump_type: form.bumpType,
      oss_key: ossKey,
      file_size: form.fileSize,
      changelog: form.changelog
    })

    ElMessage.success('版本发布成功')
    dialogVisible.value = false
    emit('success')
  } catch (e: any) {
    ElMessage.error(e.message || '发布失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.skill-info {
  background: #f5f7fa;
  padding: 16px;
  border-radius: 4px;
}
</style>
