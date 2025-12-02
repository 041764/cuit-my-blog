import axios from 'axios'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import router from '@/router'

const service = axios.create({
  baseURL: '/api',
  timeout: 5000
})

// Request interceptor
service.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    // If there is a token, usually it goes to Authorization header
    if (userStore.token) {
        config.headers['Authorization'] = `Bearer ${userStore.token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// Response interceptor
service.interceptors.response.use(
  response => {
    const res = response.data
    // Assuming 200 is success code from backend Result<T>
    if (res.code === 200) {
      return res.data
    }

    if (res.code === 401) {
      const userStore = useUserStore()
      userStore.logout()
      router.push('/login')
      ElMessage.error(res.message || '登录已过期，请重新登录')
    } else {
      ElMessage.error(res.message || 'Error')
    }

    return Promise.reject(new Error(res.message || 'Error'))
  },
  error => {
    console.error('err' + error)
    if (error.response && error.response.status === 401) {
      const userStore = useUserStore()
      userStore.logout()
      router.push('/login')
      ElMessage.error('Session expired, please login again')
    } else {
      ElMessage.error(error.message || 'Request Error')
    }
    return Promise.reject(error)
  }
)

export default service
