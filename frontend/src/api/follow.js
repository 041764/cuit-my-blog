import request from '@/utils/request'

export function followUser(userId) {
  return request({
    url: `/follows/${userId}`,
    method: 'post'
  })
}

export function unfollowUser(userId) {
  return request({
    url: `/follows/${userId}`,
    method: 'delete'
  })
}

export function getFollowStatus(userId) {
  return request({
    url: `/follows/${userId}/status`,
    method: 'get'
  })
}

export function getMyFollowing(params) {
  return request({
    url: '/follows/my/following',
    method: 'get',
    params
  })
}
