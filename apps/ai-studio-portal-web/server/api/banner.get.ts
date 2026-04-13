import { defineEventHandler } from 'h3'

// Server-side API handler for portal banners

const API_BASE = process.env.NUXT_PUBLIC_API_BASE || '/ai-studio/v1'

export default defineEventHandler(async () => {
  const apiPath = `${API_BASE}/portal/public/banner`

  try {
    const response = await $fetch(apiPath, {
      method: 'GET',
    })
    return response
  } catch (error: any) {
    console.error('[Portal SSR] Failed to fetch banners:', error.message)
    return {
      code: 500,
      message: 'Failed to fetch banners',
      data: [],
    }
  }
})
