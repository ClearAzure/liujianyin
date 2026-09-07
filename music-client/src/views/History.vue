<template>
  <div class="page-history">
    <h2><Icon icon="mdi:history" /> 最近播放</h2>
    <MusicList :songs="songs" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import MusicList from '../components/MusicList.vue'
import * as historyAPI from '../api/history'

const songs = ref([])

onMounted(async () => {
  try {
    songs.value = await historyAPI.list()
  } catch {
    songs.value = []
  }
})
</script>
