import type { Resource } from '~~/shared/types/resource'

export type ResourceType = 'skill' | 'plugin' | 'tutorial'

export interface ResourceListQuery {
  page?: number
  pageSize?: number
  type: ResourceType
  keyword?: string
  category?: string
  tag?: string
  sort?: 'latest' | 'hot' | 'recommend'
}

export interface ResourceListResponse {
  records: Resource[]
  total: number
}

interface OpenResult<T> {
  code: number
  message: string
  data: T
}

interface OpenPage<T> {
  records: T[]
  total: number
}

function normalizeResource(type: ResourceType, item: Record<string, any>): Resource {
  return {
    ...item,
    id: item.id,
    type,
    name: item.name || item.title || '',
    title: item.title || item.name || '',
    introduction: item.introduction || item.description || '',
    description: item.description || item.introduction || '',
    photo: item.photo || item.coverImage || item.icon || '',
    coverImage: item.coverImage || item.photo || item.icon || '',
    categoryName: item.categoryName || item.category || '',
    publishTime: item.publishTime || item.createdAt || item.createTime || '',
    createTime: item.createTime || item.createdAt || '',
    updateTime: item.updateTime || item.updatedAt || '',
    viewCount: item.viewCount ?? 0,
    downloadCount: item.downloadCount ?? 0
  } as Resource
}

function parseOpenPage<T>(raw: OpenResult<OpenPage<T>> | OpenPage<T>): OpenPage<T> {
  if (raw && typeof raw === 'object' && 'data' in raw) {
    return (raw as OpenResult<OpenPage<T>>).data || { records: [], total: 0 }
  }
  return raw as OpenPage<T>
}

function unwrapOpenResult<T>(raw: OpenResult<T> | T): T {
  if (raw && typeof raw === 'object' && 'data' in (raw as Record<string, any>) && 'code' in (raw as Record<string, any>)) {
    return (raw as OpenResult<T>).data
  }
  return raw as T
}

export function useResource() {
  const config = useRuntimeConfig()
  const apiBase = (config.public.apiBase || '/').replace(/\/$/, '')

  const toDownloadUrl = (path: string) => `${apiBase}${path}`

  async function getResourceList(query: ResourceListQuery): Promise<ResourceListResponse> {
    const params = {
      page: query.page || 1,
      size: query.pageSize || 10,
      keyword: query.keyword,
      category: query.category,
      tag: query.tag,
      sort: query.sort
    }

    const raw = await $fetch<OpenResult<OpenPage<Record<string, any>>> | OpenPage<Record<string, any>>>(
      `/portal/open/resource/${query.type}/list`,
      {
        method: 'GET',
        params,
        baseURL: config.public.apiBase
      }
    )

    const page = parseOpenPage(raw)
    return {
      total: page.total || 0,
      records: (page.records || []).map(item => normalizeResource(query.type, item))
    }
  }

  async function getResourceDetail(type: ResourceType, id: string | number): Promise<Resource> {
    const raw = await $fetch<OpenResult<Record<string, any>> | Record<string, any>>(
      `/portal/open/resource/${type}/${id}`,
      {
        method: 'GET',
        baseURL: config.public.apiBase
      }
    )
    const data = unwrapOpenResult(raw) || {}
    return normalizeResource(type, data)
  }

  async function getHotResources(type: ResourceType, size = 10): Promise<Resource[]> {
    const res = await getResourceList({
      type,
      page: 1,
      pageSize: size,
      sort: 'hot'
    })
    return res.records
  }

  function downloadSkill(id: number | string) {
    window.open(toDownloadUrl(`/portal/open/resource/skill/${id}/download`), '_blank')
  }

  function downloadPlugin(id: number | string) {
    window.open(toDownloadUrl(`/portal/open/resource/plugin/${id}/download`), '_blank')
  }

  async function getTutorialVideoUrl(id: number | string): Promise<string> {
    const raw = await $fetch<OpenResult<string> | string>(`/portal/open/resource/tutorial/${id}/video`, {
      method: 'GET',
      baseURL: config.public.apiBase
    })
    return unwrapOpenResult(raw) || ''
  }

  function downloadTutorialZip(id: number | string) {
    window.open(toDownloadUrl(`/portal/open/resource/tutorial/${id}/zip`), '_blank')
  }

  return {
    getResourceList,
    getResourceDetail,
    getHotResources,
    downloadSkill,
    downloadPlugin,
    getTutorialVideoUrl,
    downloadTutorialZip
  }
}
