<template>
  <el-container class="layout-container">
    <el-aside :width="sidebarWidth" class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="logo">
        <img class="logo-icon" :src="siteLogo" alt="logo">
        <span class="logo-text" v-if="!isCollapsed">{{ siteName }}</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        :collapse="isCollapsed"
        router
      >
        <template v-for="menu in visibleMenus" :key="menu.id">
          <el-sub-menu v-if="menu.children?.length" :index="String(menu.id)">
            <template #title>
              <el-icon><component :is="menu.icon || 'Menu'" /></el-icon>
              <span>{{ menu.name }}</span>
            </template>
            <el-menu-item
              v-for="child in menu.children"
              :key="child.id"
              :index="withConsolePrefix(child.path)"
            >
              <el-icon><component :is="child.icon || 'Menu'" /></el-icon>
              <span>{{ child.name }}</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="withConsolePrefix(menu.path)">
            <el-icon><component :is="menu.icon || 'Menu'" /></el-icon>
            <span>{{ menu.name }}</span>
          </el-menu-item>
        </template>
      </el-menu>

      <!-- 收起/展开按钮 -->
      <!-- <div class="collapse-trigger" @click="toggleSidebar">
        <el-icon>
          <Fold v-if="!isCollapsed" />
          <Expand v-else />
        </el-icon>
      </div> -->
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
          <el-popover placement="bottom-end" width="430" trigger="click" popper-class="console-message-popover" @show="loadMessagePreview">
            <template #reference>
              <el-badge :value="unreadSummary.totalUnread" :hidden="!unreadSummary.totalUnread" class="message-badge">
                <el-button circle>
                  <el-icon><Bell /></el-icon>
                </el-button>
              </el-badge>
            </template>
            <div class="message-popover">
              <div class="message-tabs-wrap">
                <div class="message-tabs-bar">
                  <div class="message-tabs">
                    <button
                      type="button"
                      class="message-tab"
                      :class="{ active: messageTab === 'notifications' }"
                      @click="messageTab = 'notifications'"
                    >
                      <span>通知</span>
                      <span v-if="unreadSummary.notificationUnread" class="message-tab-count">{{ unreadSummary.notificationUnread }}</span>
                    </button>
                    <button
                      type="button"
                      class="message-tab"
                      :class="{ active: messageTab === 'announcements' }"
                      @click="messageTab = 'announcements'"
                    >
                      <span>公告</span>
                      <span v-if="unreadSummary.announcementUnread" class="message-tab-count">{{ unreadSummary.announcementUnread }}</span>
                    </button>
                  </div>
                  <button type="button" class="message-view-all" @click="router.push('/console/messages')">查看全部</button>
                </div>
                <div v-if="messageTab === 'notifications'" class="message-panel">
                  <div v-if="previewNotifications.length" class="message-list">
                    <div v-for="item in previewNotifications" :key="item.id" class="message-item notification-preview" @click="markNotificationRead(item)">
                      <div class="message-preview-body">
                        <div class="message-title-line">
                          <div class="message-title">{{ item.title || item.type }}</div>
                          <div class="message-preview-time">{{ formatMessageTime(item.createdAt || item.created_at) }}</div>
                        </div>
                        <div class="message-content">{{ item.content }}</div>
                      </div>
                    </div>
                  </div>
                  <el-empty v-else description="暂无通知" :image-size="72" />
                </div>
                <div v-else class="message-panel">
                  <div v-if="previewAnnouncements.length" class="message-list">
                    <div
                      v-for="item in previewAnnouncements"
                      :key="item.id"
                      class="message-item announcement-preview"
                      :class="{ unread: !item.read, pinned: item.pinned }"
                      @click="openAnnouncementDetail(item)"
                    >
                      <div class="message-preview-body">
                        <div class="message-title-line">
                          <span class="message-title">{{ item.title }}</span>
                          <span class="message-preview-time">{{ formatMessageTime(item.publishedAt || item.published_at) }}</span>
                        </div>
                        <div class="message-content">{{ item.content }}</div>
                      </div>
                    </div>
                  </div>
                  <el-empty v-else description="暂无公告" :image-size="72" />
                </div>
              </div>
            </div>
          </el-popover>
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <!-- 头像显示 -->
              <el-avatar
                v-if="userInfo?.avatar"
                :src="userInfo.avatar"
                :size="32"
              />
              <el-avatar v-else :size="32" icon="UserFilled" />
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

    <el-dialog v-model="announcementDialogVisible" width="720px" class="announcement-rich-dialog">
      <template #header>
        <div class="announcement-dialog-hero">
          <div class="announcement-dialog-kicker">
            <span>公告</span>
            <el-tag v-if="announcementDetail?.pinned" size="small" type="danger" effect="dark">置顶</el-tag>
          </div>
          <div class="announcement-dialog-title">
            {{ announcementDetail?.title || '公告详情' }}
          </div>
          <div class="announcement-dialog-meta">{{ formatMessageTime(announcementDetail?.publishedAt || announcementDetail?.published_at) }}</div>
        </div>
      </template>
      <div v-loading="announcementDetailLoading" class="announcement-dialog-body">
        <div class="announcement-dialog-content">{{ announcementDetail?.content }}</div>
      </div>
    </el-dialog>
  </el-container>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useMenuStore, type ConsoleMenu } from '@/stores/menu'
