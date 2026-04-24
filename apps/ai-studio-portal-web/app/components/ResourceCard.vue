<template>
  <article class="resource-card csdn-card csdn-hover-card" @click="handleClick">
    <div class="resource-cover">
      <img v-if="coverUrl" :src="coverUrl" :alt="resourceName">
      <div v-else class="cover-fallback">
        <Icon :name="resourceIcon" size="32" />
      </div>
    </div>

    <div class="resource-body">
      <h3 class="resource-title text-overflow-2">{{ resourceName }}</h3>
      <p class="resource-intro text-overflow-3">{{ introText || '暂无介绍' }}</p>

      <div class="resource-foot">
        <span class="type-tag">{{ typeText }}</span>
        <span class="time">{{ formatTime(timeValue) }}</span>
      </div>
    </div>
  </article>
</template>

<script setup lang="ts">
import type { Resource } from '~~/shared/types/resource'

const props = defineProps<{
  resource: Resource
  type?: 'skill' | 'plugin' | 'tutorial'
}>()

const resourceType = computed(() => (props.type || props.resource.type || 'skill') as 'skill' | 'plugin' | 'tutorial')

const resourceName = computed(() => props.resource.name || props.resource.title || '未命名资源')
const introText = computed(() => props.resource.introduction || props.resource.description || '')
const coverUrl = computed(() => props.resource.photo || props.resource.coverImage || props.resource.icon || '')
const timeValue = computed(() => props.resource.publishTime || props.resource.createTime || props.resource.createdAt || '')

const typeText = computed(() => {
  if (resourceType.value === 'skill') return 'Skill'
  if (resourceType.value === 'plugin') return 'Plugin'
  return 'Tutorial'
})

const resourceIcon = computed(() => {
  if (resourceType.value === 'skill') return 'material-symbols:psychology-alt-outline'
  if (resourceType.value === 'plugin') return 'material-symbols:extension-outline'
  return 'material-symbols:play-lesson-outline'
})

function handleClick() {
  navigateTo(`/resources/${resourceType.value}/${props.resource.id}`)
}

function formatTime(value: string) {
  if (!value) return ''
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return ''
  return date.toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.resource-card {
  overflow: hidden;
  cursor: pointer;
}

.resource-cover {
  width: 100%;
  height: 140px;
  background: #f2f5fb;
}

.resource-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-fallback {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--csdn-primary);
  background: linear-gradient(145deg, #eaf2ff, #f9fbff);
}

.resource-body {
  padding: 12px;
}

.resource-title {
  margin: 0;
  font-size: 15px;
  line-height: 1.5;
}

.resource-intro {
  margin: 8px 0 10px;
  font-size: 13px;
  line-height: 1.65;
  color: var(--csdn-subtext);
  min-height: 64px;
}

.resource-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.type-tag {
  border-radius: 999px;
  background: var(--csdn-primary-soft);
  color: var(--csdn-primary);
  padding: 2px 8px;
  font-size: 12px;
}

.time {
  color: var(--csdn-muted);
  font-size: 12px;
}
</style>
