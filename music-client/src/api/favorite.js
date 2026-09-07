import request from '../utils/request'

export function add(musicId) {
  return request.post('/favorite/add', { musicId })
}

export function remove(musicId) {
  return request.delete(`/favorite/${musicId}`)
}

export function list() {
  return request.get('/favorite/list')
}
