import request from '../utils/request'

export function add(musicId) {
  return request.post('/favorite/add', { musicId })
}

export function remove(musicId) {
  return request.delete(`/favorite/${musicId}`)
}

// 收藏列表（完整 MusicVO 对象，顺序与后端一致）
export function list() {
  return request.get('/favorite/list')
}
