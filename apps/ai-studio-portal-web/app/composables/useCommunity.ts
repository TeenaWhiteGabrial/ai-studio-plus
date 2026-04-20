import type { Comment, CommentFormData, Favorite, FavoriteCreateData, Notification, NotificationListQuery, NotificationListResponse, BrowseHistory, Tag } from '~~/shared/types/community'

/**
 * 社区相关 API（评论、收藏、通知、标签、浏览记录）
 */
export function useCommunity() {
  // ============ 评论相关 ============

  /**
   * 获取评论列表
   */
  async function getCommentList(targetType: string, targetId: string): Promise<Comment[]> {
    const res = await $fetch<Comment[]>('/portal/comment/list', {
      method: 'GET',
      params: { targetType, targetId },
      baseURL: useRuntimeConfig().public.apiBase
    })
    return res || []
  }

  /**
   * 创建评论
   */
  async function createComment(data: CommentFormData): Promise<Comment> {
    const res = await $fetch<Comment>('/portal/comment', {
      method: 'POST',
      body: data,
      baseURL: useRuntimeConfig().public.apiBase
    })
    return res
  }

  /**
   * 删除评论
   */
  async function deleteComment(id: string): Promise<void> {
    await $fetch(`/portal/comment/${id}/delete`, {
      method: 'POST',
      baseURL: useRuntimeConfig().public.apiBase
    })
  }

  /**
   * 点赞评论
   */
  async function likeComment(id: string): Promise<void> {
    await $fetch(`/portal/comment/${id}/like`, {
      method: 'POST',
      baseURL: useRuntimeConfig().public.apiBase
    })
  }

  // ============ 收藏相关 ============

  /**
   * 获取收藏列表
   */
  async function getFavoriteList(page = 1, pageSize = 20): Promise<{ list: Favorite[]; total: number }> {
    const res = await $fetch<{ total: number; records: Favorite[] }>('/portal/favorite/list', {
      method: 'GET',
      params: { page, size: pageSize },
      baseURL: useRuntimeConfig().public.apiBase
    })
    return { list: res?.records || [], total: res?.total || 0 }
  }

  /**
   * 创建收藏
   */
  async function createFavorite(data: FavoriteCreateData): Promise<Favorite> {
    const res = await $fetch<Favorite>('/portal/favorite', {
      method: 'POST',
      body: data,
      baseURL: useRuntimeConfig().public.apiBase
    })
    return res
  }

  /**
   * 删除收藏
   */
  async function deleteFavorite(targetType: string, targetId: string): Promise<void> {
    await $fetch('/portal/favorite/remove', {
      method: 'POST',
      body: { targetType, targetId },
      baseURL: useRuntimeConfig().public.apiBase
    })
  }

  /**
   * 检查是否已收藏
   */
  async function checkFavorite(targetType: string, targetId: string): Promise<{ isFavorited: boolean }> {
    const res = await $fetch<{ isFavorited: boolean }>('/portal/favorite/check', {
      method: 'GET',
      params: { targetType, targetId },
      baseURL: useRuntimeConfig().public.apiBase
    })
    return res
  }

  // ============ 通知相关 ============

  /**
   * 获取通知列表
   */
  async function getNotificationList(query: NotificationListQuery): Promise<NotificationListResponse> {
    const res = await $fetch<{ total: number; records: Notification[]; unreadCount?: number }>('/portal/notification/list', {
      method: 'GET',
      params: query,
      baseURL: useRuntimeConfig().public.apiBase
    })
    return {
      list: res?.records || [],
      total: res?.total || 0,
      page: query.page || 1,
      pageSize: query.pageSize || 20,
      unreadCount: res?.unreadCount || 0
    }
  }

  /**
   * 标记通知已读
   */
  async function markNotificationRead(id: string): Promise<void> {
    await $fetch(`/portal/notification/${id}/read`, {
      method: 'POST',
      baseURL: useRuntimeConfig().public.apiBase
    })
  }

  /**
   * 获取未读通知数量
   */
  async function getUnreadNotificationCount(): Promise<{ count: number }> {
    const res = await $fetch<{ count: number }>('/portal/notification/unread-count', {
      method: 'GET',
      baseURL: useRuntimeConfig().public.apiBase
    })
    return res
  }

  // ============ 标签相关 ============

  /**
   * 获取标签列表
   */
  async function getTagList(): Promise<Tag[]> {
    const res = await $fetch<Tag[]>('/portal/tag/list', {
      method: 'GET',
      baseURL: useRuntimeConfig().public.apiBase
    })
    return res || []
  }

  /**
   * 创建标签
   */
  async function createTag(name: string): Promise<Tag> {
    const res = await $fetch<Tag>('/portal/tag', {
      method: 'POST',
      body: { name },
      baseURL: useRuntimeConfig().public.apiBase
    })
    return res
  }

  // ============ 浏览记录相关 ============

  /**
   * 获取浏览记录列表
   */
  async function getBrowseHistoryList(page = 1, pageSize = 20): Promise<{ list: BrowseHistory[]; total: number }> {
    const res = await $fetch<{ total: number; records: BrowseHistory[] }>('/portal/browse-history/list', {
      method: 'GET',
      params: { page, size: pageSize },
      baseURL: useRuntimeConfig().public.apiBase
    })
    return { list: res?.records || [], total: res?.total || 0 }
  }

  /**
   * 创建浏览记录
   */
  async function createBrowseHistory(data: { targetType: string; targetId: string }): Promise<void> {
    await $fetch('/portal/browse-history', {
      method: 'POST',
      body: data,
      baseURL: useRuntimeConfig().public.apiBase
    })
  }

  /**
   * 清空浏览记录
   */
  async function clearBrowseHistory(): Promise<void> {
    await $fetch('/portal/browse-history/clear', {
      method: 'POST',
      baseURL: useRuntimeConfig().public.apiBase
    })
  }

  return {
    // 评论
    getCommentList,
    createComment,
    deleteComment,
    likeComment,
    // 收藏
    getFavoriteList,
    createFavorite,
    deleteFavorite,
    checkFavorite,
    // 通知
    getNotificationList,
    markNotificationRead,
    getUnreadNotificationCount,
    // 标签
    getTagList,
    createTag,
    // 浏览记录
    getBrowseHistoryList,
    createBrowseHistory,
    clearBrowseHistory
  }
}
