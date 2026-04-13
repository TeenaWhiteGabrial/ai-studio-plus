import { defineEventHandler, getQuery } from 'h3'

// Server-side API handler for portal public resources
// This runs on the Nuxt server side and fetches data from the backend

const API_BASE = process.env.NUXT_PUBLIC_API_BASE || '/ai-studio/v1'

export default defineEventHandler(async (event) => {
  const query = getQuery(event)
  const type = event.context.params?.type || 'skill'

  // Map portal type to backend API path
  const apiPath = `${API_BASE}/portal/public/${type}`

  try {
    const response = await $fetch(apiPath, {
      method: 'GET',
      query: {
        page: query.page || 1,
        size: query.size || 20,
        category: query.category || '',
        tag: query.tag || '',
        sort: query.sort || 'latest',
        keyword: query.keyword || '',
      },
    })
    return response
  } catch (error: any) {
    console.error(`[Portal SSR] Failed to fetch ${type} list:`, error.message)
    return {
      code: 500,
      message: 'Failed to fetch resource list',
      data: [],
    }
  }
})
