<template>
  <div class="settings-page">
    <!-- 个人信息卡片 -->
    <el-card class="info-card">
      <template #header>
        <div class="card-header">
          <el-icon class="header-icon"><User /></el-icon>
          <span>个人信息</span>
        </div>
      </template>

      <div class="avatar-section">
        <div class="avatar-wrapper">
          <el-avatar :size="120" :src="userInfo?.avatar" class="avatar-large">
            {{ userInfo?.real_name?.[0] || userInfo?.username?.[0] || 'U' }}
          </el-avatar>
          <div class="avatar-edit-btn" @click="handleAvatarClick">
            <el-icon><Camera /></el-icon>
          </div>
        </div>
        <input ref="avatarInput" type="file" accept="image/*" style="display: none" @change="handleAvatarChange" />
        <div class="avatar-info">
          <h3 class="user-name">{{ userInfo?.real_name || userInfo?.username }}</h3>
          <p class="user-username">@{{ userInfo?.username }}</p>
          <p class="user-username">Git: {{ userInfo?.git_name || '-' }}</p>
          <p class="upload-tip" v-if="!uploading">支持 JPG、PNG 格式，不超过 2MB</p>
          <p class="upload-tip uploading" v-else>上传中...</p>
        </div>
      </div>

      <el-descriptions :column="2" border class="user-info-descriptions">
        <el-descriptions-item label="用户名">
          {{ userInfo?.username }}
        </el-descriptions-item>
        <el-descriptions-item label="邮箱">
          <div class="email-display">
            {{ userInfo?.email || '-' }}
            <el-icon class="edit-icon" @click="showEmailDialog = true"><Edit /></el-icon>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="Git用户名">
          <div class="email-display">
            {{ userInfo?.git_name || '-' }}
            <el-icon class="edit-icon" @click="openGitNameDialog"><Edit /></el-icon>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="部门">
          <el-tag type="info" v-if="userInfo?.dept_name">{{ userInfo?.dept_name }}</el-tag>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="团队">
          <el-tag type="success" v-if="userInfo?.team_name">{{ userInfo?.team_name }}</el-tag>
          <span v-else>-</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 安全设置卡片 -->
    <el-card class="security-card">
      <template #header>
        <div class="card-header">
          <el-icon class="header-icon"><Lock /></el-icon>
          <span>安全设置</span>
        </div>
      </template>

      <div class="security-item" @click="showPasswordDialog = true">
        <div class="security-item-content">
          <el-icon class="security-icon"><Key /></el-icon>
          <div class="security-item-text">
            <h4>修改密码</h4>
            <p>定期修改密码可以提高账户安全性</p>
          </div>
        </div>
        <el-icon class="arrow-icon"><ArrowRight /></el-icon>
      </div>
    </el-card>

    <!-- 修改邮箱弹窗 -->
    <el-dialog
      v-model="showEmailDialog"
      title="修改邮箱"
      width="400px"
      :close-on-click-modal="false"
      class="email-dialog"
    >
      <el-form :model="emailForm" label-width="80px" :rules="emailRules" ref="emailFormRef">
        <el-form-item label="当前邮箱" v-if="userInfo?.email">
          <el-input :value="userInfo.email" disabled />
        </el-form-item>
        <el-form-item label="新邮箱" prop="email">
          <el-input v-model="emailForm.email" placeholder="请输入新邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEmailDialog = false">取消</el-button>
        <el-button type="primary" @click="updateEmail" :loading="emailLoading">确认修改</el-button>
      </template>
    </el-dialog>

    <!-- 修改 Git 用户名弹窗 -->
    <el-dialog
      v-model="showGitNameDialog"
      title="修改 Git 用户名"
      width="400px"
      :close-on-click-modal="false"
      class="email-dialog"
    >
      <el-form :model="gitNameForm" label-width="90px" :rules="gitNameRules" ref="gitNameFormRef">
        <el-form-item label="Git用户名" prop="git_name">
          <el-input v-model="gitNameForm.git_name" placeholder="请输入 Git 用户名" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showGitNameDialog = false">取消</el-button>
        <el-button type="primary" @click="updateGitName" :loading="gitNameLoading">确认修改</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码弹窗 -->
    <el-dialog
      v-model="showPasswordDialog"
      title="修改密码"
      width="400px"
      :close-on-click-modal="false"
      class="password-dialog"
    >
      <el-form :model="passwordForm" label-width="100px" :rules="passwordRules" ref="passwordFormRef">
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            show-password
            placeholder="请输入当前密码"
            autocomplete="current-password"
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            show-password
            placeholder="请输入新密码（至少6位）"
            autocomplete="new-password"
          />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            show-password
            placeholder="请再次输入新密码"
            autocomplete="new-password"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="changePassword" :loading="passwordLoading">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { authApi } from '@/api'
