/**
 * 问题类型
 */
export interface Question {
  id: string
  title: string
  content: string
  authorId: string
  authorName: string
  authorAvatar?: string
  tags: string[]
  viewCount: number
  answerCount: number
  likeCount: number
  favoriteCount: number
  status: 'draft' | 'published' | 'deleted' | 'closed'
  isResolved: boolean
  acceptedAnswerId?: string
  createTime: string
  updateTime: string
  publishTime?: string
}

/**
 * 回答类型
 */
export interface Answer {
  id: string
  questionId: string
  content: string
  authorId: string
  authorName: string
  authorAvatar?: string
  likeCount: number
  isAccepted: boolean
  createTime: string
  updateTime: string
}

/**
 * 问题列表查询参数
 */
export interface QuestionListQuery {
  page?: number
  pageSize?: number
  keyword?: string
  tagId?: string
  sort?: 'latest' | 'hot' | 'unanswered' | 'recommend'
  userId?: string
  isResolved?: boolean
}

/**
 * 问题列表响应
 */
export interface QuestionListResponse {
  records: Question[]
  total: number
}

/**
 * 问题创建/更新参数
 */
export interface QuestionFormData {
  id?: string
  title: string
  content: string
  tags: string[]
}

/**
 * 回答创建参数
 */
export interface AnswerFormData {
  questionId: string
  content: string
}
