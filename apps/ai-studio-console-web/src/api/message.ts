import request from '@/utils/request'

export const messageApi = {
  summary: () => request.get('/message/summary'),
  notifications: (params: any) => request.get('/message/notifications', { params }),
  markNotificationRead: (id: number) => request.post(`/message/notifications/${id}/read`),
  markAllNotificationsRead: () => request.post('/message/notifications/read-all'),
  announcements: () => request.get('/message/announcements'),
  announcementDetail: (id: number) => request.get(`/message/announcements/${id}`),
  markAnnouncementRead: (id: number) => request.post(`/message/announcements/${id}/read`),
}
