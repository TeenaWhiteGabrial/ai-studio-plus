/**
 * 评论类型
 */
export interface Comment {
  id: string
  targetType: 'article' | 'question' | 'answer'
  targetId: string
  parentId?: string
  content: string
  authorId: string
  authorName: string
  authorAvatar?: string
  likeCount: number
  replyCount?: number
  replies?: Comment[]
  createTime: string
  updateTime: string
}

/**
 * 收藏类型
 */
export interface Favorite {
  id: string
  targetType: 'article' | 'question' | 'resource'
  targetId: string
  userId: string
  createTime: string
}

/**
 * 通知类型
 */
export interface Notification {
  id: string
  type: 'comment' | 'answer' | 'like' | 'system' | 'accept'
  title: string
  content: string
  targetType?: 'article' | 'question' | 'answer' | 'comment'
  targetId?: string
  isRead: boolean
  createTime: string
}

/**
 * 标签类型
 */
export interface Tag {
  id: string
  name: string
  color?: string
  articleCount?: number
  questionCount?: number
  status: 'normal' | 'deleted'
  createTime: string
}

/**
 * 评论创建参数
 */
export interface CommentFormData {
  targetType: 'article' | 'question' | 'answer'
  targetId: string
  parentId?: string
  content: string
}

/**
 * 收藏创建参数
 */
export interface FavoriteCreateData {
  targetType: 'article' | 'question' | 'resource'
  targetId: string
}

/**
 * 通知列表查询参数
 */
export interface NotificationListQuery {
  page?: number
  pageSize?: number
  isRead?: boolean
}

/**
 * 通知列表响应
 */
export interface NotificationListResponse {
  list: Notification[]
  total: number
  page: number
  pageSize: number
  unreadCount: number
}

/**
 * 浏览记录类型
 */
export interface BrowseHistory {
  id: string
  targetType: 'article' | 'question' | 'resource'
  targetId: string
  title: string
  coverImage?: string
  createTime: string
}
