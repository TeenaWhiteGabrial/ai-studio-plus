import { defineEventHandler } from 'h3'

// Server-side API handler for portal announcements

const API_BASE = process.env.NUXT_PUBLIC_API_BASE || '/ai-studio/v1'

export default defineEventHandler(async () => {
  const apiPath = `${API_BASE}/portal/public/announcement`

  try {
    const response = await $fetch(apiPath, {
      method: 'GET',
    })
    return response
  } catch (error: any) {
    console.error('[Portal SSR] Failed to fetch announcements:', error.message)
    return {
      code: 500,
      message: 'Failed to fetch announcements',
      data: [],
    }
  }
})
