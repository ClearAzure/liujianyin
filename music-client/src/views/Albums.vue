<template>
  <div class="page-albums">
    <div class="section">
      <h2>专辑</h2>
      <div v-if="!albums.length" class="empty">暂无专辑</div>
      <div v-else class="card-grid">
        <PlaylistCard
          v-for="al in albums"
          :key="al.id"
          :name="al.name"
          :cover-url="al.coverUrl"
          :count="al.musicCount"
          icon="mdi:album"
          @click="$router.push(`/album/${al.id}`)"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import PlaylistCard from '../components/PlaylistCard.vue'
import * as albumAPI from '../api/album'

const albums = ref([])

onMounted(async () => {
  try {
    albums.value = await albumAPI.list()
  } catch {
    albums.value = []
  }
})
</script>
