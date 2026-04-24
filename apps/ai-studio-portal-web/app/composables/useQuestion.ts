import type { Answer, AnswerFormData, Question, QuestionFormData, QuestionListQuery, QuestionListResponse } from '~~/shared/types/question'

export function useQuestion() {
  const config = useRuntimeConfig()

  async function getQuestionList(query: QuestionListQuery): Promise<QuestionListResponse> {
    const res = await $fetch<QuestionListResponse>('/portal/question/list', {
      method: 'GET',
      params: {
        page: query.page || 1,
        size: query.pageSize || 10,
        keyword: query.keyword,
        tagId: query.tagId,
        sort: query.sort || 'latest'
      },
      baseURL: config.public.apiBase
    })
    return {
      total: res?.total || 0,
      records: res?.records || []
    }
  }

  async function getQuestionDetail(id: string): Promise<Question> {
    return await $fetch<Question>(`/portal/question/${id}`, {
      method: 'GET',
      baseURL: config.public.apiBase
    })
  }

  async function createQuestion(data: QuestionFormData): Promise<number | undefined> {
    return await $fetch<number>('/portal/question', {
      method: 'POST',
      body: data,
      baseURL: config.public.apiBase
    })
  }

  async function updateQuestion(id: string, data: QuestionFormData): Promise<void> {
    await $fetch(`/portal/question/${id}`, {
      method: 'POST',
      body: data,
      baseURL: config.public.apiBase
    })
  }

  async function deleteQuestion(id: string): Promise<void> {
    await $fetch(`/portal/question/${id}/delete`, {
      method: 'POST',
      baseURL: config.public.apiBase
    })
  }

  async function getAnswerList(questionId: string): Promise<Answer[]> {
    const res = await $fetch<Answer[]>(`/portal/question/${questionId}/answers`, {
      method: 'GET',
      baseURL: config.public.apiBase
    })
    return res || []
  }

  async function createAnswer(data: AnswerFormData): Promise<number | undefined> {
    return await $fetch<number>(`/portal/question/${data.questionId}/answer`, {
      method: 'POST',
      body: { content: data.content },
      baseURL: config.public.apiBase
    })
  }

  async function updateAnswer(id: string, content: string): Promise<void> {
    await $fetch(`/portal/answer/${id}`, {
      method: 'POST',
      body: { content },
      baseURL: config.public.apiBase
    })
  }

  async function deleteAnswer(id: string): Promise<void> {
    await $fetch(`/portal/answer/${id}/delete`, {
      method: 'POST',
      baseURL: config.public.apiBase
    })
  }

  async function acceptAnswer(questionId: string | number, answerId: string | number): Promise<void> {
    await $fetch(`/portal/answer/${answerId}/accept`, {
      method: 'POST',
      params: { questionId },
      baseURL: config.public.apiBase
    })
  }

  async function likeAnswer(id: string | number): Promise<void> {
    await $fetch(`/portal/answer/${id}/like`, {
      method: 'POST',
      baseURL: config.public.apiBase
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
