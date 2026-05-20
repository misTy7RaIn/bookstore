import axios from 'axios'

// Axios 请求封装
const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截器：携带 token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = token
  }
  return config
})

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return res
  },
  error => {
    if (error.response && error.response.status === 403) {
      alert('无权限，仅管理员可操作')
    }
    console.error('请求异常:', error)
    return Promise.reject(error)
  }
)

export default request
