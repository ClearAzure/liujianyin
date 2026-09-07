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
import { onMounted, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import Header from './components/Header.vue'
import Sidebar from './components/Sidebar.vue'
import Player from './components/Player.vue'
import MusicDetail from './components/MusicDetail.vue'
import { useUserStore } from './stores/userStore'
import { useFavoriteStore } from './stores/favoriteStore'

const route = useRoute()
const userStore = useUserStore()
const favoriteStore = useFavoriteStore()

// 桌面歌词是独立的 Electron 窗口，只渲染歌词，不带主界面布局
const isLyricWindow = computed(() => route.name === 'DesktopLyric')

// 启动时若有 token，向服务端校验并刷新用户信息（歌词窗口不需要）
onMounted(() => {
  if (!isLyricWindow.value) {
    userStore.fetchUserInfo()
    // 已登录则预加载收藏列表，让播放器/详情页的心形按钮能立即反映收藏状态
    if (userStore.isLogin) favoriteStore.fetchFavorites()
  }
})

// 登录后加载收藏列表，退出后清空，保证收藏状态跨页面一致
watch(() => userStore.isLogin, (login) => {
  if (login) favoriteStore.fetchFavorites()
  else favoriteStore.clear()
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