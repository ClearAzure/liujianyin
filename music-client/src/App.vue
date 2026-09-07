<template>
  <div id="app-layout">

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
import { onMounted } from 'vue'
import Header from './components/Header.vue'
import Sidebar from './components/Sidebar.vue'
import Player from './components/Player.vue'
import MusicDetail from './components/MusicDetail.vue'
import { useUserStore } from './stores/userStore'

const userStore = useUserStore()

// 启动时若有 token，向服务端校验并刷新用户信息
onMounted(() => {
  userStore.fetchUserInfo()
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