import ThemeToggle from '@/components/ThemeToggle.vue'
import { getSiteConfig } from '@/api/site'
import { messageApi } from '@/api/message'
import { Fold, Expand, Bell } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const menuStore = useMenuStore()
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
const visibleMenus = computed(() => filterVisibleMenus(menuStore.menus))
const siteName = ref('AI Studio')
const siteLogo = ref(`${import.meta.env.BASE_URL}ai-studio-logo.svg`)
const unreadSummary = ref({ totalUnread: 0, notificationUnread: 0, announcementUnread: 0 })
const previewNotifications = ref<any[]>([])
const previewAnnouncements = ref<any[]>([])
const messageTab = ref('notifications')
const announcementDialogVisible = ref(false)
const announcementDetailLoading = ref(false)
const announcementDetail = ref<any | null>(null)
let messageTimer: number | undefined

onMounted(async () => {
  if (menuStore.menus.length === 0) {
    await menuStore.fetchMenus()
  }
  try {
    const config = await getSiteConfig()
    siteName.value = config.siteName || siteName.value
    siteLogo.value = resolveAssetUrl(config.logoUrl)
  } catch {
    siteLogo.value = `${import.meta.env.BASE_URL}ai-studio-logo.svg`
  }
  await loadUnreadSummary()
  messageTimer = window.setInterval(loadUnreadSummary, 60000)
})

onUnmounted(() => {
  if (messageTimer) window.clearInterval(messageTimer)
})

function resolveAssetUrl(url?: string) {
  if (!url) return `${import.meta.env.BASE_URL}ai-studio-logo.svg`
  if (/^(https?:)?\/\//.test(url)) return url
  return `${import.meta.env.BASE_URL}${url.replace(/^\//, '')}`
}

function filterVisibleMenus(menus: ConsoleMenu[]): ConsoleMenu[] {
  return menus
    .filter(menu => menu.hidden !== 1)
    .map(menu => ({
      ...menu,
      children: filterVisibleMenus(menu.children || []),
    }))
}

function withConsolePrefix(path?: string) {
  if (!path) return '/console/dashboard'
  return path.startsWith('/console') ? path : `/console${path}`
}

// 切换侧边栏收起/展开
function toggleSidebar() {
  isCollapsed.value = !isCollapsed.value
  localStorage.setItem('sidebarCollapsed', isCollapsed.value.toString())
}

const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout()
    menuStore.reset()
    router.push('/console/login')
  } else if (command === 'settings') {
    router.push('/console/settings')
  }
}

async function loadUnreadSummary() {
  try {
    const res: any = await messageApi.summary()
    unreadSummary.value = res.data || { totalUnread: 0, notificationUnread: 0, announcementUnread: 0 }
  } catch {
    // Polling should stay quiet; auth interceptor handles session expiry.
  }
}

async function loadMessagePreview() {
  const [notificationRes, announcementRes] = await Promise.all([
    messageApi.notifications({ page: 1, size: 5 }) as any,
    messageApi.announcements() as any,
  ])
  previewNotifications.value = notificationRes.data?.records || []
  previewAnnouncements.value = (announcementRes.data || []).slice(0, 5)
  await loadUnreadSummary()
}

async function markNotificationRead(item: any) {
  if ((item.is_read ?? item.isRead) !== 1) {
    await messageApi.markNotificationRead(item.id)
    await loadMessagePreview()
  }
}

