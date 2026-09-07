<template>
  <footer class="player-bar">
    <!-- 左侧：歌曲信息 -->
    <div class="player-left" @click="playerStore.toggleDetailPanel()">
      <img :src="playerStore.currentMusic?.coverUrl || defaultCover" class="player-cover" alt="cover" />
      <div class="player-info">
        <div class="player-name">{{ playerStore.currentMusic?.name || '未在播放' }}</div>
        <div class="player-artist">{{ playerStore.currentMusic?.artistName || '--' }}</div>
      </div>
      <el-button circle size="small" :type="isFavorited ? 'danger' : 'default'" @click.stop="toggleFavorite" title="收藏">
        <Icon icon="mdi:heart" />
      </el-button>
    </div>

    <!-- 中间：播放控制 -->
    <div class="player-center">
      <div class="player-controls">
        <el-button circle @click="playerStore.prev()" :disabled="!playerStore.hasPrev" title="上一首">
          <Icon icon="mdi:skip-previous" />
        </el-button>

        <el-button type="danger" circle size="large" class="btn-play" @click="playerStore.togglePlay()"
          :title="playerStore.isPlaying ? '暂停' : '播放'">
          <Icon :icon="playerStore.isPlaying ? 'mdi:pause' : 'mdi:play'" />
        </el-button>

        <el-button circle @click="playerStore.next()" :disabled="!playerStore.hasNext" title="下一首">
          <Icon icon="mdi:skip-next" />
        </el-button>

        <el-button circle size="small" @click="playerStore.toggleMode()" :title="modeLabel" class="btn-mode">
          <Icon :icon="modeIcon" />
        </el-button>
      </div>
      <div class="player-progress">
        <span class="time">{{ formatTime(playerStore.currentTime) }}</span>

        <input type="range" class="progress-bar" :value="playerStore.currentTime" :max="playerStore.duration || 0"
          @input="seek" />

        <span class="time">{{ formatTime(playerStore.duration) }}</span>
      </div>
    </div>

    <!-- 右侧：音量和播放列表 -->
    <div class="player-right">
      <el-button  size="middle" @click="playerStore.openLyric?.()" title="桌面歌词">
        桌面歌词
      </el-button>

      <div class="volume-box">
        <Icon icon="mdi:volume-high" class="volume-icon" />
        <input type="range" class="volume-bar" :value="playerStore.volume"
          @input="playerStore.setVolume($event.target.value)" min="0" max="100" />
      </div>
      <el-button circle size="small" @click="showList = !showList" title="播放列表">
        <Icon icon="mdi:playlist-music" />
      </el-button>
    </div>

    <!-- 隐藏的 Audio 元素 -->
    <audio ref="audioEl" :src="audioSrc" preload="auto"
    @timeupdate="onTimeUpdate"
    @loadedmetadata="onLoaded" 
    @ended="onEnded"
    @play="playerStore.isPlaying = true" 
    @pause="playerStore.isPlaying = false" 
    />

    <!-- 播放列表弹窗 -->
    <div v-if="showList" class="playlist-popup" @click.self="showList = false">
      <div class="playlist-content">
        <h4>播放列表 ({{ playerStore.playList.length }})</h4>
        <ul>
          <li v-for="(m, i) in playerStore.playList" :key="i" :class="{ active: i === playerStore.currentIndex }"
            @click="playerStore.play(m)">
            <span>{{ m.name }} - {{ m.artistName }}</span>
            <button class="btn-del" @click.stop="playerStore.removeFromList(i)">
              <!-- @click.stop  -->
              <Icon icon="mdi:close" />
            </button>
          </li>
        </ul>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { usePlayerStore } from '../stores/playerStore'
import * as historyAPI from '../api/history'

const playerStore = usePlayerStore()//播放状态数据管理
const audioEl = ref(null)//audio 元素的引用 , ref 是 Vue 3 中的响应式引用，用于在模板中绑定和操作 DOM 元素
const showList = ref(false)//播放列表弹窗的显示状态
const isFavorited = ref(false)//当前音乐是否被收藏的状态

// 计算属性，获取当前播放音乐的音频源 URL，如果没有音乐在播放，则返回空字符串
const defaultCover = 'data:image/svg+xml,' + encodeURIComponent('<svg xmlns="http://www.w3.org/2000/svg" width="50" height="50"><rect fill="#333" width="50" height="50"/><text fill="#666" x="15" y="32" font-size="16">♪</text></svg>')
// 计算属性，获取当前播放音乐的音频源 URL，如果没有音乐在播放，则返回空字符串
const audioSrc = computed(() => playerStore.currentMusic?.musicUrl || '')
// 计算属性，获取当前播放模式的图标和标签，用于在界面上显示当前的播放模式
const modeIcon = computed(() => {
  return { sequence: 'mdi:repeat', random: 'mdi:shuffle-variant', loop: 'mdi:repeat-once' }[playerStore.playMode] || 'mdi:repeat'
})
const modeLabel = computed(() => {
  return { sequence: '顺序播放', random: '随机播放', loop: '单曲循环' }[playerStore.playMode] || ''
})

