import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as userAPI from '../api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  // 从 localStorage 恢复用户信息，避免刷新后头像/名字丢失
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  // 登录/注册弹窗的显隐状态（导航守卫需要打开它）
  const showLogin = ref(false)

  const isLogin = computed(() => !!token.value)

  async function login(username, password) {
    const data = await userAPI.login(username, password)

    token.value = data.token
    userInfo.value = data.userInfo
    localStorage.setItem('token', data.token)
    localStorage.setItem('userInfo', JSON.stringify(data.userInfo))

    return data
  }

  async function register(username, password, email) {
    await userAPI.register(username, password, email)
  }

  async function fetchUserInfo() {
    if (!token.value) return

    // 调 /api/user/info 向服务端校验 token 并刷新用户信息；
    // token 失效时后端返回 401，request.js 拦截器会自动 logout()
    try {
      userInfo.value = await userAPI.getUserInfo()
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    } catch {
      logout()
    }
  }

  // 更新昵称/头像等资料，返回更新后的 UserVO 并同步到本地
  async function update(data) {
    const updated = await userAPI.update(data)
    userInfo.value = updated
    localStorage.setItem('userInfo', JSON.stringify(updated))
    return updated
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  function openLogin() {
    showLogin.value = true
  }

  function closeLogin() {
    showLogin.value = false
  }

  return { token, userInfo, isLogin, showLogin, login, register, fetchUserInfo, update, logout, openLogin, closeLogin }
})
