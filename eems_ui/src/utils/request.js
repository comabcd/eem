import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useUserStore } from '@/store/user'

// 创建axios实例
const service = axios.create({
  baseURL: '/api',
  timeout: 5000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    
    // 如果有token就添加到请求头
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果响应成功
    if (res.code === 200) {
      return res
    }
    
    // 处理业务错误
    ElMessage.error(res.message || '系统错误')
    
    // 如果是token相关错误，清除登录状态
    if (res.code === 401) {
      handleLogout()
    }
    
    return Promise.reject(new Error(res.message || '系统错误'))
  },
  error => {
    // 处理HTTP错误
    if (error.response) {
      const { status, data } = error.response
      
      // 只处理401错误
      if (status === 401) {
        // 避免重复提示
        if (!router.currentRoute.value.path.includes('/login')) {
          ElMessage.error(data?.message || '登录已过期，请重新登录')
        }
        handleLogout()
      } else {
        ElMessage.error(error.message || `请求失败(${status})`)
      }
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

// 统一的登出处理
const handleLogout = () => {
  const userStore = useUserStore()
  userStore.logout()
  
  // 如果不在登录页，才跳转
  if (!router.currentRoute.value.path.includes('/login')) {
    router.push('/login')
  }
}

export default service 