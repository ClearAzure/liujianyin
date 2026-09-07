<template>
  <div class="desktop-lyric">
    <p class="dl-line">{{ prevLine }}</p>
    <p class="dl-line dl-current">{{ currentLine }}</p>
    <p class="dl-line">{{ nextLine }}</p>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { usePlayerStore } from '../stores/playerStore'
import { parseLRC, findLyricIndex } from '../utils/lrcParser'

const playerStore = usePlayerStore()
const lrcText = ref('')
const lyrics = computed(() => parseLRC(lrcText.value))
const currentIndex = computed(() => findLyricIndex(lyrics.value, playerStore.currentTime))

const prevLine = computed(() =>
  lyrics.value[currentIndex.value - 1]?.text || ''
)
const currentLine = computed(() =>
  lyrics.value[currentIndex.value]?.text || '琉涧音'
)
const nextLine = computed(() =>
  lyrics.value[currentIndex.value + 1]?.text || ''
)

function onLyricUpdate(data) {
  lrcText.value = data?.lrcText || ''
}

onMounted(() => {
  window.electron?.onLyricUpdate?.(onLyricUpdate)
})
onUnmounted(() => {
  // cleanup
})
</script>

<style scoped>
.desktop-lyric {
  text-align: center;
  padding: 10px 30px;
  background: transparent;
  user-select: none;
  -webkit-app-region: drag;
}
.dl-line {
  margin: 2px 0;
  font-size: 16px;
  color: rgba(255,255,255,0.4);
  transition: color 0.3s;
}
.dl-current {
  font-size: 22px;
  font-weight: bold;
  color: #fff;
  text-shadow: 0 0 8px rgba(255,255,255,0.5);
}
</style>