import { User, Camera, Edit, Lock, Key, ArrowRight } from '@element-plus/icons-vue'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

// 弹窗显示状态
const showEmailDialog = ref(false)
const showGitNameDialog = ref(false)
const showPasswordDialog = ref(false)

// 上传相关
const uploading = ref(false)
const avatarInput = ref<HTMLInputElement | null>(null)

// 邮箱表单
const emailFormRef = ref<FormInstance>()
const emailForm = ref({
  email: '',
})
const emailLoading = ref(false)
const emailRules: FormRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
  ],
}

// Git 用户名表单
const gitNameFormRef = ref<FormInstance>()
const gitNameForm = ref({
  git_name: '',
})
const gitNameLoading = ref(false)
const gitNameRules: FormRules = {
  git_name: [
    { max: 50, message: 'Git 用户名不能超过 50 个字符', trigger: 'blur' },
  ],
}

// 密码表单
const passwordFormRef = ref<FormInstance>()
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})
const passwordLoading = ref(false)
const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'))
  } else if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}
const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' },
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' },
  ],
}

// 点击更换头像按钮
const handleAvatarClick = () => {
  avatarInput.value?.click()
}

// 处理文件选择
const handleAvatarChange = (e: Event) => {
  const target = e.target as HTMLInputElement
  const file = target.files?.[0]
  if (file) {
    if (beforeAvatarUpload(file)) {
      handleAvatarUpload(file)
    }
  }
  // 清空 input 以允许重复选择同一文件
  target.value = ''
}

// 上传头像前的校验
const beforeAvatarUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB！')
    return false
  }
  return true
}

// 上传头像
const handleAvatarUpload = async (file: File) => {
  try {
    uploading.value = true
    const res = await authApi.uploadAvatar(file) as any
    if (res.code === 200) {
      // 更新用户信息中的头像
      if (userInfo.value) {
        userInfo.value.avatar = res.data.oss_url
        localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
      }
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error(res.message || '头像上传失败')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '头像上传失败')
  } finally {
    uploading.value = false
  }
}

// 更新邮箱
const updateEmail = async () => {
  if (!emailFormRef.value) return
  try {
    const valid = await emailFormRef.value.validate()
    if (valid) {
      emailLoading.value = true
      const res = await authApi.updateProfile({ email: emailForm.value.email }) as any
      if (res.code === 200) {
        // 更新本地用户信息
        if (userInfo.value) {
          userInfo.value.email = emailForm.value.email
          localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
        }
        ElMessage.success('邮箱更新成功')
        emailForm.value.email = ''
        showEmailDialog.value = false
      } else {
        ElMessage.error(res.message || '邮箱更新失败')
      }
    }
  } catch (error: any) {
    ElMessage.error(error.message || '邮箱更新失败')
  } finally {
    emailLoading.value = false
  }
}

// 更新 Git 用户名
const openGitNameDialog = () => {
  gitNameForm.value.git_name = userInfo.value?.git_name || ''
  showGitNameDialog.value = true
}

const updateGitName = async () => {
  if (!gitNameFormRef.value) return
  try {
    const valid = await gitNameFormRef.value.validate()
    if (valid) {
      gitNameLoading.value = true
      const res = await authApi.updateProfile({ git_name: gitNameForm.value.git_name }) as any
      if (res.code === 200) {
        if (userInfo.value) {
          userInfo.value.git_name = gitNameForm.value.git_name
          localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
        }
        ElMessage.success('Git 用户名更新成功')
        showGitNameDialog.value = false
      } else {
        ElMessage.error(res.message || 'Git 用户名更新失败')
      }
    }
  } catch (error: any) {
    ElMessage.error(error.message || 'Git 用户名更新失败')
  } finally {
    gitNameLoading.value = false
  }
}

