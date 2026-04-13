<template>
  <div class="portal-layout">
    <header class="header">
      <div class="container header-content">
        <div class="logo">
          <NuxtLink to="/">AI Studio</NuxtLink>
        </div>
        <nav class="nav">
          <NuxtLink to="/">首页</NuxtLink>
          <NuxtLink to="/resource/skill">Skill</NuxtLink>
          <NuxtLink to="/resource/mcp">MCP</NuxtLink>
          <NuxtLink to="/resource/plugin">Plugin</NuxtLink>
          <NuxtLink to="/resource/tutorial">教程</NuxtLink>
          <NuxtLink to="/resource/installer">安装包</NuxtLink>
          <NuxtLink to="/resource/video">视频</NuxtLink>
        </nav>
        <div class="header-actions">
          <template v-if="isLoggedIn()">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                {{ userInfo?.real_name || userInfo?.username }}
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="console">控制台</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <NuxtLink to="/login">
              <el-button type="primary">登录</el-button>
            </NuxtLink>
          </template>
        </div>
      </div>
    </header>

    <main class="main">
      <slot />
    </main>

    <footer class="footer">
      <div class="container">
        <p>&copy; 2026 AI Studio. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
const { userInfo, isLoggedIn, logout } = useUser()
const router = useRouter()

const handleCommand = (command: string) => {
  if (command === 'logout') {
    logout()
    router.push('/')
  } else if (command === 'console') {
    router.push('/console/dashboard')
  }
}
</script>

<style scoped>
.portal-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
}

.logo a {
  font-size: 24px;
  font-weight: bold;
  color: #667eea;
  text-decoration: none;
}

.nav {
  display: flex;
  gap: 32px;
}

.nav a {
  color: #606266;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
}

.nav a:hover,
.nav a.router-link-active {
  color: #667eea;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  cursor: pointer;
  color: #606266;
}

.main {
  flex: 1;
  background: #f5f7fa;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.footer {
  background: #fff;
  padding: 24px 0;
  text-align: center;
  color: #909399;
  font-size: 14px;
}
</style>
