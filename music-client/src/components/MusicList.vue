<template>
  <div class="music-list">
    <div class="list-header">
      <el-button type="danger" @click="playAll" round>
        <Icon icon="mdi:play" /> 播放全部
      </el-button>
    </div>
    <table class="list-table">
      <thead>
        <tr>
          <th class="col-op"></th>
          <th>歌曲</th>
          <th>歌手</th>
          <th>专辑</th>
          <th>时长</th>
          <th class="col-actions"></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(music, i) in songs" :key="music.id" @dblclick="playAt(i)" class="music-row">
          <td class="col-op">{{ i + 1 }}</td>
          <td>
            <span class="song-name" @click="$router.push(`/music/${music.id}`)">
              {{ music.name }}
            </span>
          </td>
          <td>{{ music.artistName }}</td>
          <td>{{ music.albumName }}</td>
          <td>{{ formatDuration(music.duration) }}</td>
          <td class="col-actions">
            <div class="row-actions">
              <slot name="actions" :music="music" />
              <SongMenu :music="music" :playlist-id="playlistId" @removed="emit('removed')" />
            </div>
          </td>
        </tr>
      </tbody>

    </table>

    <div v-if="!songs.length" class="empty">暂无歌曲</div>
  </div>
</template>

<script setup>
import { usePlayerStore } from '../stores/playerStore'
import SongMenu from './SongMenu.vue'

const props = defineProps({
  songs: { type: Array, default: () => [] },
  playlistId: { type: [Number, String], default: null }
})

const emit = defineEmits(['removed'])

const playerStore = usePlayerStore()

// 播放全部歌曲
function playAll() {
  const valid = props.songs.filter(s => s.musicUrl)
  if (valid.length) {
    playerStore.playListAll(valid, 0)
  }
}

// 双击播放某一首歌的功能
function playAt(index) {
  const song = props.songs[index]
  if (!song?.musicUrl) return
  playerStore.playListAll(props.songs, index)
}

// 格式化时长，秒数转为 mm:ss
function formatDuration(sec) {
  if (!sec) return '--:--'
  const m = Math.floor(sec / 60)
  const s = sec % 60
  return `${m}:${String(s).padStart(2, '0')}`
}

// 组件负责 UI 和用户操作，Store 负责跨组件共享的业务状态
// MusicList 并不负责“真正播放音乐”，它只是负责响应用户操作，然后把播放任务交给 playerStore
</script>

<style scoped>
.row-actions { display: flex; align-items: center; justify-content: flex-end; gap: 2px; }
</style>
