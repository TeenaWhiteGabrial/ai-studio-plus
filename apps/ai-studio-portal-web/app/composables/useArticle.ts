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
    const res = await $fetch<ArticleListResponse>('/api/article/list', {
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
    const res = await $fetch<Article>(`/api/article/${id}`, {
      method: 'GET',
      baseURL: useRuntimeConfig().public.apiBase
    })
    return res
  }

  /**
   * 创建文章
   */
  async function createArticle(data: ArticleFormData): Promise<{ id: number }> {
    const res = await $fetch<{ id: number }>('/api/article', {
      method: 'POST',
      body: data,
      baseURL: useRuntimeConfig().public.apiBase
    })
    return res
  }

  /**
   * 更新文章
   */
  async function updateArticle(id: string, data: ArticleFormData): Promise<void> {
    await $fetch(`/api/article/${id}`, {
      method: 'PUT',
      body: data,
      baseURL: useRuntimeConfig().public.apiBase
    })
  }

  /**
   * 删除文章
   */
  async function deleteArticle(id: string): Promise<void> {
    await $fetch(`/api/article/${id}`, {
      method: 'DELETE',
      baseURL: useRuntimeConfig().public.apiBase
    })
  }

  /**
   * 点赞文章
   */
  async function likeArticle(id: string): Promise<void> {
    await $fetch(`/api/article/${id}/like`, {
      method: 'POST',
      baseURL: useRuntimeConfig().public.apiBase
    })
  }

  return {
    getArticleList,
    getArticleDetail,
    createArticle,
    updateArticle,
    deleteArticle,
    likeArticle
  }
}
