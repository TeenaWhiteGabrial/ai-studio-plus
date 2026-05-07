<template>
  <div class="login-container">
    <!-- 左侧装饰区域 -->
    <div class="login-left">
      <!-- 动态背景粒子 -->
      <div class="particles">
        <div class="particle" v-for="i in 25" :key="i" :style="getParticleStyle(i)"></div>
      </div>

      <!-- 漂浮几何图形 -->
      <div class="floating-shapes">
        <div class="shape shape-circle"></div>
        <div class="shape shape-square"></div>
        <div class="shape shape-triangle"></div>
        <div class="shape shape-ring"></div>
        <div class="shape shape-hexagon"></div>
      </div>

      <!-- 装饰性网格 -->
      <div class="grid-overlay"></div>

      <!-- 光晕效果 -->
      <div class="glow-effect glow-1"></div>
      <div class="glow-effect glow-2"></div>
      <div class="glow-effect glow-3"></div>

      <!-- 浮动线条 -->
      <div class="floating-lines">
        <div class="line" v-for="i in 5" :key="i" :style="getLineStyle(i)"></div>
      </div>

      <div class="left-content">
        <div class="brand-section">
          <div class="logo-wrapper">
            <img class="brand-logo-image" :src="brandLogo" alt="logo">
            <div class="logo-dots">
              <span></span><span></span><span></span>
            </div>
          </div>
          <h1 class="brand-title">AI Studio</h1>
          <p class="brand-tagline">AI Coding 效能提升平台</p>
          <div class="title-underline"></div>
        </div>

        <div class="feature-section">
          <div class="feature-card" v-for="(feature, index) in features" :key="index" :style="getFeatureCardStyle(index)">
            <div class="feature-icon-bg" :style="{ background: feature.color }">
              <span class="feature-emoji">{{ feature.emoji }}</span>
              <div class="icon-shine"></div>
            </div>
            <div class="feature-info">
              <h3>{{ feature.title }}</h3>
              <p>{{ feature.desc }}</p>
            </div>
            <div class="feature-arrow">›</div>
          </div>
        </div>

        <div class="stats-section">
          <div class="stat-item" v-for="(stat, index) in stats" :key="index">
            <span class="stat-value" :style="{ animationDelay: index * 0.2 + 's' }">{{ stat.value }}</span>
            <span class="stat-label">{{ stat.label }}</span>
            <div class="stat-bar">
              <div class="stat-bar-fill" :style="{
                width: stat.fillWidth,
                animationDelay: (index * 0.3 + 0.5) + 's'
              }"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部装饰 -->
      <div class="bottom-decoration">
        <div class="wave-container">
          <svg class="wave" viewBox="0 0 1440 120" preserveAspectRatio="none">
            <defs>
              <linearGradient id="waveGradient" x1="0%" y1="0%" x2="100%" y2="0%">
                <stop offset="0%" style="stop-color:rgba(102,126,234,0.3)"/>
                <stop offset="100%" style="stop-color:rgba(118,75,162,0.3)"/>
              </linearGradient>
            </defs>
            <path class="wave-path wave1" d="M0,64 C480,160 960,0 1440,64 L1440,120 L0,120 Z" fill="url(#waveGradient)"></path>
            <path class="wave-path wave2" d="M0,64 C480,120 960,0 1440,64 L1440,120 L0,120 Z" fill="rgba(255,255,255,0.05)"></path>
            <path class="wave-path wave3" d="M0,80 C480,140 960,20 1440,80 L1440,120 L0,120 Z" fill="rgba(255,255,255,0.03)"></path>
          </svg>
        </div>
      </div>

      <!-- 星光点缀 -->
      <div class="stars">
        <div class="star" v-for="i in 30" :key="i" :style="getStarStyle(i)"></div>
      </div>
    </div>

    <!-- 右侧登录表单 -->
    <div class="login-right">
      <!-- 右上角装饰 -->
      <div class="corner-decoration top-right"></div>
      <!-- 左下角装饰 -->
      <div class="corner-decoration bottom-left"></div>

      <!-- 漂浮装饰 -->
      <div class="floating-bubble bubble-1"></div>
      <div class="floating-bubble bubble-2"></div>
      <div class="floating-bubble bubble-3"></div>

      <div class="login-wrapper">
        <div class="login-header">
          <div class="welcome-badge">
            <span class="badge-dot"></span>
            <span class="badge-text">欢迎回来</span>
          </div>
          <h2 class="login-title">登录到 AI Studio</h2>
          <p class="login-subtitle">使用您的账户凭证访问平台</p>
          <el-alert v-if="loginError" :title="loginError" type="error" show-icon :closable="false" class="login-error-alert" />
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" size="large" class="login-form">
          <el-form-item prop="username">
            <div class="input-group">
              <span class="input-label">用户名</span>
              <el-input
                v-model="form.username"
                placeholder="请输入用户名"
                prefix-icon="User"
                class="custom-input"
              />
            </div>
          </el-form-item>
          <el-form-item prop="password">
            <div class="input-group">
              <span class="input-label">密码</span>
              <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                show-password
                @keyup.enter="handleLogin"
                class="custom-input"
              />
            </div>
          </el-form-item>
          <el-form-item class="remember-row">
            <el-checkbox v-model="rememberMe" class="remember-checkbox">
              <span class="checkbox-label">记住密码</span>
            </el-checkbox>

          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              :loading="loading"
              class="login-btn"
              @click="handleLogin"
            >
              <span class="btn-text">立即登录</span>
              <span class="btn-icon">→</span>
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import type { FormInstance } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)
const rememberMe = ref(false)
const loginError = ref('')
const form = ref({ username: '', password: '' })
const brandLogo = `${import.meta.env.BASE_URL}ai-studio-logo.svg`
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const features = [
  { emoji: '📦', title: 'Skill 技能管理', desc: '统一管理和共享团队 AI 技能，提升复用效率', color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { emoji: '🔌', title: 'MCP 服务集成', desc: '一站式 MCP 服务器配置，快速连接外部服务', color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { emoji: '🧩', title: '插件与教程', desc: '丰富的插件资源和教程，助力团队成长', color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
]

const stats = [
  { value: 'Skills', label: '技能资源', fillWidth: '85%' },
  { value: 'MCPs', label: '服务集成', fillWidth: '70%' },
  { value: '100%', label: '团队赋能', fillWidth: '95%' },
]

const particleCache = ref<Array<any>>([])
const starCache = ref<Array<any>>([])
const lineCache = ref<Array<any>>([])

function getParticleStyle(index: number) {
  if (!particleCache.value[index]) {
    particleCache.value[index] = {
      left: `${Math.random() * 100}%`,
      top: '100%',
      width: `${Math.random() * 6 + 2}px`,
      height: `${Math.random() * 6 + 2}px`,
      animationDuration: `${Math.random() * 10 + 10}s`,
      animationDelay: `${Math.random() * 5}s`,
    }
  }
  return particleCache.value[index]
}

function getStarStyle(index: number) {
  if (!starCache.value[index]) {
    starCache.value[index] = {
      left: `${Math.random() * 100}%`,
      top: `${Math.random() * 100}%`,
      animationDuration: `${Math.random() * 2 + 1}s`,
      animationDelay: `${Math.random() * 2}s`,
    }
  }
  return starCache.value[index]
}

function getLineStyle(index: number) {
  if (!lineCache.value[index]) {
    lineCache.value[index] = {
      left: `${Math.random() * 100}%`,
      height: `${Math.random() * 100 + 50}px`,
      animationDuration: `${Math.random() * 5 + 8}s`,
      animationDelay: `${index * 1.5}s`,
    }
  }
  return lineCache.value[index]
}

function getFeatureCardStyle(index: number) {
  return {
    animationDelay: `${index * 0.15}s`,
  }
}

// 页面加载时读取记住的密码
onMounted(() => {
  const remembered = userStore.getRememberedCredentials()
  if (remembered) {
    form.value.username = remembered.username
    form.value.password = remembered.password
    rememberMe.value = true
  }
})

async function handleLogin() {
  loginError.value = '' // 先清除之前的错误
  await formRef.value?.validate()
  loading.value = true
  try {
    await userStore.login(form.value.username, form.value.password)
    if (rememberMe.value) {
      userStore.saveRememberedCredentials(form.value.username, form.value.password)
    }
    router.push('/console/dashboard')
  } catch (error: any) {
    // 显示错误提示
    loginError.value = error.message || '登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.login-container {
  display: flex;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  background: #f5f7fa;
}

/* ========== 左侧装饰区域 ========== */
.login-left {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* ========== 动态粒子背景 ========== */
.particles {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  z-index: 1;
}

.particle {
  position: absolute;
  background: rgba(255,255,255,0.3);
  border-radius: 50%;
  animation: particle-rise linear infinite;
}

@keyframes particle-rise {
  0% {
    transform: translateY(0) scale(0) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  50% {
    transform: translateY(-25vh) scale(1) rotate(180deg);
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100vh) scale(0.5) rotate(360deg);
    opacity: 0;
  }
}

/* ========== 漂浮几何图形 ========== */
.floating-shapes {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 2;
  pointer-events: none;
}

.shape {
  position: absolute;
  animation: shape-float linear infinite;
}

.shape-circle {
  width: 80px;
  height: 80px;
  border: 2px solid rgba(255, 255, 255, 0.15);
  border-radius: 50%;
  top: 15%;
  left: 10%;
  animation-duration: 20s;
}

.shape-square {
  width: 60px;
  height: 60px;
  border: 2px solid rgba(255, 255, 255, 0.15);
  top: 70%;
  left: 15%;
  animation-duration: 25s;
  animation-delay: -5s;
}

.shape-triangle {
  width: 0;
  height: 0;
  border-left: 35px solid transparent;
  border-right: 35px solid transparent;
  border-bottom: 60px solid rgba(255, 255, 255, 0.1);
  top: 40%;
  left: 75%;
  animation-duration: 22s;
  animation-delay: -10s;
}

.shape-ring {
  width: 100px;
  height: 100px;
  border: 3px solid rgba(255, 255, 255, 0.1);
  border-top-color: rgba(255, 255, 255, 0.4);
  border-radius: 50%;
  top: 80%;
  left: 70%;
  animation: shape-spin 30s linear infinite;
}

.shape-hexagon {
  width: 70px;
  height: 60px;
  background: rgba(255, 255, 255, 0.08);
  clip-path: polygon(50% 0%, 100% 25%, 100% 75%, 50% 100%, 0% 75%, 0% 25%);
  top: 25%;
  left: 80%;
  animation-duration: 28s;
  animation-delay: -15s;
}

@keyframes shape-float {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  25% {
    transform: translate(20px, -20px) rotate(90deg);
  }
  50% {
    transform: translate(-10px, -40px) rotate(180deg);
  }
  75% {
    transform: translate(-30px, -20px) rotate(270deg);
  }
}

@keyframes shape-spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* ========== 网格覆盖层 ========== */
.grid-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    linear-gradient(rgba(255,255,255,0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,0.05) 1px, transparent 1px);
  background-size: 50px 50px;
  pointer-events: none;
  z-index: 3;
  animation: grid-move 20s linear infinite;
}

@keyframes grid-move {
  0% { background-position: 0 0; }
  100% { background-position: 50px 50px; }
}

/* ========== 光晕效果 ========== */
.glow-effect {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255,255,255,0.15) 0%, transparent 70%);
  animation: glow-pulse linear infinite;
  z-index: 4;
}

.glow-1 {
  width: 400px;
  height: 400px;
  top: -100px;
  left: -100px;
  animation-duration: 8s;
}

.glow-2 {
  width: 300px;
  height: 300px;
  top: 50%;
  right: -50px;
  animation-duration: 10s;
  animation-delay: -3s;
}

.glow-3 {
  width: 350px;
  height: 350px;
  bottom: 100px;
  left: 20%;
  animation-duration: 12s;
  animation: -6s;
}

@keyframes glow-pulse {
  0%, 100% {
    transform: scale(1) translate(0, 0);
    opacity: 0.5;
  }
  25% {
    transform: scale(1.2) translate(20px, -20px);
    opacity: 0.8;
  }
  50% {
    transform: scale(0.9) translate(-10px, 20px);
    opacity: 0.4;
  }
  75% {
    transform: scale(1.1) translate(-20px, -10px);
    opacity: 0.7;
  }
}

/* ========== 浮动线条 ========== */
.floating-lines {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 5;
  pointer-events: none;
}

.line {
  position: absolute;
  width: 1px;
  background: linear-gradient(to bottom, transparent, rgba(255,255,255,0.3), transparent);
  animation: line-rise linear infinite;
}

@keyframes line-rise {
  0% {
    transform: translateY(100vh);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100px);
    opacity: 0;
  }
}

/* ========== 左侧内容区域 ========== */
.left-content {
  position: relative;
  z-index: 10;
  padding: 60px 80px;
  display: flex;
  flex-direction: column;
  height: 100%;
  justify-content: space-between;
}

/* ========== 品牌区域 ========== */
.brand-section {
  text-align: center;
  color: #fff;
  padding-top: 40px;
}

.logo-wrapper {
  display: inline-flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.brand-logo-image {
  width: 96px;
  height: 96px;
  border-radius: 22px;
  object-fit: cover;
  filter: drop-shadow(0 18px 38px rgba(37, 99, 235, 0.38));
  animation: logo-breathe 4s ease-in-out infinite;
}

.logo-circle {
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid rgba(255,255,255,0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15), inset 0 0 40px rgba(255,255,255,0.1);
  position: relative;
  animation: logo-breathe 4s ease-in-out infinite;
}

@keyframes logo-breathe {
  0%, 100% {
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15), inset 0 0 40px rgba(255,255,255,0.1);
  }
  50% {
    box-shadow: 0 12px 48px rgba(0, 0, 0, 0.2), inset 0 0 60px rgba(255,255,255,0.2);
  }
}

.logo-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 24px;
  border: 2px solid transparent;
  border-top-color: rgba(255, 255, 255, 0.6);
  border-right-color: rgba(255, 255, 255, 0.4);
  animation: logo-spin 3s linear infinite;
}

