<template>
  <div class="left-wrap">
    <section class="csdn-card panel quick-nav">
      <h3 class="panel-title">频道导航</h3>
      <button
        v-for="item in channelItems"
        :key="item.key"
        class="nav-item"
        :class="{ active: item.active }"
        @click="navigateTo(item.to)"
      >
        <Icon :name="item.icon" size="18" />
        <span>{{ item.label }}</span>
      </button>
    </section>

    <section class="csdn-card panel resource-nav">
      <h3 class="panel-title">资源分类</h3>
      <button
        v-for="item in resourceItems"
        :key="item.type"
        class="nav-item"
        :class="{ active: currentType === item.type }"
        @click="navigateTo(`/resources?type=${item.type}`)"
      >
        <Icon :name="item.icon" size="18" />
        <span>{{ item.label }}</span>
      </button>
    </section>

    <section class="csdn-card panel">
      <h3 class="panel-title">热门标签</h3>
      <div class="tag-list">
        <button
          v-for="tag in displayTags"
          :key="tag.id"
          class="tag-btn"
          @click="navigateTo(`/community?tagId=${tag.id}`)"
        >
          # {{ tag.name }}
        </button>
      </div>
      <div v-if="loadingTags" class="csdn-empty">标签加载中...</div>
    </section>

    <section class="csdn-card panel creator">
      <h3 class="panel-title">创作中心</h3>
      <p class="creator-desc">发布问题、参与讨论，积累你的技术影响力。</p>
      <el-button type="primary" class="w-full" @click="goAsk">发布问题</el-button>
    </section>
  </div>
</template>

<script setup lang="ts">
import type { Tag } from '~~/shared/types/community'

const route = useRoute()
const authStore = useAuthStore()
const { getTagList } = useCommunity()

const loadingTags = ref(false)
const displayTags = ref<Tag[]>([])

const channelItems = computed(() => [
  {
    key: 'home',
    label: '推荐首页',
    icon: 'material-symbols:home-outline',
    to: '/',
    active: route.path === '/'
  },
  {
    key: 'community',
    label: '技术社区',
    icon: 'material-symbols:forum-outline',
    to: '/community',
    active: route.path.startsWith('/community')
  },
  {
    key: 'resources',
    label: '资源中心',
    icon: 'material-symbols:folder-managed-outline',
    to: '/resources',
    active: route.path.startsWith('/resources')
  },
  {
    key: 'profile',
    label: '我的主页',
    icon: 'material-symbols:person-outline',
    to: '/profile',
    active: route.path.startsWith('/profile')
  }
])

const resourceItems = [
  { type: 'skill', label: 'Skills', icon: 'material-symbols:psychology-alt-outline' },
  { type: 'plugin', label: 'Plugins', icon: 'material-symbols:extension-outline' },
  { type: 'tutorial', label: 'Tutorials', icon: 'material-symbols:play-lesson-outline' }
]

const currentType = computed(() => {
  if (!route.path.startsWith('/resources')) {
    return ''
  }
  return String(route.query.type || 'skill')
})

async function loadTags() {
  loadingTags.value = true
  try {
    const tags = await getTagList()
    displayTags.value = (tags || []).slice(0, 18)
  }
  finally {
    loadingTags.value = false
  }
}

function goAsk() {
  if (!authStore.token) {
    goLoginPage()
    return
  }
  navigateTo('/community/ask')
}

onMounted(loadTags)
</script>

<style scoped>
.left-wrap {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.panel {
  padding: 14px;
}

.panel-title {
  margin: 0 0 10px;
  font-size: 15px;
  font-weight: 700;
  color: var(--csdn-text);
}

.nav-item {
  width: 100%;
  height: 38px;
  border: 0;
  border-radius: 8px;
  background: transparent;
  color: var(--csdn-subtext);
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 10px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.nav-item:hover {
  background: var(--csdn-hover);
  color: var(--csdn-primary);
}

.nav-item.active {
  background: var(--csdn-primary-soft);
  color: var(--csdn-primary);
  font-weight: 600;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-btn {
  border: 1px solid var(--csdn-line);
  border-radius: 999px;
  background: #fff;
  color: var(--csdn-subtext);
  font-size: 12px;
  padding: 4px 10px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tag-btn:hover {
  border-color: #bdd2ff;
  color: var(--csdn-primary);
  background: #f7fbff;
}

.creator-desc {
  margin: 0 0 10px;
  color: var(--csdn-muted);
  line-height: 1.6;
  font-size: 13px;
}
</style>
