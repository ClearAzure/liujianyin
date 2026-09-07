import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'
import axios from 'axios'

// localStorage 持久化 key，刷新/重启后恢复上一次播放的音乐
const STORAGE_KEY = 'player-state'

// 从 localStorage 恢复上次会话的播放状态（失败则返回空对象）
function loadState() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY)) || {}
  } catch {
    return {}
  }
}

export const usePlayerStore = defineStore('player', () => {
  const saved = loadState()

  const currentMusic = ref(saved.currentMusic || null)// 当前播放的音乐对象（从上次会话恢复）
  const isPlaying = ref(false)// 当前是否正在播放（不持久化，刷新后默认暂停）

  const currentTime = ref(0)// 当前播放的时间，单位秒
  const duration = ref(0)// 当前播放的音乐总时长，单位秒
  const volume = ref(saved.volume ?? 80)// 当前音量，范围 0-100

  const playMode = ref(saved.playMode || 'sequence') // sequence | random | loop 播放顺序

  const playList = ref(saved.playList || [])// 播放列表，存储音乐对象的数组
  const currentIndex = ref(saved.currentIndex ?? -1)// 当前播放的音乐在播放列表中的索引，-1 表示没有音乐在播放

  const showDetailPanel = ref(false)

  // 桌面歌词：当前歌曲的 LRC 文本 + 缓存 + 窗口开关状态
  const lrcText = ref('')
  const lrcCache = new Map()
  const lyricVisible = ref(false) // 桌面歌词窗口是否打开（用于按钮高亮）

  const hasCurrent = computed(() => !!currentMusic.value)// 是否有当前播放的音乐
  const hasNext = computed(() => {
    if (playMode.value === 'loop') return true// 循环模式下总是有下一首音乐(自己)
    return currentIndex.value < playList.value.length - 1// 非循环模式下，判断当前索引是否小于播放列表长度减一返回布尔值
  })// 是否有下一首音乐
  const hasPrev = computed(() => currentIndex.value > 0)// 是否有上一首音乐

  // 播放状态改变时写入 localStorage，保证刷新/重启后底部栏仍是上次的音乐
  watch(
    () => ({
      currentMusic: currentMusic.value,
      volume: volume.value,
      playMode: playMode.value,
      playList: playList.value,
      currentIndex: currentIndex.value,
    }),
    (state) => {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(state))
    },
    { deep: true }
  )



  // 播放音乐的函数，接受一个音乐对象作为参数
  function play(music) {
    // 查找是否已在列表中
    const idx = playList.value.findIndex(m => m.id === music.id)

    if (idx >= 0) {
      currentIndex.value = idx// 如果已在列表中，直接设置当前索引为该音乐的索引
    } else {
      playList.value.push(music)// 如果不在列表中，将音乐添加到播放列表，并设置当前索引为最后一个(放到最后)
      currentIndex.value = playList.value.length - 1
    }
    
    currentMusic.value = music// 设置当前播放的音乐为传入的音乐对象
    isPlaying.value = true// 设置播放状态为正在播放
  }

  // 播放整个列表的函数，接收一个音乐对象数组和一个可选的起始索引作为参数
  function playListAll(list, startIndex = 0) {
    playList.value = [...list]
    currentIndex.value = startIndex
    if (list.length > 0) {
      currentMusic.value = list[startIndex]
      isPlaying.value = true
    }
  }


  // 切换播放状态的函数，如果当前正在播放则暂停，反之则继续播放
  function togglePlay() {
    isPlaying.value = !isPlaying.value
  }

  // 暂停播放的函数
  function pause() {
    isPlaying.value = false
  }

  // 继续播放的函数
  function resume() {
    if (currentMusic.value) {
      isPlaying.value = true
    }
  }

  // 播放下一首音乐的函数
  function next() {
    if (playList.value.length === 0) return
    let nextIdx
    if (playMode.value === 'random') {
      nextIdx = Math.floor(Math.random() * playList.value.length)
    } else if (playMode.value === 'loop') {
      nextIdx = currentIndex.value
    } else {
      nextIdx = currentIndex.value + 1
      if (nextIdx >= playList.value.length) nextIdx = 0
    }
    currentIndex.value = nextIdx
    currentMusic.value = playList.value[nextIdx]
    isPlaying.value = true
  }

  // 播放上一首音乐的函数
  function prev() {
    if (playList.value.length === 0) return
    let prevIdx = currentIndex.value - 1
    if (prevIdx < 0) prevIdx = playList.value.length - 1
    currentIndex.value = prevIdx
    currentMusic.value = playList.value[prevIdx]
    isPlaying.value = true
  }

  // 设置音量的函数，接受一个音量值作为参数，并将其限制在 0 到 100 之间(巧妙~)
  function setVolume(v) {
    volume.value = Math.max(0, Math.min(100, v))
  }

  // 切换播放模式的函数，按顺序在顺序播放、随机播放和循环播放之间切换
  function toggleMode() {
    const modes = ['sequence', 'random', 'loop']
    const idx = modes.indexOf(playMode.value)
    playMode.value = modes[(idx + 1) % 3]
  }

  // 从播放列表中移除音乐的函数，接受一个索引作为参数
  function removeFromList(index) {
    playList.value.splice(index, 1)// 从播放列表中移除指定索引的音乐
    if (index < currentIndex.value) {
      currentIndex.value--
    } else if (index === currentIndex.value) {
      if (playList.value.length === 0) {
        currentMusic.value = null
        currentIndex.value = -1
        isPlaying.value = false
      } else {
        currentMusic.value = playList.value[Math.min(currentIndex.value, playList.value.length - 1)]
      }
    }
  }

  // 切换详情面板显示状态的函数
  function toggleDetailPanel() {
    showDetailPanel.value = !showDetailPanel.value
  }

  // 把当前歌词和播放进度同步给桌面歌词窗口（若已打开）
  function syncLyric() {
    window.electron?.sendLyricSync?.({
      lrcText: lrcText.value,
      currentTime: currentTime.value,
    })
  }

  // 加载当前歌曲的歌词（带缓存），加载完立即同步到桌面歌词窗口
  async function loadLyric() {
    const url = currentMusic.value?.lyricUrl
    if (!url) {
      lrcText.value = ''
      syncLyric()
      return
    }
    if (lrcCache.has(url)) {
      lrcText.value = lrcCache.get(url)
      syncLyric()
      return
    }
    try {
      const { data } = await axios.get(url, { timeout: 5000 })
      lrcCache.set(url, data)
      lrcText.value = data
    } catch {
      lrcText.value = ''
    }
    syncLyric()
  }

  // 打开/关闭桌面歌词窗口（仅 Electron 环境有效）
  function openLyric() {
    lyricVisible.value = true
    window.electron?.openLyric?.()
    // 窗口加载需要一点时间，延迟推送一次当前状态，避免刚打开是空的
    setTimeout(syncLyric, 300)
  }

  function closeLyric() {
    lyricVisible.value = false
    window.electron?.closeLyric?.()
  }

  // 点击按钮开关桌面歌词
  function toggleLyric() {
    if (lyricVisible.value) closeLyric()
    else openLyric()
  }

  // 歌词窗口被外部关闭（如 Alt+F4）时同步状态，避免按钮高亮卡住
  window.electron?.onLyricClosed?.(() => {
    lyricVisible.value = false
  })

  // 切歌时自动加载歌词；播放进度变化时同步到桌面歌词窗口
  watch(currentMusic, loadLyric, { immediate: true })
  watch(currentTime, syncLyric)

  // 返回状态和方法，使它们可以在组件中使用
  return {
    currentMusic, isPlaying, currentTime, duration, volume, playMode,
    playList, currentIndex, showDetailPanel, lrcText, lyricVisible,
    hasCurrent, hasNext, hasPrev,
    play, playListAll, togglePlay, pause, resume, next, prev,
    setVolume, toggleMode, removeFromList, toggleDetailPanel,
    openLyric, closeLyric, toggleLyric
  }
})
