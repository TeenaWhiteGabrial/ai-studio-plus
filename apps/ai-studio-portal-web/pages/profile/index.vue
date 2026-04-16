<template>
  <div class="profile-page">
    <!-- 用户信息卡片 -->
    <div class="profile-card">
      <div class="avatar-section">
        <el-avatar :size="80" :src="userInfo?.avatar || undefined" style="background: linear-gradient(135deg, #667eea, #764ba2);">
          {{ (userInfo?.realName || userInfo?.username || 'U')[0] }}
        </el-avatar>
        <div class="user-info">
          <h2 class="username">{{ userInfo?.realName || userInfo?.username }}</h2>
          <p class="bio">{{ userInfo?.bio || '这个人很懒，什么都没写' }}</p>
          <div class="user-stats">
            <span><strong>12</strong> 文章</span>
            <span><strong>34</strong> 粉丝</span>
            <span><strong>56</strong> 收藏</span>
          </div>
        </div>
        <el-button size="small" plain style="margin-left:auto">编辑资料</el-button>
      </div>
    </div>

    <!-- 子 Tab 切换 -->
    <div class="profile-tabs">
      <div class="tab-bar">
        <span class="ptab active">我的文章</span>
        <span class="ptab">我的问答</span>
        <span class="ptab">收藏夹</span>
        <span class="ptab">浏览记录</span>
        <span class="ptab">消息通知</span>
      </div>

      <!-- 我的文章 -->
      <div class="tab-content">
        <div v-for="item in myArticles" :key="item.id" class="article-item">
          <div class="article-info">
            <h3 class="article-title">{{ item.title }}</h3>
            <div class="article-meta">
              <span class="date">{{ item.date }}</span>
              <span><i class="el-icon-view"></i> {{ item.viewsCount }}</span>
              <span><i class="el-icon-star-on"></i> {{ item.likesCount }}</span>
              <span><i class="el-icon-chat-round"></i> {{ item.commentsCount }}</span>
            </div>
          </div>
          <div class="article-actions">
            <el-button size="small" plain>编辑</el-button>
            <el-button size="small" type="danger" plain>删除</el-button>
          </div>
        </div>
        <div v-if="myArticles.length === 0" class="empty-state">
          <p>暂无文章，<NuxtLink to="/community/write" style="color:#667eea">去发布</NuxtLink></p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ middleware: 'auth' })
useHead({ title: '个人中心 - AI Studio' })

const { userInfo } = useUser()

const myArticles = ref([
  { id: 1, title: 'AI Studio 入门指南', date: '2026-04-10', viewsCount: 1234, likesCount: 89, commentsCount: 23 },
  { id: 2, title: 'Skill 开发实战', date: '2026-04-08', viewsCount: 856, likesCount: 67, commentsCount: 15 },
])

const myQuestions = ref<any[]>([])
const myFavorites = ref<any[]>([])
const myBrowseHistory = ref<any[]>([])
const myNotifications = ref<any[]>([])
</script>

<style scoped>
.profile-page { display: flex; flex-direction: column; gap: 16px; }
.profile-card { background: #fff; border-radius: 8px; padding: 24px; box-shadow: 0 1px 4px rgba(0,0,0,0.04); }
.avatar-section { display: flex; align-items: center; gap: 20px; }
.user-info { flex: 1; }
.username { font-size: 22px; font-weight: 600; color: #1a1a2e; margin-bottom: 6px; }
.bio { font-size: 14px; color: #909399; margin-bottom: 12px; }
.user-stats { display: flex; gap: 24px; font-size: 14px; color: #606266; }
.user-stats strong { color: #667eea; }
.profile-tabs { background: #fff; border-radius: 8px; box-shadow: 0 1px 4px rgba(0,0,0,0.04); overflow: hidden; }
.tab-bar { display: flex; border-bottom: 1px solid #f0f2f5; padding: 0 20px; }
.ptab { padding: 16px 24px; font-size: 14px; color: #606266; cursor: pointer; border-bottom: 2px solid transparent; transition: all 0.2s; }
.ptab:hover { color: #667eea; }
.ptab.active { color: #667eea; border-bottom-color: #667eea; font-weight: 500; }
.tab-content { padding: 20px; }
.article-item { display: flex; justify-content: space-between; align-items: center; padding: 16px 0; border-bottom: 1px solid #f5f7fa; }
.article-item:last-child { border-bottom: none; }
.article-info { flex: 1; }
.article-title { font-size: 15px; font-weight: 500; color: #1a1a2e; margin-bottom: 6px; cursor: pointer; }
.article-title:hover { color: #667eea; }
.article-meta { display: flex; gap: 20px; font-size: 12px; color: #c0c4cc; }
.article-actions { display: flex; gap: 8px; }
.empty-state { text-align: center; padding: 40px; color: #909399; font-size: 14px; }
</style>
