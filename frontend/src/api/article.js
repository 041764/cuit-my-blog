import request from '@/utils/request'

export function getArticles(params) {
  return request({
    url: '/articles',
    method: 'get',
    params
  })
}

export function getArticleById(id) {
  return request({
    url: `/articles/${id}`,
    method: 'get'
  })
}

export function createArticle(data) {
  return request({
    url: '/articles',
    method: 'post',
    data
  })
}

export function updateArticle(id, data) {
  return request({
    url: `/articles/${id}`,
    method: 'put',
    data
  })
}

export function deleteArticle(id) {
  return request({
    url: `/articles/${id}`,
    method: 'delete'
  })
}

export function getArticlesByCategory(categoryId, params = {}) {
  return request({
    url: `/articles/category/${categoryId}`,
    method: 'get',
    params
  })
}
