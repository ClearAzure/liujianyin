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
// 创建歌单
  async function create(name) {
    await playlistAPI.create(name)
    await fetchMyPlaylists()
  }
// 获取歌单详情
  async function getDetail(id) {
    return await playlistAPI.getDetail(id)
  }
// 添加音乐到歌单
  async function addMusic(playlistId, musicId) {
    await playlistAPI.addMusic(playlistId, musicId)
  }

  return { myPlaylists, fetchMyPlaylists, create, getDetail, addMusic }
})
