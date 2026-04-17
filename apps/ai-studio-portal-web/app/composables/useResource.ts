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
  const config = useRuntimeConfig()

  /**
   * 获取资源列表
   */
  async function getResourceList(query: ResourceListQuery): Promise<ResourceListResponse> {
    const res = await $fetch<ResourceListResponse>(`/portal/resource/${query.type}/list`, {
      method: 'GET',
      params: query,
      baseURL: config.public.apiBase
    })
    return res
  }

  /**
   * 获取资源详情
   */
  async function getResourceDetail(type: ResourceType, id: string): Promise<Resource> {
    const res = await $fetch<Resource>(`/portal/resource/${type}/${id}`, {
      method: 'GET',
      baseURL: config.public.apiBase
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
      baseURL: config.public.apiBase
    })
    return res?.records || []
  }

  /**
   * 下载 Skill ZIP
   */
  function downloadSkill(id: number | string) {
    window.open(`${config.public.apiBase}/portal/open/resource/skill/${id}/download`, '_blank')
  }

  /**
   * 下载 Plugin
   */
  function downloadPlugin(id: number | string) {
    window.open(`${config.public.apiBase}/portal/open/resource/plugin/${id}/download`, '_blank')
  }

  /**
   * 获取教程视频播放地址
   */
  async function getTutorialVideoUrl(id: number | string): Promise<string> {
    const res = await $fetch<{ data: string }>(`/portal/open/resource/tutorial/${id}/video`, {
      method: 'GET',
      baseURL: config.public.apiBase
    })
    return res?.data || ''
  }

  /**
   * 下载教程 ZIP 附件
   */
  function downloadTutorialZip(id: number | string) {
    window.open(`${config.public.apiBase}/portal/open/resource/tutorial/${id}/zip`, '_blank')
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
