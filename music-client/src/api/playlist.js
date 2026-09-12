import request from '../utils/request'

// 创建歌单（可带封面/简介）
export function create(data) {
  return request.post('/playlist/create', data)
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
// 更新歌单（改名/换封面）
export function update(id, data) {
  return request.put(`/playlist/${id}`, data)
}
// 从歌单移除歌曲
export function removeMusic(playlistId, musicId) {
  return request.delete(`/playlist/${playlistId}/music/${musicId}`)
}
