import request from '../utils/request'

// 专辑列表
export function list() {
  return request.get('/album/list')
}

// 专辑详情（含歌曲列表）
export function getDetail(id) {
  return request.get(`/album/${id}`)
}

// 更新专辑（封面）
export function update(id, data) {
  return request.put(`/album/${id}`, data)
}
