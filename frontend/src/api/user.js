import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/users/login',
    method: 'post',
    data
  })
}

export function register(data) {
  return request({
    url: '/users/register',
    method: 'post',
    data
  })
}

export function getUserProfile() {
  return request({
    url: '/users/profile',
    method: 'get'
  })
}

export function updateUserProfile(data) {
  return request({
    url: '/users/profile',
    method: 'put',
    data
  })
}

export function uploadAvatar(data) {
  return request({
    url: '/users/avatar',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data
  })
}

export function getUsers(params) {
  return request({
    url: '/users',
    method: 'get',
    params
  })
}

export function getUserById(userId) {
  return request({
    url: `/users/${userId}`,
    method: 'get'
  })
}

export function getUserArticles(userId, params) {
  return request({
    url: `/users/${userId}/articles`,
    method: 'get',
    params
  })
}
