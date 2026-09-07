import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as musicAPI from '../api/music'

export const useMusicStore = defineStore('music', () => {
  const searchResult = ref([])
  const recommendList = ref([])

  async function search(keyword) {
    searchResult.value = await musicAPI.search(keyword)
    return searchResult.value
  }

  async function fetchRecommend() {
    try {
      recommendList.value = await musicAPI.search('')
    } catch {
      recommendList.value = []
    }
  }

  return { searchResult, recommendList, search, fetchRecommend }
})
