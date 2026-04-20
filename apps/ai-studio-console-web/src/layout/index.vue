<template>
  <el-container class="layout-container">
    <el-aside :width="sidebarWidth" class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="logo">
        <span class="logo-text" v-if="!isCollapsed">AI Studio</span>
        <span class="logo-icon" v-else>A</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        :collapse="isCollapsed"
        router
      >
        <el-menu-item index="/console/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>工作台</span>
        </el-menu-item>
        <el-menu-item index="/console/apikey">
          <el-icon><Key /></el-icon>
          <span>API Key</span>
        </el-menu-item>
        <el-menu-item index="/console/project">
          <el-icon><Folder /></el-icon>
          <span>项目管理</span>
        </el-menu-item>
        <el-menu-item index="/console/task">
          <el-icon><Calendar /></el-icon>
          <span>每日任务</span>
        </el-menu-item>
        <el-menu-item index="/console/resource">
          <el-icon><Box /></el-icon>
          <span>资源中心</span>
        </el-menu-item>
        <el-menu-item index="/console/stats">
          <el-icon><DataLine /></el-icon>
          <span>产出统计</span>
        </el-menu-item>
        <el-menu-item index="/console/settings">
          <el-icon><Setting /></el-icon>
          <span>个人设置</span>
        </el-menu-item>
      </el-menu>

      <!-- 收起/展开按钮 -->
      <div class="collapse-trigger" @click="toggleSidebar">
        <el-icon>
          <Fold v-if="!isCollapsed" />
          <Expand v-else />
        </el-icon>
      </div>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="toggle-btn" @click="toggleSidebar">
            <Fold v-if="!isCollapsed" />
            <Expand v-else />
          </el-icon>
          <span class="page-title">{{ route.meta.title || '控制台' }}</span>
        </div>
        <div class="header-right">
          <ThemeToggle />
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="username" v-if="!isCollapsed">{{ userInfo?.real_name || userInfo?.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="settings">个人设置</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import ThemeToggle from '@/components/ThemeToggle.vue'
import {
  HomeFilled,
  Key,
  Folder,
  Calendar,
  Box,
  DataLine,
  Setting,
  Fold,
  Expand,
  UserFilled,
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
// @ts-ignore - userInfo is a ref that auto-unwraps in templates
const userInfo = userStore.userInfo

// 侧边栏收起状态
const isCollapsed = ref(false)

// 从 localStorage 读取侧边栏状态
const savedCollapsed = localStorage.getItem('sidebarCollapsed')
if (savedCollapsed === 'true') {
  isCollapsed.value = true
}

const sidebarWidth = computed(() => isCollapsed.value ? '64px' : '200px')

const activeMenu = computed(() => route.path)

// 切换侧边栏收起/展开
function toggleSidebar() {
  isCollapsed.value = !isCollapsed.value
  localStorage.setItem('sidebarCollapsed', isCollapsed.value.toString())
}

const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/console/login')
  } else if (command === 'settings') {
    router.push('/console/settings')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background: hsl(var(--card));
  border-right: 1px solid hsl(var(--border));
  transition: width 0.3s ease;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: hsl(var(--secondary));
  border-bottom: 1px solid hsl(var(--border));
  transition: all 0.3s ease;
}

.logo-text {
  color: hsl(var(--primary));
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
}

.logo-icon {
  color: hsl(var(--primary));
  font-size: 24px;
  font-weight: 600;
}

.sidebar.collapsed .logo {
  padding: 0;
}

.sidebar-menu {
  border-right: none;
  background: transparent;
  padding: 8px 0;
}

:deep(.el-menu-item) {
  color: hsl(var(--muted-foreground));
  margin: 4px 8px;
  border-radius: calc(var(--radius) - 2px);
  height: 40px;
  line-height: 40px;
  transition: all 0.2s ease;
}

:deep(.el-menu-item:hover) {
  background: hsl(var(--secondary));
  color: hsl(var(--foreground));
}

:deep(.el-menu-item.is-active) {
  background: hsl(var(--secondary));
  color: hsl(var(--primary));
  box-shadow: var(--ai-glow-sm);
}

:deep(.el-menu-item .el-icon) {
  font-size: 18px;
}

.collapse-trigger {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 8px;
  background: hsl(var(--secondary));
  color: hsl(var(--muted-foreground));
  transition: all 0.2s ease;
}

.collapse-trigger:hover {
  background: hsl(var(--muted));
  color: hsl(var(--foreground));
}

.sidebar.collapsed .collapse-trigger {
  left: 50%;
}

.header {
  background: hsl(var(--card));
  border-bottom: 1px solid hsl(var(--border));
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-right{
  display: flex;
  align-items: center;
  gap: 8px;
}
.toggle-btn {
  cursor: pointer;
  padding: 4px;
  border-radius: 6px;
  transition: all 0.2s ease;
  font-size: 28px;
}

.toggle-btn:hover {
  background: hsl(var(--secondary));
}

.page-title {
  font-size: 16px;
  font-weight: 500;
  color: hsl(var(--foreground));
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 12px;
  border-radius: calc(var(--radius) - 2px);
  transition: all 0.2s ease;
}

.user-info:hover {
  background: hsl(var(--secondary));
}

.username {
  font-size: 14px;
  color: hsl(var(--foreground));
}

:deep(.el-avatar) {
  background: hsl(var(--primary));
  color: white;
}

:deep(.el-dropdown-menu) {
  background: hsl(var(--card));
  border-color: hsl(var(--border));
}

:deep(.el-dropdown-menu__item) {
  color: hsl(var(--foreground));
}

:deep(.el-dropdown-menu__item:hover) {
  background: hsl(var(--secondary));
  color: hsl(var(--primary));
}

.main-content {
  background: hsl(var(--background));
  padding: 20px;
}
</style>
