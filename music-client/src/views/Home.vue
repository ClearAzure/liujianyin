<template>
  <div class="page-home">
    <div class="section">
      <h2>推荐歌曲</h2>
      <div class="card-grid">
        <MusicCard v-for="m in songs" :key="m.id" :music="m" />
      </div>
    </div>
    <div class="section">
      <h2>热门推荐</h2>
      <MusicList :songs="songs" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import MusicCard from '../components/MusicCard.vue'
import MusicList from '../components/MusicList.vue'
import * as musicAPI from '../api/music'

const songs = ref([])

onMounted(async () => {
  try {
    // 首页用搜索空关键词或推荐接口获取数据
    songs.value = await musicAPI.search('')
    //songs是充满对象的数组，每个对象包含歌曲的详细信息，如id、name、artistName、albumName等基本信息和...资源链接和...其他信息
  } catch {
    songs.value = []
  }
})
</script>
