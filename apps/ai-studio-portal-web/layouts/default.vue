<template>
  <div class="portal-layout">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-inner">
        <div class="logo">
          <NuxtLink to="/">AI Studio</NuxtLink>
        </div>
        <nav class="nav">
          <NuxtLink to="/" class="nav-item">首页</NuxtLink>
          <NuxtLink to="/resources" class="nav-item">资源中心</NuxtLink>
          <NuxtLink to="/community" class="nav-item">技术社区</NuxtLink>
          <NuxtLink to="/profile" class="nav-item">个人中心</NuxtLink>
        </nav>
        <div class="header-actions">
          <template v-if="isLoggedIn()">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-avatar :size="32" :src="userInfo?.avatar || undefined">
                  {{ (userInfo?.realName || userInfo?.username || 'U')[0] }}
                </el-avatar>
                <span class="username">{{ userInfo?.realName || userInfo?.username }}</span>
                <i class="el-icon-arrow-down"></i>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="console">控制台</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <NuxtLink to="/login">
              <el-button type="primary" plain size="small">登录</el-button>
            </NuxtLink>
          </template>
        </div>
      </div>
    </header>

    <!-- 三栏主体区域 -->
    <div class="main-wrapper">
      <!-- 左侧边栏 -->
      <aside class="sidebar-left">
        <div class="sidebar-section">
          <h3 class="sidebar-title">资源分类</h3>
          <ul class="category-list">
            <li><NuxtLink to="/resources/skill" class="category-item">
              <span class="cat-icon">📦</span><span>Skill</span>
            </NuxtLink></li>
            <li><NuxtLink to="/resources/plugin" class="category-item">
              <span class="cat-icon">🧩</span><span>Plugin</span>
            </NuxtLink></li>
          </ul>
        </div>
        <div class="sidebar-section">
          <h3 class="sidebar-title">热门标签</h3>
          <div class="tag-cloud">
            <span v-for="tag in hotTags" :key="tag.id" class="tag-item">{{ tag.name }}</span>
          </div>
        </div>
      </aside>

      <!-- 主内容区 -->
      <main class="main-content">
        <slot />
      </main>

      <!-- 右侧边栏 -->
      <aside class="sidebar-right">
        <div class="sidebar-card">
          <h3 class="card-title">热门文章</h3>
          <ul class="rank-list">
            <li v-for="(item, i) in hotArticles" :key="i" class="rank-item">
              <span class="rank-num" :class="{ top: i < 3 }">{{ i + 1 }}</span>
              <span class="rank-title">{{ item.title }}</span>
            </li>
          </ul>
        </div>
        <div class="sidebar-card">
          <h3 class="card-title">热门资源</h3>
          <ul class="rank-list">
            <li v-for="(item, i) in hotResources" :key="i" class="rank-item">
              <span class="rank-num" :class="{ top: i < 3 }">{{ i + 1 }}</span>
              <span class="rank-title">{{ item.name }}</span>
            </li>
          </ul>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
const { userInfo, isLoggedIn, logout } = useUser()
const router = useRouter()

const hotTags = ref<Array<{id: number; name: string}>>([])
const hotArticles = ref<Array<{id: number; title: string}>>([])
const hotResources = ref<Array<{id: number; name: string}>>([])

const handleCommand = (command: string) => {
  if (command === 'logout') {
    logout()
    router.push('/')
  } else if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'console') {
    router.push('/console/dashboard')
  }
}
</script>

<style scoped>
.portal-layout { min-height: 100vh; display: flex; flex-direction: column; background: #f0f2f5; }

/* 顶部导航 */
.header { background: #fff; box-shadow: 0 1px 4px rgba(0,0,0,0.08); position: sticky; top: 0; z-index: 100; }
.header-inner { max-width: 1400px; margin: 0 auto; padding: 0 20px; height: 60px; display: flex; align-items: center; gap: 40px; }
.logo a { font-size: 22px; font-weight: 700; color: #667eea; text-decoration: none; letter-spacing: 1px; }
.nav { display: flex; gap: 8px; flex: 1; }
.nav-item { padding: 8px 16px; border-radius: 6px; color: #4a4a68; text-decoration: none; font-size: 15px; font-weight: 500; transition: all 0.2s; }
.nav-item:hover, .nav-item.router-link-active { background: #f0f2f5; color: #667eea; }
.header-actions { display: flex; align-items: center; gap: 12px; }
.user-info { display: flex; align-items: center; gap: 8px; cursor: pointer; padding: 4px 8px; border-radius: 6px; transition: background 0.2s; }
.user-info:hover { background: #f0f2f5; }
.username { font-size: 14px; color: #4a4a68; }

/* 三栏布局 */
.main-wrapper { max-width: 1400px; margin: 0 auto; padding: 20px; display: flex; gap: 20px; width: 100%; flex: 1; }

/* 左侧边栏 */
.sidebar-left { width: 240px; flex-shrink: 0; display: flex; flex-direction: column; gap: 16px; }
.sidebar-section { background: #fff; border-radius: 8px; padding: 20px; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
.sidebar-title { font-size: 16px; font-weight: 600; color: #1a1a2e; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 2px solid #f0f2f5; }
.category-list { list-style: none; padding: 0; margin: 0; }
.category-item { display: flex; align-items: center; gap: 10px; padding: 10px 12px; border-radius: 6px; color: #4a4a68; text-decoration: none; font-size: 14px; transition: all 0.2s; }
.category-item:hover, .category-item.router-link-active { background: linear-gradient(135deg, rgba(102,126,234,0.1), rgba(118,75,162,0.1)); color: #667eea; }
.cat-icon { font-size: 18px; }
.tag-cloud { display: flex; flex-wrap: wrap; gap: 8px; }
.tag-item { padding: 4px 12px; background: #f0f2f5; border-radius: 4px; font-size: 12px; color: #606266; cursor: pointer; transition: all 0.2s; }
.tag-item:hover { background: linear-gradient(135deg, #667eea, #764ba2); color: #fff; }

/* 主内容 */
.main-content { flex: 1; min-width: 0; }

/* 右侧边栏 */
.sidebar-right { width: 300px; flex-shrink: 0; display: flex; flex-direction: column; gap: 16px; }
.sidebar-card { background: #fff; border-radius: 8px; padding: 20px; box-shadow: 0 1px 4px rgba(0,0,0,0.06); }
.card-title { font-size: 16px; font-weight: 600; color: #1a1a2e; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 2px solid #f0f2f5; }
.rank-list { list-style: none; padding: 0; margin: 0; }
.rank-item { display: flex; align-items: center; gap: 12px; padding: 10px 0; border-bottom: 1px solid #f5f7fa; }
.rank-item:last-child { border-bottom: none; }
.rank-num { width: 20px; height: 20px; border-radius: 4px; background: #f0f2f5; color: #909399; font-size: 12px; font-weight: 600; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.rank-num.top { background: linear-gradient(135deg, #667eea, #764ba2); color: #fff; }
.rank-title { font-size: 13px; color: #4a4a68; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; cursor: pointer; }
.rank-title:hover { color: #667eea; }

/* 响应式 */
@media (max-width: 1200px) { .sidebar-right { display: none; } }
@media (max-width: 900px) { .sidebar-left { display: none; } }
@media (max-width: 768px) { .nav { gap: 4px; } .nav-item { padding: 8px 10px; font-size: 14px; } }
</style>
