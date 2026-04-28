import type { Article, ArticleListQuery, ArticleListResponse } from '~~/shared/types/article'

export function useArticle() {
  const config = useRuntimeConfig()
  const authStore = useAuthStore()

  function getAuthHeaders() {
    if (!authStore.token) {
      return undefined
    }
    const authorization = config.public.tokenType ? `${config.public.tokenType} ${authStore.token}` : authStore.token
    return { Authorization: authorization }
  }

  const isUnauthorized = (error: unknown) => {
    const err = error as { status?: number; statusCode?: number; response?: { status?: number } }
    return err?.status === 401 || err?.statusCode === 401 || err?.response?.status === 401
  }

  async function getArticleList(query: ArticleListQuery): Promise<ArticleListResponse> {
    try {
      const res = await $fetch<ArticleListResponse>('/portal/article/list', {
        method: 'GET',
        params: {
          page: query.page || 1,
          size: query.pageSize || 10,
          keyword: query.keyword,
          tagId: query.tagId,
          sort: query.sort || 'latest'
        },
        baseURL: config.public.apiBase,
        headers: getAuthHeaders()
      })
      return {
        total: res?.total || 0,
        records: res?.records || []
      }
    } catch (error) {
      if (isUnauthorized(error)) {
        return { total: 0, records: [] }
      }
      throw error
    }
  }

  async function getArticleDetail(id: string): Promise<Article> {
    return await $fetch<Article>(`/portal/article/${id}`, {
      method: 'GET',
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
  }

  async function likeArticle(id: string): Promise<void> {
    await $fetch(`/portal/article/${id}/like`, {
      method: 'POST',
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
  }

  async function isArticleLiked(id: string): Promise<boolean> {
    const res = await $fetch<boolean>(`/portal/article/${id}/is-liked`, {
      method: 'GET',
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
    return !!res
  }

  return {
    getArticleList,
    getArticleDetail,
    likeArticle,
    isArticleLiked
  }
}
