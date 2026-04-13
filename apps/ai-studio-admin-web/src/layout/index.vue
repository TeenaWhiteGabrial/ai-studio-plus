<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapsed ? '64px' : '220px'" class="sidebar">
      <div class="logo" :class="{ collapsed: isCollapsed }">
        <div class="logo-icon">AI</div>
        <span v-if="!isCollapsed" class="logo-text">AI Studio</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapsed"
        router
        background-color="transparent"
        text-color="rgba(255,255,255,0.72)"
        active-text-color="#ffffff"
        class="sidebar-menu"
      >
        <template v-for="menu in menuStore.menus" :key="menu.id">
          <el-sub-menu v-if="menu.children?.length" :index="String(menu.id)">
            <template #title>
              <el-icon><component :is="menu.icon" /></el-icon>
              <span>{{ menu.name }}</span>
            </template>
            <el-menu-item
              v-for="child in menu.children"
              :key="child.id"
              :index="'/admin' + child.path"
            >
              <el-icon><component :is="child.icon" /></el-icon>
              <span>{{ child.name }}</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="'/admin' + menu.path">
            <el-icon><component :is="menu.icon" /></el-icon>
            <span>{{ menu.name }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container class="main-wrapper">
      <!-- 顶部 Header -->
      <el-header class="header">
        <div class="header-left">
          <div class="collapse-btn" @click="isCollapsed = !isCollapsed">
            <el-icon><Fold v-if="!isCollapsed" /><Expand v-else /></el-icon>
          </div>
          <div class="breadcrumb-area">
            <span class="page-title">{{ route.meta?.title || 'AI Studio' }}</span>
          </div>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar size="small" class="user-avatar">
                {{ userStore.userInfo?.real_name?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="username">{{ userStore.userInfo?.real_name || userStore.userInfo?.username }}</span>
              <el-icon class="arrow-down"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区域 -->
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="page" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useMenuStore } from '@/stores/menu'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const menuStore = useMenuStore()
const isCollapsed = ref(false)
const activeMenu = computed(() => route.path)

onMounted(async () => {
  if (menuStore.menus.length === 0) {
    await menuStore.fetchMenus()
  }
})

async function handleCommand(cmd: string) {
  if (cmd === 'logout') {
    await ElMessageBox.confirm('确认退出登录？', '提示', { type: 'warning' })
    userStore.logout()
    menuStore.reset()
    router.push('/admin/login')
  }
}
</script>

<style scoped>
/* ===== 整体布局 ===== */
.layout-container {
  height: 100vh;
  overflow: hidden;
}

/* ===== 侧边栏 ===== */
.sidebar {
  background: linear-gradient(180deg, #1a2332 0%, #0f1923 100%);
  transition: width 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
  z-index: 10;
}

/* ===== Logo ===== */
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  gap: 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  flex-shrink: 0;
  overflow: hidden;
  white-space: nowrap;
}
.logo.collapsed {
  padding: 0;
  justify-content: center;
}
.logo-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: linear-gradient(135deg, #4096ff, #1677ff);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 700;
  color: #fff;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(22, 119, 255, 0.4);
}
.logo-text {
  font-size: 16px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 0.5px;
}

/* ===== 菜单 ===== */
.sidebar-menu {
  border: none !important;
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}
.sidebar-menu:not(.el-menu--collapse) {
  width: 220px;
}

/* 激活菜单项 */
:deep(.el-menu-item.is-active) {
  background: rgba(22, 119, 255, 0.15) !important;
  color: #fff !important;
  border-right: 3px solid #4096ff;
  border-radius: 0 4px 4px 0;
}
:deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.06) !important;
  color: #fff !important;
}
:deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.06) !important;
  color: #fff !important;
}
:deep(.el-sub-menu .el-menu) {
  background: rgba(0, 0, 0, 0.2) !important;
}
:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  transition: background 0.2s ease, color 0.2s ease !important;
  font-size: 13px;
}

/* ===== 右侧主区域 ===== */
.main-wrapper {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

/* ===== 顶部 Header ===== */
.header {
  height: 56px !important;
  background: #fff;
  border-bottom: 1px solid #edf0f5;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.04);
  flex-shrink: 0;
  z-index: 9;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.collapse-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  cursor: pointer;
  color: #606266;
  font-size: 18px;
  transition: background 0.2s ease, color 0.2s ease;
}
.collapse-btn:hover {
  background: #f0f2f5;
  color: #1677ff;
}
.page-title {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a2e;
}
.header-right {
  display: flex;
  align-items: center;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 10px;
  border-radius: 20px;
  transition: background 0.2s ease;
}
.user-info:hover {
  background: #f0f2f5;
}
.user-avatar {
  background: linear-gradient(135deg, #4096ff, #1677ff) !important;
  font-weight: 600;
}
.username {
  font-size: 13px;
  color: #374151;
  font-weight: 500;
}
.arrow-down {
  font-size: 12px;
  color: #9ca3af;
}

/* ===== 内容区域 ===== */
.main-content {
  background: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
  flex: 1;
}
</style>

