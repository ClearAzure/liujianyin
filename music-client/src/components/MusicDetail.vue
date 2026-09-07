<template>
  <teleport to="body">
    <transition name="slide-up">
      <div v-if="playerStore.showDetailPanel && playerStore.currentMusic" class="detail-overlay">
        <button class="back-btn" @click="closePanel">
          <Icon icon="mdi:chevron-down" /> 收起
        </button>

        <div class="detail-body">
          <!-- 左侧封面 -->
          <div class="detail-left">
            <div class="cover-wrapper">
              <img :src="playerStore.currentMusic.coverUrl || defaultCover" alt="cover" />
            </div>
            <div class="detail-actions">
              <el-button
                circle size="large"
                :type="isFav ? 'danger' : 'default'"
                @click="toggleFav"
                :title="isFav ? '取消收藏' : '收藏'"
              >
                <Icon :icon="isFav ? 'mdi:heart' : 'mdi:heart-outline'" />
              </el-button>
              <el-button circle size="large" @click="playerStore.prev()" title="上一首">
                <Icon icon="mdi:skip-previous" />
              </el-button>
              <el-button
                type="danger" circle
                class="btn-play-lg"
                @click="playerStore.togglePlay()"
                :title="playerStore.isPlaying ? '暂停' : '播放'"
              >
                <Icon :icon="playerStore.isPlaying ? 'mdi:pause' : 'mdi:play'" />
              </el-button>
              <el-button circle size="large" @click="playerStore.next()" title="下一首">
                <Icon icon="mdi:skip-next" />
              </el-button>
              <el-button circle size="large" @click="playerStore.toggleMode()" :title="modeLabel">
                <Icon :icon="modeIcon" />
              </el-button>
            </div>
          </div>

          <!-- 右侧信息+歌词 -->
          <div class="detail-right">
            <div class="song-info">
              <h1>{{ playerStore.currentMusic.name }}</h1>
              <p class="artist">{{ playerStore.currentMusic.artistName }}</p>
              <p class="album">专辑：{{ playerStore.currentMusic.albumName || '未知' }}</p>
              <p class="count">播放量：{{ playerStore.currentMusic.playCount || 0 }}</p>
            </div>
            <div class="lyric-area">
              <Lyric :lrcText="playerStore.lrcText" />
            </div>
          </div>
        </div>
      </div>
    </transition>
  </teleport>
</template>

<script setup>
import { computed } from 'vue'
import { usePlayerStore } from '../stores/playerStore.js'
import { useFavoriteStore } from '../stores/favoriteStore.js'
import { useUserStore } from '../stores/userStore.js'
import Lyric from '../components/Lyric.vue'

const playerStore = usePlayerStore()
const favoriteStore = useFavoriteStore()
const userStore = useUserStore()
const isFav = computed(() => favoriteStore.isFavorite(playerStore.currentMusic?.id))

const defaultCover = 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="300" height="300"><rect fill="#1a1a2e" width="300" height="300"/><text fill="#444" x="120" y="170" font-size="50">♪</text></svg>')

const modeIcon = computed(() => {
  return { sequence: 'mdi:repeat', random: 'mdi:shuffle-variant', loop: 'mdi:repeat-once' }[playerStore.playMode] || 'mdi:repeat'
})
const modeLabel = computed(() => {
  return { sequence: '顺序播放', random: '随机播放', loop: '单曲循环' }[playerStore.playMode] || ''
})

function closePanel() {
  playerStore.showDetailPanel = false
}

async function toggleFav() {
  if (!userStore.isLogin) {
    userStore.openLogin()
    return
  }
  const music = playerStore.currentMusic
  if (!music) return
  try {
    await favoriteStore.toggle(music)
  } catch { /* ignore */ }
}
</script>

<style>
.detail-overlay {
  position: fixed; inset: 0; z-index: 10000;
  background: linear-gradient(180deg, #1a1a2e 0%, #0f0f23 100%);
  display: flex; flex-direction: column;
}
.back-btn {
  position: absolute; top: 12px; left: 50%; transform: translateX(-50%); z-index: 10;
  display: inline-flex; align-items: center; gap: 4px;
  background: rgba(255,255,255,0.1); border: none; color: #fff;
  padding: 8px 28px; border-radius: 20px; cursor: pointer; font-size: 14px;
  user-select: none; -webkit-app-region: no-drag;
}
.back-btn:hover { background: rgba(255,255,255,0.2); }
.detail-body {
  flex: 1; display: flex; overflow: hidden; padding: 60px 40px 20px; gap: 48px;
}
.detail-left {
  width: 380px; flex-shrink: 0;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
}
.cover-wrapper {
  width: 320px; height: 320px; border-radius: 12px; overflow: hidden;
  box-shadow: 0 8px 40px rgba(0,0,0,0.5);
}
.cover-wrapper img { width: 100%; height: 100%; object-fit: cover; }
.detail-actions { display: flex; align-items: center; gap: 12px; margin-top: 32px; }
.btn-play-lg {
  width: 56px !important; height: 56px !important; font-size: 24px;
}
.detail-right { flex: 1; display: flex; flex-direction: column; overflow: hidden; }
.song-info { margin-bottom: 24px; }
.song-info h1 { font-size: 32px; margin-bottom: 8px; }
.song-info .artist { font-size: 18px; color: var(--text-secondary); margin-bottom: 4px; }
.song-info .album, .song-info .count { font-size: 13px; color: var(--text-muted); margin-bottom: 2px; }
.lyric-area { flex: 1; overflow-y: auto; }

.slide-up-enter-active { transition: transform 0.3s cubic-bezier(0.22, 0.61, 0.36, 1); }
.slide-up-leave-active { transition: transform 0.15s ease-in; }
.slide-up-enter-from { transform: translateY(100%); }
.slide-up-enter-to   { transform: translateY(0); }
.slide-up-leave-from  { transform: translateY(0); }
.slide-up-leave-to    { transform: translateY(100%); }
</style>
