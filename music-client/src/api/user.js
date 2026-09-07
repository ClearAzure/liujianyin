import request from '../utils/request'

export function login(username, password) {
  return request.post('/user/login', { username, password })
}

export function register(username, password, email) {
  return request.post('/user/register', { username, password, email })
}

export function getUserInfo() {
  return request.get('/user/info')
}

// 更新个人资料（昵称/头像），返回更新后的 UserVO
export function update(data) {
  return request.put('/user/info', data)
}
