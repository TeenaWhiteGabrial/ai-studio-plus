import type { Article, ArticleListQuery, ArticleListResponse } from '~~/shared/types/article'

function normalizeArticle(item: Record<string, any>): Article {
  return {
    ...item,
    id: String(item.id),
    title: item.title || '',
    content: item.content || '',
    summary: item.summary || '',
    coverImage: item.coverImage ?? item.cover_image ?? '',
    authorId: String(item.authorId ?? item.author_id ?? ''),
    authorName: item.authorName ?? item.author_name ?? item.realName ?? item.real_name ?? item.username ?? '',
    authorAvatar: item.authorAvatar ?? item.author_avatar ?? '',
    tags: Array.isArray(item.tags) ? item.tags : [],
    categoryId: item.categoryId ?? item.category_id,
    categoryName: item.categoryName ?? item.category_name ?? '',
    viewCount: item.viewCount ?? item.viewsCount ?? item.views_count ?? 0,
    viewsCount: item.viewsCount ?? item.views_count ?? item.viewCount ?? 0,
    likeCount: item.likeCount ?? item.likesCount ?? item.likes_count ?? 0,
    likesCount: item.likesCount ?? item.likes_count ?? item.likeCount ?? 0,
    commentCount: item.commentCount ?? item.commentsCount ?? item.comments_count ?? 0,
    commentsCount: item.commentsCount ?? item.comments_count ?? item.commentCount ?? 0,
    favoriteCount: item.favoriteCount ?? item.favorite_count ?? 0,
    status: item.status,
    createTime: item.createTime ?? item.create_time ?? item.createdAt ?? item.created_at ?? '',
    createdAt: item.createdAt ?? item.created_at ?? item.createTime ?? item.create_time ?? '',
    updateTime: item.updateTime ?? item.update_time ?? item.updatedAt ?? item.updated_at ?? '',
    updatedAt: item.updatedAt ?? item.updated_at ?? item.updateTime ?? item.update_time ?? '',
    publishTime: item.publishTime ?? item.publish_time ?? item.publishedAt ?? item.published_at ?? '',
    publishedAt: item.publishedAt ?? item.published_at ?? item.publishTime ?? item.publish_time ?? '',
  } as Article
}

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
        records: (res?.records || []).map(item => normalizeArticle(item as Record<string, any>))
      }
    } catch (error) {
      if (isUnauthorized(error)) {
        return { total: 0, records: [] }
      }
      throw error
    }
  }

  async function getArticleDetail(id: string): Promise<Article> {
    const res = await $fetch<Record<string, any>>(`/portal/article/${id}`, {
      method: 'GET',
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
    return normalizeArticle(res)
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
