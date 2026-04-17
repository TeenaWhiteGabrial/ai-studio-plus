<template>
  <div class="profile-page">
    <!-- 左侧导航 -->
    <div class="profile-nav bg-white rounded-lg p-4">
      <div class="user-card">
        <el-avatar v-if="authStore.userName" :size="64" class="mb-3">
          {{ authStore.userName.charAt(0) }}
        </el-avatar>
        <h3 class="user-name">{{ authStore.userName }}</h3>
      </div>
      <nav class="nav-list mt-6">
        <div
          v-for="item in navItems"
          :key="item.key"
          class="nav-item"
          :class="{ active: currentTab === item.key }"
          @click="currentTab = item.key"
        >
          <Icon :name="item.icon" class="mr-2" />
          {{ item.name }}
        </div>
      </nav>
    </div>

    <!-- 右侧内容 -->
    <div class="profile-content">
      <!-- 我的资源 -->
      <div v-if="currentTab === 'resources'" class="content-section">
        <h2 class="section-title">我的资源</h2>
        <div v-if="loadingResources" class="text-center py-8 text-gray-400">
          加载中...
        </div>
        <div v-else-if="myResources.length > 0" class="resource-list">
          <ResourceCard
            v-for="resource in myResources"
            :key="resource.id"
            :resource="resource"
          />
        </div>
        <div v-else class="empty-state">
          <Icon name="material-symbols:folder-open" class="text-5xl text-gray-300 mb-3" />
          <p>暂无发布的资源</p>
        </div>
      </div>

      <!-- 收藏夹 -->
      <div v-if="currentTab === 'favorites'" class="content-section">
        <h2 class="section-title">我的收藏</h2>
        <div v-if="loadingFavorites" class="text-center py-8 text-gray-400">
          加载中...
        </div>
        <div v-else-if="favorites.length > 0" class="article-list">
          <ArticleCard
            v-for="item in favorites"
            :key="item.id"
            :article="item"
          />
        </div>
        <div v-else class="empty-state">
          <Icon name="material-symbols:bookmark-outline" class="text-5xl text-gray-300 mb-3" />
          <p>暂无收藏</p>
        </div>
      </div>

      <!-- 浏览记录 -->
      <div v-if="currentTab === 'history'" class="content-section">
        <h2 class="section-title">
          浏览记录
          <el-button v-if="browseHistory.length > 0" text size="small" @click="handleClearHistory">
            清空记录
          </el-button>
        </h2>
        <div v-if="loadingHistory" class="text-center py-8 text-gray-400">
          加载中...
        </div>
        <div v-else-if="browseHistory.length > 0" class="history-list">
          <div
            v-for="item in browseHistory"
            :key="item.id"
            class="history-item"
            @click="handleHistoryClick(item)"
          >
            <div class="history-cover">
              <img v-if="item.coverImage" :src="item.coverImage" :alt="item.title" />
              <Icon v-else name="material-symbols:article" class="text-2xl text-gray-400" />
            </div>
            <div class="history-info">
              <span class="history-title text-overflow-1">{{ item.title }}</span>
              <span class="history-time">{{ formatTime(item.createTime) }}</span>
            </div>
          </div>
        </div>
        <div v-else class="empty-state">
          <Icon name="material-symbols:history" class="text-5xl text-gray-300 mb-3" />
          <p>暂无浏览记录</p>
        </div>
      </div>

      <!-- 消息通知 -->
      <div v-if="currentTab === 'notifications'" class="content-section">
        <h2 class="section-title">
          消息通知
          <el-button v-if="notifications.length > 0" text size="small" @click="handleMarkAllRead">
            全部标为已读
          </el-button>
        </h2>
        <div v-if="loadingNotifications" class="text-center py-8 text-gray-400">
          加载中...
        </div>
        <div v-else-if="notifications.length > 0" class="notification-list">
          <div
            v-for="item in notifications"
            :key="item.id"
            class="notification-item"
            :class="{ unread: !item.isRead }"
            @click="handleNotificationClick(item)"
          >
            <Icon :name="getNotificationIcon(item.type)" class="notification-icon" />
            <div class="notification-content">
              <span class="notification-title">{{ item.title }}</span>
              <span class="notification-time">{{ formatTime(item.createTime) }}</span>
            </div>
          </div>
        </div>
        <div v-else class="empty-state">
          <Icon name="material-symbols:notifications-none" class="text-5xl text-gray-300 mb-3" />
          <p>暂无通知</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Article } from '~~/shared/types/article'
import type { BrowseHistory, Notification } from '~~/shared/types/community'
import type { Resource } from '~~/shared/types/resource'

definePageMeta({
  middleware: ['auth']
})

const authStore = useAuthStore()
const { getFavoriteList, getBrowseHistoryList, clearBrowseHistory, getNotificationList, markNotificationRead, markAllNotificationRead } = useCommunity()

const currentTab = ref('resources')

const navItems = [
  { name: '我的资源', key: 'resources', icon: 'material-symbols:folder' },
  { name: '收藏夹', key: 'favorites', icon: 'material-symbols:bookmark' },
  { name: '浏览记录', key: 'history', icon: 'material-symbols:history' },
  { name: '消息通知', key: 'notifications', icon: 'material-symbols:notifications' }
]

// 我的资源
const myResources = ref<Resource[]>([])
const loadingResources = ref(false)

// 收藏
const favorites = ref<Article[]>([])
const loadingFavorites = ref(false)

// 浏览记录
const browseHistory = ref<BrowseHistory[]>([])
const loadingHistory = ref(false)

// 通知
const notifications = ref<Notification[]>([])
const loadingNotifications = ref(false)

