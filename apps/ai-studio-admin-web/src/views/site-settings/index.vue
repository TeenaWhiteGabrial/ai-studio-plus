<template>
  <div class="site-settings-page">
    <section class="settings-hero">
      <div>
        <p>Site Settings</p>
        <h1>网站设置</h1>
        <span>统一管理 portal 的品牌展示、浏览器标题和页脚信息。</span>
      </div>
      <div class="brand-preview">
        <img :src="previewLogo" alt="logo">
        <strong>{{ form.siteName || 'AI Studio' }}</strong>
      </div>
    </section>

    <el-card class="settings-card" shadow="never">
      <el-form label-position="top" :model="form" class="settings-form">
        <el-row :gutter="18">
          <el-col :span="12">
            <el-form-item label="网站标题" required>
              <el-input v-model="form.siteName" maxlength="100" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系方式">
              <el-input v-model="form.contacts" maxlength="500" show-word-limit />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="网站描述">
          <el-input v-model="form.siteDescription" type="textarea" :rows="3" maxlength="500" show-word-limit />
        </el-form-item>

        <el-row :gutter="18">
          <el-col :span="12">
            <el-form-item label="Logo 地址">
              <el-input v-model="form.logoUrl" placeholder="/ai-studio-logo.svg" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="浏览器图标地址">
              <el-input v-model="form.iconUrl" placeholder="/favicon.png" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="底部说明">
          <el-input v-model="form.footerText" maxlength="500" show-word-limit />
        </el-form-item>

        <el-row :gutter="18">
          <el-col :span="12">
            <el-form-item label="版权信息">
              <el-input v-model="form.footerCopyright" maxlength="300" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备案信息">
              <el-input v-model="form.footerRecord" maxlength="200" show-word-limit />
            </el-form-item>
          </el-col>
        </el-row>

        <div class="links-head">
          <span>底部链接</span>
          <el-button type="primary" plain @click="addFooterLink">添加链接</el-button>
        </div>
        <div class="footer-links-editor">
          <div v-for="(link, index) in form.footerLinks" :key="index" class="link-row">
            <el-input v-model="link.name" placeholder="名称" />
            <el-input v-model="link.url" placeholder="路径或 URL" />
            <el-button type="danger" plain @click="removeFooterLink(index)">删除</el-button>
          </div>
        </div>

        <div class="form-actions">
          <el-button :loading="loading" @click="loadConfig">重置</el-button>
          <el-button type="primary" :loading="saving" @click="saveConfig">保存设置</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { siteConfigApi, type SiteConfig } from '@/api'

const loading = ref(false)
const saving = ref(false)
const form = reactive<SiteConfig>({
  siteName: 'AI Studio',
  siteDescription: 'AI 应用开发平台',
  logoUrl: '/ai-studio-logo.svg',
  iconUrl: '/favicon.png',
  footerText: '面向研发团队的 AI 技术社区与资源平台。',
  footerCopyright: 'Copyright © 2026 AI Studio',
  footerRecord: '',
  footerLinks: [
    { name: '首页', url: '/' },
    { name: '社区', url: '/community' },
    { name: '资源中心', url: '/resources' },
    { name: '个人中心', url: '/profile' },
  ],
  contacts: '',
})

const previewLogo = computed(() => resolveAssetUrl(form.logoUrl))

onMounted(loadConfig)

function resolveAssetUrl(url?: string) {
  if (!url) return `${import.meta.env.BASE_URL}ai-studio-logo.svg`
  if (/^(https?:)?\/\//.test(url)) return url
  return `${import.meta.env.BASE_URL}${url.replace(/^\//, '')}`
}

async function loadConfig() {
  loading.value = true
  try {
    Object.assign(form, await siteConfigApi.get())
  } finally {
    loading.value = false
  }
}

function addFooterLink() {
  form.footerLinks.push({ name: '', url: '' })
}

function removeFooterLink(index: number) {
  form.footerLinks.splice(index, 1)
}

async function saveConfig() {
  if (!form.siteName.trim()) {
    ElMessage.warning('请输入网站标题')
    return
  }
  saving.value = true
  try {
    const payload = {
      ...form,
      footerLinks: form.footerLinks.filter(link => link.name.trim() && link.url.trim()),
    }
    Object.assign(form, await siteConfigApi.update(payload))
    ElMessage.success('网站设置已保存')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.site-settings-page {
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
    radial-gradient(circle at 78% 24%, rgba(125, 211, 252, 0.3), transparent 28%),
    linear-gradient(135deg, #111827, #0f172a);
}

.settings-hero p {
  margin: 0 0 8px;
  color: #a3e635;
  font-size: 12px;
  font-weight: 900;
}

.settings-hero h1 {
  margin: 0 0 8px;
  font-size: 30px;
}

.settings-hero span {
  color: rgba(226, 232, 240, 0.78);
}

.brand-preview {
  min-width: 220px;
  display: flex;
  align-items: center;
  gap: 12px;
  border: 1px solid rgba(125, 211, 252, 0.22);
  border-radius: 8px;
  padding: 14px;
  background: rgba(15, 23, 42, 0.7);
}

.brand-preview img {
  width: 44px;
  height: 44px;
  border-radius: 8px;
  object-fit: cover;
}

.settings-card {
  margin-top: 18px;
  border-radius: 8px;
}

.settings-form {
  max-width: 980px;
}

.links-head {
  margin: 6px 0 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 700;
}

.footer-links-editor {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.link-row {
  display: grid;
  grid-template-columns: 180px 1fr 86px;
  gap: 10px;
}

.form-actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
