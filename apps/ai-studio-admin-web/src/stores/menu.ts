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

export const useMenuStore = defineStore('menu', () => {
  const menus = ref<MenuItem[]>([])

  async function fetchMenus() {
    const res = await request.get('/menu/tree') as any
    menus.value = (res.data || []).map((menu: MenuItem) => ({
      ...menu,
      name: menu.path === '/dashboard' ? '数据看板' : menu.name,
    }))
  }

  function reset() {
    menus.value = []
  }

  return { menus, fetchMenus, reset }
})
