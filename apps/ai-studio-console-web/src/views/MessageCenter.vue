<template>
  <div class="message-center">
    <el-card>
      <template #header>
        <div class="page-header">
          <span>消息中心</span>
          <el-button type="primary" plain @click="markAllNotificationsRead">全部通知已读</el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="通知" name="notifications">
          <el-table :data="notifications" v-loading="notificationLoading" border>
            <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
            <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />
            <el-table-column prop="type" label="类型" width="170">
              <template #default="{ row }">{{ ruleLabel(row.type) }}</template>
            </el-table-column>
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="(row.is_read ?? row.isRead) ? 'info' : 'warning'">
                  {{ (row.is_read ?? row.isRead) ? '已读' : '未读' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="created_at" label="时间" width="180" />
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button v-if="!(row.is_read ?? row.isRead)" size="small" @click="markNotificationRead(row)">已读</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination v-model:current-page="notificationQuery.page" v-model:page-size="notificationQuery.size" :total="notificationTotal" layout="total, prev, pager, next" class="mt-4" @change="loadNotifications" />
        </el-tab-pane>

        <el-tab-pane label="公告" name="announcements">
          <div class="announcement-toolbar">
            <div>
              <div class="announcement-toolbar-title">公告</div>
              <div class="announcement-toolbar-subtitle">共 {{ announcements.length }} 条，{{ unreadAnnouncementCount }} 条未读</div>
            </div>
            <el-button type="primary" plain @click="loadAnnouncements">刷新</el-button>
          </div>
          <div class="announcement-list">
            <div
              v-for="item in announcements"
              :key="item.id"
              class="announcement-item"
              :class="{ unread: !item.read, pinned: item.pinned }"
              @click="openAnnouncementDetail(item)"
            >
              <div class="announcement-main">
                <div class="announcement-header">
                  <div class="announcement-title-wrap">
                    <span v-if="!item.read" class="unread-dot" />
                    <el-tag v-if="item.pinned" size="small" type="danger" effect="light">置顶</el-tag>
                    <span class="announcement-title">{{ item.title }}</span>
                  </div>
                  <el-tag :type="item.read ? 'info' : 'warning'" effect="plain" round>
                    {{ item.read ? '已读' : '未读' }}
                  </el-tag>
                </div>
                <div class="announcement-content">{{ item.content }}</div>
              </div>
              <div class="announcement-footer">
                <span class="announcement-time">{{ item.publishedAt || item.published_at || '未发布' }}</span>
                <div class="announcement-actions">
                  <el-button size="small" type="primary" @click.stop="openAnnouncementDetail(item)">查看详情</el-button>
                  <el-button v-if="!item.read" link type="primary" @click.stop="markAnnouncementRead(item)">标记已读</el-button>
                </div>
              </div>
            </div>
            <el-empty v-if="!announcements.length" description="暂无公告" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog v-model="announcementDialogVisible" width="720px" class="announcement-rich-dialog">
      <template #header>
        <div class="announcement-dialog-hero">
          <div class="announcement-dialog-kicker">
            <span>公告</span>
            <el-tag v-if="announcementDetail?.pinned" size="small" type="danger" effect="dark">置顶</el-tag>
          </div>
          <div class="announcement-dialog-title">{{ announcementDetail?.title || '公告详情' }}</div>
          <div class="announcement-dialog-meta">{{ announcementDetail?.publishedAt || announcementDetail?.published_at || '未发布' }}</div>
        </div>
      </template>
      <div v-loading="announcementDetailLoading" class="announcement-dialog-body">
        <div class="announcement-dialog-content">{{ announcementDetail?.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { messageApi } from '@/api/message'

const ruleTypes = [
  { value: 'DAILY_TASK_MISSING', label: '每日任务未录入' },
  { value: 'TASK_ASSIGNED', label: '任务登记提醒' },
  { value: 'ARTICLE_LIKED', label: '文章点赞' },
  { value: 'ARTICLE_COMMENTED', label: '文章评论' },
  { value: 'ARTICLE_TAKEN_DOWN', label: '文章下架' },
  { value: 'RESOURCE_APPROVED', label: '资源审核通过' },
  { value: 'RESOURCE_TAKEN_DOWN', label: '资源未通过/下架' },
]

const activeTab = ref('notifications')
const notificationLoading = ref(false)
const notifications = ref<any[]>([])
const notificationTotal = ref(0)
const notificationQuery = reactive({ page: 1, size: 10 })
const announcements = ref<any[]>([])
const announcementDialogVisible = ref(false)
const announcementDetailLoading = ref(false)
const announcementDetail = ref<any | null>(null)
const unreadAnnouncementCount = computed(() => announcements.value.filter(item => !item.read).length)

function ruleLabel(type: string) {
  return ruleTypes.find(item => item.value === type)?.label || type
}

async function loadNotifications() {
  notificationLoading.value = true
  try {
    const res: any = await messageApi.notifications(notificationQuery)
    notifications.value = res.data?.records || []
    notificationTotal.value = res.data?.total || 0
  } finally {
    notificationLoading.value = false
  }
}

async function loadAnnouncements() {
  const res: any = await messageApi.announcements()
  announcements.value = res.data || []
}

async function markNotificationRead(row: any) {
  await messageApi.markNotificationRead(row.id)
  ElMessage.success('已标记为已读')
  loadNotifications()
}

async function markAllNotificationsRead() {
  await messageApi.markAllNotificationsRead()
  ElMessage.success('通知已全部标记为已读')
  loadNotifications()
}

async function markAnnouncementRead(row: any) {
  await messageApi.markAnnouncementRead(row.id)
  row.read = true
}

async function openAnnouncementDetail(row: any) {
  announcementDialogVisible.value = true
  announcementDetailLoading.value = true
  try {
    const res: any = await messageApi.announcementDetail(row.id)
    announcementDetail.value = res.data
    if (!row.read) {
      await messageApi.markAnnouncementRead(row.id)
      row.read = true
      if (announcementDetail.value) announcementDetail.value.read = true
    }
  } finally {
    announcementDetailLoading.value = false
  }
}

onMounted(async () => {
  await Promise.all([loadNotifications(), loadAnnouncements()])
})
</script>

<style scoped>
.message-center {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-header,
.announcement-header,
.announcement-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.announcement-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.announcement-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 16px 18px;
  margin-bottom: 14px;
  background: hsl(var(--secondary));
  border: 1px solid hsl(var(--border));
  border-radius: 8px;
}

.announcement-toolbar-title {
  color: hsl(var(--foreground));
  font-size: 16px;
  font-weight: 700;
}

.announcement-toolbar-subtitle {
  margin-top: 4px;
  color: hsl(var(--muted-foreground));
  font-size: 13px;
}

.announcement-item {
  position: relative;
  padding: 16px 18px 14px;
  overflow: hidden;
  background: hsl(var(--card));
  border: 1px solid hsl(var(--border));
  border-radius: 8px;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
}

.announcement-item::before {
  position: absolute;
  inset: 0 auto 0 0;
  width: 3px;
  background: transparent;
  content: "";
}

.announcement-item.unread::before {
  background: hsl(var(--primary));
}

.announcement-item.pinned {
  background: linear-gradient(90deg, hsl(var(--primary) / 0.08), hsl(var(--card)) 42%);
  border-color: hsl(var(--primary) / 0.34);
}

.announcement-item.unread {
  background: hsl(var(--primary) / 0.045);
}

.announcement-item:hover {
  border-color: hsl(var(--primary) / 0.38);
  box-shadow: 0 10px 24px hsl(var(--foreground) / 0.08);
  transform: translateY(-1px);
}

.announcement-main {
  min-width: 0;
}

.announcement-title-wrap {
  flex: 1 1 auto;
  display: flex;
  align-items: center;
  min-width: 0;
  gap: 8px;
}

.announcement-title-wrap :deep(.el-tag),
.announcement-header > :deep(.el-tag) {
  flex: 0 0 auto;
}

.unread-dot {
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: hsl(var(--primary));
  flex: 0 0 auto;
}

.announcement-title {
  flex: 1 1 auto;
  min-width: 0;
  color: hsl(var(--foreground));
  font-size: 15px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.announcement-content {
  display: -webkit-box;
  margin-top: 10px;
  color: hsl(var(--muted-foreground));
  font-size: 14px;
  line-height: 1.65;
  overflow: hidden;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  white-space: pre-line;
}

.announcement-footer {
  padding-top: 12px;
  margin-top: 12px;
  border-top: 1px solid hsl(var(--border) / 0.7);
  color: hsl(var(--muted-foreground));
  font-size: 13px;
}

.announcement-time {
  white-space: nowrap;
}

.announcement-actions,
.dialog-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.dialog-header {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
  padding-right: 28px;
}

.dialog-title-row {
  color: hsl(var(--foreground));
  font-size: 18px;
  font-weight: 700;
  line-height: 1.4;
}

.dialog-meta {
  color: hsl(var(--muted-foreground));
  font-size: 13px;
}

.dialog-body {
  padding-top: 4px;
}

.dialog-content {
  min-height: 160px;
  padding: 18px 2px 4px;
  border-top: 1px solid hsl(var(--border));
  color: hsl(var(--foreground));
  font-size: 15px;
  line-height: 1.9;
  white-space: pre-wrap;
}

:deep(.announcement-dialog .el-dialog) {
  border-radius: 8px;
}

@media (max-width: 720px) {
  .announcement-footer {
    align-items: flex-start;
    flex-direction: column;
  }

  .announcement-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
