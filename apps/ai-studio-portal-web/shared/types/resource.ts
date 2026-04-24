import { ROLE_STATUS } from './auth'

export interface Resource {
  id: string | number
  type?: 'skill' | 'plugin' | 'tutorial' | string
  name?: string
  title?: string
  introduction?: string
  description?: string
  content?: string
  contentType?: 'richText' | 'markdown' | string
  photo?: string
  coverImage?: string
  icon?: string
  category?: string
  categoryName?: string
  publishTime?: string
  createTime?: string
  updateTime?: string
  createdAt?: string
  updatedAt?: string
  latestVersion?: string
  totalVersions?: number
  latestVersionId?: number
  viewCount?: number
  downloadCount?: number
  zipFileName?: string
  zipFileUrl?: string
  videoUrl?: string
  link?: string
  [key: string]: any
}

export interface NavigationItem {
  id: string
  columnName: string
  columnLink: string
  sequence: string | number
  runType: '0' | '1'
  icon?: string
  child: NavigationItem[]
}

export interface WorkBenchItem {
  name: string
  link: string
  needLogin?: boolean
  showRoles?: ROLE_STATUS[]
}

export enum GetDataWayEnum {
  RESOURCE = 'Resource',
  API = 'Api',
  STATIC = 'Static',
  JSON = 'Json'
}
