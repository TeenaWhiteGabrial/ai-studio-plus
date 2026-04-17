<template>
  <div class="left-sidebar-content">
    <!-- 资源分类导航 -->
    <div class="sidebar-card">
      <div class="sidebar-title">
        <Icon name="material-symbols:category" class="mr-2" />
        资源分类
      </div>
      <div class="category-list">
        <div
          v-for="item in resourceCategories"
          :key="item.path"
          class="category-item"
          :class="{ active: currentResourceType === item.type }"
          @click="handleCategoryClick(item)"
        >
          <Icon :name="item.icon" class="mr-2" />
          <span>{{ item.name }}</span>
        </div>
      </div>
    </div>

    <!-- 热门标签 -->
    <div class="sidebar-card mt-4">
      <div class="sidebar-title">
        <Icon name="material-symbols:tag" class="mr-2" />
        热门标签
      </div>
      <div class="tag-list">
        <el-tag
          v-for="tag in hotTags"
          :key="tag.id"
          class="tag-item"
          :style="{ '--tag-color': tag.color || '#3b82f6' }"
          @click="handleTagClick(tag)"
        >
          {{ tag.name }}
        </el-tag>
        <div v-if="loadingTags" class="text-center text-gray-400 py-2">
          加载中...
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Tag } from '~~/shared/types/community'

const route = useRoute()
const { getTagList } = useCommunity()

const loadingTags = ref(false)
const hotTags = ref<(Tag & { color?: string })[]>([])

// 资源分类
const resourceCategories = [
  { name: '技能', type: 'skill', path: '/resources', icon: 'material-symbols:smart-toy' },
  { name: '插件', type: 'plugin', path: '/resources', icon: 'material-symbols:extension' }
]

const currentResourceType = computed(() => {
  const type = route.params.type
  return type as string || 'skill'
})

// 加载热门标签
async function loadHotTags() {
  loadingTags.value = true
  try {
    const res = await getTagList()
    // 取前20个标签
    hotTags.value = (res || []).slice(0, 20).map((tag: Tag, index: number) => ({
      ...tag,
      color: tagColors[index % tagColors.length]
    }))
  }
  catch (err) {
    console.error('加载标签失败:', err)
  }
  finally {
    loadingTags.value = false
  }
}

const tagColors = [
  '#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6',
  '#06b6d4', '#84cc16', '#f97316', '#ec4899', '#6366f1'
]

function handleCategoryClick(item: typeof resourceCategories[0]) {
  navigateTo(`${item.path}?type=${item.type}`)
}

function handleTagClick(tag: Tag & { color?: string }) {
  navigateTo(`/community?tagId=${tag.id}`)
}

onMounted(() => {
  loadHotTags()
})
</script>

<style scoped>
.sidebar-card {
  @apply bg-white rounded-lg p-4 shadow-sm;
}

.sidebar-title {
  @apply text-base font-medium text-gray-800 mb-4 flex items-center;
}

.category-list {
  @apply space-y-1;
}

.category-item {
  @apply flex items-center px-3 py-2 rounded-lg cursor-pointer transition-all duration-200;
  @apply hover:bg-gray-100 hover:text-primary;
}

.category-item.active {
  @apply bg-primary-faint text-primary font-medium;
}

.tag-list {
  @apply flex flex-wrap gap-2;
}

.tag-item {
  @apply cursor-pointer transition-all duration-200;
  background-color: color-mix(in srgb, var(--tag-color) 10%, transparent);
  border-color: color-mix(in srgb, var(--tag-color) 30%, transparent);
  color: var(--tag-color);
}

.tag-item:hover {
  background-color: color-mix(in srgb, var(--tag-color) 20%, transparent);
  border-color: var(--tag-color);
}
</style>
