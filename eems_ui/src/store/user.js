import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '../utils/request'

// 角色映射
const roleMap = {
  1: 'admin',        // 系统管理员
  2: 'dept_admin',   // 部门管理员
  3: 'hr_admin',     // 人力资源管理员
  4: 'employee'      // 普通员工
}

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const username = ref('')
  const role = ref(0)
  const userId = ref(0)
  const realName = ref('')

  // 登录
  const login = async (loginForm) => {
    try {
      const res = await request({
        url: '/auth/login',
        method: 'post',
        data: loginForm
      })
      
      // 先检查响应数据是否完整
      if (!res.data || !res.data.token) {
        throw new Error('登录响应数据不完整')
      }
      
      // 更新 store 中的状态
      token.value = res.data.token
      username.value = res.data.username
      role.value = res.data.role
      userId.value = res.data.id  // 使用 id 而不是 userId
      realName.value = res.data.realName

      // 保存到 localStorage
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('userRole', roleMap[res.data.role] || 'employee')
      localStorage.setItem('username', res.data.username)
      localStorage.setItem('userId', res.data.id)
      localStorage.setItem('realName', res.data.realName)

      return res
    } catch (error) {
      // 确保在登录失败时清除所有状态
      logout()
      throw error
    }
  }

  // 退出登录
  const logout = () => {
    token.value = ''
    username.value = ''
    role.value = 0
    userId.value = 0
    realName.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('userRole')
    localStorage.removeItem('username')
    localStorage.removeItem('userId')
    localStorage.removeItem('realName')
  }

  // 获取用户角色
  const getUserRole = () => {
    return roleMap[role.value] || 'employee'
  }

  return {
    token,
    username,
    role,
    userId,
    realName,
    login,
    logout,
    getUserRole
  }
}) 