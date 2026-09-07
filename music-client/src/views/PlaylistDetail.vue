<template>
  <div class="page-playlist-detail">
    <h2 v-if="playlist"><Icon icon="mdi:playlist-music" /> {{ playlist.name }}</h2>
    <MusicList v-if="playlist" :songs="playlist.songs || []" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { usePlaylistStore } from '../stores/playlistStore'
import MusicList from '../components/MusicList.vue'

const route = useRoute()
const playlistStore = usePlaylistStore()
const playlist = ref(null)

onMounted(async () => {
  playlist.value = await playlistStore.getDetail(route.params.id)
})
</script>
