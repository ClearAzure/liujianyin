import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as favoriteAPI from '../api/favorite'

// 收藏状态的唯一数据源：播放器心形按钮、详情页收藏按钮、收藏页列表都从这里读写，
// 保证任意一处操作后其余位置即时同步。
export const useFavoriteStore = defineStore('favorite', () => {
  const favoriteSongs = ref([]) // 收藏的歌曲列表（完整 MusicVO 对象，顺序与后端一致）
  const loaded = ref(false) // 是否已从后端加载过

  // 收藏的音乐 id 集合，用于 O(1) 判断某首歌是否已收藏
  const favoriteIds = computed(() => new Set(favoriteSongs.value.map(s => s.id)))

  function isFavorite(id) {
    return id != null && favoriteIds.value.has(id)
  }

  // 从后端拉取完整收藏列表（登录后/进入收藏页时调用）
  async function fetchFavorites() {
    try {
      favoriteSongs.value = await favoriteAPI.list()
    } catch {
      favoriteSongs.value = []
    } finally {
      loaded.value = true
    }
  }

  // 收藏一首歌（传入完整 music 对象，后端成功后本地插入到最前，与后端 DESC 顺序一致）
  async function add(music) {
    await favoriteAPI.add(music.id)
    if (!isFavorite(music.id)) {
      favoriteSongs.value = [music, ...favoriteSongs.value]
    }
  }

  // 取消收藏一首歌
  async function remove(musicId) {
    await favoriteAPI.remove(musicId)
    favoriteSongs.value = favoriteSongs.value.filter(s => s.id !== musicId)
  }

  // 切换收藏状态，返回切换后的状态（true = 已收藏）
  async function toggle(music) {
    if (isFavorite(music.id)) {
      await remove(music.id)
      return false
    }
    await add(music)
    return true
  }

  // 退出登录时清空，避免串号
  function clear() {
    favoriteSongs.value = []
    loaded.value = false
  }

  return {
    favoriteSongs, loaded, favoriteIds, isFavorite,
    fetchFavorites, add, remove, toggle, clear
  }
})
