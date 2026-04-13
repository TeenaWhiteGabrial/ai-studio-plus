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
    menus.value = res.data
  }

  function reset() {
    menus.value = []
  }

  return { menus, fetchMenus, reset }
})