.logo-ring-2 {
  width: 110%;
  height: 110%;
  border-width: 1.5px;
  animation-duration: 5s;
  animation-direction: reverse;
}

.logo-ring-3 {
  width: 120%;
  height: 120%;
  border-width: 1px;
  animation-duration: 7s;
}

@keyframes logo-spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.logo-letter {
  font-size: 50px;
  font-weight: 700;
  color: #fff;
  text-shadow: 0 2px 20px rgba(0, 0, 0, 0.2);
  animation: letter-glow 3s ease-in-out infinite;
}

@keyframes letter-glow {
  0%, 100% {
    text-shadow: 0 2px 20px rgba(0, 0, 0, 0.2), 0 0 40px rgba(255,255,255,0.3);
  }
  50% {
    text-shadow: 0 2px 30px rgba(0, 0, 0, 0.3), 0 0 60px rgba(255,255,255,0.6);
  }
}

.logo-dots {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.logo-dots span {
  width: 10px;
  height: 10px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  animation: dot-pulse 2s ease-in-out infinite;
  box-shadow: 0 0 10px rgba(255,255,255,0.5);
}

.logo-dots span:nth-child(2) { animation-delay: 0.2s; }
.logo-dots span:nth-child(3) { animation-delay: 0.4s; }

@keyframes dot-pulse {
  0%, 100% { transform: scale(1); opacity: 0.6; }
  50% { transform: scale(1.3); opacity: 1; }
}

.brand-title {
  font-size: 52px;
  font-weight: 800;
  margin-bottom: 12px;
  letter-spacing: 3px;
  text-shadow: 0 2px 20px rgba(0, 0, 0, 0.15);
  background: linear-gradient(180deg, #fff 0%, rgba(255,255,255,0.8) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: title-shift 5s ease-in-out infinite;
}

@keyframes title-shift {
  0%, 100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

.brand-tagline {
  font-size: 16px;
  opacity: 0.9;
  font-weight: 300;
  letter-spacing: 2px;
  color: rgba(255, 255, 255, 0.85);
}

.title-underline {
  width: 100px;
  height: 3px;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.8), transparent);
  margin: 20px auto;
  animation: underline-slide 3s ease-in-out infinite;
}

@keyframes underline-slide {
  0%, 100% {
    width: 60px;
    opacity: 0.5;
  }
  50% {
    width: 120px;
    opacity: 1;
  }
}

/* ========== 特性卡片区域 ========== */
.feature-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin: 30px 0;
}

