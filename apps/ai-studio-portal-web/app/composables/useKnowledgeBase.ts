interface KnowledgeApiResponse<T> {
  success: boolean
  data?: T
  error?: string
  message?: string
}

export interface KnowledgeDocument {
  id: string
  fileName: string
  filePath: string
  fileType: string
  fileSize: number
  status: string
  chunkCount: number
  indexedAt?: string
  errorMessage?: string
}

export interface KnowledgeStats {
  kbId: string
  kbName: string
  status: string
  fileCount: number
  chunkCount: number
  vectorCount: number
  vectorDimension: number
  queueStatus?: {
    queueSize: number
    running: number
    concurrency: number
  }
}

export interface KnowledgeSearchResult {
  id: string
  score: number
  metadata: {
    documentId?: string
    filePath?: string
    fileName?: string
    chunkIndex?: number
    content?: string
    [key: string]: any
  }
}

const DEFAULT_KNOWLEDGE_API_BASE = 'http://localhost:3002/ai-studio/v1/knowledge'

function trimBaseUrl(url: string) {
  return String(url || '').replace(/\/$/, '')
}

export function useKnowledgeBase() {
  const config = useRuntimeConfig()
  const baseURL = computed(() => trimBaseUrl(String(config.public.knowledgeApiBase || DEFAULT_KNOWLEDGE_API_BASE)))

  async function request<T>(url: string, options: Record<string, any> = {}) {
    const response = await $fetch<KnowledgeApiResponse<T>>(url, {
      ...options,
      baseURL: baseURL.value,
    })

    if (!response.success) {
      throw new Error(response.error || response.message || '知识库服务请求失败')
    }

    return response.data as T
  }

  function health() {
    return $fetch<{ status: string; timestamp: string; uptime: number }>('/health', {
      baseURL: baseURL.value,
    })
  }

  function getStats() {
    return request<KnowledgeStats>('/stats')
  }

  function listDocuments() {
    return request<KnowledgeDocument[]>('/documents')
  }

  function search(query: string, topK = 10) {
    return request<KnowledgeSearchResult[]>('/search', {
      method: 'POST',
      body: {
        query,
        topK,
      },
    })
  }

  function upload(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return request<{ id: string; filename: string; size: number; status: string }>('/upload', {
      method: 'POST',
      body: formData,
    })
  }

  function removeDocument(id: string) {
    return request<{ message?: string }>(`/documents/${encodeURIComponent(id)}`, {
      method: 'DELETE',
    })
  }

  function reindexDocument(id: string) {
    return request<{ message?: string }>(`/documents/${encodeURIComponent(id)}/reindex`, {
      method: 'POST',
    })
  }

  return {
    baseURL,
    health,
    getStats,
    listDocuments,
    search,
    upload,
    removeDocument,
    reindexDocument,
  }
}
