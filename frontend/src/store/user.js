import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const userId = ref(localStorage.getItem('userId') || '')
  const role = ref(localStorage.getItem('role') || '')
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

  function setUser(data) {
    userId.value = data.userId
    role.value = data.role
    // Handle optional fields
    if (data.token) {
        token.value = data.token
        localStorage.setItem('token', data.token)
    }
    if (data.userInfo) {
        userInfo.value = data.userInfo
        localStorage.setItem('userInfo', JSON.stringify(data.userInfo))
    }
    
    localStorage.setItem('userId', data.userId)
    localStorage.setItem('role', data.role)
  }

  function updateUserInfo(partial) {
    const nextInfo = { ...userInfo.value, ...partial }
    userInfo.value = nextInfo
    localStorage.setItem('userInfo', JSON.stringify(nextInfo))
  }

  function logout() {
    userId.value = ''
    role.value = ''
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('userId')
    localStorage.removeItem('role')
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return { userId, role, token, userInfo, setUser, updateUserInfo, logout }
})
