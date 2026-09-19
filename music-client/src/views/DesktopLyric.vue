<template>
  <div class="desktop-lyric">
    <p class="dl-line">{{ prevLine }}</p>
    <p class="dl-line dl-current">{{ currentLine }}</p>
    <p class="dl-line">{{ nextLine }}</p>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { parseLRC, findLyricIndex } from '../utils/lrcParser'

// 歌词和进度都由主窗口通过 IPC 推送（本窗口是独立进程，不能用自己的 playerStore）
const lrcText = ref('')
const currentTime = ref(0)
const lyrics = computed(() => parseLRC(lrcText.value))
const currentIndex = computed(() => findLyricIndex(lyrics.value, currentTime.value))

const prevLine = computed(() => lyrics.value[currentIndex.value - 1]?.text || '')
const currentLine = computed(() => lyrics.value[currentIndex.value]?.text || '琉涧音')
const nextLine = computed(() => lyrics.value[currentIndex.value + 1]?.text || '')

function onLyricUpdate(data) {
  if (data?.lrcText !== undefined) lrcText.value = data.lrcText
  if (data?.currentTime !== undefined) currentTime.value = data.currentTime
}

onMounted(() => {
  // 先挂监听再报到，避免主窗口推回来的第一帧数据丢失
  window.electron?.onLyricUpdate?.(onLyricUpdate)
  // 告诉主窗口「我准备好了」，让它推一次当前歌词/进度（替代原来的 setTimeout 猜测）
  window.electron?.notifyLyricReady?.()
})

onUnmounted(() => {
  // cleanup
})
</script>

<style scoped>
.desktop-lyric {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  padding: 0 24px;
  background: transparent;
  user-select: none;
  -webkit-app-region: drag; /* 无边框窗口，可拖拽移动 */
  overflow: hidden;
}
.dl-line {
  margin: 3px 0;
  font-size: 16px;
  line-height: 1.4;
  color: rgba(255, 255, 255, 0.45);
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.8);
  white-space: nowrap;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: color 0.3s;
}
.dl-current {
  font-size: 22px;
  font-weight: 600;
  color: #fff;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.9);
}
</style>
