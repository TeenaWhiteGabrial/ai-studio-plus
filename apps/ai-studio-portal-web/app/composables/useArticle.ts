import type { Article, ArticleListQuery, ArticleListResponse, ArticleFormData } from '~~/shared/types/article'
import type { ApiResponse } from '~~/shared/types/response'

/**
 * 文章相关 API
 */
export function useArticle() {
  /**
   * 获取文章列表
   * 后端直接返回 PageResult{ total, records }
   */
  async function getArticleList(query: ArticleListQuery): Promise<ArticleListResponse> {
    const res = await $fetch<ArticleListResponse>('/portal/article/list', {
      method: 'GET',
      params: query,
      baseURL: useRuntimeConfig().public.apiBase
    })
    return res
  }

  /**
   * 获取文章详情
   */
  async function getArticleDetail(id: string): Promise<Article> {
    const res = await $fetch<Article>(`/portal/article/${id}`, {
      method: 'GET',
      baseURL: useRuntimeConfig().public.apiBase
    })
    return res
  }

  /**
   * 点赞/取消点赞文章
   */
  async function likeArticle(id: string): Promise<void> {
    await $fetch(`/portal/article/${id}/like`, {
      method: 'POST',
      baseURL: useRuntimeConfig().public.apiBase
    })
  }

  /**
   * 检查是否已点赞
   */
  async function isArticleLiked(id: string): Promise<boolean> {
    const res = await $fetch<{ isLiked: boolean }>(`/portal/article/${id}/is-liked`, {
      method: 'GET',
      baseURL: useRuntimeConfig().public.apiBase
    })
    return res?.isLiked || false
  }

  return {
    getArticleList,
    getArticleDetail,
    likeArticle,
    isArticleLiked
  }
}
