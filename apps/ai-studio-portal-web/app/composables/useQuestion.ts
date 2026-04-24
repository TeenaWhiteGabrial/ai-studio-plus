import type { Answer, AnswerFormData, Question, QuestionFormData, QuestionListQuery, QuestionListResponse } from '~~/shared/types/question'

function normalizeQuestion(item: Record<string, any>): Question {
  const bestAnswerId = item.acceptedAnswerId ?? item.bestAnswerId ?? item.best_answer_id
  const createdAt = item.createdAt ?? item.created_at ?? ''
  const updatedAt = item.updatedAt ?? item.updated_at ?? ''

  return {
    ...item,
    id: String(item.id ?? ''),
    title: item.title || '',
    content: item.content || '',
    authorId: String(item.authorId ?? item.author_id ?? ''),
    authorName: item.authorName ?? item.author_name ?? '',
    authorAvatar: item.authorAvatar ?? item.author_avatar,
    tags: item.tags ?? '',
    viewCount: item.viewCount ?? item.viewsCount ?? item.views_count ?? 0,
    viewsCount: item.viewsCount ?? item.views_count ?? item.viewCount ?? 0,
    answerCount: item.answerCount ?? item.answersCount ?? item.answers_count ?? 0,
    answersCount: item.answersCount ?? item.answers_count ?? item.answerCount ?? 0,
    likeCount: item.likeCount ?? item.likesCount ?? item.likes_count ?? 0,
    likesCount: item.likesCount ?? item.likes_count ?? item.likeCount ?? 0,
    favoriteCount: item.favoriteCount ?? item.favorite_count ?? 0,
    status: item.status ?? 'published',
    isResolved: Boolean(item.isResolved ?? item.hasBestAnswer ?? item.has_best_answer),
    hasBestAnswer: item.hasBestAnswer ?? item.has_best_answer ?? 0,
    acceptedAnswerId: bestAnswerId !== undefined && bestAnswerId !== null ? String(bestAnswerId) : undefined,
    createTime: item.createTime ?? createdAt,
    createdAt,
    updateTime: item.updateTime ?? updatedAt,
    updatedAt,
    publishTime: item.publishTime ?? item.publishedAt ?? item.published_at
  } as Question
}

function normalizeAnswer(item: Record<string, any>): Answer {
  const createdAt = item.createdAt ?? item.created_at ?? ''
  const updatedAt = item.updatedAt ?? item.updated_at ?? ''

  return {
    ...item,
    id: String(item.id ?? ''),
    questionId: String(item.questionId ?? item.question_id ?? ''),
    content: item.content || '',
    authorId: String(item.authorId ?? item.author_id ?? ''),
    authorName: item.authorName ?? item.author_name ?? '',
    authorAvatar: item.authorAvatar ?? item.author_avatar,
    likeCount: item.likeCount ?? item.likesCount ?? item.likes_count ?? 0,
    likesCount: item.likesCount ?? item.likes_count ?? item.likeCount ?? 0,
    isAccepted: Boolean(item.isAccepted ?? item.isBest ?? item.is_best),
    isBest: item.isBest ?? item.is_best ?? 0,
    createTime: item.createTime ?? createdAt,
    createdAt,
    updateTime: item.updateTime ?? updatedAt,
    updatedAt
  } as Answer
}

export function useQuestion() {
  const config = useRuntimeConfig()
  const authStore = useAuthStore()

  function getAuthHeaders() {
    if (!authStore.token) {
      return undefined
    }
    const authorization = config.public.tokenType ? `${config.public.tokenType} ${authStore.token}` : authStore.token
    return { Authorization: authorization }
  }

  async function getQuestionList(query: QuestionListQuery): Promise<QuestionListResponse> {
    const res = await $fetch<{ total?: number; records?: Record<string, any>[] }>('/portal/question/list', {
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
      records: (res?.records || []).map(normalizeQuestion)
    }
  }

  async function getQuestionDetail(id: string): Promise<Question> {
    const res = await $fetch<Record<string, any>>(`/portal/question/${id}`, {
      method: 'GET',
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
    return normalizeQuestion(res || {})
  }

  async function createQuestion(data: QuestionFormData): Promise<number | undefined> {
    return await $fetch<number>('/portal/question', {
      method: 'POST',
      body: data,
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
  }

  async function updateQuestion(id: string, data: QuestionFormData): Promise<void> {
    await $fetch(`/portal/question/${id}`, {
      method: 'POST',
      body: data,
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
  }

  async function deleteQuestion(id: string): Promise<void> {
    await $fetch(`/portal/question/${id}/delete`, {
      method: 'POST',
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
  }

  async function getAnswerList(questionId: string): Promise<Answer[]> {
    const res = await $fetch<Record<string, any>[]>(`/portal/question/${questionId}/answers`, {
      method: 'GET',
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
    return (res || []).map(normalizeAnswer)
  }

  async function createAnswer(data: AnswerFormData): Promise<number | undefined> {
    return await $fetch<number>(`/portal/question/${data.questionId}/answer`, {
      method: 'POST',
      body: { content: data.content },
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
  }

  async function updateAnswer(id: string, content: string): Promise<void> {
    await $fetch(`/portal/answer/${id}`, {
      method: 'POST',
      body: { content },
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
  }

  async function deleteAnswer(id: string): Promise<void> {
    await $fetch(`/portal/answer/${id}/delete`, {
      method: 'POST',
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
  }

  async function acceptAnswer(questionId: string | number, answerId: string | number): Promise<void> {
    await $fetch(`/portal/answer/${answerId}/accept`, {
      method: 'POST',
      params: { questionId },
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
  }

  async function likeAnswer(id: string | number): Promise<void> {
    await $fetch(`/portal/answer/${id}/like`, {
      method: 'POST',
      baseURL: config.public.apiBase,
      headers: getAuthHeaders()
    })
  }

  return {
    getQuestionList,
    getQuestionDetail,
    createQuestion,
    updateQuestion,
    deleteQuestion,
    getAnswerList,
    createAnswer,
    updateAnswer,
    deleteAnswer,
    acceptAnswer,
    likeAnswer
  }
}