.feature-card {
  display: flex;
  align-items: center;
  gap: 20px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 18px 24px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  animation: feature-slide-in 0.6s ease-out backwards;
  position: relative;
  overflow: hidden;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100.%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.1), transparent);
  transition: left 0.5s ease;
}

.feature-card:hover::before {
  left: 100%;
}

.feature-card:hover {
  background: rgba(255, 255, 255, 0.18);
  transform: translateX(15px) scale(1.02);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
}

@keyframes feature-slide-in {
  from {
    opacity: 0;
    transform: translateX(-30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.feature-icon-bg {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  position: relative;
  overflow: hidden;
}

.icon-shine {
  position: absolute;
  width: 100%;
  height: 100%;
  background: linear-gradient(45deg, transparent 40%, rgba(255,255,255,0.3) 50%, transparent 60%);
  animation: shine 3s ease-in-out infinite;
}

@keyframes shine {
  0% { transform: translateX(-100%) rotate(45deg); }
  100% { transform: translateX(100%) rotate(45deg); }
}

.feature-emoji {
  font-size: 28px;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.2));
}

.feature-info h3 {
  color: #fff;
  font-size: 17px;
  font-weight: 600;
  margin-bottom: 4px;
}

.feature-info p {
  color: rgba(255, 255, 255, 0.75);
  font-size: 13px;
}

.feature-arrow {
  margin-left: auto;
  font-size: 24px;
  color: rgba(255, 255, 255, 0.5);
  transition: all 0.3s ease;
}

.feature-card:hover .feature-arrow {
  color: #fff;
  transform: translateX(5px);
}

/* ========== 统计数据区域 ========== */
.stats-section {
  display: flex;
  justify-content: space-between;
  gap: 30px;
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 28px 32px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  animation: stats-fade-in 1s ease-out 0.45s backwards;
}

@keyframes stats-fade-in {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.stat-value {
  font-size: 34px;
  font-weight: 700;
  color: #fff;
  animation: stat-count-up 1s ease-out backwards;
}

@keyframes stat-count-up {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.stat-label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.75);
  font-weight: 500;
}

.stat-bar {
  width: 100%;
  height: 4px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 2px;
  overflow: hidden;
  margin-top: 4px;
}

.stat-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, rgba(255,255,255,0.4) 0%, rgba(255,255,255,0.9) 100%);
  border-radius: 2px;
  animation: bar-fill 1.2s ease-out backwards;
}

