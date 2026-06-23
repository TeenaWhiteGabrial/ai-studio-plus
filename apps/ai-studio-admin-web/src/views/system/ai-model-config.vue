<template>
  <div class="config-page">
    <section class="settings-hero ai-hero">
      <div>
        <p>AI Runtime</p>
        <h1>AI 模型配置</h1>
        <span>维护用于研发活动分析的模型地址、模型名和 API Key。启用后分析任务会优先走真实模型。</span>
      </div>
      <div class="config-status">
        <strong>{{ form.modelName || '未配置模型' }}</strong>
        <span>{{ form.enabled === 1 ? 'AI 分析已启用' : 'AI 分析未启用' }}</span>
        <small>{{ form.hasApiKey ? 'API Key 已配置' : 'API Key 未配置' }}</small>
      </div>
    </section>

    <el-card class="settings-card" shadow="never">
      <el-form label-position="top" :model="form" class="settings-form">
        <el-row :gutter="18">
          <el-col :span="8">
            <el-form-item label="Provider" required>
              <el-input v-model="form.providerName" placeholder="例如：openai-compatible" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="模型服务 URL" required>
              <el-input v-model="form.baseUrl" placeholder="例如：https://api.openai.com/v1" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="模型名称" required>
              <el-input v-model="form.modelName" placeholder="例如：gpt-4.1" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="18">
          <el-col :span="12">
            <el-form-item label="API Key">
              <el-input
                v-model="form.apiKey"
                show-password
                placeholder="留空则保留当前 API Key"
              />
              <div class="field-hint">
                {{ form.hasApiKey ? '当前已配置 API Key，留空保存会保留现有值。' : '首次保存请填写 API Key。' }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Prompt 版本">
              <el-input v-model="form.promptVersion" placeholder="例如：gitlab-activity-v1" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="18">
          <el-col :span="8">
            <el-form-item label="请求超时（ms）">
              <el-input-number v-model="form.requestTimeoutMs" :min="1000" :step="1000" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="最大重试次数">
              <el-input-number v-model="form.maxRetries" :min="0" :max="10" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="启用状态">
              <el-switch
                v-model="enabledSwitch"
                inline-prompt
                active-text="启用"
                inactive-text="停用"
              />
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
import { aiModelConfigApi, type AiModelConfig } from '@/api'

const loading = ref(false)
const saving = ref(false)
const form = reactive<AiModelConfig>({
  providerName: '',
  baseUrl: '',
  modelName: '',
  apiKey: '',
  hasApiKey: false,
  promptVersion: '',
  requestTimeoutMs: 30000,
  maxRetries: 2,
  enabled: 0,
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
    const config = await aiModelConfigApi.get()
    Object.assign(form, config, { apiKey: '' })
  } finally {
    loading.value = false
  }
}

async function saveConfig() {
  if (!form.providerName.trim() || !form.baseUrl.trim() || !form.modelName.trim()) {
    ElMessage.warning('请完整填写 Provider、模型服务 URL 和模型名称')
    return
  }
  if (!form.hasApiKey && !form.apiKey.trim()) {
    ElMessage.warning('请输入 API Key')
    return
  }
  saving.value = true
  try {
    const config = await aiModelConfigApi.update({
      providerName: form.providerName.trim(),
      baseUrl: form.baseUrl.trim(),
      modelName: form.modelName.trim(),
      apiKey: form.apiKey.trim(),
      promptVersion: form.promptVersion.trim(),
      requestTimeoutMs: form.requestTimeoutMs,
      maxRetries: form.maxRetries,
      enabled: form.enabled,
    })
    Object.assign(form, config, { apiKey: '' })
    ElMessage.success('AI 模型配置已保存')
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

.ai-hero {
  background:
    radial-gradient(circle at 82% 22%, rgba(125, 211, 252, 0.28), transparent 28%),
    linear-gradient(135deg, #102a43, #0f172a);
}

.settings-hero p {
  margin: 0 0 8px;
  color: #7dd3fc;
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
