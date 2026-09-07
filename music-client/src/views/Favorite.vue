<template>
  <div class="page-favorite">
    <h2><Icon icon="mdi:heart" /> 我喜欢的音乐</h2>
    <MusicList :songs="favoriteStore.favoriteSongs">
      <template #actions="{ music }">
        <el-button link type="danger" size="small" @click="remove(music)">取消收藏</el-button>
      </template>
    </MusicList>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useFavoriteStore } from '../stores/favoriteStore'
import MusicList from '../components/MusicList.vue'

const favoriteStore = useFavoriteStore()

onMounted(() => {
  // 每次进入页面刷新一次，保证与后端一致
  favoriteStore.fetchFavorites()
})

async function remove(music) {
  try {
    await favoriteStore.remove(music.id)
  } catch { /* ignore */ }
}
</script>
