<template>
  <div class="page-artists">
    <div class="section">
      <h2>歌手</h2>
      <div v-if="!artists.length" class="empty">暂无歌手</div>
      <div v-else class="card-grid">
        <PlaylistCard
          v-for="a in artists"
          :key="a.id"
          :name="a.name"
          :cover-url="a.avatarUrl"
          :count="a.musicCount"
          icon="mdi:account-music"
          @click="$router.push(`/artist/${a.id}`)"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import PlaylistCard from '../components/PlaylistCard.vue'
import * as artistAPI from '../api/artist'

const artists = ref([])

onMounted(async () => {
  try {
    artists.value = await artistAPI.list()
  } catch {
    artists.value = []
  }
})
</script>
