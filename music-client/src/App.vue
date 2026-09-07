<template>
  <!-- 桌面歌词窗口：只渲染歌词本身，不带主界面 Header/Sidebar/Player 等 -->
  <router-view v-if="isLyricWindow" />

  <div v-else id="app-layout">
    <Header />
    <div class="main-body">
      <Sidebar />
      <div class="content">
        <router-view />
      </div>
    </div>
    <Player />
    <MusicDetail />
  </div>
</template>

<script setup>
import { onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import Header from './components/Header.vue'
import Sidebar from './components/Sidebar.vue'
import Player from './components/Player.vue'
import MusicDetail from './components/MusicDetail.vue'
import { useUserStore } from './stores/userStore'

const route = useRoute()
const userStore = useUserStore()

// 桌面歌词是独立的 Electron 窗口，只渲染歌词，不带主界面布局
const isLyricWindow = computed(() => route.name === 'DesktopLyric')

// 启动时若有 token，向服务端校验并刷新用户信息（歌词窗口不需要）
onMounted(() => {
  if (!isLyricWindow.value) {
    userStore.fetchUserInfo()
  }
})
</script>

<!-- ┌──────────────────────────────────────────────┐
│                  Header                      │
│              顶部导航栏 / 标题栏              │
├──────────────┬───────────────────────────────┤
│              │                               │
│   Sidebar    │          router-view          │
│   左侧菜单    │          页面内容              │
│              │                               │
│              │                               │
├──────────────┴───────────────────────────────┤
│                  Player                       │
│                 播放器栏                      │
└───────────────────────────────────────────────┘

             MusicDetail
             音乐详情/歌词等 -->