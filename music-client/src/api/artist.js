import request from '../utils/request'

// 歌手列表
export function list() {
  return request.get('/artist/list')
}

// 歌手详情（含歌曲列表）
export function getDetail(id) {
  return request.get(`/artist/${id}`)
}

// 更新歌手（头像）
export function update(id, data) {
  return request.put(`/artist/${id}`, data)
}
