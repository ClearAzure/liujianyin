import request from '../utils/request'

// 创建歌单
export function create(name) {
  return request.post('/playlist/create', { name })
}
// 获取我的歌单列表
export function getMyPlaylists() {
  return request.get('/playlist/my')
}
// 获取歌单
export function getDetail(id) {
  return request.get(`/playlist/${id}`)
}
// 添加音乐到歌单
export function addMusic(playlistId, musicId) {
  return request.post('/playlist/addMusic', { playlistId, musicId })
}
// 删除歌单
export function remove(id) {
  return request.delete(`/playlist/${id}`)
}
