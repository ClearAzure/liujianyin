<template>
  <div class="music-card" @click="playIt" :title="`播放 ${music.name}`">
<!-- 音乐卡片封面 -->
    <div class="card-cover">
      <img :src="music.coverUrl || defaultCover" alt="cover" />
      <div class="card-play-btn">
        <Icon icon="mdi:play" />
      </div>
    </div>
<!-- 音乐卡片信息:歌曲名和作者 -->
    <div class="card-name">{{ music.name }}</div>
    <div class="card-artist">{{ music.artistName }}</div>
  </div>
</template>

<script setup>
import { usePlayerStore } from '../stores/playerStore'
import { useRouter } from 'vue-router'
//接受父组件传入的 music 对象
const props = defineProps({
  music: { type: Object, required: true }
})

const playerStore = usePlayerStore()
const router = useRouter()

const defaultCover = 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="120" height="120"><rect fill="#222" width="120" height="120"/><text fill="#555" x="40" y="68" font-size="28">♪</text></svg>')

function playIt() {
  playerStore.play(props.music)
}
</script>