async function openAnnouncementDetail(item: any) {
  announcementDialogVisible.value = true
  announcementDetailLoading.value = true
  try {
    const res: any = await messageApi.announcementDetail(item.id)
    announcementDetail.value = res.data
    if (!item.read) {
      await messageApi.markAnnouncementRead(item.id)
      item.read = true
      if (announcementDetail.value) announcementDetail.value.read = true
    }
    await loadUnreadSummary()
  } finally {
    announcementDetailLoading.value = false
  }
}

async function markAnnouncementRead(item: any) {
  if (!item.read) {
    await messageApi.markAnnouncementRead(item.id)
    await loadMessagePreview()
  }
}

function formatMessageTime(value?: string) {
  if (!value) return '未发布'
  const normalized = value.replace('T', ' ')
  return normalized.length >= 16 ? normalized.slice(0, 16) : normalized
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
  gap: 10px;
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
  width: 32px;
  height: 32px;
  border-radius: 8px;
  object-fit: cover;
  flex: 0 0 auto;
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

.message-badge {
  display: inline-flex;
}

.message-popover-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 300px;
  overflow: auto;
}

.message-item {
  position: relative;
  padding: 10px;
  border: 1px solid transparent;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s ease, border-color 0.2s ease;
}

.message-item:hover {
  background: hsl(var(--secondary));
  border-color: hsl(var(--border));
}

.message-title-row {
  display: flex;
  align-items: center;
  min-width: 0;
  gap: 6px;
}

.message-unread-dot {
  width: 7px;
  height: 7px;
  border-radius: 999px;
  background: hsl(var(--primary));
  flex: 0 0 auto;
}

.announcement-preview.unread {
  background: hsl(var(--primary) / 0.08);
  border-color: hsl(var(--primary) / 0.18);
}