@keyframes bar-fill {
  from { width: 0; }
  to { width: var(--fill-width); }
}

/* ========== 底部波浪装饰 ========== */
.bottom-decoration {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 140px;
  overflow: hidden;
  z-index: 6;
}

.wave-container {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
}

.wave {
  width: 100%;
  height: 100%;
}

.wave-path {
  animation: wave-flow 10s ease-in-out infinite;
}

.wave1 {
  animation-duration: 12s;
}

.wave2 {
  animation-duration: 15s;
  animation-direction: reverse;
}

.wave3 {
  animation-duration: 18s;
  animation-delay: -5s;
}

@keyframes wave-flow {
  0%, 100% {
    transform: translateX(0) scaleY(1);
  }
  50% {
    transform: translateX(-30px) scaleY(1.15);
  }
}

/* ========== 星光点缀 ========== */
.stars {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 7;
  pointer-events: none;
}

.star {
  position: absolute;
  width: 3px;
  height: 3px;
  background: #fff;
  border-radius: 50%;
  animation: star-twinkle linear infinite;
  box-shadow: 0 0 6px rgba(255,255,255,0.8), 0 0 12px rgba(255,255,255,0.4);
}

@keyframes star-twinkle {
  0%, 100% {
    opacity: 0.2;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.5);
  }
}

