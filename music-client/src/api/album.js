import request from '../utils/request'

// 专辑列表
export function list() {
  return request.get('/album/list')
}

// 专辑详情（含歌曲列表）
export function getDetail(id) {
  return request.get(`/album/${id}`)
}

// 更新专辑（名称/封面/简介）
export function update(id, data) {
  return request.put(`/album/${id}`, data)
}

// 删除专辑
export function remove(id) {
  return request.delete(`/album/${id}`)
}