// 监听播放状态的变化，根据 isPlaying 的值来控制 audio 元素的播放和暂停
// watch 是“把 Pinia 的状态同步给 audio”，@play/@pause 是“把 audio 的真实状态同步回 Pinia”。
// flush: 'post' 保证切歌时 src 已更新到 DOM 后再 play，避免播放旧源
watch(() => playerStore.isPlaying, (playing) => {
  const el = audioEl.value
  if (!el) return
  if (playing) {
    el.play().catch((err) => {
      // play() 失败（源未就绪/被中断）时不能静默吞掉，否则按钮卡在“暂停”却没声音
      console.warn('[player] 播放失败:', err)
      playerStore.isPlaying = false
    })
  } else {
    el.pause()
  }
}, { flush: 'post' })

// 监听当前播放音乐的变化：记录历史，并在播放态下自动接着播新歌
// flush: 'post' 保证 :src 已更新到 DOM 后再 play，避免播放旧源
// 关键：切歌时 isPlaying 不变（仍是 true），watch(isPlaying) 不会触发，必须在这里手动 play
watch(() => playerStore.currentMusic, (music) => {
  if (!music) return
  recordHistory(music.id)
  const el = audioEl.value
  if (!el) return
  if (playerStore.isPlaying) {
    el.play().catch((err) => {
      console.warn('[player] 播放失败:', err)
      playerStore.isPlaying = false
    })
  }
}, { flush: 'post' })

// 监听音量的变化，根据 volume 的值来设置 audio 元素的音量
watch(() => playerStore.volume, (v) => {
  if (audioEl.value) audioEl.value.volume = v / 100
})

// 组件挂载后，若已从 localStorage 恢复了上次播放的歌曲，显式 load 一次，
// 确保音频源开始预加载，刷新后第一次点播放就不用等冷下载
onMounted(() => {
  if (playerStore.currentMusic?.musicUrl) {
    audioEl.value?.load()
  }
})

// 监听音频源的变化，当音频源发生变化时，重置当前播放时间和总时长
function onTimeUpdate() {
  if (audioEl.value) {
    playerStore.currentTime = audioEl.value.currentTime
  }
}

// 监听音频元数据加载完成的事件，当音频元数据加载完成时，获取总时长并更新到 playerStore 中
function onLoaded() {
  if (audioEl.value) {
    playerStore.duration = audioEl.value.duration
  }
}

// 监听音频播放结束的事件，当音频播放结束时，自动播放下一首音乐
function onEnded() {
  playerStore.next()
}

// 用户拖动进度条时，更新 audio 元素的当前播放时间
function seek(e) {
  if (audioEl.value) {
    audioEl.value.currentTime = Number(e.target.value)
  }
}

// 收藏
function toggleFavorite() {
  // TODO: 对接后端
}

// 记录播放历史
function recordHistory(musicId) {
  historyAPI.add(musicId).catch(() => { })
}

// 格式化时间，将秒数转换为 mm:ss 的格式
function formatTime(sec) {
  if (!sec || isNaN(sec)) return '00:00'
  const m = Math.floor(sec / 60)
  const s = Math.floor(sec % 60)
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}
</script>

<!-- playerStore.js

负责：

现在播放哪首？
是否正在播放？
播放到几秒？
音量多少？
播放模式？
播放列表？

Player.vue
负责：

真正操作 <audio>
真正播放 MP3
真正暂停
获取当前播放时间
获取歌曲总时长
监听歌曲播放结束
拖动进度条 -->

<!-- 用户拖动音量
      ↓
playerStore.setVolume()
      ↓
volume 改变
      ↓
watch 检测到
      ↓
audioEl.volume = v / 100
      ↓
真正的音量改变

而播放：
用户点击播放
      ↓
playerStore.togglePlay()
      ↓
isPlaying 改变
      ↓
watch 检测到
      ↓
audioEl.play()
      ↓
真正播放

进度：
audio自己播放
      ↓
触发 timeupdate
      ↓
onTimeUpdate()
      ↓
playerStore.currentTime
      ↓
Vue重新渲染
      ↓
进度条移动

这就是一个非常典型的：

状态 ↔ 实际 DOM/浏览器 API 双向同步 

vue数据流
-->

<!--                     Pinia
              playerStore
                    │
       ┌────────────┼────────────┐
       ↓            ↓            ↓
 currentMusic   isPlaying     volume
       │            │            │
       │            │            │
       ↓            ↓            ↓
    audioSrc       watch        watch
       │            │            │
       └────────────┼────────────┘
                    ↓
               <audio>
                    │
             真正播放 MP3
                    │
        ┌───────────┼───────────┐
        ↓           ↓           ↓
   timeupdate    ended       metadata
        ↓           ↓           ↓
 currentTime      next()     duration -->