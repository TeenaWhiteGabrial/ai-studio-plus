import { defineStore } from 'pinia'
import { ref } from 'vue'

export type Theme = 'light' | 'dark'

export const useThemeStore = defineStore('theme', () => {
  // 从 localStorage 读取保存的主题
  const savedTheme = localStorage.getItem('theme') as Theme
  const theme = ref<Theme>(savedTheme || 'light')

  function setTheme(newTheme: Theme) {
    theme.value = newTheme
    localStorage.setItem('theme', newTheme)

    // 更新 HTML 根元素的 class
    if (newTheme === 'dark') {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }

  function toggleTheme() {
    setTheme(theme.value === 'dark' ? 'light' : 'dark')
  }

  // 初始化主题
  function initTheme() {
    setTheme(theme.value)
  }

  return {
    theme,
    setTheme,
    toggleTheme,
    initTheme,
  }
})
