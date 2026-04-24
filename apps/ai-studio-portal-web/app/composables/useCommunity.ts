import type {
  BrowseHistory,
  Comment,
  CommentFormData,
  Favorite,
  FavoriteCreateData,
  Notification,
  NotificationListQuery,
  NotificationListResponse,
  Tag
} from '~~/shared/types/community'

function normalizeComment(item: Record<string, any>): Comment {
  return {
    ...item,
    id: String(item.id),
    targetType: item.targetType || item.commentType,
    targetId: String(item.targetId),
    parentId: item.parentId ? String(item.parentId) : undefined,
    authorId: String(item.authorId),
    authorName: item.authorName || '',
    authorAvatar: item.authorAvatar,
    content: item.content || '',
    likeCount: item.likeCount ?? item.likesCount ?? 0,
    createTime: item.createTime || item.createdAt || '',
    updateTime: item.updateTime || item.updatedAt || '',
    replies: Array.isArray(item.replies) ? item.replies.map(normalizeComment) : []
  }
}

function normalizeBrowseHistory(item: Record<string, any>): BrowseHistory {
  return {
    ...item,
    id: String(item.id),
    targetType: item.targetType,
    targetId: String(item.targetId),
    title: item.title || '',
    coverImage: item.coverImage || '',
    createTime: item.createTime || item.createdAt || ''
  }
}

function normalizeNotification(item: Record<string, any>): Notification {
  return {
    ...item,
    id: String(item.id),
    type: item.type || 'system',
    title: item.title || item.content || '系统通知',
    content: item.content || '',
    targetType: item.targetType || item.sourceType,
    targetId: item.targetId ? String(item.targetId) : item.sourceId ? String(item.sourceId) : undefined,
    isRead: Boolean(item.isRead === true || item.isRead === 1),
    createTime: item.createTime || item.createdAt || ''
  }
}

export function useCommunity() {
  const config = useRuntimeConfig()
  const authStore = useAuthStore()

  function getAuthHeaders() {
    if (!authStore.token) {
      return undefined
    }
    const authorization = config.public.tokenType ? `${config.public.tokenType} ${authStore.token}` : authStore.token
    return { Authorization: authorization }
  }

  async function getCommentList(targetType: string, targetId: string): Promise<Comment[]> {
    const res = await $fetch<Record<string, any>[]>('/portal/comment/list', {
      method: 'GET',
      params: { targetType, targetId },
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
    return (res || []).map(normalizeComment)
  }

  async function createComment(data: CommentFormData): Promise<number | undefined> {
    return await $fetch<number>('/portal/comment', {
      method: 'POST',
      body: data,
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
  }

  async function deleteComment(id: string): Promise<void> {
    await $fetch(`/portal/comment/${id}/delete`, {
      method: 'POST',
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
  }

  async function likeComment(id: string): Promise<void> {
    await $fetch(`/portal/comment/${id}/like`, {
      method: 'POST',
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
  }

  async function getFavoriteList(targetType?: string, page = 1, pageSize = 20): Promise<{ list: Favorite[]; total: number }> {
    const res = await $fetch<{ total: number; records: Favorite[] }>('/portal/favorite/list', {
      method: 'GET',
      params: { targetType, page, size: pageSize },
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
    return {
      list: res?.records || [],
      total: res?.total || 0
    }
  }

  async function createFavorite(data: FavoriteCreateData): Promise<void> {
    await $fetch('/portal/favorite', {
      method: 'POST',
      body: data,
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
  }

  async function deleteFavorite(targetType: string, targetId: string): Promise<void> {
    await $fetch('/portal/favorite/remove', {
      method: 'POST',
      body: { targetType, targetId },
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
  }

  async function checkFavorite(targetType: string, targetId: string): Promise<boolean> {
    const res = await $fetch<boolean>('/portal/favorite/check', {
      method: 'GET',
      params: { targetType, targetId },
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
    return !!res
  }

  async function getNotificationList(_query: NotificationListQuery = {}): Promise<NotificationListResponse> {
    const res = await $fetch<Record<string, any>[]>('/portal/notification/list', {
      method: 'GET',
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
    const list = (res || []).map(normalizeNotification)
    const unreadCount = list.filter(item => !item.isRead).length
    return {
      list,
      total: list.length,
      page: 1,
      pageSize: list.length || 20,
      unreadCount
    }
  }

  async function markNotificationRead(id: string): Promise<void> {
    await $fetch(`/portal/notification/${id}/read`, {
      method: 'POST',
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
  }

  async function markAllNotificationRead(): Promise<void> {
    const list = await getNotificationList()
    const unreadIds = list.list.filter(item => !item.isRead).map(item => item.id)
    await Promise.all(unreadIds.map(id => markNotificationRead(id)))
  }

  async function getUnreadNotificationCount(): Promise<{ count: number }> {
    return await $fetch<{ count: number }>('/portal/notification/unread-count', {
      method: 'GET',
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
  }

  async function getTagList(type?: string): Promise<Tag[]> {
    const res = await $fetch<Tag[]>('/portal/tag/list', {
      method: 'GET',
      params: { type },
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
    return res || []
  }

  async function createTag(name: string, type = 'article'): Promise<void> {
    await $fetch('/portal/tag', {
      method: 'POST',
      body: { name, type },
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
  }

  async function getBrowseHistoryList(): Promise<{ list: BrowseHistory[]; total: number }> {
    const res = await $fetch<Record<string, any>[]>('/portal/browse-history/list', {
      method: 'GET',
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
    const list = (res || []).map(normalizeBrowseHistory)
    return {
      list,
      total: list.length
    }
  }

  async function createBrowseHistory(data: { targetType: string; targetId: string }): Promise<void> {
    await $fetch('/portal/browse-history', {
      method: 'POST',
      body: data,
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
  }

  async function clearBrowseHistory(): Promise<void> {
    await $fetch('/portal/browse-history/clear', {
      method: 'POST',
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
  }

  return {
    getCommentList,
    createComment,
    deleteComment,
    likeComment,
    getFavoriteList,
    createFavorite,
    deleteFavorite,
    checkFavorite,
    getNotificationList,
    markNotificationRead,
    markAllNotificationRead,
    getUnreadNotificationCount,
    getTagList,
    createTag,
    getBrowseHistoryList,
    createBrowseHistory,
    clearBrowseHistory
  }
}