/* ========== 右侧登录表单 ========== */
.login-right {
  width: 560px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  box-shadow: -10px 0 40px rgba(0, 0, 0, 0.08);
}

/* 角落装饰 */
.corner-decoration {
  position: absolute;
  width: 250px;
  height: 250px;
  pointer-events: none;
}

.corner-decoration.top-right {
  top: -125px;
  right: -125px;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.12) 0%, transparent 70%);
  border-radius: 50%;
  animation: corner-pulse 8s ease-in-out infinite;
}

.corner-decoration.bottom-left {
  bottom: -125px;
  left: -125px;
  background: radial-gradient(circle, rgba(118, 75, 162, 0.12) 0%, transparent 70%);
  border-radius: 50%;
  animation: corner-pulse 10s ease-in-out infinite reverse;
}

@keyframes corner-pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.2);
    opacity: 0.8;
  }
}

/* 漂浮气泡 */
.floating-bubble {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.08) 0%, rgba(118, 75, 162, 0.08) 100%);
  animation: bubble-float linear infinite;
  pointer-events: none;
}

.bubble-1 {
  width: 80px;
  height: 80px;
  top: 15%;
  left: 10%;
  animation-duration: 15s;
}

.bubble-2 {
  width: 50px;
  height: 50px;
  top: 70%;
  right: 15%;
  animation-duration: 12s;
  animation-delay: -5s;
}

