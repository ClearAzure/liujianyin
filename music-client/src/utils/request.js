import axios from 'axios'
import { useUserStore } from '../stores/userStore'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000//一个请求如果 30 秒还没有得到响应，就认为超时。
})

//所有通过 request 发出去的请求，在真正发送之前，都会经过请求拦截器
request.interceptors.request.use(config => {
  const userStore = useUserStore()
  if (userStore.token) {
    config.headers.Authorization = `Bearer ${userStore.token}`
  }
  return config
}, error => Promise.reject(error))

//所有通过 request 发出去的请求，在得到响应之后，都会经过响应拦截器
request.interceptors.response.use(
  response => {
    const data = response.data
    if (data.code === 200) {
      return data.data
    }
    return Promise.reject(new Error(data.message || '请求失败'))
  },
  error => {
    if (error.response?.status === 401) {
      const userStore = useUserStore()
      userStore.logout()
    }
    return Promise.reject(error)
  }
)

export default request
