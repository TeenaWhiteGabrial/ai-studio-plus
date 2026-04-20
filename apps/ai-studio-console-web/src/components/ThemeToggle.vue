<template>
  <el-button
    class="theme-toggle"
    @click="toggle"
    :title="theme === 'dark' ? '切换到浅色模式' : '切换到深色模式'"
  >
    <el-icon v-if="theme === 'dark'" :size="20">
      <Sunny />
    </el-icon>
    <el-icon v-else :size="20">
      <Moon />
    </el-icon>
  </el-button>
</template>

<script setup lang="ts">
import { useThemeStore } from '@/stores/theme'
import { Sunny, Moon } from '@element-plus/icons-vue'

const themeStore = useThemeStore()

const theme = themeStore.theme

function toggle() {
  themeStore.toggleTheme()
}
</script>

<style scoped>
.theme-toggle {
  height: 36px;
  width: 36px;
  padding: 0;
  border: none;
  background: transparent;
  color: inherit;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.theme-toggle:hover {
  background: var(--ai-bg-hover);
}
</style>
