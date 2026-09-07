import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as playlistAPI from '../api/playlist'

export const usePlaylistStore = defineStore('playlist', () => {
  //歌单列表
  const myPlaylists = ref([])

  //本质在调用封装api

// 获取我的歌单列表
  async function fetchMyPlaylists() {
    myPlaylists.value = await playlistAPI.getMyPlaylists()
    return myPlaylists.value
  }
// 创建歌单（返回新建的歌单对象，便于后续直接添加歌曲）
  async function create(name) {
    const playlist = await playlistAPI.create(name)
    await fetchMyPlaylists()
    return playlist
  }
// 获取歌单详情
  async function getDetail(id) {
    return await playlistAPI.getDetail(id)
  }
// 添加音乐到歌单
  async function addMusic(playlistId, musicId) {
    await playlistAPI.addMusic(playlistId, musicId)
  }
// 更新歌单（改名/换封面）
  async function update(id, data) {
    await playlistAPI.update(id, data)
    await fetchMyPlaylists()
  }
// 从歌单移除歌曲
  async function removeMusic(playlistId, musicId) {
    await playlistAPI.removeMusic(playlistId, musicId)
  }

  return { myPlaylists, fetchMyPlaylists, create, getDetail, addMusic, update, removeMusic }
})
