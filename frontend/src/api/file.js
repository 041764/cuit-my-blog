import request from '@/utils/request'

export function uploadFile(data, type = 'content') {
  return request({
    url: '/files/upload',
    method: 'post',
    params: { type },
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data
  })
}
