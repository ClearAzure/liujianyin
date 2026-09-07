import request from '../utils/request'

export function add(musicId) {
  return request.post('/history/add', { musicId })
}

export function list() {
  return request.get('/history/list')
}
