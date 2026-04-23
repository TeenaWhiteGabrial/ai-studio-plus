import { ref, onMounted, watch } from 'vue'
import { skillApi, pluginApi, tutorialApi, mcpApi, authApi } from '@/api'
import { useUserStore } from '@/stores/user'

export function useResourceCenter() {
  const activeTab = ref('skill')
  const viewMode = ref<'card' | 'list'>('list')

  // 当前用户 ID
  const currentUserId = ref<number | null>(null)

  // Skill
  const skillList = ref<any[]>([])
  const skillLoading = ref(false)
  const skillCount = ref(0)
  const skillPagination = ref({ page: 1, size: 12 })

  // Plugin
  const pluginList = ref<any[]>([])
  const pluginLoading = ref(false)
  const pluginCount = ref(0)
  const pluginPagination = ref({ page: 1, size: 12 })

  // Tutorial
  const tutorialList = ref<any[]>([])
  const tutorialLoading = ref(false)
  const tutorialCount = ref(0)
  const tutorialPagination = ref({ page: 1, size: 12 })

  // MCP
  const mcpList = ref<any[]>([])
  const mcpLoading = ref(false)
  const mcpCount = ref(0)
  const mcpPagination = ref({ page: 1, size: 12 })

  const loadSkills = async () => {
    skillLoading.value = true
    try {
      const res = await skillApi.list({ page: skillPagination.value.page, size: skillPagination.value.size })
      if (res.code === 200) {
        skillList.value = res.data?.records || []
        skillCount.value = res.data?.total || 0
      }
    } catch (error) {
      console.error('加载 Skills 失败:', error)
    } finally {
      skillLoading.value = false
    }
  }

  const loadPlugins = async () => {
    pluginLoading.value = true
    try {
      const res = await pluginApi.list({ page: pluginPagination.value.page, size: pluginPagination.value.size })
      if (res.code === 200) {
        pluginList.value = res.data?.records || []
        pluginCount.value = res.data?.total || 0
      }
    } catch (error) {
      console.error('加载 Plugins 失败:', error)
    } finally {
      pluginLoading.value = false
    }
  }

  const loadTutorials = async () => {
    tutorialLoading.value = true
    try {
      const res = await tutorialApi.list({ page: tutorialPagination.value.page, size: tutorialPagination.value.size })
      if (res.code === 200) {
        tutorialList.value = res.data?.records || []
        tutorialCount.value = res.data?.total || 0
      }
    } catch (error) {
      console.error('加载教程失败:', error)
    } finally {
      tutorialLoading.value = false
    }
  }

  const loadMcpServers = async () => {
    mcpLoading.value = true
    try {
      const res = await mcpApi.list({ page: mcpPagination.value.page, size: mcpPagination.value.size })
      if (res.code === 200) {
        mcpList.value = res.data?.records || []
        mcpCount.value = res.data?.total || 0
      }
    } catch (error) {
      console.error('加载 MCP 失败:', error)
    } finally {
      mcpLoading.value = false
    }
  }

  const isCurrentUserResource = (resource: any) => {
    return resource.creator_id === currentUserId.value
  }

  watch(activeTab, (tab) => {
    switch (tab) {
      case 'skill': loadSkills(); break
      case 'plugin': loadPlugins(); break
      case 'tutorial': loadTutorials(); break
      case 'mcp': loadMcpServers(); break
    }
  })

  onMounted(async () => {
    const userStore = useUserStore()
    if (userStore.userInfo && userStore.userInfo.user_id) {
      currentUserId.value = userStore.userInfo.user_id
    } else {
      try {
        const res = await authApi.getUserInfo()
        if (res.code === 200 && res.data) {
          currentUserId.value = res.data.id
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
      }
    }
    loadSkills()
  })

  return {
    activeTab,
    viewMode,
    currentUserId,
    isCurrentUserResource,
    // Skill
    skillList, skillLoading, skillCount, skillPagination, loadSkills,
    // Plugin
    pluginList, pluginLoading, pluginCount, pluginPagination, loadPlugins,
    // Tutorial
    tutorialList, tutorialLoading, tutorialCount, tutorialPagination, loadTutorials,
    // MCP
    mcpList, mcpLoading, mcpCount, mcpPagination, loadMcpServers,
  }
}

// 共享工具函数
export function useResourceUtils() {
  const getStatusType = (status: number) => {
    switch (status) {
      case 0: return 'info'
      case 1: return 'success'
      case 2: return 'danger'
      default: return 'info'
    }
  }

  const getStatusText = (status: number) => {
    switch (status) {
      case 0: return '待审核'
      case 1: return '已通过'
      case 2: return '已拒绝'
      default: return '未知'
    }
  }

  const formatDate = (dateStr: string) => {
    if (!dateStr) return '-'
    return new Date(dateStr).toLocaleString('zh-CN', {
      year: 'numeric', month: '2-digit', day: '2-digit',
      hour: '2-digit', minute: '2-digit',
    })
  }

  const generateNextVersion = (currentVersion: string, bumpType: 'PATCH' | 'MINOR' | 'MAJOR' = 'PATCH') => {
    const parts = currentVersion.split('.')
    let major = parseInt(parts[0]) || 0
    let minor = parseInt(parts[1]) || 0
    let patch = parseInt(parts[2]) || 0
    switch (bumpType) {
      case 'MAJOR':
        major++
        minor = 0
        patch = 0
        break
      case 'MINOR':
        minor++
        patch = 0
        break
      case 'PATCH':
      default:
        patch++
        break
    }
    return `${major}.${minor}.${patch}`
  }

  return { getStatusType, getStatusText, formatDate, generateNextVersion }
}