// 修改密码
const changePassword = async () => {
  if (!passwordFormRef.value) return
  try {
    const valid = await passwordFormRef.value.validate()
    if (valid) {
      passwordLoading.value = true
      const res = await authApi.changePassword({
        oldPassword: passwordForm.value.oldPassword,
        newPassword: passwordForm.value.newPassword,
      }) as any
      if (res.code === 200) {
        ElMessage.success('密码修改成功')
        // 清空表单
        passwordForm.value = {
          oldPassword: '',
          newPassword: '',
          confirmPassword: '',
        }
        showPasswordDialog.value = false
      } else {
        ElMessage.error(res.message || '密码修改失败')
      }
    }
  } catch (error: any) {
    ElMessage.error(error.message || '密码修改失败')
  } finally {
    passwordLoading.value = false
  }
}
</script>

<style scoped>
.settings-page {
  padding: 0;
}

.info-card,
.security-card {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
}

.header-icon {
  font-size: 20px;
  color: hsl(var(--primary));
}

/* 头像区域 */
.avatar-section {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 20px 0;
}

.avatar-wrapper {
  position: relative;
  flex-shrink: 0;
}

.avatar-large {
  background: hsl(var(--primary));
  color: white;
  font-size: 48px;
  border: 4px solid hsl(var(--secondary));
}

.avatar-edit-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 36px;
  height: 36px;
  background: hsl(var(--primary));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: white;
  border: 3px solid hsl(var(--background));
  transition: all 0.3s ease;
}

.avatar-edit-btn:hover {
  background: hsl(var(--primary-hover));
  transform: scale(1.1);
}

.avatar-info {
  flex: 1;
}

.user-name {
  font-size: 20px;
  font-weight: 600;
  color: hsl(var(--foreground));
  margin: 0 0 8px 0;
}

.user-username {
  font-size: 14px;
  color: hsl(var(--muted-foreground));
  margin: 0 0 16px 0;
}

.upload-tip {
  font-size: 12px;
  color: hsl(var(--muted-foreground));
  margin: 0;
}

.upload-tip.uploading {
  color: hsl(var(--primary));
}

/* 用户信息描述 */
.user-info-descriptions {
  margin-top: 20px;
}

:deep(.el-descriptions__label) {
  width: 80px;
  font-weight: 500;
}

:deep(.el-descriptions__content) {
  color: hsl(var(--foreground));
}

.email-display {
  display: flex;
  align-items: center;
  gap: 8px;
}

.edit-icon {
  font-size: 16px;
  cursor: pointer;
  color: hsl(var(--muted-foreground));
  transition: all 0.2s ease;
}

.edit-icon:hover {
  color: hsl(var(--primary));
  transform: scale(1.1);
}

/* 安全设置 */
.security-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: hsl(var(--secondary));
}

.security-item:hover {
  background: hsl(var(--muted));
  transform: translateY(-2px);
}

.security-item-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.security-icon {
  font-size: 24px;
  color: hsl(var(--primary));
  background: hsl(var(--secondary));
  padding: 12px;
  border-radius: 12px;
}

.security-item-text h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: hsl(var(--foreground));
}

.security-item-text p {
  margin: 0;
  font-size: 13px;
  color: hsl(var(--muted-foreground));
}

.arrow-icon {
  font-size: 18px;
  color: hsl(var(--muted-foreground));
  transition: all 0.3s ease;
}

.security-item:hover .arrow-icon {
  color: hsl(var(--primary));
  transform: translateX(4px);
}

/* 弹窗样式 */
:deep(.email-dialog .el-dialog__header),
:deep(.password-dialog .el-dialog__header) {
  border-bottom: 1px solid hsl(var(--border));
}

:deep(.el-form-item__label) {
  color: hsl(var(--foreground));
}

:deep(.el-input.is-disabled .el-input__inner) {
  background: hsl(var(--muted));
  color: hsl(var(--muted-foreground));
}
</style>