.message-title {
  color: hsl(var(--foreground));
  font-size: 14px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.message-content {
  display: -webkit-box;
  margin-top: 4px;
  color: hsl(var(--muted-foreground));
  font-size: 12px;
  line-height: 1.4;
  overflow: hidden;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.announcement-dialog-header {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
  padding-right: 28px;
}

.announcement-dialog-title {
  display: flex;
  align-items: center;
  gap: 8px;
  color: hsl(var(--foreground));
  font-size: 18px;
  font-weight: 700;
  line-height: 1.4;
}

.announcement-dialog-meta {
  color: hsl(var(--muted-foreground));
  font-size: 13px;
}

.announcement-dialog-body {
  padding-top: 4px;
}

.announcement-dialog-content {
  min-height: 160px;
  padding: 18px 2px 4px;
  border-top: 1px solid hsl(var(--border));
  color: hsl(var(--foreground));
  font-size: 15px;
  line-height: 1.8;
  white-space: pre-wrap;
}

:deep(.announcement-dialog .el-dialog) {
  border-radius: 8px;
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
  position: relative;
  background: hsl(var(--background));
  padding: 20px;
}
</style>

<style>
.console-message-popover.el-popper {
  padding: 0;
  overflow: hidden;
  border: 1px solid hsl(var(--primary) / 0.22);
  border-radius: 8px;
  box-shadow: 0 18px 48px hsl(var(--foreground) / 0.18);
}

.console-message-popover .message-popover {
  background: hsl(var(--card));
}

.console-message-popover .message-tabs-wrap {
  position: relative;
  padding: 12px 14px 10px;
}

.console-message-popover .message-tabs-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}

.console-message-popover .message-tabs {
  display: inline-flex;
  align-items: center;
  gap: 22px;
  padding: 0;
  background: transparent;
  border: none;
  border-radius: 0;
}

.console-message-popover .message-view-all {
  flex: 0 0 auto;
  padding: 0;
  color: hsl(var(--primary));
  background: transparent;
  border: none;
  cursor: pointer;
  font: inherit;
  font-size: 13px;
  font-weight: 600;
  line-height: 1;
}

.console-message-popover .message-view-all:hover {
  color: hsl(var(--foreground));
}

.console-message-popover .message-tab {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 0;
  height: 32px;
  padding: 0 2px;
  color: hsl(var(--muted-foreground));
  background: transparent;
  border: none;
  border-radius: 0;
  cursor: pointer;
  font: inherit;
  font-size: 14px;
  font-weight: 700;
  transition: color 0.18s ease;
}

.console-message-popover .message-tab::after {
  position: absolute;
  right: 0;
  bottom: 0;
  left: 0;
  height: 2px;
  background: transparent;
  border-radius: 999px;
  content: "";
}

.console-message-popover .message-tab:hover {
  color: hsl(var(--foreground));
}

.console-message-popover .message-tab.active {
  color: hsl(var(--foreground));
  background: transparent;
  box-shadow: none;
}

.console-message-popover .message-tab.active::after {
  background: hsl(var(--primary));
}

.console-message-popover .message-tab-count {
  position: absolute;
  top: -3px;
  right: -16px;
  min-width: 16px;
  height: 16px;
  padding: 0 4px;
  color: white;
  background: #f56c6c;
  border-radius: 999px;
  font-size: 10px;
  font-weight: 700;
  line-height: 16px;
  text-align: center;
  box-shadow: 0 0 0 2px hsl(var(--card));
}

.console-message-popover .message-tab.active .message-tab-count {
  box-shadow: 0 0 0 2px hsl(var(--card));
}

.console-message-popover .message-panel {
  min-height: 88px;
}

.console-message-popover .message-list {
  display: flex;
  flex-direction: column;
  max-height: 360px;
  overflow: auto;
  padding: 0;
}

.console-message-popover .message-item {
  min-height: 68px;
  padding: 12px 0;
  border-bottom: 1px solid hsl(var(--border));
  cursor: pointer;
  transition: background-color 0.18s ease;
}

.console-message-popover .message-item:last-child {
  border-bottom: none;
}

.console-message-popover .message-item:hover {
  background: hsl(var(--secondary) / 0.45);
}

.console-message-popover .announcement-preview.unread {
  background: transparent;
}

.console-message-popover .announcement-preview.pinned {
  box-shadow: none;
}

.console-message-popover .message-preview-body {
  min-width: 0;
}

.console-message-popover .message-title-line,
.console-message-popover .notification-preview .message-title {
  display: flex;
  align-items: center;
  min-width: 0;
  gap: 8px;
}

.console-message-popover .message-title {
  display: block;
  flex: 1 1 auto;
  min-width: 0;
  color: hsl(var(--foreground));
  font-size: 14px;
  font-weight: 800;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.console-message-popover .message-preview-time {
  flex: 0 0 auto;
  max-width: 138px;
  overflow: hidden;
  color: hsl(var(--muted-foreground));
  font-size: 11px;
  text-align: right;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.console-message-popover .message-content {
  display: -webkit-box;
  margin-top: 6px;
  color: hsl(var(--muted-foreground));
  font-size: 12px;
  line-height: 1.55;
  overflow: hidden;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.announcement-rich-dialog.el-dialog,
.announcement-rich-dialog .el-dialog {
  overflow: hidden;
  border-radius: 8px;
  box-shadow: 0 24px 70px hsl(var(--foreground) / 0.22);
}

.announcement-rich-dialog .el-dialog__header {
  padding: 0;
  margin: 0;
}

.announcement-rich-dialog .el-dialog__headerbtn {
  top: 14px;
  right: 14px;
}

.announcement-rich-dialog .el-dialog__headerbtn .el-dialog__close {
  color: white;
}

.announcement-rich-dialog .el-dialog__body {
  padding: 0;
}

.announcement-rich-dialog .announcement-dialog-hero {
  padding: 28px 56px 24px 32px;
  color: white;
  background: linear-gradient(135deg, hsl(var(--primary)), hsl(var(--primary) / 0.68));
}

.announcement-rich-dialog .announcement-dialog-kicker {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
  color: hsl(0 0% 100% / 0.82);
  font-size: 13px;
  font-weight: 700;
}

.announcement-rich-dialog .announcement-dialog-title {
  display: block;
  max-width: 100%;
  overflow: hidden;
  color: white;
  font-size: 22px;
  font-weight: 850;
  line-height: 1.35;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.announcement-rich-dialog .announcement-dialog-meta {
  margin-top: 12px;
  color: hsl(0 0% 100% / 0.78);
  font-size: 13px;
}

.announcement-rich-dialog .announcement-dialog-body {
  padding: 26px 32px 32px;
  background: hsl(var(--background));
}

.announcement-rich-dialog .announcement-dialog-content {
  min-height: 220px;
  padding: 24px;
  color: hsl(var(--foreground));
  background: hsl(var(--card));
  border: 1px solid hsl(var(--border));
  border-radius: 8px;
  font-size: 15px;
  line-height: 1.95;
  white-space: pre-wrap;
}
</style>
