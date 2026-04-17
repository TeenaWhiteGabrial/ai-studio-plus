import type { Resource } from '~~/shared/types/resource'

/**
 * 资源类型
 */
export type ResourceType = 'skill' | 'plugin' | 'tutorial'

/**
 * 资源列表查询参数
 */
export interface ResourceListQuery {
  page?: number
  pageSize?: number
  type: ResourceType
  keyword?: string
  categoryId?: string
  tagIds?: string[]
  sort?: 'latest' | 'hot' | 'recommend'
}

/**
 * 资源列表响应
 */
export interface ResourceListResponse {
  records: Resource[]
  total: number
}

/**
 * 资源相关 API
 */
export function useResource() {
  /**
   * 获取资源列表
   */
  async function getResourceList(query: ResourceListQuery): Promise<ResourceListResponse> {
    const res = await $fetch<ResourceListResponse>(`/portal/resource/${query.type}/list`, {
      method: 'GET',
      params: query,
      baseURL: useRuntimeConfig().public.apiBase
    })
    return res
  }

  /**
   * 获取资源详情
   */
  async function getResourceDetail(type: ResourceType, id: string): Promise<Resource> {
    const res = await $fetch<Resource>(`/portal/resource/${type}/${id}`, {
      method: 'GET',
      baseURL: useRuntimeConfig().public.apiBase
    })
    return res
  }

  /**
   * 获取热门资源列表
   */
  async function getHotResources(type: ResourceType, size = 10): Promise<Resource[]> {
    const res = await $fetch<{ records: Resource[] }>(`/portal/resource/${type}/list`, {
      method: 'GET',
      params: { sort: 'hot', size },
      baseURL: useRuntimeConfig().public.apiBase
    })
    return res?.records || []
  }

  return {
    getResourceList,
    getResourceDetail,
    getHotResources
  }
}
