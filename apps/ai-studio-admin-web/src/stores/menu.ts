import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

interface MenuItem {
  id: number
  parentId: number
  name: string
  path: string
  component: string
  icon: string
  sort: number
  hidden: number
  children?: MenuItem[]
}

function normalizeMenu(menu: MenuItem): MenuItem {
  if (menu.path !== '/dashboard') return menu

  return {
    ...menu,
    name: '数据看板',
    icon: menu.icon || 'DataAnalysis',
    children: [
      {
        id: 100001,
        parentId: menu.id,
        name: '产出数据看板',
        path: '/dashboard/output',
        component: 'dashboard/output',
        icon: 'TrendCharts',
        sort: 1,
        hidden: 0,
      },
      {
        id: 100002,
        parentId: menu.id,
        name: '网站数据看板',
        path: '/dashboard/portal',
        component: 'dashboard/portal',
        icon: 'Monitor',
        sort: 2,
        hidden: 0,
      },
    ],
  }
}

const knowledgeMenu: MenuItem = {
  id: 900001,
  parentId: 0,
  name: '知识库管理',
  path: '/knowledge',
  component: 'knowledge/index',
  icon: 'Files',
  sort: 90,
  hidden: 0,
}

const siteSettingsMenu: MenuItem = {
  id: 900002,
  parentId: 0,
  name: '网站设置',
  path: '/site-settings',
  component: 'site-settings/index',
  icon: 'Setting',
  sort: 91,
  hidden: 0,
}

export const useMenuStore = defineStore('menu', () => {
  const menus = ref<MenuItem[]>([])

  async function fetchMenus() {
    const res = await request.get('/menu/tree') as any
    const remoteMenus = (res.data || []).map(normalizeMenu)
    const additions = [knowledgeMenu, siteSettingsMenu].filter(
      item => !remoteMenus.some((menu: MenuItem) => menu.path === item.path)
    )
    menus.value = [...remoteMenus, ...additions]
  }

  function reset() {
    menus.value = []
  }

  return { menus, fetchMenus, reset }
})
