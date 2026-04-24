/**
 * 文章类型
 */
export interface Article {
  id: string
  title: string
  content: string
  summary?: string
  coverImage?: string
  authorId: string
  authorName: string
  authorAvatar?: string
  tags: string[]
  categoryId?: string
  categoryName?: string
  viewCount: number
  viewsCount?: number
  likeCount: number
  likesCount?: number
  commentCount: number
  commentsCount?: number
  favoriteCount: number
  status: 'draft' | 'published' | 'deleted'
  createTime: string
  createdAt?: string
  updateTime: string
  updatedAt?: string
  publishTime?: string
  publishedAt?: string
}

/**
 * 文章列表查询参数
 */
export interface ArticleListQuery {
  page?: number
  pageSize?: number
  type?: 'article' | 'question'
  keyword?: string
  tagId?: string
  categoryId?: string
  sort?: 'latest' | 'hot' | 'recommend'
  userId?: string
}

/**
 * 文章列表响应
 */
export interface ArticleListResponse {
  records: Article[]
  total: number
}

/**
 * 文章创建/更新参数
 */
export interface ArticleFormData {
  id?: string
  title: string
  content: string
  summary?: string
  coverImage?: string
  tags: string[]
  categoryId?: string
}
