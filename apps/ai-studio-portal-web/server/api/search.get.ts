import { defineEventHandler, getQuery } from 'h3'

// Server-side API handler for portal search

const API_BASE = process.env.NUXT_PUBLIC_API_BASE || '/ai-studio/v1'

export default defineEventHandler(async (event) => {
  const query = getQuery(event)

  const apiPath = `${API_BASE}/portal/public/search`

  try {
    const response = await $fetch(apiPath, {
      method: 'GET',
      query: {
        q: query.q || '',
        types: query.types || '',
        sort: query.sort || 'relevance',
        page: query.page || 1,
        size: query.size || 20,
      },
    })
    return response
  } catch (error: any) {
    console.error('[Portal SSR] Failed to search:', error.message)
    return {
      code: 500,
      message: 'Search failed',
      data: {
        list: [],
        total: 0,
      },
    }
  }
})