.bubble-3 {
  width: 30px;
  height: 30px;
  bottom: 20%;
  left: 20%;
  animation-duration: 10s;
  animation-delay: -8s;
}

@keyframes bubble-float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  25% {
    transform: translate(15px, -15px) scale(1.1);
  }
  50% {
    transform: translate(-10px, -25px) scale(0.95);
  }
  75% {
    transform: translate(-20px, 10px) scale(1.05);
  }
}

.login-wrapper {
  width: 100%;
  max-width: 420px;
  padding: 60px 50px;
}

/* 登录头部 */
.login-header {
  margin-bottom: 40px;
}

.welcome-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  padding: 8px 16px;
  border-radius: 20px;
  margin-bottom: 20px;
}

.badge-dot {
  width: 8px;
  height: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  animation: badge-pulse 2s ease-in-out infinite;
}

@keyframes badge-pulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.2); opacity: 0.8; }
}

.badge-text {
  font-size: 13px;
  color: #667eea;
  font-weight: 500;
}

.login-title {
  font-size: 32px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 12px;
}

.login-subtitle {
  font-size: 15px;
  color: #909399;
  line-height: 1.6;
}

.login-error-alert {
  margin-top: 16px;
}

/* 登录表单 */
.login-form {
  margin-top: 32px;
}

.login.login-form :deep(.el-form-item) {
  width: 100%;
}

.login-form :deep(.el-form-item--default-width) {
  width: 100%;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.input-label {
  font-size: 14px;
  font-weight: 600;
  color: #4a4a68;
}

.custom-input {
  width: 100%;
}

.custom-input :deep(.el-input__wrapper) {
  height: 52px;
  border-radius: 12px;
  padding: 0 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  background: #f8f9fc;
  width: 100%;
}

/* 确保用户名和密码输入框宽度一致 */
.custom-input :deep(.el-input__inner) {
  font-size: 15px;
  color: #1a1a2e;
  padding-right: 0 !important;
}

.custom-input :deep(.el-input__wrapper:hover) {
  background: #fff;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.15);
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  background: #fff;
  box-shadow: 0 4px 24px rgba(102, 126, 234, 0.25);
}

.custom-input :deep(.el-input__suffix) {
  display: flex;
  align-items: center;
}

.remember-row {
  margin-bottom: 28px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.remember-checkbox {
  color: #606266;
}

.checkbox-label {
  font-size: 14px;
}

.forgot-link {
  font-size: 14px;
  color: #667eea;
  text-decoration: none;
  transition: color 0.3s ease;
}

.forgot-link:hover {
  color: #764ba2;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 54px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
}

.login-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 30px rgba(102, 126, 234, 0.45);
}

.login-btn:active {
  transform: translateY(-1px);
}

.btn-text {
  letter-spacing: 1px;
}

.btn-icon {
  font-size: 18px;
  transition: transform 0.3s ease;
}

.login-btn:hover .btn-icon {
  transform: translateX(5px);
}

/* 登录底部 */
.login-footer {
  margin-top: 32px;
  text-align: center;
}

.footer-text {
  font-size: 14px;
  color: #909399;
}

.signup-link {
  color: #667eea;
  font-weight: 600;
  text-decoration: none;
  margin-left: 4px;
  transition: color 0.3s ease;
}

.signup-link:hover {
  color: #764ba2;
}

/* 响应式设计 */
@media screen and (max-width: 1024px) {
  .login-left {
    display: none;
  }

  .login-right {
    width: 100%;
  }

  .login-wrapper {
    padding: 40px 30px;
  }
}
</style>
