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

export const useMenuStore = defineStore('menu', () => {
  const menus = ref<MenuItem[]>([])

  async function fetchMenus() {
    const res = await request.get('/menu/tree') as any
    menus.value = (res.data || []).map(normalizeMenu)
  }

  function reset() {
    menus.value = []
  }

  return { menus, fetchMenus, reset }
})
