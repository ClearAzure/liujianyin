import request from '../utils/request'

export function search(keyword) {
  return request.get('/music/search', { params: { keyword } })//keyword: keyword的语法糖
}// Axios 的 get() 方法本身需要你明确告诉它：哪些东西是 Query Parameter？

export function play(id) {
  return request.post(`/music/${id}/play`)
}

export function random() {
  return request.get('/music/random')
}

export function hot(page = 1, size = 20) {
  return request.get('/music/hot', { params: { page, size } })
}

// 删除歌曲（管理员）
export function remove(id) {
  return request.delete(`/music/${id}`)
}
