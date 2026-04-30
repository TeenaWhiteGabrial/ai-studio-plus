import request from '@/utils/request'

export const authApi = {
  // 修改密码
  changePassword: (data: { oldPassword: string; newPassword: string }) => {
    console.log('changePassword 调用，数据：', data)
    return request.post('/auth/change-password', data, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
  },

  // 更新个人资料（邮箱、Git用户名、头像）
  updateProfile: (data: { email?: string; git_name?: string; avatar?: string }) => {
    console.log('updateProfile 调用，数据：', data)
    return request.post('/auth/update-profile', data, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
  },

  // 上传头像
  uploadAvatar: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/auth/avatar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 获取用户信息
  getUserInfo: () => request.get('/auth/user-info'),
}
