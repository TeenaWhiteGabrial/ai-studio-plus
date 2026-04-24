import type { Article, ArticleFormData, ArticleListQuery, ArticleListResponse } from '~~/shared/types/article'

interface ConsoleArticlePayload extends ArticleFormData {
  publishType?: 0 | 1 | 2
  scheduledPublishTime?: string
}

export function useArticle() {
  const config = useRuntimeConfig()

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
        baseURL: config.public.apiBase
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
      baseURL: config.public.apiBase
    })
  }

  async function likeArticle(id: string): Promise<void> {
    await $fetch(`/portal/article/${id}/like`, {
      method: 'POST',
      baseURL: config.public.apiBase
    })
  }

  async function isArticleLiked(id: string): Promise<boolean> {
    const res = await $fetch<boolean>(`/portal/article/${id}/is-liked`, {
      method: 'GET',
      baseURL: config.public.apiBase
    })
    return !!res
  }

  async function createArticle(data: ConsoleArticlePayload): Promise<number | undefined> {
    const res = await $fetch<number>('/console/article', {
      method: 'POST',
      body: data,
      baseURL: config.public.apiBase
    })
    return res
  }

  async function updateArticle(id: string, data: ConsoleArticlePayload): Promise<void> {
    await $fetch(`/console/article/${id}`, {
      method: 'POST',
      body: data,
      baseURL: config.public.apiBase
    })
  }

  return {
    getArticleList,
    getArticleDetail,
    likeArticle,
    isArticleLiked,
    createArticle,
    updateArticle
  }
}
