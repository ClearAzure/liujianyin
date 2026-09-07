<template>
  <div class="page-favorite">
    <h2><Icon icon="mdi:heart" /> 我喜欢的音乐</h2>
    <MusicList :songs="songs" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import MusicList from '../components/MusicList.vue'
import * as favoriteAPI from '../api/favorite'

const songs = ref([])

onMounted(async () => {
  try {
    songs.value = await favoriteAPI.list()
  } catch {
    songs.value = []
  }
})
</script>
