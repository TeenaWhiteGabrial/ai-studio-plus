import request from '@/utils/request'

export interface ArticleFolder {
  id: number
  userId?: number
  parentId?: number | null
  folderName: string
  createdAt?: string
  updatedAt?: string
}

export interface ArticlePayload {
  title: string
  content: string
  summary?: string
  coverImage?: string
  folderId?: number | null
  tagIds?: number[]
  publishType?: number
  scheduledPublishTime?: string | null
}

const unwrap = (res: any) => res?.data ?? res

const normalizeArticle = (item: any) => {
  if (!item) return item
  return {
    ...item,
    coverImage: item.coverImage ?? item.cover_image,
    authorId: item.authorId ?? item.author_id,
    authorName: item.authorName ?? item.author_name,
    folderId: item.folderId ?? item.folder_id,
    viewsCount: item.viewsCount ?? item.views_count ?? 0,
    likesCount: item.likesCount ?? item.likes_count ?? 0,
    commentsCount: item.commentsCount ?? item.comments_count ?? 0,
    favoriteCount: item.favoriteCount ?? item.favorite_count ?? 0,
    followersCount: item.followersCount ?? item.followers_count ?? 0,
    createdAt: item.createdAt ?? item.created_at,
    updatedAt: item.updatedAt ?? item.updated_at,
    publishedAt: item.publishedAt ?? item.published_at,
    tagIds: item.tagIds ?? item.tag_ids ?? [],
  }
}

const normalizeFolder = (item: any): ArticleFolder => ({
  ...item,
  userId: item.userId ?? item.user_id,
  parentId: item.parentId ?? item.parent_id ?? null,
  folderName: item.folderName ?? item.folder_name,
  createdAt: item.createdAt ?? item.created_at,
  updatedAt: item.updatedAt ?? item.updated_at,
})

const toArticleRequest = (data: ArticlePayload) => ({
  title: data.title,
  content: data.content,
  summary: data.summary,
  cover_image: data.coverImage,
  folder_id: data.folderId ?? null,
  tag_ids: data.tagIds || [],
  publish_type: data.publishType,
  scheduled_publish_time: data.scheduledPublishTime,
})

export const articleApi = {
  list: async (params: { status?: number; folderId?: number; keyword?: string; sort?: string; page?: number; size?: number }) => {
    const res = await request.get('/article/list', { params }) as any
    const data = unwrap(res) || {}
    return {
      total: data.total || 0,
      records: (data.records || []).map(normalizeArticle),
    }
  },
  detail: async (id: number) => normalizeArticle(unwrap(await request.get(`/article/${id}`))),
  create: (data: ArticlePayload) => request.post('/article', toArticleRequest(data)),
  update: (id: number, data: ArticlePayload) => request.post(`/article/${id}`, toArticleRequest(data)),
  delete: (id: number) => request.post(`/article/${id}/delete`),
  publish: (id: number) => request.post(`/article/${id}/publish`),
  schedule: (id: number, publishTime: string) => request.post(`/article/${id}/schedule`, null, { params: { publishTime } }),
  cancelSchedule: (id: number) => request.post(`/article/${id}/cancel-schedule`),
  folders: async () => (unwrap(await request.get('/article/folder/list')) || []).map(normalizeFolder),
  createFolder: (data: { folderName: string; parentId?: number | null }) =>
    request.post('/article/folder', { folder_name: data.folderName, parent_id: data.parentId ?? null }),
  updateFolder: (id: number, data: { folderName: string; parentId?: number | null }) =>
    request.post(`/article/folder/${id}`, { folder_name: data.folderName, parent_id: data.parentId ?? null }),
  deleteFolder: (id: number) => request.post(`/article/folder/${id}/delete`),
  tags: async () => unwrap(await request.get('/tag/list', { params: { type: 'article' } })) || [],
}
