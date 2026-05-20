import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'
import router from '@/router'

/**
 * 用户状态仓库
 */
export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  // 登录
  async function login(username, password) {
    const res = await request.post('/user/login', { username, password })
    if (res.code === 200) {
      token.value = res.data.token
      userInfo.value = res.data
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('userInfo', JSON.stringify(res.data))
      router.push('/')
    } else {
      throw new Error(res.message)
    }
  }

  // 注册（邮箱验证码）
  async function register(username, password, phone, email, code) {
    const res = await request.post('/user/register', { username, password, phone, email, code })
    if (res.code === 200) {
      router.push('/login')
    } else {
      throw new Error(res.message)
    }
  }

  // 退出
  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/login')
  }

  return { token, userInfo, login, register, logout }
})