async function loadMyResources() {
  loadingResources.value = true
  try {
    // TODO: 调用用户资源列表API
    myResources.value = []
  }
  catch (err) {
    console.error('加载资源失败:', err)
  }
  finally {
    loadingResources.value = false
  }
}

async function loadFavorites() {
  loadingFavorites.value = true
  try {
    const res = await getFavoriteList()
    // TODO: 转换收藏数据为文章格式
    favorites.value = []
  }
  catch (err) {
    console.error('加载收藏失败:', err)
  }
  finally {
    loadingFavorites.value = false
  }
}

async function loadBrowseHistory() {
  loadingHistory.value = true
  try {
    const res = await getBrowseHistoryList()
    browseHistory.value = res.list || []
  }
  catch (err) {
    console.error('加载浏览记录失败:', err)
  }
  finally {
    loadingHistory.value = false
  }
}

async function loadNotifications() {
  loadingNotifications.value = true
  try {
    const res = await getNotificationList({ pageSize: 50 })
    notifications.value = res.list || []
  }
  catch (err) {
    console.error('加载通知失败:', err)
  }
  finally {
    loadingNotifications.value = false
  }
}

async function handleClearHistory() {
  try {
    await ElMessageBox.confirm('确定要清空所有浏览记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await clearBrowseHistory()
    browseHistory.value = []
    ElMessage.success('已清空')
  }
  catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

async function handleMarkAllRead() {
  try {
    await markAllNotificationRead()
    notifications.value.forEach((n) => {
      n.isRead = true
    })
    ElMessage.success('已全部标为已读')
  }
  catch (err) {
    ElMessage.error('操作失败')
  }
}

function handleHistoryClick(item: BrowseHistory) {
  if (item.targetType === 'article') {
    navigateTo(`/community/article/${item.targetId}`)
  }
  else if (item.targetType === 'question') {
    navigateTo(`/community/question/${item.targetId}`)
  }
  else if (item.targetType === 'resource') {
    navigateTo(`/resources/skill/${item.targetId}`)
  }
}

async function handleNotificationClick(item: Notification) {
  if (!item.isRead) {
    try {
      await markNotificationRead(item.id)
      item.isRead = true
    }
    catch (err) {
      console.error('标记已读失败:', err)
    }
  }
  if (item.targetId) {
    if (item.targetType === 'article') {
      navigateTo(`/community/article/${item.targetId}`)
    }
    else if (item.targetType === 'question') {
      navigateTo(`/community/question/${item.targetId}`)
    }
  }
}

function getNotificationIcon(type: string) {
  switch (type) {
    case 'comment':
      return 'material-symbols:chat'
    case 'answer':
      return 'material-symbols:chat-bubble'
    case 'like':
      return 'material-symbols:favorite'
    case 'accept':
      return 'material-symbols:check-circle'
    default:
      return 'material-symbols:notifications'
  }
}

function formatTime(time: string) {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / (1000 * 60))
  if (minutes < 1)
    return '刚刚'
  if (minutes < 60)
    return `${minutes} 分钟前`
  const hours = Math.floor(minutes / 60)
  if (hours < 24)
    return `${hours} 小时前`
  const days = Math.floor(hours / 24)
  if (days < 7)
    return `${days} 天前`
  return date.toLocaleDateString('zh-CN')
}

watch(currentTab, (tab) => {
  if (tab === 'resources') {
    loadMyResources()
  }
  else if (tab === 'favorites') {
    loadFavorites()
  }
  else if (tab === 'history') {
    loadBrowseHistory()
  }
  else if (tab === 'notifications') {
    loadNotifications()
  }
})

onMounted(() => {
  loadMyResources()
})
</script>

<style scoped>
.profile-page {
  @apply flex gap-6;
}

.profile-nav {
  @apply w-56 flex-shrink-0;
}

.user-card {
  @apply text-center py-4 border-b border-gray-100;
}

.user-name {
  @apply font-medium text-gray-800;
}

.nav-list {
  @apply space-y-2;
}

.nav-item {
  @apply flex items-center px-3 py-2 rounded-lg cursor-pointer transition-all text-gray-600;
}

.nav-item:hover {
  @apply bg-gray-100;
}

.nav-item.active {
  @apply bg-primary-faint text-primary font-medium;
}

.profile-content {
  @apply flex-1 min-w-0;
}

.content-section {
  @apply bg-white rounded-lg p-6;
}

.section-title {
  @apply text-lg font-medium text-gray-800 mb-6 flex items-center justify-between;
}

.resource-list {
  @apply grid grid-cols-4 gap-4;
}

.article-list {
  @apply grid grid-cols-2 gap-4;
}

.empty-state {
  @apply flex flex-col items-center justify-center py-12 text-gray-400;
}

.history-list {
  @apply space-y-3;
}

.history-item {
  @apply flex items-center gap-4 p-3 rounded-lg cursor-pointer hover:bg-gray-50 transition-colors;
}

.history-cover {
  @apply w-16 h-16 rounded-lg bg-gray-100 flex items-center justify-center overflow-hidden flex-shrink-0;
}

.history-cover img {
  @apply w-full h-full object-cover;
}

.history-info {
  @apply flex-1 min-w-0 flex flex-col justify-between;
}

.history-title {
  @apply text-gray-800 font-medium;
}

.history-time {
  @apply text-sm text-gray-400;
}

.notification-list {
  @apply space-y-2;
}

.notification-item {
  @apply flex items-center gap-4 p-4 rounded-lg cursor-pointer hover:bg-gray-50 transition-colors;
}

.notification-item.unread {
  @apply bg-blue-50;
}

.notification-icon {
  @apply text-2xl text-gray-400;
}

.notification-content {
  @apply flex-1 flex items-center justify-between;
}

.notification-title {
  @apply text-gray-800;
}

.notification-time {
  @apply text-sm text-gray-400;
}
</style>
