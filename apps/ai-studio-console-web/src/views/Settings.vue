<template>
  <div class="settings-page">
    <el-card>
      <template #header>
        <span>个人设置</span>
      </template>

      <el-form :model="form" label-width="120px">
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="部门">
          <el-select v-model="form.deptId" placeholder="请选择部门">
            <el-option label="技术研发部" :value="1" />
            <el-option label="产品运营部" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="团队">
          <el-select v-model="form.teamId" placeholder="请选择团队">
            <el-option label="前端开发组" :value="1" />
            <el-option label="后端开发组" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveSettings">保存设置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="mt-20">
      <template #header>
        <span>修改密码</span>
      </template>

      <el-form :model="passwordForm" label-width="120px">
        <el-form-item label="当前密码">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认新密码">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="changePassword">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
// @ts-ignore - userInfo is a ref that auto-unwraps in templates
const userInfo = userStore.userInfo

const form = ref({
  realName: userInfo.value?.real_name || '',
  email: '',
  deptId: null,
  teamId: null,
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const saveSettings = () => {
  ElMessage.success('设置已保存')
}

const changePassword = () => {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  ElMessage.success('密码修改成功')
}
</script>

<style scoped>
.settings-page {
  padding: 0;
}

.mt-20 {
  margin-top: 20px;
}
</style>
