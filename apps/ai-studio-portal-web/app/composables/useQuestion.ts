import type { Question, QuestionListQuery, QuestionListResponse, QuestionFormData, Answer, AnswerFormData } from '~~/shared/types/question'

/**
 * 问答相关 API
 */
export function useQuestion() {
  /**
   * 获取问题列表
   */
  async function getQuestionList(query: QuestionListQuery): Promise<QuestionListResponse> {
    const res = await $fetch<QuestionListResponse>('/api/question/list', {
      method: 'GET',
      params: query,
      baseURL: useRuntimeConfig().public.apiBase
    })
    return res
  }

  /**
   * 获取问题详情
   */
  async function getQuestionDetail(id: string): Promise<Question> {
    const res = await $fetch<Question>(`/api/question/${id}`, {
      method: 'GET',
      baseURL: useRuntimeConfig().public.apiBase
    })
    return res
  }

  /**
   * 创建问题
   */
  async function createQuestion(data: QuestionFormData): Promise<{ id: number }> {
    const res = await $fetch<{ id: number }>('/api/question', {
      method: 'POST',
      body: data,
      baseURL: useRuntimeConfig().public.apiBase
    })
    return res
  }

  /**
   * 更新问题
   */
  async function updateQuestion(id: string, data: QuestionFormData): Promise<void> {
    await $fetch(`/api/question/${id}`, {
      method: 'PUT',
      body: data,
      baseURL: useRuntimeConfig().public.apiBase
    })
  }

  /**
   * 删除问题
   */
  async function deleteQuestion(id: string): Promise<void> {
    await $fetch(`/api/question/${id}`, {
      method: 'DELETE',
      baseURL: useRuntimeConfig().public.apiBase
    })
  }

  /**
   * 获取问题回答列表
   */
  async function getAnswerList(questionId: string): Promise<Answer[]> {
    const res = await $fetch<Answer[]>(`/api/question/${questionId}/answer`, {
      method: 'GET',
      baseURL: useRuntimeConfig().public.apiBase
    })
    return res || []
  }

  /**
   * 创建回答
   */
  async function createAnswer(data: AnswerFormData): Promise<{ id: number }> {
    const res = await $fetch<{ id: number }>(`/api/question/${data.questionId}/answer`, {
      method: 'POST',
      body: { content: data.content },
      baseURL: useRuntimeConfig().public.apiBase
    })
    return res
  }

  /**
   * 更新回答
   */
  async function updateAnswer(id: string, content: string): Promise<void> {
    await $fetch(`/api/answer/${id}`, {
      method: 'PUT',
      body: { content },
      baseURL: useRuntimeConfig().public.apiBase
    })
  }

  /**
   * 删除回答
   */
  async function deleteAnswer(id: string): Promise<void> {
    await $fetch(`/api/answer/${id}`, {
      method: 'DELETE',
      baseURL: useRuntimeConfig().public.apiBase
    })
  }

  /**
   * 采纳回答
   */
  async function acceptAnswer(questionId: string, answerId: string): Promise<void> {
    await $fetch(`/api/question/${questionId}/accept/${answerId}`, {
      method: 'POST',
      baseURL: useRuntimeConfig().public.apiBase
    })
  }

  /**
   * 点赞回答
   */
  async function likeAnswer(id: string): Promise<void> {
    await $fetch(`/api/answer/${id}/like`, {
      method: 'POST',
      baseURL: useRuntimeConfig().public.apiBase
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
