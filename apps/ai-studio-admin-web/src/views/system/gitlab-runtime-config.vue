<template>
  <div class="config-page">
    <section class="settings-hero gitlab-hero">
      <div>
        <p>GitLab Runtime</p>
        <h1>GitLab 运行配置</h1>
        <span>维护 GitLab 接入地址、Webhook 密钥与调度参数，保存后新任务会按最新配置生效。</span>
      </div>
      <div class="config-status">
        <strong>{{ form.baseUrl || '未配置 GitLab 地址' }}</strong>
        <span>{{ form.enabled === 1 ? '运行配置已启用' : '运行配置未启用' }}</span>
        <small>{{ form.schedulesEnabled ? '调度已开启' : '调度未开启' }}</small>
      </div>
    </section>

    <el-card class="settings-card" shadow="never">
      <el-form label-position="top" :model="form" class="settings-form">
        <el-row :gutter="18">
          <el-col :span="12">
            <el-form-item label="GitLab Base URL" required>
              <el-input v-model="form.baseUrl" placeholder="例如：https://gitlab.example.com" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="运行状态">
              <el-switch
                v-model="enabledSwitch"
                inline-prompt
                active-text="启用"
                inactive-text="停用"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="18">
          <el-col :span="12">
            <el-form-item label="Private Token">
              <el-input
                v-model="form.privateToken"
                show-password
                placeholder="留空则保留当前 Token"
              />
              <div class="field-hint">
                {{ form.hasPrivateToken ? '当前已配置 Token，留空保存会保留现有值。' : '首次保存请填写 Token。' }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Webhook Token">
              <el-input
                v-model="form.webhookToken"
                show-password
                placeholder="留空则保留当前 Webhook Token"
              />
              <div class="field-hint">
                {{ form.hasWebhookToken ? '当前已配置 Webhook Token，留空保存会保留现有值。' : '如启用 webhook 校验，请填写。' }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">调度配置</el-divider>

        <el-row :gutter="18">
          <el-col :span="8">
            <el-form-item label="调度开关">
              <el-switch
                v-model="form.schedulesEnabled"
                inline-prompt
                active-text="开"
                inactive-text="关"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="日报分析 Cron">
              <el-input v-model="form.dailyAnalyzeCron" placeholder="0 15 20 * * ?" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="日报生成 Cron">
              <el-input v-model="form.dailyReportCron" placeholder="0 30 20 * * ?" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="18">
          <el-col :span="8">
            <el-form-item label="补偿任务 Cron">
              <el-input v-model="form.backfillCron" placeholder="0 0/30 * * * ?" />
            </el-form-item>
          </el-col>
        </el-row>

        <div class="form-actions">
          <el-button :loading="loading" @click="loadConfig">重置</el-button>
          <el-button type="primary" :loading="saving" @click="saveConfig">保存配置</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { gitlabRuntimeConfigApi, type GitlabRuntimeConfig } from '@/api'

const loading = ref(false)
const saving = ref(false)
const form = reactive<GitlabRuntimeConfig>({
  baseUrl: '',
  privateToken: '',
  webhookToken: '',
  hasPrivateToken: false,
  hasWebhookToken: false,
  schedulesEnabled: false,
  dailyAnalyzeCron: '',
  dailyReportCron: '',
  backfillCron: '',
  enabled: 1,
})

const enabledSwitch = computed({
  get: () => form.enabled === 1,
  set: (value: boolean) => {
    form.enabled = value ? 1 : 0
  },
})

onMounted(loadConfig)

async function loadConfig() {
  loading.value = true
  try {
    const config = await gitlabRuntimeConfigApi.get()
    Object.assign(form, config, { privateToken: '', webhookToken: '' })
  } finally {
    loading.value = false
  }
}

async function saveConfig() {
  if (!form.baseUrl.trim()) {
    ElMessage.warning('请输入 GitLab Base URL')
    return
  }
  saving.value = true
  try {
    const config = await gitlabRuntimeConfigApi.update({
      baseUrl: form.baseUrl.trim(),
      privateToken: form.privateToken.trim(),
      webhookToken: form.webhookToken.trim(),
      schedulesEnabled: form.schedulesEnabled,
      dailyAnalyzeCron: form.dailyAnalyzeCron.trim(),
      dailyReportCron: form.dailyReportCron.trim(),
      backfillCron: form.backfillCron.trim(),
      enabled: form.enabled,
    })
    Object.assign(form, config, { privateToken: '', webhookToken: '' })
    ElMessage.success('GitLab 配置已保存')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.config-page {
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
}

.gitlab-hero {
  background:
    radial-gradient(circle at 82% 22%, rgba(251, 146, 60, 0.28), transparent 28%),
    linear-gradient(135deg, #111827, #1f2937);
}

.settings-hero p {
  margin: 0 0 8px;
  color: #fdba74;
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

.settings-card {
  margin-top: 18px;
  border-radius: 8px;
}

.settings-form {
  max-width: 1080px;
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
