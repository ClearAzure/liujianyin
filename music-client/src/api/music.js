import request from '../utils/request'

export function search(keyword) {
  return request.get('/music/search', { params: { keyword } })//keyword: keyword的语法糖
}// Axios 的 get() 方法本身需要你明确告诉它：哪些东西是 Query Parameter？

export function getDetail(id) {
  return request.get(`/music/${id}`)
}

export function random() {
  return request.get('/music/random')
}
