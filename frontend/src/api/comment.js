import request from '@/utils/request'

export function getCommentsByArticleId(articleId) {
  return request({
    url: `/comments/article/${articleId}`,
    method: 'get'
  })
}

export function createComment(data) {
  return request({
    url: '/comments',
    method: 'post',
    data
  })
}

export function deleteComment(id) {
  return request({
    url: `/comments/${id}`,
    method: 'delete'
  })
}
