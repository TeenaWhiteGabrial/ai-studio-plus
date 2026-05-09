<template>
  <div class="mail-config-page">
    <section class="settings-hero">
      <div>
        <p>Mail Settings</p>
        <h1>邮件配置</h1>
        <span>统一维护邮件日报的发件邮箱与安全码，保存后会立即用于后端邮件发送。</span>
      </div>
      <div class="config-status">
        <strong>{{ form.senderEmail || '未配置发件邮箱' }}</strong>
        <span>{{ form.hasAuthCode ? '安全码已配置' : '安全码未配置' }}</span>
        <small v-if="form.updatedAt">最近更新：{{ form.updatedAt }}</small>
      </div>
    </section>

    <el-card class="settings-card" shadow="never">
      <el-form label-position="top" :model="form" class="settings-form">
        <el-row :gutter="18">
          <el-col :span="12">
            <el-form-item label="发件邮箱" required>
              <el-input v-model="form.senderEmail" placeholder="例如：robot@qq.com" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱安全码" required>
              <el-input
                v-model="form.authCode"
                show-password
                placeholder="请输入邮箱安全码或授权码"
              />
              <div class="field-hint">
                {{ form.hasAuthCode ? '已配置安全码。如需变更请重新输入；留空保存则保留当前配置。' : '首次保存必须填写安全码。' }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-alert
          type="info"
          :closable="false"
          show-icon
          title="当前页面只维护发件邮箱和安全码；SMTP Host、端口和 SSL 仍沿用后端默认邮件通道配置。"
        />

        <div class="form-actions">
          <el-button :loading="loading" @click="loadConfig">重置</el-button>
          <el-button type="primary" :loading="saving" @click="saveConfig">保存配置</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { mailConfigApi, type MailConfig } from '@/api'

const loading = ref(false)
const saving = ref(false)
const form = reactive<MailConfig>({
  senderEmail: '',
  authCode: '',
  hasAuthCode: false,
  updatedAt: '',
})

onMounted(loadConfig)

async function loadConfig() {
  loading.value = true
  try {
    const config = await mailConfigApi.get()
    Object.assign(form, config, { authCode: '' })
  } finally {
    loading.value = false
  }
}

async function saveConfig() {
  if (!form.senderEmail.trim()) {
    ElMessage.warning('请输入发件邮箱')
    return
  }
  if (!form.hasAuthCode && !form.authCode.trim()) {
    ElMessage.warning('请输入安全码')
    return
  }
  saving.value = true
  try {
    const config = await mailConfigApi.update({
      senderEmail: form.senderEmail.trim(),
      authCode: form.authCode.trim(),
    })
    Object.assign(form, config, { authCode: '' })
    ElMessage.success('邮件配置已保存')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.mail-config-page {
  padding: 22px;
}

.settings-hero {
  min-height: 150px;
  border-radius: 8px;
  padding: 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  color: #fff;
  background:
    radial-gradient(circle at 82% 22%, rgba(134, 239, 172, 0.24), transparent 28%),
    linear-gradient(135deg, #0f172a, #111827);
}

.settings-hero p {
  margin: 0 0 8px;
  color: #fbbf24;
  font-size: 12px;
  font-weight: 900;
}

.settings-hero h1 {
  margin: 0 0 8px;
  font-size: 30px;
}

.settings-hero span {
  color: rgba(226, 232, 240, 0.82);
}

.config-status {
  min-width: 260px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 16px;
  border-radius: 8px;
  border: 1px solid rgba(148, 163, 184, 0.22);
  background: rgba(15, 23, 42, 0.68);
}

.config-status strong {
  font-size: 16px;
}

.config-status small {
  color: rgba(226, 232, 240, 0.6);
}

.settings-card {
  margin-top: 18px;
  border-radius: 8px;
}

.settings-form {
  max-width: 980px;
}

.field-hint {
  margin-top: 6px;
  color: #6b7280;
  font-size: 12px;
  line-height: 1.5;
}

.form-actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
