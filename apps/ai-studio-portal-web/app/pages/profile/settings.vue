<template>
  <div class="settings-page">
    <div class="settings-container bg-white rounded-lg p-6">
      <h1 class="page-title">个人设置</h1>

      <!-- 头像设置 -->
      <div class="settings-section">
        <h3 class="section-title">头像设置</h3>
        <div class="avatar-upload">
          <el-avatar :size="80" class="avatar-preview">
            {{ authStore.userName?.charAt(0) }}
          </el-avatar>
          <div class="upload-actions">
            <el-button @click="triggerAvatarUpload">
              <Icon name="material-symbols:upload" class="mr-1" />
              更换头像
            </el-button>
            <p class="upload-tip">支持 JPG、PNG 格式，文件小于 2MB</p>
          </div>
          <input
            ref="avatarInput"
            type="file"
            accept="image/*"
            style="display: none"
            @change="handleAvatarChange"
          />
        </div>
      </div>

      <!-- 基本信息 -->
      <div class="settings-section">
        <h3 class="section-title">基本信息</h3>
        <el-form :model="formData" label-width="80px" class="settings-form">
          <el-form-item label="用户名">
            <el-input v-model="formData.userName" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="formData.phone" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="formData.email" />
          </el-form-item>
          <el-form-item label="个人简介">
            <el-input
              v-model="formData.bio"
              type="textarea"
              :rows="3"
              placeholder="请输入个人简介"
              maxlength="200"
            />
          </el-form-item>
        </el-form>
      </div>

      <!-- 保存按钮 -->
      <div class="settings-actions">
        <el-button @click="handleReset">重置</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSave">保存</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  middleware: ['auth']
})

const authStore = useAuthStore()

const formData = ref({
  userName: authStore.userName || '',
  phone: authStore.phone || '',
  email: authStore.email || '',
  bio: ''
})

const avatarInput = ref<HTMLInputElement | null>(null)
const submitting = ref(false)

function triggerAvatarUpload() {
  avatarInput.value?.click()
}

function handleAvatarChange(event: Event) {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (file) {
    if (file.size > 2 * 1024 * 1024) {
      ElMessage.warning('图片大小不能超过 2MB')
      return
    }
    // TODO: 上传头像
    ElMessage.info('头像上传功能开发中')
  }
}

async function handleSave() {
  submitting.value = true
  try {
    // TODO: 调用保存用户信息API
    authStore.setLoginInfo({
      userName: formData.value.userName,
      phone: formData.value.phone,
      email: formData.value.email
    })
    ElMessage.success('保存成功')
  }
  catch (err) {
    ElMessage.error('保存失败')
  }
  finally {
    submitting.value = false
  }
}

function handleReset() {
  formData.value = {
    userName: authStore.userName || '',
    phone: authStore.phone || '',
    email: authStore.email || '',
    bio: ''
  }
}
</script>

<style scoped>
.settings-page {
  @apply max-w-2xl mx-auto;
}

.settings-container {
  @apply p-8;
}

.page-title {
  @apply text-2xl font-bold text-gray-800 mb-8;
}

.settings-section {
  @apply mb-8 pb-8 border-b border-gray-100 last:border-0;
}

.section-title {
  @apply text-lg font-medium text-gray-800 mb-4;
}

.avatar-upload {
  @apply flex items-center gap-6;
}

.avatar-preview {
  @apply bg-primary-faint text-primary text-2xl font-bold;
}

.upload-actions {
  @apply flex flex-col gap-2;
}

.upload-tip {
  @apply text-xs text-gray-400;
}

.settings-form {
  @apply max-w-md;
}

.settings-actions {
  @apply flex justify-end gap-4 pt-6 border-t border-gray-100;
}
</style>
